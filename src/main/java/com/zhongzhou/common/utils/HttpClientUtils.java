package com.zhongzhou.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * HttpClient 工具类
 *
 * @author wdj
 * @date 2018/2/7
 */
@Slf4j
public class HttpClientUtils {

    private HttpClientUtils(){
    }

    /**
     * 超时时间设置
     */
    private static final int TIME_OUT = 15 * 1000;
    private static final String CHARSET = "UTF-8";
    private static final String ERROR_MSG = "HttpClient postMethod UrlEncodedFormEntity error!!!";

    private static PoolingHttpClientConnectionManager connectionManager;

    static {
        initConnManager();
    }

    private static void initConnManager() {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", buildSSLConnectionSocketFactory())
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(30000).build());

    }

    /**
     * 涉及到https的Socket工厂
     *
     * @return
     */
    private static SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
            // 优先绕过安全证书
            return new SSLConnectionSocketFactory(createIgnoreVerifySSL(), new String[]{"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"},
                    null,
                    new BrowserCompatHostnameVerifier());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("ssl connection fail", e);
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    /**
     * 实现一个X509TrustManager接口，绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                //why empty
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                //why empty
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

        };

        SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    private static class HttpClientHolder {
        static CloseableHttpClient httpClient;

        static {
            initClient();
        }

        static void initClient() {
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setConnectionManager(connectionManager);
            //解决post/redirect/post 302跳转问题
            httpClientBuilder.setRedirectStrategy(new CustomRedirectStrategy());

            // 请求重试处理策略
            ServiceUnavailableRetryStrategy serviceUnavailableRetryStrategy = new ServiceUnavailableRetryStrategy() {
                @Override
                public boolean retryRequest(HttpResponse httpResponse, int i, HttpContext httpContext) {
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    // 正常请求不需要重试
                    if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_FOUND) {
                        return false;
                    }

                    // 默认3次重试
                    int defaultRetryTimes = 3;
                    if (httpContext instanceof CustomHttpClientContext) {
                        defaultRetryTimes = ((CustomHttpClientContext) httpContext).getRetryTimes();
                    }

                    if (i <= defaultRetryTimes) {
                        if (log.isErrorEnabled()){
                            log.error(String.format("第%s次请求[%s]异常：%s 状态码:%s", i, httpContext.getAttribute("http.cookie-origin"), httpResponse.getStatusLine().getReasonPhrase(), statusCode));
                        }
                        return true;
                    }
                    HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
                    HttpRequest request = clientContext.getRequest();
                    // 如果请求是幂等的，就再次尝试
                    boolean requestEnclosing = !(request instanceof HttpEntityEnclosingRequest);
                    if (requestEnclosing && i <= defaultRetryTimes) {
                    	log.error("判断HttpRequest不是HttpEntityEnclosingRequest的实例对象,执行重试请求,重试间隔：0.5s");
                    	return true;
                    }
                    // 停止重试
                    return false;
                }

                @Override
                public long getRetryInterval() {
                    // 重试时间间隔0.5s
                    long tSpan = 500;
                    return tSpan;
                }
            };
            httpClientBuilder.setServiceUnavailableRetryStrategy(serviceUnavailableRetryStrategy);
            httpClient = httpClientBuilder.build();
        }
    }

    /**
     * 获取httpClient实例
     *
     * @return
     * @throws IOException
     */
    public static CloseableHttpClient getHttpClient() throws IOException {
        return HttpClientHolder.httpClient;
    }

    /**
     * 解决post请求被无限redirect
     */
    @Slf4j
    private static class CustomRedirectStrategy extends LaxRedirectStrategy {

        CustomRedirectStrategy() {
        }

        @Override
        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            URI uri = this.getLocationURI(request, response, context);
            String method = request.getRequestLine().getMethod();
            if ("post".equalsIgnoreCase(method)) {
                try {
                    HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
                    httpRequestWrapper.setURI(uri);
                    httpRequestWrapper.removeHeaders("Content-Length");
                    return httpRequestWrapper;
                } catch (Exception var7) {
                    this.log.error("强转为HttpRequestWrapper出错");
                    return new HttpPost(uri);
                }
            } else {
                return new HttpGet(uri);
            }
        }
    }

    private static class CustomHttpClientContext extends HttpClientContext {

        private static final String RETRY_TIMES = "http.retry.times";

        CustomHttpClientContext setRetryTimes(int retryTimes) {
            setAttribute(RETRY_TIMES, retryTimes);
            return this;
        }

        int getRetryTimes() {
            return (int) (getAttribute(RETRY_TIMES) == null ? 0 : getAttribute(RETRY_TIMES));
        }

        CustomHttpClientContext(final HttpContext context) {
            super(context);
        }

        public static CustomHttpClientContext create() {
            return new CustomHttpClientContext(new BasicHttpContext());
        }

    }

    /**
     * POST请求URL获取内容
     *
     * @param url     url
     * @param param   param
     * @return
     */
    public static HttpClientResult postMethod(String url, String param) {
        return postMethod(url, null, param);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url     url
     * @param headers header
     * @param param   param
     * @return
     */
    public static HttpClientResult postMethod(String url, Map<String, String> headers, String param) {
        return postMethod(url, headers, param, 0);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url        url
     * @param headers    header
     * @param param      参数
     * @param retryTimes 重试次数
     * @return
     */
    public static HttpClientResult postMethod(String url, Map<String, String> headers, String param, int retryTimes) {
        HttpPost httppost = new HttpPost(url);
        configRequest(httppost, headers);
        httppost.setEntity(new StringEntity(param, CHARSET));
        return getResponseResult(httppost, retryTimes);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url        url
     * @param headers    header
     * @param param      参数
     * @param retryTimes 重试次数
     * @param timeOut 相应超时时间
     * @return
     */
    public static HttpClientResult postMethod(String url, Map<String, String> headers, String param, int retryTimes, int timeOut) {
        HttpPost httppost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut).setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httppost.setConfig(requestConfig);
        configRequest(httppost, headers);
        httppost.setEntity(new StringEntity(param, CHARSET));
        return getResponseResult(httppost, retryTimes);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url        url
     * @param params      参数
     * @return
     */
    public static HttpClientResult postMethod(String url, Map<String, String> params) {
        HttpPost httppost = new HttpPost(url);
        configRequest(httppost, null);
        try {
            // 创建参数列表
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                list.add(new BasicNameValuePair(stringStringEntry.getKey(), stringStringEntry.getValue()));
            }
            // url格式编码
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, CHARSET);
            httppost.setEntity(urlEncodedFormEntity);
        }catch (Exception e){
            log.error(ERROR_MSG, e);
            return null;
        }

        return getResponseResult(httppost, 0);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url        url
     * @param data      参数
     * @return
     */
    public static HttpClientResult postMethod(String url, Object data) {
        HttpPost httppost = new HttpPost(url);
        configRequest(httppost, null);
        try {
            httppost.setEntity(new StringEntity(JSON.toJSONString(data), CHARSET));
        }catch (Exception e){
            log.error("HttpClient postMethod JSON toJSONString error!!!", e);
            return null;
        }

        return getResponseResult(httppost, 0);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url        url
     * @param file      参数
     * @return
     */
    public static HttpClientResult uploadMedia(String url, File file) {
        HttpPost httppost = new HttpPost(url);
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();
        httppost.setConfig(requestConfig);
        try {
            HttpEntity requestEntity = MultipartEntityBuilder.create().addPart("file",
                    new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, file.getName())).build();
            httppost.setEntity(requestEntity);
        }catch (Exception e){
            log.error(ERROR_MSG, e);
            return null;
        }

        return getResponseResult(httppost, 0);
    }

    /**
     * POST请求URL获取内容
     *
     * @param url        url
     * @param file      参数
     * @return
     */
    public static HttpClientResult uploadMedia(String url, File file, Integer timeOut) {
    	if (timeOut == null || timeOut < 0) {
    		timeOut = 3000;
    	}
        HttpPost httppost = new HttpPost(url);
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
        		.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httppost.setConfig(requestConfig);
        try {
            HttpEntity requestEntity = MultipartEntityBuilder.create().addPart("file",
                    new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, file.getName())).build();
            httppost.setEntity(requestEntity);
        }catch (Exception e){
            log.error(ERROR_MSG, e);
            return null;
        }

        return getResponseResult(httppost, 0);
    }
    
    /**
     * GET请求URL获取内容
     *
     * @param url     url
     * @return
     */
    public static HttpClientResult getMethod(String url) {
        return getMethod(url, null);
    }

    /**
     * GET请求URL获取内容
     *
     * @param url     url
     * @param headers header
     * @return
     */
    public static HttpClientResult getMethod(String url, Map<String, String> headers) {
        return getMethod(url, headers, 0);
    }

    /**
     * GET请求URL获取内容
     *
     * @param url        url
     * @param headers    header
     * @param retryTimes 重试次数
     * @return
     */
    public static HttpClientResult getMethod(String url, Map<String, String> headers, int retryTimes) {
        if (log.isDebugEnabled()){
            log.debug(String.format("开始调用接口：%s", url));
        }
        HttpGet httpget = new HttpGet(url);
        configRequest(httpget, headers);
        return getResponseResult(httpget, retryTimes);
    }

    /**
     * GET请求URL获取内容
     *
     * @param url        url
     * @param headers    header
     * @param retryTimes 重试次数
     * @param timeOut 重试次数
     * @return
     */
    public static HttpClientResult getMethod(String url, Map<String, String> headers, int retryTimes, int timeOut) {
        HttpGet httpget = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut).setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpget.setConfig(requestConfig);
        configRequest(httpget, headers);
        return getResponseResult(httpget, retryTimes);
    }

    /**
     * 设置请求参数
     *
     * @param httpRequestBase request
     * @param headers         header
     */
    private static void configRequest(HttpRequestBase httpRequestBase, Map<String, String> headers) {
        // 设置Header等
        if (!CollectionUtils.isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequestBase.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIME_OUT).setConnectTimeout(TIME_OUT).setSocketTimeout(TIME_OUT).build();
        httpRequestBase.setConfig(requestConfig);
        httpRequestBase.addHeader("Content-Type", "application/json;charset=utf-8");
    }


    private static HttpClientResult getResponseResult(HttpRequestBase requestBase, int retryTimes) {
        HttpClientResult robotUrlResult = new HttpClientResult();
        CustomHttpClientContext clientContext = CustomHttpClientContext.create().setRetryTimes(retryTimes);
        try (CloseableHttpResponse response = getHttpClient().execute(requestBase, clientContext)) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, CHARSET);
            EntityUtils.consume(entity);
            robotUrlResult.setResponseCode(response.getStatusLine().getStatusCode());
            robotUrlResult.setResult(result);
            return robotUrlResult;
        } catch (Exception e) {
            log.error(requestBase.getURI() + "请求失败：", e);
        }
        robotUrlResult.setResponseCode(-1);
        return robotUrlResult;
    }
    
    /**
     * @param url 请求url
     * @return JSONObject
     * @describe 向指定url发起get请求
     * @author y.you
     * @data 2017年09月05日 15:58
     * @version V4.0
     */
    public static JSONObject httpGet(String url, int timeOut) {
        if (timeOut <= 0) {
            timeOut = 3000;
        }
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(timeOut).setConnectTimeout(timeOut).build();
        httpGet.setConfig(requestConfig);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            response = httpClient.execute(httpGet, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (log.isWarnEnabled()){
                    log.warn(String.format("request url failed, http code=%s, url=%s", response.getStatusLine().getStatusCode(), url));
                }
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, CHARSET);
                JSONObject result = JSONObject.parseObject(resultStr);
                return result;
            }
        } catch (Exception e) {
            log.warn("request url=" + url + ", exception, msg=" + e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("IO异常：", e);
                }
            }
        }
        return null;
    }
    
    /**
     * @param url     请求路径
     * @param data    请求数据
     * @param timeOut 连接超时时间，默认3秒
     * @return JSONObject
     * @describe 向指定url发起post请求
     * @author y.you
     * @data 2017年09月05日 15:39
     * @version V4.0
     */
    public static JSONObject httpPost(String url, Object data, int timeOut) {
        if (timeOut <= 0) {
            timeOut = 3000;
        }
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).build();
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "application/json");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            StringEntity requestEntity = new StringEntity(JSON.toJSONString(data), CHARSET);
            httpPost.setEntity(requestEntity);
            response = httpClient.execute(httpPost, new BasicHttpContext());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                if (log.isWarnEnabled()){
                    log.warn(String.format("request url failed, http code=%s, url=%s", response.getStatusLine().getStatusCode(), url));
                }
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String resultStr = EntityUtils.toString(entity, CHARSET);
                JSONObject result = JSONObject.parseObject(resultStr);
                return result;
            }
        } catch (Exception e) {
            log.warn("request url=" + url + ", exception, msg=" + e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error("IO异常：", e);
                }
            }
        }
        return null;
    }
    
}
