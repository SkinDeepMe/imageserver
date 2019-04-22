package com.dingjust.utils;

import org.xvolks.jnative.JNative;

import java.util.Properties;

import org.xvolks.jnative.Type;

/**
 * Created by KQN on 2018/12/17.
 */
public class JNativeUtils {

    private static JNative instance = null;

    /*
     * CLib的so文件存放路径
     */
    private static String CLIB_PATH;

    private static String OWNER_NAME = "";
    private static String ACTIVATION_CODE = "";
    private static int PNG_COMPRESSION = 0;
    private static int PNG_FILTER_ENABLE = 0;

    /*
     * 初始化类的时候加载clib.properties文件，获取相应配置参数
     */
    static {
        try {
            Properties properties = new Properties();
            properties.load(JNativeUtils.class.getResourceAsStream("/config/clib.properties"));
            CLIB_PATH = properties.getProperty("CLIB_PATH");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(String ownerName, String activationCode,
                     int pngCompression, int pngFilterEnable) {
        OWNER_NAME = ownerName;
        ACTIVATION_CODE = activationCode;
        PNG_COMPRESSION = pngCompression;
        PNG_FILTER_ENABLE = pngFilterEnable;
    }

    private static JNative getInstance() {
        if (instance == null) {
            try {
                instance = new JNative(CLIB_PATH, "adrapeInit");
                instance.setParameter(0, Type.STRING, OWNER_NAME);
                instance.setParameter(1, Type.STRING, ACTIVATION_CODE);
                instance.invoke();

                instance = new JNative(CLIB_PATH, "adrapeSetPNGcompression");
                instance.setParameter(0, PNG_COMPRESSION);
                instance.invoke();

                instance = new JNative(CLIB_PATH, "adrapeSetPNGfilter");
                instance.setParameter(0, PNG_FILTER_ENABLE);
                instance.invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public int adrapeSetProject(String drapeModelFile) throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeSetProject");
        jNative.setParameter(0, Type.STRING, drapeModelFile);
        jNative.setRetVal(Type.INT);
        jNative.invoke();
//		String str = jNative.getRetVal();
        return jNative.getRetValAsInt();
    }

    public int adrapeSetOutput(String outputImageFile) throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeSetOutput");
        jNative.setParameter(0, Type.STRING, outputImageFile);
        jNative.setRetVal(Type.INT);
        jNative.invoke();
        return jNative.getRetValAsInt();
    }

    public int adrapeSetTexture(int textureNumber, String drapeTextureFile)
            throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeSetTexture");
        jNative.setParameter(0, textureNumber);
        jNative.setParameter(1, Type.STRING, drapeTextureFile);
        jNative.setRetVal(Type.INT);
        jNative.invoke();
        return jNative.getRetValAsInt();
    }

    public void adrapeSetDivision(int divisionFactor) throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeSetDivision");
        jNative.setParameter(0, divisionFactor);
        jNative.invoke();
    }

    public void adrapeShowBackground(int show) throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeShowBackground");
        jNative.setParameter(0, show);
        jNative.invoke();
    }

    public int adrapeRender() throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeRender");
        jNative.setRetVal(Type.INT);
        jNative.invoke();
        return jNative.getRetValAsInt();
    }

    public void adrapeFree() throws Exception {
        JNative jNative = getInstance();
        jNative = new JNative(CLIB_PATH, "adrapeFree");
        jNative.invoke();
    }
}
