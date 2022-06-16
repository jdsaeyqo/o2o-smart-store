package com.ssafy.smartstore.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.ssafy.smartstore.R
import com.ssafy.smartstore.databinding.ActivityLoginBinding
import com.ssafy.smartstore.dto.ProfileImage
import com.ssafy.smartstore.dto.UserDTO
import com.ssafy.smartstore.viewmodel.UserViewModel

// F04: 회원 관리 - 회원 로그인 - 추가된 회원 정보를 이용해서 로그인 할 수 있다. 로그아웃을 하기 전까지 앱을 실행시켰 을 때 로그인이 유지되어야 한다.
// F05: 회원 관리 - 회원 로그아웃

private const val TAG = "LoginActivity_싸피"

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.googleLogin.isEnabled = false
        binding.googleLogin.setOnClickListener {
            initAuth()
        }

        prefs = getSharedPreferences("user_info", MODE_PRIVATE)

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        }



        binding.btnLogin.setOnClickListener {

            val id = binding.etId.text.toString()
            val pass = binding.etPass.text.toString()

            userViewModel.loginUser(UserDTO(id, "", pass, 0))

        }
        userViewModel.loginSuccess.observe(this){
            if(userViewModel.loginSuccess.value != null){
                if(!it){
                    Toast.makeText(this@LoginActivity, "로그인에 실패하였습니다", Toast.LENGTH_SHORT)
                        .show()

                }else{
                    val user = userViewModel.user.value as UserDTO

                    Log.d(TAG, ": $user")
                    val editor = prefs.edit()
                    editor.putString("ID", user.id)
                    editor.putString("NAME", user.name)
                    editor.commit()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

        }


        binding.btnJoin.setOnClickListener {
            val intent = Intent(this@LoginActivity, JoinActivity::class.java)
            startActivity(intent)
        }

    }

    // firebase authentication 관련
    private var mAuth: FirebaseAuth? = null
    var mGoogleSignInClient: GoogleSignInClient? = null     //구글로그인창 객체?

    // 인증 초기화
    private fun initAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // default_web_client_id 값은 build 타임에 values.xml 파일에 자동 생성
            .requestIdToken(getString(R.string.default_web_client_id))      //클라이언트 id로 토큰 요청
            .requestEmail() // 인증 방식: gmail
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        // Google에서 제공되는 signInIntent를 이용해서 인증 시도
        val signInIntent = mGoogleSignInClient!!.signInIntent

        //콜백함수 부르며 launch
        requestActivity.launch(signInIntent)
    }

    // 구글 인증 결과 획득 후 동작 처리
    private val requestActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        Log.d(
            TAG,
            "firebaseAuthWithGoogle: Activity.RESULT_OK): ${RESULT_OK}, activityResult.resultCode:${activityResult.resultCode}"
        )
        if (activityResult.resultCode == Activity.RESULT_OK) {

            // 인증 결과 획득
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "firebaseAuthWithGoogle: account: ${account}")
                firebaseAuthWithGoogle(account!!.idToken)
            } catch (e: ApiException) {
                Log.w(TAG, "google sign in failed: ", e)
            }
        }
    }

    // 구글 인증 결과 성공 여부에 따른 처리
    private fun firebaseAuthWithGoogle(idToken: String?) {
        Log.d(TAG, "firebaseAuthWithGoogle: idToken: ${idToken}")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    updateUI(user)

                } else {
                    updateUI(null)
                }
            }
    }

    // 인증 성공 여부에 따른 화면 처리
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            binding.googleLogin.isEnabled = true

            Log.d(TAG, "updateUI: updateUI: 사용자의 사진 : ${user.photoUrl}")
            Log.d(TAG, "updateUI: 사용자의 이메일: ${user.email}")
            Log.d(TAG, "updateUI: 사용자의 uid: ${user.uid}")

            val id = user.email
            val pass = user.uid
            val name = user.displayName
            val stamp = 0

            userViewModel.checkIsUsed(id!!)

            userViewModel.isUsed.observe(this) {
                if (userViewModel.isUsed.value != null) {
                    val isUsed = it
                    if (!isUsed) {
                        userViewModel.insertUser(UserDTO(id, name!!, pass, stamp))
                        FirebaseFirestore.getInstance().collection("profileImages").document(id)
                            .set(
                                ProfileImage()
                            )

                    }
                }
            }

            val editor = prefs.edit()
            editor.putString("ID", id)
            editor.putString("NAME", name)
            editor.commit()

            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()


        } else {
            Toast.makeText(this, "인증실패", Toast.LENGTH_SHORT).show()
        }
    }



    /******** 위치서비스 활성화 여부 check *********/
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private var needRequest = false

    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            "앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { _, _ ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS를 켰는지 검사함
                if (checkLocationServicesStatus()) {
                    needRequest = true
                    return
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "위치 서비스가 꺼져 있어, 현재 위치를 확인할 수 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }


}