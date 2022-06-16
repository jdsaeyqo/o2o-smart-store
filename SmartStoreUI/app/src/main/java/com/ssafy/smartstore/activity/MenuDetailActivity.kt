package com.ssafy.smartstore.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.adapter.CommentAdapter
import com.ssafy.smartstore.dto.ProductDTO
import com.ssafy.smartstore.databinding.ActivityMenuDetailBinding
import com.ssafy.smartstore.databinding.DialogRatingBarBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.fragment.OrderFragment
import com.ssafy.smartstore.service.CommentService
import com.ssafy.smartstore.viewmodel.CommentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// F09: 상품관리 - 상품별 정보 조회 - 상품별로 이름, 이미지, 단가, 총 주문 수량을 출력한다.

private const val TAG = "MenuDetailActivity_싸피"

class MenuDetailActivity : AppCompatActivity() {

    private lateinit var commentList: MutableList<Comment>
    private lateinit var commentAdapter: CommentAdapter

    private lateinit var prefs: SharedPreferences

    private lateinit var binding: ActivityMenuDetailBinding

    private lateinit var product: ProductDTO
    private lateinit var userId: String

    private val commentViewModel: CommentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("user_info", MODE_PRIVATE)
        userId = prefs.getString("ID", "")!!

        product = intent?.extras?.get("product") as ProductDTO

        Log.d(TAG, "onCreate: ${product.id}")

        val resName = product.img.substring(0, product.img.length - 4)
        val packageName = "com.ssafy.smartstore"
        val resId = resources.getIdentifier(resName, "drawable", packageName)
        binding.ivMenuDetail.setImageResource(resId)
        binding.tvName.text = product.name
        binding.tvPrice.text = product.price.toString()

        binding.btnPut.setOnClickListener {
            val quantity = binding.tvQuantity.text.toString().toInt()
            val intent = Intent(this, OrderFragment::class.java)
            intent.putExtra("product", product)
            intent.putExtra("quantity", quantity)

            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnMinus.setOnClickListener {
            if (binding.tvQuantity.text.toString().toInt() > 1) {
                val q = binding.tvQuantity.text.toString().toInt() - 1
                binding.tvQuantity.text = q.toString()
            }
        }

        binding.btnAdd.setOnClickListener {
            val q = binding.tvQuantity.text.toString().toInt() + 1
            binding.tvQuantity.text = q.toString()

        }

        binding.btnAddComment.setOnClickListener {
            if (binding.etComment.text.toString() == "") {
                Toast.makeText(this, "상품평을 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ratingDialog: DialogRatingBarBinding =
                DialogRatingBarBinding.inflate(layoutInflater)
            var progress = 0
            val newComment = binding.etComment.text.toString()
            val dialog = AlertDialog.Builder(this)
                .setView(ratingDialog.root)
                .setTitle("별점을 매겨주세요")
                .setPositiveButton("등록") { dialog, id ->
                    progress = ratingDialog.ratingBar.progress * 2
                    val comment = Comment(0, userId, product.id, progress.toDouble(), newComment)

                    commentViewModel.insertComment(comment)

                    binding.etComment.setText("")

                    dialog.dismiss()

                }
                .setNegativeButton("취소") { dialog, id ->
                    dialog.dismiss()

                }
            dialog.show()

        }

        commentList = mutableListOf()

        commentViewModel.getCommentList(product.id)

        commentViewModel.commentList.observe(this) {
            if (commentViewModel.commentList.value != null) {
                commentList = it
                var rating = 0.0
                commentList.forEach { comment ->
                    rating += comment.rating
                }
                binding.ratingBar.rating = rating.toFloat() / commentList.size / 2
                binding.rvComment.layoutManager =
                    LinearLayoutManager(
                        this@MenuDetailActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                commentAdapter = CommentAdapter(commentViewModel)
                commentAdapter.userId = userId
                commentAdapter.ctx = this
                commentAdapter.commentList = commentList
                binding.rvComment.adapter = commentAdapter
            }
        }

        commentViewModel.insertResult.observe(this){
            if(commentViewModel.insertResult.value != null){
                Toast.makeText(this@MenuDetailActivity, "상품평이 등록되었습니다", Toast.LENGTH_SHORT)
                    .show()
                commentViewModel.getCommentList(product.id)
            }
        }

    }

}