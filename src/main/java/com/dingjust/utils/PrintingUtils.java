package com.dingjust.utils;

import com.dingjust.constants.RenderConstans;
import com.dingjust.datas.DrapeData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 烫印
 * Created by KQN on 2019/5/20.
 */
public class PrintingUtils {
    /**
     * 根据原图和烫印图,生成效果图编码
     *
     * @param url
     * @param imageFile
     * @param printingData
     * @return
     */
    public static File printing(String url, File imageFile, DrapeData printingData) throws IOException {
        //烫印图片路径
        String pritingUrl = new StringBuilder(RenderConstans.BASE_PATH).append(RenderConstans.SLASH).append(printingData.getUrl()).toString();
        File printingFile = new File(pritingUrl);
        //输出路径
        String imageName = url.substring(url.lastIndexOf(RenderConstans.SLASH), url.lastIndexOf(RenderConstans.POINT));
        String pritingName = pritingUrl.substring(pritingUrl.lastIndexOf(RenderConstans.SLASH) + 1, pritingUrl.lastIndexOf(RenderConstans.POINT));
        File outFile = new File(new StringBuilder(RenderConstans.BASE_PATH).append(RenderConstans.SLASH).append(RenderConstans._UI).append(RenderConstans.SLASH)
                .append(RenderConstans.PRINTING_FILE).append(RenderConstans.SLASH).append(RenderConstans.TARGET).append(imageName).append(RenderConstans.MINUS)
                .append(pritingName).append(RenderConstans.POINT).append(RenderConstans.PNG).toString());
        if (!outFile.exists()) {
            merge(imageFile, printingFile, 0.5, 0.4, 0.3, outFile);
        }
        return outFile;
    }

    ;

    /**
     * 合并烫印图片
     *
     * @param imageFile    原图
     * @param printingFile 烫印图
     * @param scale        比例
     * @param outFile      输出路径
     */
    public static void merge(File imageFile, File printingFile, double xScale, double yScale, double scale, File outFile) throws IOException {
        FileOutputStream outImgStream = null;
        try {
            //原图
            Image srcImg = ImageIO.read(imageFile);
            int srcImgWidth = srcImg.getWidth(null);//获取底图的宽
            int srcImgHeight = srcImg.getHeight(null);//获取底图的高
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufImg.createGraphics();
            bufImg = g2d.getDeviceConfiguration()
                    .createCompatibleImage(srcImgWidth, srcImgHeight,
                            Transparency.TRANSLUCENT);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            //烫印图
            Image priImg = ImageIO.read(printingFile);
            int priImgWidth = priImg.getWidth(null);//获取烫印图的宽
            int priImgHeight = priImg.getHeight(null);//获取烫印图的高
            //按比例居中,取最小比例
            double scaleWidth = srcImgWidth * scale / priImgWidth;
            double scaleHeight = srcImgHeight * scale / priImgHeight;
            if (scaleHeight < scaleWidth) {
                scaleWidth = scaleHeight;
            }
            double newWidth = priImgWidth * scaleWidth;
            double newHeight = priImgHeight * scaleWidth;
            int x = (int) (srcImgWidth * xScale - newWidth / 2);
            int y = (int) (srcImgHeight * yScale - newHeight / 2);
            //烫印
            g.drawImage(priImg, x, y, (int) newWidth, (int) newHeight, null);
            g.dispose();
            // 输出图片
            outImgStream = new FileOutputStream(outFile);
            ImageIO.write(bufImg, RenderConstans.PNG, outImgStream);
            outImgStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("烫印错误:" + e.getMessage());
        } finally {
            if (null != outImgStream) {
                outImgStream.close();
            }
        }
    }
}
