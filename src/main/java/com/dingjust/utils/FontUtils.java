package com.dingjust.utils;

import java.awt.*;
import java.io.*;

/**
 * Created by KQN on 2018/12/12.
 */
public class FontUtils {
    /**
     * 读取自定义字体
     *
     * @param fontFile
     * @param fontSize
     * @return
     * @throws Exception
     */
    public static Font getDefinedFont(File fontFile, float fontSize) throws Exception {
        Font definedFont = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            is = new FileInputStream(fontFile);
            bis = new BufferedInputStream(is);
            definedFont = Font.createFont(Font.TRUETYPE_FONT, is);
            //设置字体大小，float型
            definedFont = definedFont.deriveFont(fontSize);
        } catch (FontFormatException e) {
            throw new IllegalStateException("字体转换错误:" + e.getMessage());
        } catch (IOException e) {
            throw new IllegalStateException("读取/输出文件错误:" + e.getMessage());
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return definedFont;
    }
}
