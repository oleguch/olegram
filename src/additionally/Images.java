package additionally;

import gui.GuiHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Images {


    private Images() {
        System.out.println("гав");
    }

    private static BufferedImage buttonImage;
    private static BufferedImage buttonImagePressed;
    private static BufferedImage buttonClose;
    private static BufferedImage buttonClosePressed;
    private static BufferedImage buttonMinimize;
    private static BufferedImage buttonMinimizePressed;
    private static BufferedImage background;
    private static BufferedImage logo;
    private static BufferedImage iconPhone;
    private static BufferedImage iconLock;
    private static BufferedImage logoMini;
    private static BufferedImage smallUserImage;
    private static BufferedImage largeUserImage;
    private static BufferedImage sendMessageIconPress;
    private static BufferedImage sendMessageIcon;
    private static BufferedImage pencilIcon;
    private static BufferedImage pencilIconPress;
    private static BufferedImage settingsIcon;
    private static BufferedImage settingsIconPress;
    private static BufferedImage logoMicro;
    private static BufferedImage iconBack;
    private static BufferedImage iconPlus;
    private static BufferedImage deleteUserButton;
    private static BufferedImage searchIcon;

    public static BufferedImage getIconLock() {
        if (iconLock == null)
            iconLock = getImage("img/icon-lock.png");
        return iconLock;
    }
    public static BufferedImage getLogoMini() {
        if (logoMini == null)
            logoMini = getImage("img/logo-mini.png");
        return logoMini;
    }

    public static BufferedImage getIconPhone() {
        if (iconPhone == null)
            iconPhone = getImage("img/icon-phone.png");
        return iconPhone;
    }

    public static BufferedImage getLogo() {
        if (logo == null)
            logo = getImage("img/logo.png");
        return logo;
    }

    public static BufferedImage getBackground() {
        if (background == null)
            background = getImage("img/background.png");
        return background;
    }

    public static BufferedImage getButtonImage() {
        if (buttonImage == null)
            buttonImage = getImage("img/button-background.png");
        return buttonImage;
    }

    public static BufferedImage getButtonImagePressed() {
        if (buttonImagePressed == null)
            buttonImagePressed = getImage("img/button-background-press.png");
        return buttonImagePressed;
    }

    public static BufferedImage getButtonClose() {
        if (buttonClose== null)
            buttonClose = getImage("img/icon-close.png");
        return buttonClose;
    }

    public static BufferedImage getButtonClosePressed() {
        if (buttonClosePressed== null)
            buttonClosePressed = getImage("img/icon-close-press.png");
        return buttonClosePressed;
    }

    public static BufferedImage getButtonMinimize() {
        if (buttonMinimize == null)
            buttonMinimize = getImage("img/icon-hide.png");
        return buttonMinimize;
    }

    public static BufferedImage getButtonMinimizePressed() {
        if (buttonMinimizePressed == null)
            buttonMinimizePressed = getImage("img/icon-hide-press.png");
        return buttonMinimizePressed;
    }

    public static BufferedImage getIconBack() {
        if (iconBack == null)
            iconBack= getImage("img/icon-back.png");
        return iconBack;
    }

    public static BufferedImage getIconPlus() {
        if (iconPlus== null)
            iconPlus= getImage("img/icon-plus.png");
        return iconPlus;
    }
    public static BufferedImage getDeleteUserButton() {
        if (deleteUserButton == null)
            deleteUserButton = getImage("img/delete-user.png");
        return deleteUserButton;
    }

    public static BufferedImage getSearchIcon() {
        if (searchIcon == null)
            searchIcon = getImage("img/icon-search.png");
        return searchIcon;
    }


    private static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(Images.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
            return new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR);            //заглушка
        }
    }

    public static void decorateAsImageButton(JButton button, Image image, Image imagePress, Color foreground) {
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(null);
        button.setBorderPainted(false);
        if (foreground != null)
            button.setForeground(foreground);
        else
            button.setText("");
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        Dimension size = button.getPreferredSize();
        button.setIcon(new ImageIcon(scaleImage(image, size.width, size.height)));
        button.setPressedIcon(imagePress == null ? null : new ImageIcon(scaleImage(imagePress, size.width, size.height)));
    }

    private static BufferedImage scaleImage(Image image, int width, int height) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            g2d.drawImage(image, 0,0, width, height, null);
        } finally {
            g2d.dispose();
        }
        return bufferedImage;
    }

    public static Rectangle getAreaToFill(Dimension area, Dimension image, boolean notCrop){
        double scaleX = area.getWidth() / image.getWidth();
        double scaleY = area.getHeight() / image.getHeight();
        double scale = notCrop ? Math.max(scaleX, scaleY) : Math.min(scaleX, scaleY);
        int width = (int) Math.round(image.getWidth() * scale);
        int height = (int) Math.round(image.getHeight() * scale);
        int x = (area.width - width) / 2;
        int y = (area.height - height) / 2;
        return new Rectangle(x,y,width,height);
    }
    public static BufferedImage getUserImage(boolean small) {
        return small ? getSmallUserImage() : getLargeUserImage();
    }

    private static BufferedImage loadImage(String name) {

        return GuiHelper.loadImage("img/" + name, Images.class);
    }


    public synchronized static BufferedImage getSmallUserImage() {
        if (smallUserImage == null)
            smallUserImage = loadImage("images(2).jpg");
        return smallUserImage;
    }

    public synchronized static BufferedImage getLargeUserImage() {
        if (largeUserImage == null)
            largeUserImage = loadImage("User-icon.png");
        return largeUserImage;
    }

    public static BufferedImage getSettingsIcon() {
        if (settingsIcon == null)
            settingsIcon = getImage("img/icon-settings.png");
        return settingsIcon;
    }

    public static BufferedImage getSettingsIconPress() {
        if (settingsIconPress == null)
            settingsIconPress = getImage("img/icon-settings-press.png");
        return settingsIconPress;
    }

    public static BufferedImage getPencilIcon() {
        if (pencilIcon == null)
            pencilIcon = getImage("img/icon-edit.png");
        return pencilIcon;
    }

    public static BufferedImage getPencilIconPress() {
        if (pencilIconPress == null)
            pencilIconPress = getImage("img/icon-edit-press.png");
        return pencilIconPress;
    }

    public static BufferedImage getSendMessageImage() {
        if (sendMessageIcon == null)
            sendMessageIcon = getImage("img/button-send.png");
        return sendMessageIcon;
    }

    public static BufferedImage getSendMessageImagePress() {
        if (sendMessageIconPress == null)
            sendMessageIconPress = getImage("img/button-send-press.png");
        return sendMessageIconPress;
    }

    public static BufferedImage getLogoMicro() {
        if (logoMicro == null)
            logoMicro= getImage("img/logo-micro.png");
        return logoMicro;
    }
}
