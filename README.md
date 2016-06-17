# RxOhttp
基于RxJava封装的OkHttp网络请求库，目前只支持基本的网络操作和文件上传
下载和HTTPS作者太忙了,还没时间搞

用法：
1.初始化 RxOkHttp.init(MyApplication.gContext, BuildConfig.DEBUG, new BaseParameterGenerator(), null);
2.普通网络请求:
    public Observable<String> request() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                //默认post
                .requestMethod(RxOkHttpRequest.Method.GET)
                .url("http://baike.baidu.com/api/openapi/BaikeLemmaCardApi")
                .put("scope", 103)
                .put("format", "json")
                .put("appid", "379020")
                .put("bk_key", "关键字")
                .put("bk_length", 600)
//                .put("file",filePath)
//                //自定义json解析器
//                .parser(new CustomParser())
//                //添加header
//                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
