package com.dingjust.utils;

import com.dingjust.constants.RenderConstans;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KQN on 2019/2/15.
 */
public class RestfulUtils {

    /**
     * 调用接口
     *
     * @param url
     * @param type
     * @param body
     * @return
     */
    public static String send(String url, RequestMethod type, String body) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(type.toString());
            conn.setRequestProperty(RenderConstans.SEND_CONN, RenderConstans.SEND_CONN_TYPE);
            conn.setRequestProperty(RenderConstans.SEND_ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            conn.setRequestProperty(RenderConstans.SEND_CONTENT, MediaType.APPLICATION_JSON_VALUE);

            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), RenderConstans.DEFAULT_CHAR_SET);
            // 发送请求参数
            out.write(body);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), RenderConstans.DEFAULT_CHAR_SET));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
        //使用finally块来关闭输出流、输入流
        finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            // 释放资源
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }
    }

}
