import org.javagram.dao.*;
import org.javagram.dao.proxy.TelegramProxy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.ParseException;


public class MyFrame extends JFrame{
    private FormConfirmSMS formConfirmSMS = new FormConfirmSMS();
    private FormPhone formPhone = new FormPhone();
    private FormNewUser formNewUser = new FormNewUser();
    private FormUsersList formUsersList = new FormUsersList();
    private MainForm mainForm = new MainForm();
    private Decoration decoration;
    private String phoneNumber;





    private static final int NAME_EMPTY=1,
                            SURNAME_EMPTY = 2,
                            FIELD_OK = 3;
    private TelegramDAO telegramDAO;
    private TelegramProxy telegramProxy;

    MyFrame(TelegramDAO telegramDAO) throws Exception {
        this.telegramDAO = telegramDAO;
        setContentPane(formPhone.getRootPanel());
        decoration = new Decoration(this);
        decoration.setContentPanel(formPhone.getRootPanel());
        //decoration.setContentPanel(formConfirmSMS.getRootPanel());
        //decoration.setContentPanel(formNewUser.getRootPanel());
        setUndecorated(true);
        setTitle("Olegram");
        setSize(800, 600);




        mainForm.setContactsPanel(formUsersList);



        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {           //установка фокуса
                super.windowOpened(e);
                formPhone.setFocusToFieldPhone();

            }
        });
        decoration.addActionListenerForMinimize(e -> setExtendedState(JFrame.ICONIFIED));

        //ЗАКРЫТИЕ ОКНА
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    telegramDAO.close();
                    System.exit(0);
                } catch (Exception e1) {
                    abort(e1);
                }
            }
        });
        //decoration.addActionListenerForClose(e -> dispose());
        decoration.addActionListenerForClose(e -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        //СМЕНА ФОРМ
        formPhone.addActionListenerForChangeForm(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkPhoneAndSendCode();
            }
        });
        formConfirmSMS.addActionListenerForChangeForm(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmByCodeFromSMS();
            }
        });
        formNewUser.addActionListenerForChangeForm(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (checkFieldsFormNewUser()) {
                    case NAME_EMPTY:
                        showMessageError("Не заполнено поле Имя");
                        formNewUser.setFocusToName();
                        break;
                    case SURNAME_EMPTY:
                        showMessageError("Не заполнено поле Фамилия");
                        formNewUser.setFocusToSurname();
                        break;
                    case FIELD_OK:
                        toFormConfirmSMS();
                        break;
                }
            }
        });
    }

    //проверка ввода полей имени-фамилии нового пользователя
    private int checkFieldsFormNewUser()  {
        PersonNewUser person = formNewUser.getPerson();
        if (person.getName().isEmpty()) {
            return NAME_EMPTY;
        } else if (person.getSurname().isEmpty()) {
            return SURNAME_EMPTY;
        } else {
            return FIELD_OK;
        }
    }

    //на форму регистрации нового пльзователя
    private void toFormNewUser() {
        nextForm(formNewUser.getRootPanel());
        formNewUser.setFocusToName();
    }

    //на форму подтверждения кода смс
    private void toFormConfirmSMS() {
        nextForm(formConfirmSMS.getRootPanel());
        formConfirmSMS.setPhoneNumberToLabel(phoneNumber);
        formConfirmSMS.setFocusToCodeField();
    }

    //проверка введенного номера телефона
    private void checkPhoneAndSendCode() {
        try {
            try {
                phoneNumber = formPhone.getPhoneNumber();
                telegramDAO.acceptNumber(phoneNumber);
                telegramDAO.sendCode();
                if (telegramDAO.canSignIn())     //если телефон зарегистрирован, показать форму ввода кода смс
                    toFormConfirmSMS();
                else
                    toFormNewUser();                //иначе - форму регистрации
            } catch (ApiException e2) {
                if (e2.isPhoneNumberInvalid()) {
                    showMessageError("Номер телефона введен неверно");
                    return;
                }
                throw e2;
            } catch (ParseException e) {
                showMessageError("Номер введен не полностью");
                formPhone.setFocusToFieldPhone();
            }
        } catch (Exception e) {
            catchException(e);
        }
    }

    //проверка ввода кода смс
    private void confirmByCodeFromSMS() {
        String smsCode = formConfirmSMS.getCode();
        try {


            try {
                if (telegramDAO.canSignIn())                              //Если пользователь зарегистрирован, то авторизовываем, иначе регистрируем
                    telegramDAO.signIn(smsCode);                                                        //отправляем только код из смс и авторизовываем пользователя
                else {
                    PersonNewUser person = formNewUser.getPerson();
                    telegramDAO.signUp(smsCode, person.getName(), person.getSurname());         //регистрируем, отправив код из смс, имя и фамилию
                }
                toFormUserList();                                                           //показать список друзей в консоли
            } catch (IOException e2) {                                                      //при ошибке показать тип и сообщение
                e2.printStackTrace();
                showMessageError(e2.getClass().toString() + "\n" + " " + e2.getMessage());
            } catch (ApiException e3) {
                if (e3.isCodeInvalid()) {
                    showMessageError("Код введен неверно");
                    return;
                }
                if (e3.isCodeEmpty()) {
                    showMessageError("Введите код подтверждения");
                    return;
                }
                if (e3.isCodeExpired()) {
                    showMessageError("Код устарел. Отправляю новый");
                    sendAndRequestCode();
                }
            }
        }  catch (Exception e) {
            catchException(e);
        }
    }

    private void sendAndRequestCode() throws IOException, ApiException {
        sendCode();
        showCodeRequest();
    }

    private void showCodeRequest() {
        formConfirmSMS.clearCode();
        formConfirmSMS.setFocusToCodeField();
    }

    private void sendCode() throws IOException, ApiException {
        telegramDAO.sendCode();
    }


    private void catchException(Exception e) {
        if (e instanceof IOException) {
            showMessageError("Потеряно соединение с сервером");
        } else if (e instanceof ApiException) {
            showMessageError("Непредвиденная ошибка API :: " + e.getMessage());
        } else {
            showMessageError("Непредвиденная ошибка");
        }
        abort(e);
    }

    private void abort(Throwable e) {
        if (e != null)
            e.printStackTrace();
        else
            System.err.println("Unknown Error");
        try {
            telegramDAO.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(-1);
    }


    private void toFormUserList() throws IOException, ApiException {
        //nextForm(formUsersList);
        nextForm(mainForm);
        createTelegramProxy();
    }

    //переключение формы
    private void nextForm (JPanel panel) {
        decoration.setContentPanel(panel);
    }

    //показать сообщение об ошибке
    private void showMessageError(String message) {
        //Decoration.showOptionDialog(MyFrame.this, message, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, null, null);
        JButton[] okButton = Helper.createDecoratedButtons(JOptionPane.DEFAULT_OPTION);
        Decoration.showOptionDialog(MyFrame.this, message, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, okButton, okButton[0]);
    }


    private void createTelegramProxy() throws ApiException {
        telegramProxy = new TelegramProxy(telegramDAO);
        updateTelegramProxy();
    }

    private void destroyTelegramProxy() {
        telegramProxy = null;
        updateTelegramProxy();
    }

    private void updateTelegramProxy() {
        //messagesFrozen++;
        try {
            formUsersList.setTelegramProxy(telegramProxy);
            formUsersList.setSelectedValue(null);
            //createMessagesForm();
            //displayDialog(null);
            displayMe(telegramProxy != null ? telegramProxy.getMe() : null);
        } finally {
            //messagesFrozen--;
        }
        formUsersList.revalidate();
        formUsersList.repaint();
        //mainForm.revalidate();
        //mainForm.repaint();
    }

    private void displayMe(Me me) {
        if (me == null) {
            mainForm.setMeText(null);
            mainForm.setMePhoto(null);
        } else {
            mainForm.setMeText(me.getFirstName() + " " + me.getLastName());
            mainForm.setMePhoto(Helper.getPhoto(telegramProxy, me, true, true));
        }
    }

    private void updateContacts() {
        //messagesFrozen++;
        try {
            Person person = formUsersList.getSelectedValue();
            formUsersList.setTelegramProxy(telegramProxy);
            formUsersList.setSelectedValue(person);
        } finally {
            //messagesFrozen--;
        }
    }


}

