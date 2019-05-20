package com.dingjust.constants;

import java.util.Properties;

/**
 * Created by KQN on 2018/12/12.
 */
public class RenderConstans {
    //image render
    public static final String IMAGES_ERVER = "imageserver";
    public static final String SIGNTURE = "SIGNTURE";
    public static final String SIGNTURE_FILE = "signture";
    public static final String PRINTING_FILE = "printing";
    public static final String SOURCE = "source";
    public static final String TARGET = "target";
    public static final String SHARE = "share";
    public static final String FILE = "file";
    public static final String QRCODE = "qrcode";
    public static final String IMAGE = "image";
    public static final String IMAGE_FILE = "images";
    public static final String FONT_FILE = "fonts";
    public static final String JPG = "jpg";
    public static final String PNG = "png";
    public static final String TTF = ".ttf";
    public static final String _UI = "_ui";
    public static final String RENDER = "render";
    public static final String NOT_FOUND_JPG = "_ui/images/notfound.png";
    public static final String VIEW = "view";
    public static final String CODE = "code";
    public static final String URL = "url";
    public static final String MSG = "msg";
    public static final String TEXT = "text";
    public static final String COLOR = "color";
    public static final String FONT = "font";
    public static final String PARTS = "parts";
    public static final String SORT = "sort";
    public static final String LOGO = "logo";
    public static final String VALUE = "value";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String PRINTING = "PRINTING";
    public static final int STATUS_200 = 200;
    public static final int STATUS_500 = 500;
    //symbol
    public static final String COLON = ":";
    public static final String ENTER = "\n";
    public static final String EQUAL = "=";
    public static final String AND = "&";
    public static final String COMMA = ",";
    public static final String UNDER_LINE = "_";
    public static final String STAR = "*";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String SLASH = "/";
    public static final String POUND = "#";
    public static final String POINT = ".";

    //Properties
    public static Properties properties;
    public static String BASE_PATH;

    //Restful
    public static final String SEND_CONN = "connection";
    public static final String SEND_CONN_TYPE = "Keep-Alive";
    public static final String SEND_ACCEPT = "accept";
    public static final String SEND_CONTENT = "Content-Type";
    public static final String DEFAULT_CHAR_SET = "UTF-8";
    public static final String CHARACTER_UTF8 = "utf-8";

    static {
        try {
            properties = new Properties();
            properties.load(RenderConstans.class.getResourceAsStream("/config/local.properties"));
            BASE_PATH = properties.getProperty("BASE_PATH");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
