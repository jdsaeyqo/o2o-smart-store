package com.ssafy.smartstore.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.dto.UserDTO
import com.ssafy.smartstore.databinding.ActivityJoinBinding
import com.ssafy.smartstore.dto.ProfileImage
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "JoinActivity_싸피"

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding

    private var check = false

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCheck.setOnClickListener {

            if (binding.etId.text.toString().isEmpty()) {
                Toast.makeText(this, "빈칸을 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = binding.etId.text.toString()
            userViewModel.checkIsUsed(id)

            userViewModel.isUsed.observe(this) {
                if (userViewModel.isUsed.value != null) {
                    val isUsed = it

                    if (!isUsed) {
                        Toast.makeText(this@JoinActivity, "사용가능한 아이디입니다.", Toast.LENGTH_SHORT)
                            .show()
                        check = true

                    } else {
                        Toast.makeText(this@JoinActivity, "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }

        }

        binding.btnJoin.setOnClickListener {

            if (!check) {
                Toast.makeText(this, "중복확인을 해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.etId.text.toString().isEmpty() || binding.etPass.text.toString()
                    .isEmpty() || binding.etNickName.text.toString().isEmpty()
            ) {
                Toast.makeText(this, "빈칸을 채워주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = binding.etId.text.toString()
            val pass = binding.etPass.text.toString()
            val name = binding.etNickName.text.toString()

            userViewModel.insertUser(UserDTO(id, name, pass, 0))
            FirebaseFirestore.getInstance().collection("profileImages").document(id).set(ProfileImage())
            Toast.makeText(this@JoinActivity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
            finish()


        }
    }


}