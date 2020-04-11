# Retrofit_Rxjava_Okhttp_DownloadImage_Train

1.功能

使用Retrofit+Rxjava+Okhttp框架下载图片进行展示且存入本地

2.导包

    implementation 'com.squareup.retrofit2:retrofit:2.0.2'
    implementation 'com.squareup.retrofit2:converter-gson:2.0.2'
    implementation 'com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0'


    implementation 'io.reactivex.rxjava2:rxjava:2.0.1'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'

    implementation 'com.squareup.okhttp3:okhttp:3.1.2'

3.Retrofit+Rxjava+Okhttp框架简介

RxJava + Retrofit + okHttp 已成为当前Android 网络请求最流行的方式。

（1）Retrofit 负责请求的数据和请求的结果，使用接口的方式呈现
（2）RxJava 负责异步，各种线程之间的切换
（3）OkHttp 负责请求的过程。

