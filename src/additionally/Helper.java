package additionally;

import gui.GuiHelper;

import gui.additionally.ContactInfo;
import gui.additionally.MyScrollbarUI;
import org.javagram.dao.KnownPerson;
import org.javagram.dao.Person;
import org.javagram.dao.proxy.TelegramProxy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Helper {
    public static JButton createDecoratedButton(int buttonType) {
        JButton button = new JButton(getTextButton(buttonType));
        Dimension size = new Dimension(80,30);
        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setSize(size);
        Images.decorateAsImageButton(button, Images.getButtonImage(), Images.getButtonImagePressed(), Color.WHITE);
        Fonts.setFontToComponent(button, Fonts.getFontButtonDialog(), Color.WHITE);
        return button;
    }

    public static JButton[] createDecoratedButtons(int buttonsType) {
        switch (buttonsType) {
            case JOptionPane.DEFAULT_OPTION:
                return new JButton[] {
                        createDecoratedButton(JOptionPane.DEFAULT_OPTION)
                };
            case JOptionPane.OK_CANCEL_OPTION:
                return new JButton[] {
                        createDecoratedButton(JOptionPane.DEFAULT_OPTION),
                        createDecoratedButton(JOptionPane.CANCEL_OPTION)
                };
            case JOptionPane.YES_NO_OPTION:
                return new JButton[] {
                        createDecoratedButton(JOptionPane.YES_OPTION),
                        createDecoratedButton(JOptionPane.NO_OPTION)
                };
            case JOptionPane.YES_NO_CANCEL_OPTION:
                return new JButton[] {
                        createDecoratedButton(JOptionPane.YES_OPTION),
                        createDecoratedButton(JOptionPane.NO_OPTION),
                        createDecoratedButton(JOptionPane.CANCEL_OPTION)
                };
            default:
                return null;
        }
    }

    private static String getTextButton(int buttonType) {
        switch (buttonType) {
            case JOptionPane.DEFAULT_OPTION:
                return "OK";
            case JOptionPane.CANCEL_OPTION:
                return "ОТМЕНА";
            case JOptionPane.YES_OPTION:
                return "ДА";
            case JOptionPane.NO_OPTION:
                return "НЕТ";
            default:
                return null;
        }
    }

    public static void decorateScrollPane(JScrollPane scrollPane) {
        int width = 3;

        JScrollBar verticalScrollBar =  scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new MyScrollbarUI());
        verticalScrollBar.setPreferredSize(new Dimension(width, Integer.MAX_VALUE));

        JScrollBar horizontalScrollBar =  scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new MyScrollbarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, width));

        for (String corner : new String[] {ScrollPaneConstants.LOWER_RIGHT_CORNER, ScrollPaneConstants.LOWER_LEFT_CORNER,
                ScrollPaneConstants.UPPER_LEFT_CORNER, ScrollPaneConstants.UPPER_RIGHT_CORNER}) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.white);
            scrollPane.setCorner(corner, panel);
        }
    }

    public static BufferedImage getPhoto(TelegramProxy telegramProxy, Person person, boolean small) {
        BufferedImage image;

        try {
            image = telegramProxy.getPhoto(person, small);
        } catch (Exception e) {
            e.printStackTrace();
            image = null;
        }

        if(image == null)
            image = Images.getUserImage(small);
        return image;
    }

    public static BufferedImage getPhoto(TelegramProxy telegramProxy, Person person, boolean small, boolean circle) {
        BufferedImage photo = getPhoto(telegramProxy, person, small);
        if(circle)
            photo = GuiHelper.makeCircle(photo);
        return photo;
    }

    public static ContactInfo toContactInfo(KnownPerson person, TelegramProxy proxy, boolean small, boolean makeCircle) {
        ContactInfo info = toContactInfo(person);
        if(proxy != null)
            info.setPhoto(getPhoto(proxy, person, small, makeCircle));
        return info;
    }
    public static ContactInfo toContactInfo(KnownPerson person) {
        return new ContactInfo(person.getPhoneNumber(), person.getFirstName(), person.getLastName(), person.getId());
    }

    public static void clearBoth(JComponent textPane) {
        clearBackground(textPane);
        clearBorder(textPane);
    }

    public static void clearBackground(JComponent component) {
        component.setOpaque(false);
        component.setBackground(new Color(0, 0, 0, 0));//Для Nimbus

    }

    public static void clearBorder(JComponent component) {
        component.setBorder(BorderFactory.createEmptyBorder());
    }
}
