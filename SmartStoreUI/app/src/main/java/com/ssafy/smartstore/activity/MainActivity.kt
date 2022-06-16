package com.ssafy.smartstore.activity

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.R
import com.ssafy.smartstore.databinding.ActivityMainBinding
import com.ssafy.smartstore.databinding.DialogCardBinding
import com.ssafy.smartstore.databinding.DialogPopupBinding
import com.ssafy.smartstore.service.FirebaseTokenService
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.viewmodel.RecentOrderViewModel
import com.ssafy.smartstore.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.altbeacon.beacon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "MainActivity_싸피"

class MainActivity : AppCompatActivity(), BeaconConsumer {
    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferences

    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userGrade: String

    private val userViewModel: UserViewModel by viewModels()

    private var flag = false
    private lateinit var beaconManager: BeaconManager
    private val region = Region("altbeacon", null, null, null)
    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var needBLERequest = true
    private val PERMISSIONS_CODE = 100

    private lateinit var accelerometerSensor: Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener

    // 모든 퍼미션 관련 배열
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
    )


    companion object {
        // Notification Channel ID
        const val channel_id = "smart_store"

        // RetroFit 수업 후 Network 에 업로드 할 수 있도록 구성
        fun uploadToken(token: String) {
            // 새로운 토큰 수신 시 서버로 전송
            val storeService = IntentApplication.retrofit.create(FirebaseTokenService::class.java)
            storeService.uploadToken(token).enqueue(object : Callback<String> {

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val res = response.body()
                        Log.d(TAG, "onResponse: $res")
                    } else {
                        Log.d(TAG, "onResponse: Error Code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, t.message ?: "토큰 정보 등록 중 통신오류")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }

        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // FCM 토큰 수신
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
                return@OnCompleteListener
            }

            // 새로운 FCM 등록 토큰을 얻음
            uploadToken(task.result!!)

            // token log 남기기
            Log.d(TAG, "token: ${task.result ?: "task.result is null"}")
        })

        createNotificationChannel(channel_id, "smartStore")


        startScan()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorEventListener = AccelerometerListener()

        prefs = getSharedPreferences("user_info", MODE_PRIVATE)
        userId = prefs.getString("ID", "")!!
        userName = prefs.getString("NAME", "")!!

        userViewModel.getInfo(userId)

        userViewModel.userInfo.observe(this){
            if(userViewModel.userInfo.value != null){
                Log.d(TAG, "userInfo: $it")
                val info = it
                val grade = info["grade"] as Map<String, Any>
                val step = (grade["step"] as Double).toInt()
                val title = grade["title"] as String
                userGrade = "$title $step"
            }
        }


    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            sensorEventListener,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_UI
        )

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }


    // 블루투스 켰는지 확인
    private fun isEnableBLEService(): Boolean {
        if (!bluetoothAdapter!!.isEnabled) {
            return false
        }
        return true
    }

    // Beacon Scan 시작
    private fun startScan() {
        // 블루투스 Enable 확인
        if (!isEnableBLEService()) {
            requestEnableBLE()
            Log.d(TAG, "startScan: 블루투스가 켜지지 않았습니다.")
            return
        }

        // 위치 정보 권한 허용 및 GPS Enable 여부 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSIONS_CODE
            )
        }
        Log.d(TAG, "startScan: beacon Scan start")

        // Beacon Service bind
        beaconManager.bind(this)
    }

    // 블루투스 ON/OFF 여부 확인 및 키도록 하는 함수
    private fun requestEnableBLE() {
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 사용자의 블루투스 사용이 가능한지 확인
        if (isEnableBLEService()) {
            needBLERequest = false
            startScan()
        }
    }

    // 위치 정보 권한 요청 결과 콜백 함수
    // ActivityCompat.requestPermissions 실행 이후 실행
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_CODE -> {
                if (grantResults.isNotEmpty()) {
                    for ((i, permission) in permissions.withIndex()) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //권한 획득 실패
                            Log.i(TAG, "$permission 권한 획득에 실패하였습니다.")
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onBeaconServiceConnect() {

        beaconManager.addMonitorNotifier(object : MonitorNotifier {

            override fun didEnterRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 발견하였습니다.------------${region.toString()}")
                    beaconManager.startRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didExitRegion(region: Region?) {
                try {
                    Log.d(TAG, "비콘을 찾을 수 없습니다.")
                    beaconManager.stopRangingBeaconsInRegion(region!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun didDetermineStateForRegion(i: Int, region: Region?) {}
        })


        beaconManager.addRangeNotifier { beacons, region ->
            for (beacon in beacons) {
                // Major, Minor로 Beacon 구별, 1미터 이내에 들어오면 메세지 출력

                if (isYourBeacon(beacon)) {
                    // 한번만 띄우기 위한 조건
                    Log.d(
                        TAG,
                        "distance: " + beacon.distance + " Major : " + beacon.id2 + ", Minor" + beacon.id3
                    )
                    //Thread{
                    if (beacon.distance > 0.1) {
                        flag = true
                        Log.d(TAG, "Beacon0.1: 0.1 ")
                        showDialog()
                        beaconManager.stopMonitoringBeaconsInRegion(region)
                        beaconManager.stopRangingBeaconsInRegion(region)
                    }
                    //}.run()


                }
            }

            if (beacons.isEmpty()) {

            }
        }

        try {

            beaconManager.startMonitoringBeaconsInRegion(region)


        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // 찾고자 하는 Beacon이 맞는지, 정해둔 거리 내부인지 확인
    private fun isYourBeacon(beacon: Beacon): Boolean {

        return (beacon.distance <= 10)
    }

    // 꼭 Destroy를 시켜서 beacon scan을 중지 시켜야 한다.
    // beacon scan을 중지 하지 않으면 일정 시간 이후 다시 scan이 가능하다.
    override fun onDestroy() {
        beaconManager.stopMonitoringBeaconsInRegion(region)
        beaconManager.stopRangingBeaconsInRegion(region)
        beaconManager.unbind(this)
        super.onDestroy()
    }

    fun showDialog() {

        CoroutineScope(Dispatchers.Main).launch {
            var dialog = Dialog(this@MainActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding1 = DialogPopupBinding.inflate(layoutInflater)
            dialog.setContentView(binding1.root)

            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            binding1.btnClose.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    // Notification 수신을 위한 채널 추가
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private inner class AccelerometerListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            if (event.sensor == accelerometerSensor) {
                val total = Math.sqrt(
                    Math.pow(event.values[0].toDouble(), 2.0)
                            + Math.pow(event.values[1].toDouble(), 2.0)
                            + Math.pow(event.values[2].toDouble(), 2.0)
                )

                Log.d(TAG, "onSensorChanged: $total")

                if (total > 30) {
                    var dialog = Dialog(this@MainActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    val binding1 = DialogCardBinding.inflate(layoutInflater)
                    dialog.setContentView(binding1.root)

                    dialog.window!!.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT
                    )

                    binding1.btnClose.setOnClickListener {
                        dialog.dismiss()
                    }

                    binding1.tvContent.text = "$userName 님의 회원 등급은 ${userGrade}단계 입니다"
                    binding1.tvUserInfo.text = "${userName}님 ${userGrade}"

                    dialog.show()
                }
            }


        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }


}