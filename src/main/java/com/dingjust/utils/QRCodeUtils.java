package com.dingjust.utils;

import com.dingjust.constants.RenderConstans;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by KQN on 2019/1/18.
 */
public class QRCodeUtils {
    private static final int QRCOLOR = 0x000000; // 二维码颜色
    private static final int BGWHITE = 0xFFFFFFFF; // 背景颜色

    // 用于设置QR二维码参数
    public static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;

        {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            put(EncodeHintType.CHARACTER_SET, RenderConstans.CHARACTER_UTF8);// 设置编码方式
            put(EncodeHintType.MARGIN, 0);
        }
    };

    // 生成带logo的二维码图片
    public static void drawLogoQRCode(File logoFile, File codeFile, String qrUrl, int size) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
                }
            }
            int width = image.getWidth();
            int height = image.getHeight();
            // 构建绘图对象
            Graphics2D g = image.createGraphics();
            // 读取Logo图片
            BufferedImage logo = ImageIO.read(logoFile);
            // 开始绘制logo图片
            g.drawImage(logo, width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, null);
            g.dispose();
            logo.flush();
            image.flush();
            ImageIO.write(image, RenderConstans.PNG, codeFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("生成二维码异常:" + e.getMessage());
        }
    }

    // 合成图片
    public static void mergeQRCode(File shareFile, File imgFile, File codeFile, String note) throws Exception {
        FileOutputStream outImgStream = null;
        try {
            Image srcImg = ImageIO.read(imgFile);
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            int qrCodeWidth = new Double(srcImgWidth * 0.3).intValue();//二维宽度
            int textWidth = srcImgWidth - qrCodeWidth;//文字宽度
            int border = new Double(srcImgWidth * 0.05).intValue();//边框宽度

            BufferedImage bufImg = new BufferedImage(srcImgWidth + border * 2, srcImgHeight + qrCodeWidth + border * 2, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = bufImg.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, srcImgWidth + border * 2, srcImgHeight + qrCodeWidth + border * 2);

            g.drawImage(srcImg, border, border, srcImgWidth, srcImgHeight, null);
            // 画二维码到面板
            Image srcCode = ImageIO.read(codeFile);
            g.drawImage(srcCode, textWidth + border, srcImgHeight + border, qrCodeWidth, qrCodeWidth, null);

            // 画文字到新的面板
            g.setColor(Color.BLACK);
            g.setFont(new Font("楷体", Font.BOLD, qrCodeWidth / 5)); // 字体、字型、字号
            int strWidth = g.getFontMetrics().stringWidth(note);
            int lineHeight = g.getFontMetrics().getHeight();
            if (strWidth > textWidth) {
                // 长度过长就换行
                int sub = note.length() / 2 + 1;
                String note1 = note.substring(0, sub);
                String note2 = note.substring(sub, note.length());
                int strWidth1 = g.getFontMetrics().stringWidth(note1);
                g.drawString(note1, (textWidth - strWidth1) / 2 + border, srcImgHeight + (qrCodeWidth - lineHeight) / 2 + border);
                //第二行
                g.drawString(note2, (textWidth - strWidth1) / 2 + border, srcImgHeight + qrCodeWidth / 2 + lineHeight + border);
            } else {
                g.drawString(note, (textWidth - strWidth) / 2 + border, srcImgHeight + qrCodeWidth / 2 + border); // 画文字
            }
            g.dispose();
            bufImg.flush();
            // 输出图片
            outImgStream = new FileOutputStream(shareFile);
            ImageIO.write(bufImg, RenderConstans.PNG, outImgStream);
            outImgStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("合成分享图片异常:" + e.getMessage());
        } finally {
            outImgStream.close();
        }
    }

    //直接生成分享图片
    public static void createShareImg(File shareFile, File imgFile, File logoFile, File fontFile, String qrUrl, String note) throws Exception {
        FileOutputStream outImgStream = null;
        try {
            Image srcImg = ImageIO.read(imgFile);
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            int qrCodeWidth = new Double(srcImgWidth * 0.3).intValue();//二维码宽度
            int textWidth = new Double(srcImgWidth * 0.6).intValue();//文字宽度
            int border = new Double(srcImgWidth * 0.05).intValue();//边框宽度

            BufferedImage bufImg = new BufferedImage(srcImgWidth + border * 2, srcImgHeight + qrCodeWidth + border * 2, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = bufImg.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, srcImgWidth + border * 2, srcImgHeight + qrCodeWidth + border * 2);

            g.drawImage(srcImg, border, border, srcImgWidth, srcImgHeight, null);

            // 画文字到新的面板
            g.setColor(Color.BLACK);
            if (null != fontFile) {
                g.setFont(FontUtils.getDefinedFont(fontFile, qrCodeWidth / 5));
            } else {
                g.setFont(new Font("楷体", Font.BOLD, qrCodeWidth / 5)); // 字体、字型、字号
            }
            int strWidth = g.getFontMetrics().stringWidth(note);
            int lineHeight = g.getFontMetrics().getHeight();
            if (strWidth > textWidth) {
                // 长度过长就换行
                int sub = note.length() / 2 + 1;
                String note1 = note.substring(0, sub);
                String note2 = note.substring(sub, note.length());
                int strWidth1 = g.getFontMetrics().stringWidth(note1);
                g.drawString(note1, (textWidth - strWidth1) / 2 + border, srcImgHeight + (qrCodeWidth - lineHeight) / 2 + border);
                //第二行
                g.drawString(note2, (textWidth - strWidth1) / 2 + border, srcImgHeight + qrCodeWidth / 2 + lineHeight + border);
            } else {
                g.drawString(note, (textWidth - strWidth) / 2 + border, srcImgHeight + qrCodeWidth / 2 + border); // 画文字
            }

            //生成二维码
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeWidth, hints);
            BufferedImage qrCodeImage = new BufferedImage(qrCodeWidth, qrCodeWidth, BufferedImage.TYPE_INT_RGB);
            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
            for (int x = 0; x < qrCodeWidth; x++) {
                for (int y = 0; y < qrCodeWidth; y++) {
                    qrCodeImage.setRGB(x, y, bm.get(x, y) ? QRCOLOR : BGWHITE);
                }
            }
            // 构建绘图对象
            Graphics2D g2 = qrCodeImage.createGraphics();
            // 读取Logo图片
            BufferedImage logo = ImageIO.read(logoFile);
            // 开始绘制logo图片
            g2.drawImage(logo, qrCodeWidth * 2 / 5, qrCodeWidth * 2 / 5, qrCodeWidth * 2 / 10, qrCodeWidth * 2 / 10, null);
            g2.dispose();
            logo.flush();
            qrCodeImage.flush();
            // 画二维码到面板
            g.drawImage(qrCodeImage, textWidth + border, srcImgHeight + border, qrCodeWidth, qrCodeWidth, null);

            g.dispose();
            bufImg.flush();
            // 输出图片
            outImgStream = new FileOutputStream(shareFile);
            ImageIO.write(bufImg, RenderConstans.PNG, outImgStream);
            outImgStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("直接生成分享图片异常:" + e.getMessage());
        } finally {
            outImgStream.close();
        }
    }

}
