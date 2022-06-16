package com.ssafy.smartstore

import android.app.Application
import android.content.SharedPreferences
import com.ssafy.smartstore.dto.UserDTO
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class IntentApplication : Application() {
    // ipconfig를 통해 IP 확인하기
    // 핸드폰으로 접속은 같은 인터넷으로 연결 되어있어야함 (유,무선)
    val SERVER_URL = "http://192.168.123.100:9999/"

    companion object {
        // 전역변수 문법을 통해 Retrofit 인스턴스를 앱 실행시 1번만 생성하여 사용 (Singleton)
        lateinit var retrofit: Retrofit
        lateinit var fcmPrefs : SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()

        // 앱이 처음 생성되는 순간, Retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fcmPrefs = getSharedPreferences("FCM", MODE_PRIVATE)
    }
}
class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        val delegate = retrofit!!.nextResponseBodyConverter<Any>(this, type!!, annotations!!)
        return Converter<ResponseBody, Any> {
            if (it.contentLength() == 0L) return@Converter UserDTO("","","",0)
            delegate.convert(it)
        }
    }

}