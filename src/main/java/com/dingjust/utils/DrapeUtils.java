package com.dingjust.utils;

import com.dingjust.datas.DrapeData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KQN on 2018/12/12.
 */
public class DrapeUtils {

    /**
     * 按顺序渲染全部图片
     *
     * @param datas
     * @return
     */
    public static List<File> renderAll(List<DrapeData> datas) {
        List<File> parts = new ArrayList<File>();
        for (DrapeData data : datas) {
            //TODO 按顺序渲染部件
        }
        return parts;
    }
}
