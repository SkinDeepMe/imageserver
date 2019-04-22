package com.dingjust.datas;

import java.util.List;

/**
 * Created by KQN on 2019/2/18.
 */
public class AbsPartData {
    String code;
    int sort;
    List<PartData> parts;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<PartData> getParts() {
        return parts;
    }

    public void setParts(List<PartData> parts) {
        this.parts = parts;
    }
}
