package com.dingjust.utils;

import com.dingjust.constants.RenderConstans;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by KQN on 2018/12/12.
 */
public class SigntureUtils {
    /**
     * 绣字
     *
     * @param sourcePath 原始图路径
     * @param tarImgPath 目标路径
     * @param text       文本
     * @param color      颜色
     * @param font       字体
     * @throws Exception
     */
    public static File signture(File sourcePath, File tarImgPath, String text, Color color, Font font) throws Exception {
        FileOutputStream outImgStream = null;
        try {
            Image srcImg = ImageIO.read(sourcePath);
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(color); //设置字体颜色
            g.setFont(font); //设置字体
            //设置绣字的坐标,居中
            FontMetrics metrics = g.getFontMetrics(font);
            int x = (srcImgWidth - metrics.stringWidth(text)) / 2;
            int y = (srcImgHeight + metrics.getHeight()) / 2;
            g.drawString(text, x, y);  //画出绣字
            g.dispose();
            // 输出图片
            outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, RenderConstans.JPG, outImgStream);
            outImgStream.flush();
            return tarImgPath;
        } catch (Exception e) {
            throw new IllegalStateException("绣字错误:" + e.getMessage());
        } finally {
            outImgStream.close();
        }
    }
}
