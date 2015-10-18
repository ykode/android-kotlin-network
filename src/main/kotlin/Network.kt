package com.ykode.research.RxNetSample

import rx.Observable
import retrofit.http.*
import retrofit.Retrofit
import retrofit.GsonConverterFactory
import retrofit.RxJavaCallAdapterFactory

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import com.google.gson.FieldNamingPolicy
import java.lang.reflect.Type

interface UserService {
  @Headers("User-Agent: YKode Sample App/1.0")
  @FormUrlEncoded
  @POST("/post")
  public fun registerUser(@Field("username") name: String,
                          @Field("email") email: String): Observable<Model.User>

  companion object {
    fun create() : UserService {
      val gsonBuilder = GsonBuilder()

      gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      gsonBuilder.registerTypeAdapter(Model.User::class.java, UserDeserializer)
  
      val restAdapter = Retrofit.Builder()
        .baseUrl("https://httpbin.org/post")
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        .build()

      return restAdapter.create(UserService::class.java)
    }
  }
}

internal object UserDeserializer : JsonDeserializer<Model.User> {
  override fun deserialize(je : JsonElement, type : Type, 
    jdc: JsonDeserializationContext) : Model.User 
  {
    val form = je.asJsonObject.get("form")
    return Gson().fromJson(form, type)
  }
}
