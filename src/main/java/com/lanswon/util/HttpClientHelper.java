package com.lanswon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.lanswon.entity.ResultMsg;

/**
 * 发送http请求帮助类
 *
 * @author zh
 */
public class HttpClientHelper {
    /**
     * 发送get请求（httpClient  请求activiti后台专用）
     *
     * @param url
     * @param params  本例中为map转换为String的格式
     * @param charset
     * @return
     * @throws Exception
     */
    public static ResultMsg sendGet(String url, Object params, String charset) throws Exception {
        ResultMsg rm = new ResultMsg();

        url = url + params;
        System.out.println(url);
        // 创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httpGet实例
        HttpGet httpGet = new HttpGet(url);
//		httpGet.setHeader("Content-Type", "application/json");
        CloseableHttpResponse resp = httpClient.execute(httpGet);
        if (resp != null && resp.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = resp.getEntity(); // 获取网页内容
            String result = EntityUtils.toString(entity, charset);
            if (!StringUtils.isEmpty(result)) {
                rm = JSONObject.parseObject(result, ResultMsg.class);
            }
        }
        if (resp != null) {
            resp.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }

        return rm;
    }

    /**
     * 发送get请求（httpClient,两参数，返回String）
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public static String sendGet(String url, Object params) throws Exception {
        ResultMsg rm = new ResultMsg();

        url = url + params;
        System.out.println(url);
        // 创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建httpGet实例
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse resp = httpClient.execute(httpGet);
        if (resp != null && resp.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = resp.getEntity(); // 获取网页内容
            String result = EntityUtils.toString(entity);
            return result;
        }
        if (resp != null) {
            resp.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }

        return null;
    }


    /**
     * 发送post请求（httpURLConnection)
     *
     * @param urlParam
     * @param params
     * @param charset
     * @return
     */
    public static String sendPost(String urlParam, Map<String, Object> params, String charset) {
        StringBuffer resultBuffer = null;
        StringBuffer sbParams = new StringBuffer();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> entrykey : params.entrySet()) {
                sbParams.append(entrykey.getKey());
                sbParams.append("=");
                sbParams.append(entrykey.getValue());
                sbParams.append("&");
            }
        }
        HttpURLConnection con = null;
        OutputStreamWriter osw = null;
        BufferedReader br = null;
        // 发送请求
        try {
            URL url = new URL(urlParam);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (sbParams != null && sbParams.length() > 0) {
                osw = new OutputStreamWriter(con.getOutputStream(), charset);
                osw.write(sbParams.substring(0, sbParams.length() - 1));
                osw.flush();
            }
            // 读取返回数据
            resultBuffer = new StringBuffer();
            //需要有content-length，否则报错
            System.out.println(con.getHeaderField("Content-Length"));
            int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
            if (contentLength > 0) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
                String temp;
                while ((temp = br.readLine()) != null) {
                    resultBuffer.append(temp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    osw = null;
                    throw new RuntimeException(e);
                } finally {
                    if (con != null) {
                        con.disconnect();
                        con = null;
                    }
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    br = null;
                    throw new RuntimeException(e);
                } finally {
                    if (con != null) {
                        con.disconnect();
                        con = null;
                    }
                }
            }
        }
        return resultBuffer.toString();

    }
}
