package com.zt.task.system.okhttp;

import com.orhanobut.logger.Logger;
import com.zt.task.system.util.ThreadPoolUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author
 */
public class OkHTTPManger {
    private static OkHttpClient okHttpClient;
    private volatile static OkHTTPManger instance;
    //提交json数据
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    //提交字符串数据
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");
    private static String responseStrGETAsyn;

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    // 使用getCacheDir()来作为缓存文件的存放路径（/data/data/包名/cache） ，
    // 如果你想看到缓存文件可以临时使用 getExternalCacheDir()（/sdcard/Android/data/包名/cache）。
    private static File cacheFile;
    private static Cache cache;

    public OkHTTPManger() {
        //        if (APP.getInstance().getApplicationContext().getCacheDir()!=null){
        //            cacheFile = new File(APP.getInstance().getCacheDir(), "Test");
        //            cache = new Cache(cacheFile, 1024 * 1024 * 10);
        //        }

        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder()
                //                .addInterceptor(new HeaderInterceptor())
                //                .addNetworkInterceptor(new CacheInterceptor())
//                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                        //          自动管理Cookie发送Request都不用管Cookie这个参数也不用去response获取新Cookie什么的了。还能通过cookieStore获取当前保存的Cookie。
                    }
                });
    }


    /**
     * 懒汉式加锁单例模式
     *
     * @return
     */
    public static OkHTTPManger getInstance() {
        if (instance == null) {
            synchronized (OkHTTPManger.class) {
                if (instance == null) {
                    instance = new OkHTTPManger();
                }
            }
        }
        return instance;
    }


    /**
     * get同步请求不需要传参数
     * 通过response.body().string()获取返回的字符串
     *
     * @param url
     * @return
     */
    public String getSyncBackString(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            // 将response转化成String
            String responseStr = response.body().string();
            return responseStr;
        } catch (IOException e) {
            Logger.e("GET同步请求解析为String异常" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get同步请求
     * 通过response.body().bytes()获取返回的二进制字节数组
     *
     * @param url
     * @return
     */
    public byte[] getSyncBackByteArray(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            // 将response转化成String
            byte[] responseStr = response.body().bytes();
            return responseStr;
        } catch (IOException e) {
            Logger.e("GET同步请求解析为byte数组异常" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get同步请求
     * 通过response.body().byteStream()获取返回的二进制字节流
     *
     * @param url
     * @return
     */
    public InputStream getSyncBackByteStream(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            // 将response转化成String
            InputStream responseStr = response.body().byteStream();
            return responseStr;
        } catch (IOException e) {
            Logger.e("GET同步请求解析为String异常" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get异步请求不传参数
     * 通过response.body().string()获取返回的字符串
     * 异步返回值不能更新UI，要开启新线程
     *
     * @param url
     * @return
     */
    public String getAsynBackStringWithoutParms(String url, String token, final MyDataCallBack myDataCallBack) {

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            myDataCallBack.onBefore(request);
            // 请求加入调度
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //                 Logger.e("GET异步请求为String失败"+e.toString());
                    myDataCallBack.requestFailure(request, e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseStrGETAsyn = response.body().string();
                    try {
                        myDataCallBack.requestSuccess(responseStrGETAsyn);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Logger.e("GET异步请求为String解析异常失败" + e.toString());
                    }
                }
            });

            myDataCallBack.onAfter();
        } catch (Exception e) {
            Logger.e("GET异步请求解析为String异常" + e.toString());
            e.printStackTrace();
        }
        return responseStrGETAsyn;
    }


    private void addMapParmsToFromBody(Map<String, String> params, FormBody.Builder builder) {
        for (Map.Entry<String, String> map : params.entrySet()) {
            String key = map.getKey();
            String value;
            /**
             * 判断值是否是空的
             */
            if (map.getValue() == null) {
                value = "";
            } else {
                value = map.getValue();
            }
            /**
             * 把key和value添加到formbody中
             */
            builder.add(key, value);
        }
    }

    /**
     * Json_POST请求参数
     *
     * @param url  url
     * @param json json
     * @return requestBody
     */
    private Request buildJsonPostRequest(String url, String json) {
        //        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        RequestBody requestBody = RequestBody.create(JSON, json);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * String_POST请求参数
     *
     * @param url  url
     * @param json json
     * @return requestBody
     */
    private Request buildStringPostRequest(String url, String json) {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, json);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private String getFileName(String url) {
        int separatorIndex = url.lastIndexOf("/");
        return (separatorIndex < 0) ? url : url.substring(separatorIndex + 1, url.length());

    }

    private void sendSuccessResultCallback(final String absolutePath, final MyDataCallBack myDataCallBack) {
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                if (myDataCallBack != null) {
                    myDataCallBack.requestSuccess(absolutePath);
                }
            }
        });
    }
}
