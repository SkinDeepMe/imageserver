package com.dingjust.controllers;

import com.dingjust.constants.RenderConstans;
import com.dingjust.datas.AbsPartData;
import com.dingjust.datas.DrapeData;
import com.dingjust.datas.PartData;
import com.dingjust.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by KQN on 2018/12/11.
 */
@Path(RenderConstans.SLASH + RenderConstans.RENDER)
public class RenderController {

    @GET
    public String check() {
        return RenderConstans.IMAGES_ERVER;
    }

    /**
     * 渲染产品图
     *
     * @param scale
     * @param inJson
     * @return
     */
    @POST
    @Path(RenderConstans.IMAGE)
    public JSONObject renderImage(@DefaultValue("1.0") @QueryParam("scale") double scale, JSONObject inJson) {
        JSONObject outJson = new JSONObject();
        int render = RenderConstans.STATUS_200;
        try {
            String productCode = inJson.getString(RenderConstans.CODE);
            String view = inJson.getString(RenderConstans.VIEW);
            List<DrapeData> parts = FileUtils.readParts(inJson);
            File imageFile = FileUtils.getMD5Image(productCode, view, parts, scale);
            if (imageFile.exists()) {//已经渲染过
                outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(imageFile));
            } else {//未渲染
                //测试
                outJson.put(RenderConstans.IMAGE, imageFile.toURI().toString());
                //判断比例
                if (scale == 1) {
                    outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(ImageUtils.mergeImage(DrapeUtils.renderAll(FileUtils.createDrapeData(productCode, view, parts)), imageFile)));
                } else {
                    File renderedImageFile = FileUtils.getMD5Image(productCode, view, parts, 1.0);
                    if (renderedImageFile.exists()) {//原图已渲染
                        outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(ImageUtils.compressImage(renderedImageFile, imageFile, scale)));
                    } else {//原图未渲染
                        outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(ImageUtils.compressImage(ImageUtils.mergeImage(DrapeUtils.renderAll(FileUtils.createDrapeData(productCode, view, parts)), renderedImageFile), imageFile, scale)));
                    }
                }
            }
        } catch (Exception e) {
            render = RenderConstans.STATUS_500;
            outJson.put(RenderConstans.MSG, e.getMessage());
            outJson.put(RenderConstans.URL, RenderConstans.NOT_FOUND_JPG);
        }
        outJson.put(RenderConstans.CODE, render);
        return outJson;
    }

    /**
     * 绣字
     *
     * @param inJson
     * @return
     */
    @POST
    @Path(RenderConstans.SIGNTURE_FILE)
    public JSONObject renderSignture(JSONObject inJson) {
        JSONObject outJson = new JSONObject();
        int render = RenderConstans.STATUS_200;
        try {
            String productCode = inJson.getString(RenderConstans.CODE);
            String view = inJson.getString(RenderConstans.VIEW);
            JSONObject parts = inJson.getJSONObject(RenderConstans.PARTS);
            Iterator i = parts.keys();
            JSONObject signture = parts.getJSONObject((String) i.next());
            String code = signture.getString(RenderConstans.CODE);
            String text = signture.getString(RenderConstans.TEXT);
            String color = RenderConstans.POUND + signture.getString(RenderConstans.COLOR);
            String font = signture.getString(RenderConstans.FONT);
            File imageFile = FileUtils.getMD5Signture(productCode, view, code, text, color, font);
            if (imageFile.exists()) {//已经渲染过
                outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(imageFile));
            } else {//未渲染
                String basePath = RenderConstans.BASE_PATH + RenderConstans.SLASH + RenderConstans._UI + RenderConstans.SLASH;
                if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(text) && !StringUtils.isEmpty(color) && !StringUtils.isEmpty(font)) {
                    File fontFile = new File(
                            new StringBuilder(basePath).append(RenderConstans.FONT_FILE).append(RenderConstans.SLASH).append(font).append(RenderConstans.TTF).toString()
                    );
                    Font fontStyle = FontUtils.getDefinedFont(fontFile, 48);
                    File sourceFile = new File(
                            new StringBuilder(basePath).append(RenderConstans.SIGNTURE_FILE).append(RenderConstans.SLASH).append(productCode).append(RenderConstans.SLASH).append(code).append(RenderConstans.POINT).append(RenderConstans.JPG).toString()
                    );
                    if (!sourceFile.exists()) {
                        throw new IllegalStateException("绣字源图片不存在:" + sourceFile.toString());
                    }
                    outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(SigntureUtils.signture(sourceFile, imageFile, text, Color.decode(color), fontStyle)));
                } else {
                    throw new IllegalStateException("绣字参数不全");
                }
            }
        } catch (Exception e) {
            render = RenderConstans.STATUS_500;
            outJson.put(RenderConstans.MSG, e.getMessage());
            outJson.put(RenderConstans.URL, RenderConstans.NOT_FOUND_JPG);
        }
        outJson.put(RenderConstans.CODE, render);
        return outJson;
    }

    /**
     * 分享
     *
     * @param inJson
     * @return
     */
    @POST
    @Path(RenderConstans.SHARE)
    public JSONObject renderShare(JSONObject inJson) {
        JSONObject outJson = new JSONObject();
        int render = RenderConstans.STATUS_200;
        try {
            String imageName = DigestUtils.md5Hex(inJson.toString());
            String basePath = RenderConstans.BASE_PATH + RenderConstans.SLASH;
            File logo = new File(new StringBuilder(basePath).append(RenderConstans._UI).append(RenderConstans.SLASH).append(RenderConstans.QRCODE).append(RenderConstans.SLASH)
                    .append(RenderConstans.LOGO).append(RenderConstans.POINT).append(RenderConstans.JPG).toString());
            File image = new File(basePath + inJson.getString(RenderConstans.IMAGE));
            String url = inJson.getString(RenderConstans.URL);
            String text = inJson.getString(RenderConstans.TEXT);
            File share = new File(new StringBuilder(basePath).append(RenderConstans._UI).append(RenderConstans.SLASH).append(RenderConstans.SHARE).append(RenderConstans.SLASH)
                    .append(imageName).append(RenderConstans.POINT).append(RenderConstans.PNG).toString());
            File fontFile = new File(new StringBuilder(basePath).append(RenderConstans._UI).append(RenderConstans.SLASH).append(RenderConstans.FONT_FILE).append(RenderConstans.SLASH).append("FZMWFont").append(RenderConstans.TTF).toString());
            //直接生成分享图片
            QRCodeUtils.createShareImg(share, image, logo, fontFile, url, text);
            outJson.put(RenderConstans.URL, FileUtils.formatImageUrl(share));
        } catch (Exception e) {
            e.printStackTrace();
            render = RenderConstans.STATUS_500;
            outJson.put(RenderConstans.MSG, e.getMessage());
            outJson.put(RenderConstans.URL, RenderConstans.NOT_FOUND_JPG);
        }
        outJson.put(RenderConstans.CODE, render);
        return outJson;
    }

    /**
     * 解析产品数据
     *
     * @param inJson
     * @return
     */
    @POST
    public JSONObject render(JSONObject inJson) {
        JSONObject outJson = new JSONObject();
        String code = inJson.getString(RenderConstans.CODE);
        String view = inJson.getString(RenderConstans.VIEW);
        //解析部件
        JSONArray absParts = inJson.getJSONArray(RenderConstans.PARTS);
        List<AbsPartData> absPartDatas = new ArrayList<>();
        absParts.stream().forEach(json -> {
            AbsPartData absPartData = new AbsPartData();
            JSONObject absPart = JSONObject.fromObject(json);
            absPartData.setSort(absPart.getInt(RenderConstans.SORT));
            absPartData.setCode(absPart.getString(RenderConstans.CODE));
            JSONArray parts = absPart.getJSONArray(RenderConstans.VALUE);
            List<PartData> partDatas = new ArrayList<>();
            parts.stream().forEach(obj -> {
                JSONObject part = JSONObject.fromObject(obj);
                PartData partData = new PartData();
                partData.setCode(part.getString(RenderConstans.CODE));
                partData.setName(part.getString(RenderConstans.NAME));
                partDatas.add(partData);
            });
            absPartData.setParts(partDatas);
            absPartDatas.add(absPartData);
        });
        //排序.编码
        absPartDatas.sort(Comparator.comparingInt(AbsPartData::getSort));
        List<PartData> md5s = new ArrayList<>();
        PartData baseData = new PartData();
        baseData.setCode(code + RenderConstans.MINUS + view);
        baseData.setName(code);
        md5s.add(baseData);
        int index = 0;
        while (index < absPartDatas.size()) {
            md5s = rewrite(md5s, absPartDatas.get(index).getParts());
            index++;
        }
        md5s.stream().forEach(md5 -> md5.setMd5(DigestUtils.md5Hex(md5.getCode())));
        outJson.put(RenderConstans.VALUE, JSONArray.fromObject(md5s));
        return outJson;
    }

    /**
     * 上传图片
     *
     * @param request
     * @return
     * @throws IOException
     */
    @POST
    @Path("createImage")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject createImage(@Context HttpServletRequest request) throws IOException {
        FileOutputStream outImgStream = null;
        JSONObject outJson = new JSONObject();
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            Map<String, List<FileItem>> items = upload.parseParameterMap(request);
            FileItem image = items.get(RenderConstans.IMAGE).get(0);
            String product = items.get("product").get(0).getString();
            String code = items.get(RenderConstans.CODE).get(0).getString();
            //产品文件夹
            String productPath = new StringBuilder(RenderConstans.BASE_PATH)
                    .append(RenderConstans.SLASH).append(RenderConstans._UI).append(RenderConstans.SLASH).append(RenderConstans.IMAGE_FILE)
                    .append(RenderConstans.SLASH).append(product).toString();
            File productFile = new File(productPath);
            if (!productFile.exists()) {
                productFile.mkdir();
            }
            //图片
            String imagePath = new StringBuilder(productPath)
                    .append(RenderConstans.SLASH).append(code).append(RenderConstans.POINT).append(RenderConstans.PNG).toString();
            File imageFile = new File(imagePath);
            outImgStream = new FileOutputStream(imageFile);
            ImageIO.write(ImageIO.read(image.getInputStream()), RenderConstans.PNG, outImgStream);
            outJson.put(RenderConstans.URL, RenderConstans.IMAGES_ERVER + RenderConstans.SLASH + FileUtils.formatImageUrl(imageFile));
        } catch (Exception e) {
            outJson.put(RenderConstans.MSG, e.getMessage());
        } finally {
            if (null != outImgStream) {
                outImgStream.close();
            }
        }
        return outJson;
    }

    /**
     * 拼接CODE
     *
     * @param md5s
     * @param parts
     * @return
     */
    private List<PartData> rewrite(List<PartData> md5s, List<PartData> parts) {
        List<PartData> result = new ArrayList<>();
        md5s.stream().forEach(md5 -> {
            for (PartData part : parts) {
                PartData data = new PartData();
                data.setCode(md5.getCode() + RenderConstans.MINUS + part.getCode());
                data.setName(md5.getName() + RenderConstans.MINUS + part.getName());
                result.add(data);
            }
        });
        return result;
    }

}
