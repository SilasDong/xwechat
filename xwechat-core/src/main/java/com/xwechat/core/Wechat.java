/**
 * @author yuanwq, date: 2017年7月22日
 */
package com.xwechat.core;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 微信api请求的执行器，包含httpClient、线程池等。所有微信请求都从这里发起
 * 
 * @author yuanwq
 */
public class Wechat {
  // private static final Logger logger = LoggerFactory.getLogger(Wechat.class);

  private static class Holder {
    private static final Wechat instance = new Wechat();
  }

  public static Wechat get() {
    return Holder.instance;
  }

  private final OkHttpClient httpClient;
  private final ObjectMapper objectMapper;

  // private final ExecutorService executorService;

  private Wechat() {
    this.httpClient = new OkHttpClient();
    this.objectMapper = new ObjectMapper();

    // ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true)
    // .setNameFormat("wechat-%d").setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
    // @Override
    // public void uncaughtException(Thread t, Throwable e) {
    // logger.error("encouter error in wechat thread " + t.getName(), e);
    // }
    // }).build();
    // this.executorService = Executors.newCachedThreadPool(threadFactory);
  }

  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  public Response rawCall(IWechatRequest<?> request) throws IOException {
    return httpClient.newCall(request.toOkHttpRequest()).execute();
  }

  public <R extends IWechatResponse> R call(IWechatRequest<R> request) throws IOException {
    Response rawResponse = rawCall(request);
    return mapJsonResponse(rawResponse.body().string(), request.getResponseClass());
  }

  private <R extends IWechatResponse> R mapJsonResponse(String text, Class<R> responseClass)
      throws IOException {
    return objectMapper.readValue(text, responseClass);
  }
}
