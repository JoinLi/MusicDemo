package com.li.bean;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Zhk on 2015/12/20.
 */
public interface APIService {
    @GET("{qq}/search.php?")
    Observable<String> getCtring(@Path("qq") String type, @Query("keywords") String keywords, @Query("page") int page);

    @GET("{qq}/{parm}")
    Observable<String> getSong(@Path("qq") String type, @Path("parm") String parm);
}
