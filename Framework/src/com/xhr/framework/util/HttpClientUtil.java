package com.xhr.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.params.HttpParams;

import android.os.Bundle;

import com.xhr.framework.app.JsonResult;

/**
 * 网络访问辅助类
 *
 * @author wang.xy
 */

/**
 * @author alina
 * @version
 *          2012-08-02，zeng.ww，增加httpGet,getString,parseUrl,decodeUrl,encodeUrl等方法
 * <br>
 *          2012-10-31，tan.xx，get， post新增带参数requestType处理等方法<br>
 */
public class HttpClientUtil {

    /**
     * 这个变量需要重构<br>
     * 这个属性不能用于判断网络是否可用，判断网络是否可用请用 NetUtil.isNetworkAvailable() 方法；<br>
     * 这个方法仅用于用于判断返回的json是否有异常，如果有异常，表示有可能是使用了错误的网络，如CMCC等；
     */
    public static boolean LAST_REQUEST_IS_OK = true;

    /************************ 以下是http请求处理类型---start ****************/

    // 普通请求。当使用该请求时，相同地址的请求可以重复发送
    public static final int NORMAL_REQUEST = 1;
    // 当收到请求后，会取消上一次还未完成的相同URL请求
    public static final int CANCEL_SAME_URL_PREVIOUS_REQUEST = 2;
    // 当收到请求后，会取消上一次还未完成的重复请求
    public static final int CANCEL_REPEAT_PREVIOUS_REQUEST = 3;
    // 默认请求方式
    public static final int DEFALUT_REQUEST_TYPE = CANCEL_REPEAT_PREVIOUS_REQUEST;

    /************************ http请求处理类型---end ***********************/

    private static final String TAG = "HttpClientUtil";
    private static final String INTERROGATION = "?";

    private static IPDWHttpClient PDW_HTTP_CLIENT = new DefaultPDWHttpClient();

    /**
     * @param pdwHttpClient
     */
    public static void setPDWHttpClient(IPDWHttpClient pdwHttpClient) {
        if (pdwHttpClient == null) {
            throw new NullPointerException("http client 不能为空");
        }

        PDW_HTTP_CLIENT = pdwHttpClient;
    }

    // /**
    // * 登录成功后的Cookie信息
    // */
    // private static CookieStore COOKIE_STORE;

    /**
     *
     * @param cookieStore
     *            存储coockie
     */
    public static void setCookieStore(CookieStore cookieStore) {
        PDW_HTTP_CLIENT.setCookieStore(cookieStore);
    }

    /**
     * 通过post方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param httpParams
     *            http参数
     * @param postParams
     *            参数
     * @return json数据 json数据异常
     * @throws NetworkException
     *             网络异常
     * @throws MessageException
     *             业务异常
     */
    public static JsonResult post(String url, HttpParams httpParams, List<NameValuePair> postParams)
            throws NetworkException, MessageException {
        return PDW_HTTP_CLIENT.post(url, httpParams, postParams);
    }

    /**
     * 通过post方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param requestType
     *            请求处理类型
     * @param httpParams
     *            http参数
     * @param postParams
     *            参数
     * @return json数据 json数据异常
     * @throws NetworkException
     *             网络异常
     * @throws MessageException
     *             业务异常
     */
    public static JsonResult post(String url, int requestType, HttpParams httpParams, List<NameValuePair> postParams)
            throws NetworkException, MessageException {
        return PDW_HTTP_CLIENT.post(url, requestType, httpParams, postParams);
    }

    /**
     * 通过post方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param httpParams
     *            http参数
     * @param postParams
     *            参数
     * @param file
     *            文件
     * @return json数据 json数据异常
     * @throws NetworkException
     *             网络异常
     * @throws MessageException
     *             业务异常
     * @throws UnsupportedEncodingException
     */
    public static JsonResult post(String url, HttpParams httpParams, List<NameValuePair> postParams, List<File> fileList)
            throws NetworkException, MessageException, UnsupportedEncodingException {
        return PDW_HTTP_CLIENT.post(url, httpParams, postParams, fileList);
    }

    /**
     * 通过get方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param getParams
     *            附加在Url后面的参数
     * @return json数据
     * @throws MessageException
     * @throws NetworkException
     *             异常信息
     */
    public static JsonResult get(String url, List<NameValuePair> getParams) throws NetworkException, MessageException {
        return PDW_HTTP_CLIENT.get(url, getParams);
    }

    /**
     * 通过get方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param requestType
     *            请求处理类型
     * @param getParams
     *            附加在Url后面的参数
     * @return json数据 异常信息
     * @throws NetworkException
     *
     * @throws MessageException
     */
    public static JsonResult get(String url, int requestType, List<NameValuePair> getParams) throws NetworkException,
            MessageException {
        return PDW_HTTP_CLIENT.get(url, requestType, getParams);
    }

    /**
     * 通过get方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param httpParams
     *            http参数
     * @param getParams
     *            附加在Url后面的参数
     * @return json数据
     * @throws NetworkException
     *             异常信息
     * @throws MessageException
     * @throws NetworkException
     */
    public static JsonResult get(String url, HttpParams httpParams, List<NameValuePair> getParams)
            throws NetworkException, MessageException {
        return PDW_HTTP_CLIENT.get(url, getParams);
    }

    /**
     * 通过get方式，跟服务器进行数据交互。该方法已经进行了网络检查
     *
     * @param url
     *            url地址
     * @param httpParams
     *            http参数
     * @param getParams
     *            附加在Url后面的参数
     * @return json数据
     * @param requestType
     * @throws NetworkException
     *             异常信息
     * @throws MessageException
     */
    public static JsonResult get(String url, int requestType, HttpParams httpParams, List<NameValuePair> getParams)
            throws NetworkException, MessageException {
        return PDW_HTTP_CLIENT.get(url, requestType, getParams);
    }

    /**
     * 接口返回字符串时使用
     *
     * @param url
     * @param getParams
     * @throws NetworkException
     * @return JsonResult
     * @throws
     */
    public static JsonResult getByString(String url, List<NameValuePair> getParams) throws NetworkException {
        return PDW_HTTP_CLIENT.getByString(url, getParams);
    }

    /**
     * @param url
     * @param getParams
     * @return string
     */
    public static String buildUrl(String url, List<NameValuePair> getParams) {
        if (getParams != null && getParams.size() > 0) {
            String returnUrl = url;
            if (url.indexOf(INTERROGATION) < 0) {
                returnUrl = url + INTERROGATION;
            }
            String tempParamters = "";
            for (int i = 0; i < getParams.size(); i++) {
                NameValuePair nameValuePair = getParams.get(i);
                tempParamters = tempParamters + "&" + nameValuePair.getName() + "="
                        + URLEncoder.encode(nameValuePair.getValue());
            }
            returnUrl = returnUrl + tempParamters.substring(1);
            EvtLog.d(TAG, returnUrl);

            return returnUrl;
        }

        return url;
    }

    /**
     *
     * @param url
     *            url地址
     * @param httpParams
     *            参数
     * @return 字节码
     * @throws ClientProtocolException
     *             协议异常
     * @throws IOException
     *             IO流异常
     */
    // public static byte[] getBytes(String url, HttpParams httpParams) throws
    // ClientProtocolException, IOException {
    // return PDW_HTTP_CLIENT.getBytes(url, httpParams);
    // }

    /**
     *
     * @param url
     *            url地址
     * @param requestType
     *            请求处理类型
     * @param httpParams
     *            参数
     * @return 字节码
     * @throws ClientProtocolException
     *             协议异常
     * @throws IOException
     *             IO流异常
     */
    // public static byte[] getBytes(String url, int requestType, HttpParams
    // httpParams) throws ClientProtocolException,
    // IOException {
    // return PDW_HTTP_CLIENT.getBytes(url, requestType, httpParams);
    // }

    /**
     * @param url
     * @param httpParams
     * @param getParams
     * @return http响应
     * @throws NetworkException
     *             自定义网络异常
     * @throws Exception
     */
    public static HttpResponse getResponse(String url, HttpParams httpParams, List<NameValuePair> getParams)
            throws NetworkException {
        return PDW_HTTP_CLIENT.getResponse(url, httpParams, getParams);
    }

    /**
     * @param url
     * @param requestType
     *            请求处理类型
     * @param httpParams
     * @param getParams
     * @return http响应
     * @throws NetworkException
     *             自定义网络异常
     * @throws Exception
     */
    public static HttpResponse getResponse(String url, int requestType, HttpParams httpParams,
                                           List<NameValuePair> getParams) throws NetworkException {
        return PDW_HTTP_CLIENT.getResponse(url, requestType, httpParams, getParams);
    }

    /**
     * @param response
     * @return response中的相应内容
     * @throws IOException
     */
    public static String getResponseString(HttpResponse response) throws IOException {
        return PDW_HTTP_CLIENT.getResponseString(response);
    }

    /**
     * 获取网络返回的原始json格式数据
     *
     * @param url
     *            请求地址
     * @param getParams
     *            参数
     * @return JSONObject 返回类型
     * @throws
     */
    // public static JSONObject httpGet(String url, List<NameValuePair>
    // getParams) {
    // return PDW_HTTP_CLIENT.httpGet(url, getParams);
    // }

    /**
     * 获取网络返回的原始json格式数据
     *
     * @param url
     *            请求地址
     * @param requestType
     *            请求处理类型
     * @param getParams
     *            参数
     * @return JSONObject 返回类型
     * @throws
     */
    // public static JSONObject httpGet(String url, int requestType,
    // List<NameValuePair> getParams) {
    // return PDW_HTTP_CLIENT.httpGet(url, requestType, getParams);
    // }

    /**
     * 获取接口返回的字符内容
     *
     * @param url
     *            接口地址
     * @param getParams
     *            参数
     * @return String 返回类型
     * @throws
     */
    // public static String getString(String url, List<NameValuePair> getParams)
    // {
    // return PDW_HTTP_CLIENT.getString(url, getParams);
    // }

    /**
     * 获取接口返回的字符内容
     *
     * @param url
     *            接口地址
     * @param requestType
     *            请求处理类型
     * @param getParams
     *            参数
     * @return String 返回类型
     * @throws
     */
    // public static String getString(String url, int requestType,
    // List<NameValuePair> getParams) {
    // return PDW_HTTP_CLIENT.getString(url, requestType, getParams);
    // }

    /*********************** 微博使用相关方法 **************************/
    /**
     * 把请求地址中的参数装入到bundle对象中.
     *
     * @param url
     *            请求地址
     * @return a dictionary bundle of keys and values
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("weiboconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    /**
     * 对Url进行解码
     *
     * @param s
     *            请求地址
     * @return 解码后的值
     */
    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String[] array = s.split("&");
            for (String parameter : array) {
                String[] v = parameter.split("=");
                if (v.length == 2) {
                    params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
                } else if (v.length == 1) {
                    params.putString(URLDecoder.decode(v[0]), "");
                }
            }
        }
        return params;
    }

    /**
     * 对Url进行编码
     *
     * @param parameters
     *            参数
     * @return 编码后的地址
     */
    public static String encodeUrl(UrlParameters parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int loc = 0; loc < parameters.size(); loc++) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String value = (parameters.getValue(loc) == null || "".equals(parameters.getValue(loc))) ? "" : URLEncoder
                    .encode(parameters.getValue(loc));
            sb.append(URLEncoder.encode(parameters.getKey(loc)) + "=" + value);
        }
        return sb.toString();
    }

    /**
     * 设置CookieStore
     *
     * @param domain
     *            域名
     * @param name
     *            Cookie名称
     * @param values
     *            值 参数
     * @throws
     */
    public static void setCookieStores(String domain, String name, String values) {
        PDW_HTTP_CLIENT.setCookieStores(domain, name, values);
    }

    /**
     * 获取当前请求的Cookies
     *
     * @return 返回
     * @throws
     */
    public static String getCookies() {
        return PDW_HTTP_CLIENT.getCookies();
    }
}
