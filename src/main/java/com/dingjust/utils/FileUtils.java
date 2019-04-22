package com.dingjust.utils;

import com.dingjust.constants.RenderConstans;
import com.dingjust.datas.DrapeData;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by KQN on 2018/12/12.
 */
public class FileUtils {
    /**
     * 根据参数md5名称,避免重复渲染
     *
     * @param prodcutCode
     * @param view
     * @param parts
     * @param scale
     * @return
     */
    public static File getMD5Image(String prodcutCode, String view, List<DrapeData> parts, double scale) {
        //_ui路径
        StringBuilder sb = new StringBuilder(RenderConstans.BASE_PATH);
        //产品路径
        sb.append(RenderConstans.SLASH).append(RenderConstans._UI).append(RenderConstans.SLASH).append(RenderConstans.IMAGE_FILE).append(RenderConstans.SLASH)
                .append(prodcutCode).append(RenderConstans.SLASH);
        //图片名称
        StringBuilder imageName = new StringBuilder(prodcutCode);
        imageName.append(RenderConstans.MINUS).append(view);
        for (DrapeData data : parts) {
            imageName.append(RenderConstans.MINUS).append(data.getDrapeName());
        }
        sb.append(DigestUtils.md5Hex(imageName.toString()));
        sb.append(RenderConstans.MINUS).append(scale);
        sb.append(RenderConstans.POINT).append(RenderConstans.PNG);
        return new File(sb.toString());
    }

    /**
     * 根据参数md5名称,避免重复绣字
     *
     * @param prodcutCode
     * @param view
     * @param code
     * @param text
     * @param color
     * @param font
     * @return
     */
    public static File getMD5Signture(String prodcutCode, String view, String code, String text, String color, String font) {
        //_ui路径
        StringBuilder sb = new StringBuilder(RenderConstans.BASE_PATH);
        //产品路径
        sb.append(RenderConstans.SLASH).append(RenderConstans._UI).append(RenderConstans.SLASH).append(RenderConstans.SIGNTURE_FILE).append(RenderConstans.SLASH)
                .append(prodcutCode).append(RenderConstans.SLASH);
        //图片名称
        StringBuilder imageName = new StringBuilder(prodcutCode);
        imageName.append(RenderConstans.MINUS).append(view)
                .append(RenderConstans.MINUS).append(code)
                .append(RenderConstans.MINUS).append(text)
                .append(RenderConstans.MINUS).append(color)
                .append(RenderConstans.MINUS).append(font);
        sb.append(DigestUtils.md5Hex(imageName.toString()));
        sb.append(RenderConstans.POINT).append(RenderConstans.JPG);
        return new File(sb.toString());
    }

    /**
     * 格式化图片路径
     *
     * @param iamgeFile
     * @return
     */
    public static String formatImageUrl(File iamgeFile) {
        String path = iamgeFile.toURI().toString();
        return path.substring(path.indexOf(RenderConstans._UI), path.length());
    }

    /**
     * 解析部件顺序
     *
     * @param json
     * @return
     */
    public static List<DrapeData> readParts(JSONObject json) {
        List<DrapeData> datas = new ArrayList<>();
        JSONObject parts = json.getJSONObject(RenderConstans.PARTS);
        for (Object key : parts.keySet()) {
            DrapeData data = new DrapeData();
            JSONObject part = parts.getJSONObject((String) key);
            data.setDrapeName(part.getString(RenderConstans.CODE));
            data.setSort(part.getInt(RenderConstans.SORT));
            datas.add(data);
        }
        datas.sort(Comparator.comparingInt(DrapeData::getSort));
        return datas;
    }

    /**
     * 创建渲染队列
     *
     * @param productCode
     * @param view
     * @param datas
     * @return
     */
    public static List<DrapeData> createDrapeData(String productCode, String view, List<DrapeData> datas) {
        String fabricName = null;
        for (DrapeData data : datas) {
            if (null == fabricName) {
                data.setView(view);
                data.setDrapeName(productCode);
                data.setFabricName(fabricName);
            } else {
                data.setView(view);
                data.setFabricName(fabricName);
            }
        }
        return datas;
    }
}
