package shiyu.learn.retrofit_rxjava_image_train;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Bitmap bitmap = null;
    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = findViewById(R.id.btn_download);
        mImageView = findViewById(R.id.img);
        mButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_download:
                downLoadImg();
                break;
        }

    }


    //使用Retrofit+Rxjava+Okhttp框架下载图片
    private void downLoadImg() {


        new Thread(new Runnable() {
            @Override
            public void run() {
                //通过OkHttpClient 可以配置很多东西，比如请求超时时间，读取超时时间
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();
                //创建一个Retrofit 实例，并且完成相关的配置
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL4)
                        .addConverterFactory(GsonConverterFactory.create())//GsonConverterFactory 是默认提供的Gson 转换器
                        .client(client)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //配合RxJava 使用
                        .build();

                //用Retrofit 创建 接口实例 ApiService,并且调用接口中的方法进行网络请求
                ApiService apiService = retrofit.create(ApiService.class);
                Observable<ResponseBody> observable = apiService.downLoadImg("34264.jpg");

                /*在不指定线程的情况下， RxJava 遵循的是线程不变的原则，
                即：在哪个线程调用 subscribe()，
                就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。
                如果需要切换线程，就需要用到 Scheduler （调度器）。*/
                observable.subscribeOn(Schedulers.io())
                        //Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
                        // 行为模式和 newThread() 差不多，
                        // 区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，
                        // 因此多数情况下 io() 比 newThread() 更有效率。
                        // 不要把计算工作放在 io() 中，可以避免创建不必要的线程。
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            //请求数据回调方法
                            @Override
                            public void onNext(ResponseBody value) {
                                byte[] bys = new byte[0];
                                try {
                                    bys = value.bytes(); //注意：把byte[]转换为bitmap时，也是耗时操作，也必须在子线程
                                    bitmap = BitmapFactory.decodeByteArray(bys, 0, bys.length);
                                    //开启主线程更新UI
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mImageView.setImageBitmap(bitmap);

                                            try {//调用saveFile方法
                                                saveFile(bitmap,
                                                        Environment.getExternalStorageDirectory() + "/imgpic/");
                                                Log.e("TAG", Environment.getExternalStorageDirectory() + "/imgpic/");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });


            }
        }).start();
    }

    //保存图片到SD卡
    public void saveFile(Bitmap bm, String fileName) throws IOException {

        String imgName = UUID.randomUUID().toString() + ".jpg"; //随机生成不同的名字
        File jia = new File(fileName);              //新创的文件夹的名字
        if (!jia.exists()) {   //判断文件夹是否存在，不存在则创建
            jia.mkdirs();
        }
        File file = new File(jia + "/" + imgName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);//压缩

        bos.flush();
        bos.close();

    }



}
