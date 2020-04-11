package shiyu.learn.retrofit_rxjava_image_train;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface ApiService {

    @GET("bimg/338/{fileName}")  //{fileName}是动态码
    @Streaming//GET下载文件必须结合@Streaming使用
    Observable<ResponseBody> downLoadImg(@Path("fileName") String fileName);

}

