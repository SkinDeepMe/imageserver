package com.dingjust.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * Created by KQN on 2018/12/12.
 */
public class ImageUtils {
    /**
     * 合并图片
     *
     * @param partUrls
     * @param imagePath
     * @return
     * @throws Exception
     */
    public static File mergeImage(List<File> partUrls, File imagePath) throws Exception {
        if (CollectionUtils.isEmpty(partUrls)) {
            throw new IllegalStateException("图片列表为空");
        }
        Graphics2D g = null;
        try {
            // 根据传进来的字符串数组，依次取图合并
            BufferedImage image = null;
            int width = 0;
            int height = 0;
            boolean setImageParam = false;
            for (File url : partUrls) {
                Image formerImage = ImageIO.read(url);
                if (!setImageParam) {// 第一张图作为底图
                    image = (BufferedImage) formerImage;
                    width = formerImage.getWidth(null);
                    height = formerImage.getHeight(null);
                    setImageParam = true;
                } else {// 依次合并
                    if (image != null) {
                        g = image.createGraphics();
                        g.drawImage(formerImage, 0, 0, width, height, null);
                    }
                }

            }
            ImageIO.write(image, "png", imagePath);
            return imagePath;
        } catch (Exception e) {
            throw new IllegalStateException("图片合并异常:" + e.getMessage());
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    /**
     * 缩放图片
     *
     * @param sourcePath
     * @param targetPath
     * @param scale
     * @return
     */
    public static File compressImage(File sourcePath, File targetPath, double scale) {
        Graphics2D g2d = null;
        try {
            // 图片压缩
            if (0 < scale && scale < 1) {
                File f2 = sourcePath;
                BufferedImage bi2 = ImageIO.read(f2);
                int newWidth = (int) (bi2.getWidth(null) * scale);
                int newHeight = (int) (bi2.getHeight(null) * scale);
                BufferedImage to = new BufferedImage(newWidth, newHeight,
                        BufferedImage.TYPE_INT_RGB);
                g2d = to.createGraphics();
                to = g2d.getDeviceConfiguration().createCompatibleImage(newWidth,
                        newHeight, Transparency.TRANSLUCENT);
                g2d.dispose();
                g2d = to.createGraphics();
                Image from = bi2.getScaledInstance(newWidth, newHeight, bi2.SCALE_AREA_AVERAGING);
                g2d.drawImage(from, 0, 0, null);
                g2d.dispose();
                ImageIO.write(to, "png", targetPath);
                return targetPath;
            } else {
                return sourcePath;
            }
        } catch (Exception e) {
            throw new IllegalStateException("缩放失败:" + e.getMessage());
        } finally {
            if (null != g2d) {
                g2d.dispose();
            }
        }
    }
}
