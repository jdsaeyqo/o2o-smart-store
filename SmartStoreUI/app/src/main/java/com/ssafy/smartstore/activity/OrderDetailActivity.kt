package com.ssafy.smartstore.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.adapter.OrderDetailAdapter
import com.ssafy.smartstore.databinding.ActivityOrderDetailBinding
import com.ssafy.smartstore.dto.OrderDetail
import com.ssafy.smartstore.viewmodel.OrderViewModel

// F08: 주문관리 - 주문 상세 조회 - 주문 번호에 기반하여 주문을 조회할 수 있다. 이때 주문 상세 항목들 어떤 상품이 몇개 주문 되었는지에 대한 정보도 포함한다.

private const val TAG = "OrderDetailActivity_싸피"

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var orderDetailList: MutableList<OrderDetail>
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    private lateinit var binding: ActivityOrderDetailBinding

    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val oid = intent?.extras?.get("oid").toString().toInt()

        orderDetailList = mutableListOf()

        orderViewModel.getOrderDetail(oid)

        orderViewModel.orderDetail.observe(this) {
            if (orderViewModel.orderDetail.value != null) {
                val list = it
                val date = list[0]["order_time"] as String
                var totalSum = 0
                list.forEach { li ->
                    val img = li["img"] as String
                    val name = li["name"] as String
                    val unitPrice = (li["unitprice"] as Double).toInt()
                    val quantity = (li["quantity"] as Double).toInt()
                    val totalPrice = (li["totalprice"] as Double).toInt()
                    totalSum += totalPrice

                    orderDetailList.add(OrderDetail(img, name, unitPrice, quantity, totalPrice))
                }
                initOrderDetailAdapter()
                binding.tvStatus.text = "픽업 완료"
                binding.tvDate.text = date.substring(0, 19).replace("T", " ")
                binding.tvPrice.text = "${totalSum}원"
            }
        }

    }

    private fun initOrderDetailAdapter() {
        binding.rvOrderDetail.layoutManager =
            LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        orderDetailAdapter = OrderDetailAdapter(this@OrderDetailActivity)
        orderDetailAdapter.orderDetailList = orderDetailList
        binding.rvOrderDetail.adapter = orderDetailAdapter

    }

}