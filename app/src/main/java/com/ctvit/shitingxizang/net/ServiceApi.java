package com.ctvit.shitingxizang.net;



import com.ctvit.shitingxizang.entity.CardGroupsEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by goldze on 2017/6/15.
 */

public interface ServiceApi {
    @GET("action/apiv2/banner?catalog=1")
    Observable<CardGroupsEntity> demoGet();

    @FormUrlEncoded
    @POST("action/apiv2/banner")
    Observable<CardGroupsEntity> demoPost(@Field("catalog") String catalog);

    //财经上导航
    @FormUrlEncoded
    @POST("financemobileinf/rest/cctv/cardgroups")
    Observable<CardGroupsEntity> getCardGroups(@FieldMap Map<String, String> map);


}
