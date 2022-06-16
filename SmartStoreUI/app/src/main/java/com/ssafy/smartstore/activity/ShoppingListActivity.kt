package com.ssafy.smartstore.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.adapter.ShoppingListAdapter
import com.ssafy.smartstore.dto.OrderDTO
import com.ssafy.smartstore.dto.OrderDetailDTO
import com.ssafy.smartstore.dto.ProductDTO
import com.ssafy.smartstore.dto.StampDTO
import com.ssafy.smartstore.databinding.ActivityShoppingListBinding
import com.ssafy.smartstore.dto.ShoppingList
import com.ssafy.smartstore.service.OrderService
import com.ssafy.smartstore.util.FCMUtil
import com.ssafy.smartstore.viewmodel.OrderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// F06: 주문 관리 - 상품 주문 - 로그인한 사용자는 상품 상세 화면 에서 n개를 선정하여 장바구니에 등록할 수 있다. 로그인 한 사용자만 자기의 계정으로 구매를 처리할 수 있다.
// 장바구니 화면

private const val TAG = "ShoppingListActivity_싸피"

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingListBinding

    private lateinit var shoppingMap: HashMap<ProductDTO, Int>
    private lateinit var shoppingList: MutableList<ShoppingList>
    private lateinit var shoppingListAdapter: ShoppingListAdapter

    private var tableNum = -1

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pIntent: PendingIntent
    private lateinit var filters: Array<IntentFilter>
    private lateinit var prefs: SharedPreferences

    private lateinit var list: MutableList<Map<String, Any>>

    private val orderViewModel: OrderViewModel by viewModels()

    private var flag = 0

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shoppingList = mutableListOf()

        if (intent.extras?.get("orderList") != null) {
            shoppingMap = intent.extras?.get("orderList") as HashMap<ProductDTO, Int>
            flag = 1
            setMap()

            Log.d(TAG, "onCreate: $shoppingMap")
        } else {
            flag = 2

            val oid = intent.extras?.get("oid")
            orderViewModel.getOrderDetail(oid as Int)

        }

        orderViewModel.orderDetail.observe(this) {
            if (orderViewModel.orderDetail.value != null) {
                list = it
                setList()
            }
        }

        initShoppingListAdapter()

        prefs = getSharedPreferences("user_info", MODE_PRIVATE)
        val userId = prefs.getString("ID", "")
        Log.d("prefs", "$userId")

        binding.btnOrder.isEnabled = false
        binding.btnOrder.text = "주문 불가"

        binding.btnIn.setOnClickListener {
            binding.btnIn.isSelected = true
            binding.btnOut.isSelected = false
            binding.btnOrder.isEnabled = false
            binding.btnOrder.text = "주문 불가"
            Toast.makeText(this, "테이블 NFC를 먼저 태깅해주세요", Toast.LENGTH_LONG).show()

        }

        binding.btnOut.setOnClickListener {
            binding.btnIn.isSelected = false
            binding.btnOut.isSelected = true
            binding.btnOrder.isEnabled = true
            binding.btnOrder.text = "주문 하기"
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            finish()
        }

        val intent = Intent(this, ShoppingListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val tagFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        filters = arrayOf(tagFilter)

        binding.btnOrder.setOnClickListener {
            orderViewModel.getLastOrderId()
        }

        orderViewModel.orderLastOrderId.observe(this) {
            if (orderViewModel.orderLastOrderId.value != null) {
                val detailList = mutableListOf<OrderDetailDTO>()
                val oid = it + 1
                if (flag == 1) {
                    for (p in shoppingMap) {
                        val product = p.key
                        val quantity = p.value
                        detailList.add(OrderDetailDTO(0, oid, product.id, quantity))

                    }
                } else if (flag == 2) {

                    list.forEach { li ->
                        val pid = (li["p_id"] as Double).toInt()
                        val quantity = (li["quantity"] as Double).toInt()
                        detailList.add(OrderDetailDTO(0, oid, pid, quantity))
                    }
                }
                orderViewModel.makeOrder(oid, userId!!,tableNum,detailList)
            }
        }

        orderViewModel.orderResult.observe(this){
            if(orderViewModel.orderResult.value != null){
                val res = it
                if (res > 0) {
                    Toast.makeText(this@ShoppingListActivity, "주문이 완료되었습니다", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this@ShoppingListActivity, "주문이 실패하였습니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    private fun setMap() {
        shoppingList.clear()
        var totalQuantity = 0
        var totalPrice = 0

        for (product in shoppingMap) {
            val key = product.key
            val quantity = product.value

            totalQuantity += quantity
            totalPrice += key.price * quantity

            shoppingList.add(
                ShoppingList(
                    key.img,
                    key.name,
                    key.price,
                    quantity,
                    key.price * quantity
                )
            )
        }

        binding.tvQuantity.text = "총 $totalQuantity 개"
        binding.tvTotalPrice.text = "$totalPrice 원"
    }

    private fun setList() {
        shoppingList.clear()
        var totalQuantity = 0
        var totalSum = 0

        list.forEach {
            val img = it["img"] as String
            val name = it["name"] as String
            val unitPrice = (it["unitprice"] as Double).toInt()
            val quantity = (it["quantity"] as Double).toInt()
            val totalPrice = (it["totalprice"] as Double).toInt()
            totalSum += totalPrice
            totalQuantity += quantity

            shoppingList.add(ShoppingList(img, name, unitPrice, quantity, totalPrice))
        }
        binding.tvQuantity.text = "총 $totalQuantity 개"
        binding.tvTotalPrice.text = "$totalSum 원"
    }

    private fun initShoppingListAdapter() {
        binding.rvShoppingList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        shoppingListAdapter = ShoppingListAdapter(this)
        shoppingListAdapter.shoppingList = shoppingList

        shoppingListAdapter.onRemoveButtonClick = object : ShoppingListAdapter.OnRemoveButtonClick {
            override fun onRemoveClick(view: View, position: Int) {
                when (flag) {
                    1 -> {

                        val p = shoppingList[position]

                        val iter = shoppingMap.iterator()
                        while (iter.hasNext()) {
                            val next = iter.next()
                            if (next.key.name == p.name) {
                                iter.remove()
                            }
                        }
                        setMap()
                        initShoppingListAdapter()

                    }
                    2 -> {
                        list.removeAt(position)
                        setList()
                        initShoppingListAdapter()
                    }
                }

            }
        }
        binding.rvShoppingList.adapter = shoppingListAdapter
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pIntent, filters, null)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 1. 인텐트에서 NEDF메시지 배열 데이터 가져옴
        val rawMsg = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

        // 2. Data를 변환
        if (rawMsg != null) {

            val msgArr = arrayOfNulls<NdefMessage>(rawMsg.size)

            for (i in msgArr.indices) {
                msgArr[i] = rawMsg[i] as NdefMessage
            }

            // 3. NdefMessage에서 NdefRecord -> payload
            val recInfo = msgArr[0]!!.records[0]
            val payload = recInfo.payload
            val msg = String(payload)
            val data = recInfo.type
            val recType = String(data)

            if (recType == "T") {
                tableNum = msg.substring(3).toInt()
                Log.d("onNewIntent", "테그 데이터 :${msg.substring(3)}")
                Toast.makeText(this, "${tableNum}번 테이블 번호가 등록되었습니다", Toast.LENGTH_SHORT).show()
                binding.btnOrder.isEnabled = true
                binding.btnOrder.text = "주문 하기"

            }

        }
    }

}