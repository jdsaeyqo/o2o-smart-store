package com.ssafy.smartstore.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.activity.LoginActivity
import com.ssafy.smartstore.activity.OrderDetailActivity
import com.ssafy.smartstore.adapter.MyOrderAdapter
import com.ssafy.smartstore.databinding.FragmentMypageBinding
import com.ssafy.smartstore.dto.MyOrder
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.viewmodel.RecentOrderViewModel
import com.ssafy.smartstore.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMypageBinding
    private lateinit var myOrderList: MutableList<MyOrder>
    private lateinit var myOrderAdapter: MyOrderAdapter

    private lateinit var userId: String
    private lateinit var userName: String

    private lateinit var ctx: Context
    private lateinit var prefs: SharedPreferences

    private val userViewModel: UserViewModel by activityViewModels()
    private val recentOrderViewModel: RecentOrderViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = ctx.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        userId = prefs.getString("ID", "")!!
        userName = prefs.getString("NAME", "")!!

        binding.tvName.text = "${userName}님"
        myOrderList = mutableListOf()

        binding.ivUser.setOnClickListener {
            openGallery()
        }

        getProfileImage()
        getUserInfo()
        getRecentOrders()


        binding.btnLogout.setOnClickListener {
            prefs.edit().remove("ID").commit()
            prefs.edit().remove("PASS").commit()

            val intent = Intent(ctx, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

    }

    private fun getUserInfo() {
        userViewModel.userInfo.observe(viewLifecycleOwner) {
            if (userViewModel.userInfo.value != null) {
                val info = userViewModel.userInfo.value!!
                val grade = info["grade"] as Map<String, Any>
                val img = grade["img"] as String
                val step = (grade["step"] as Double).toInt()
                val to = (grade["to"] as Double).toInt()
                val title = grade["title"] as String

                val resName = img.substring(0, img.length - 4)
                val packageName = "com.ssafy.smartstore"
                val resId = resources.getIdentifier(resName, "drawable", packageName)
                binding.ivGrade.setImageResource(resId)

                when (title) {
                    "씨앗" -> {
                        binding.progressBar.max = 10
                        val current = 10 - to
                        binding.progressBar.progress = current
                        binding.tvRestStamp.text = "$current / 10"
                    }
                    "꽃" -> {
                        binding.progressBar.max = 15
                        val current = 15 - to
                        binding.tvRestStamp.text = "$current / 15"
                    }
                    "열매" -> {
                        binding.progressBar.max = 20
                        val current = 20 - to
                        binding.tvRestStamp.text = "$current / 20"
                    }
                    "커피콩" -> {
                        binding.progressBar.max = 25
                        val current = 25 - to
                        binding.tvRestStamp.text = "$current / 25"
                    }
                    "커피나무" -> {
                        binding.progressBar.max = 1
                        binding.progressBar.progress = 1
                        binding.tvRestStamp.text = "0 / 0"
                    }

                }

                binding.tvGrade.text = "$title ${step}단계"
                binding.tvNextGrade.text = "다음 단계까지 ${to}개 남았습니다"

            }
        }

    }

    private fun getRecentOrders() {
        recentOrderViewModel.getRecentOrderList(userId)
        recentOrderViewModel.recentOrderList.observe(viewLifecycleOwner) {
            if (recentOrderViewModel.recentOrderList.value != null) {
                myOrderList = recentOrderViewModel.recentOrderList.value!!
                initRecentOrdersAdapter()
            }
        }
    }

    private fun initRecentOrdersAdapter() {
        myOrderAdapter = MyOrderAdapter(ctx)
        myOrderAdapter.myOrderList = myOrderList

        myOrderAdapter.setItemClickListener(object : MyOrderAdapter.ItemClickListener {
            override fun onClick(view: View, myOrder: MyOrder, position: Int) {
                val intent = Intent(ctx, OrderDetailActivity::class.java)
                intent.putExtra("oid", myOrder.oid)
                startActivity(intent)
            }

        })

        binding.rvOrder.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvOrder.adapter = myOrderAdapter
    }

    private fun getProfileImage() {
        userId.let { uid ->
            FirebaseFirestore.getInstance().collection("profileImages").document(uid)
                .addSnapshotListener { value, error ->

                    if (value == null) {
                        return@addSnapshotListener
                    }

                    if (value.data != null) {
                        val url = value.data!!["imageUri"] ?: return@addSnapshotListener

                        Glide.with(ctx).load(url).apply(RequestOptions().circleCrop())
                            .into(binding.ivUser)

                    } else {
                        return@addSnapshotListener
                    }

                }

        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }


    private val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {

            val imageUri: Uri? = it.data?.data

            val storageRef = userId.let { uid ->
                FirebaseStorage.getInstance().reference.child("profileImages").child(uid)
            }

            if (imageUri != null) {
                storageRef.putFile(imageUri).continueWithTask {
                    return@continueWithTask storageRef.downloadUrl
                }.addOnSuccessListener { u ->

                    val tsDoc = FirebaseFirestore.getInstance().collection("profileImages")
                        .document(userId)
                    tsDoc.update("imageUri", u.toString())
                }
            }

        }
    }
}