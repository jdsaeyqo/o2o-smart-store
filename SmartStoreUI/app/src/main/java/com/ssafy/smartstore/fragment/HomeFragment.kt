package com.ssafy.smartstore.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.activity.ShoppingListActivity
import com.ssafy.smartstore.adapter.AlarmAdapter
import com.ssafy.smartstore.adapter.RecentOrderAdapter
import com.ssafy.smartstore.databinding.FragmentHomeBinding
import com.ssafy.smartstore.dto.MyOrder
import com.ssafy.smartstore.util.FCMUtil
import com.ssafy.smartstore.viewmodel.RecentOrderViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private var alarmList: MutableList<String> = mutableListOf()
    private lateinit var alarmAdapter: AlarmAdapter

    private var myRecentOrderList = mutableListOf<MyOrder>()
    private lateinit var recentOrderAdapter: RecentOrderAdapter
    private val recentOrderViewModel : RecentOrderViewModel by viewModels()

    private lateinit var prefs: SharedPreferences
    private lateinit var ctx: Context

    private lateinit var userId: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = ctx.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)

        userId = prefs.getString("ID", "")!!
        val userName = prefs.getString("NAME", "")

        initAlarmAdapter()
        initRecentOrders()

        binding.tvName.text = "${userName}님"

    }

    private fun initRecentOrders() {
        recentOrderViewModel.getRecentOrderList(userId)
            recentOrderViewModel.recentOrderList.observe(viewLifecycleOwner){
                if(recentOrderViewModel.recentOrderList.value != null){
                    myRecentOrderList = recentOrderViewModel.recentOrderList.value!!
                   initRecentOrdersAdapter()
                }
            }

    }

    private fun initRecentOrdersAdapter() {
        recentOrderAdapter = RecentOrderAdapter(ctx)
        recentOrderAdapter.recentOrderList = myRecentOrderList

        recentOrderAdapter.setItemClickListener(object : RecentOrderAdapter.ItemClickListener {
            override fun onClick(view: View, myOrder: MyOrder, position: Int) {

                val intent = Intent(ctx, ShoppingListActivity::class.java)
                intent.putExtra("oid", myOrder.oid)
                startActivity(intent)
            }

        })

        binding.rvRecentOrder.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvRecentOrder.adapter = recentOrderAdapter
    }

    private fun initAlarmAdapter() {
        alarmList = FCMUtil.getSharedPreferenceToList()
        binding.rvAlarm.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        alarmAdapter = AlarmAdapter()
        alarmAdapter.alarmList = alarmList
        alarmAdapter.onRemoveButtonClick = object : AlarmAdapter.OnRemoveButtonClick {
            override fun onRemoveClick(view: View, position: Int) {
                alarmList.removeAt(position)
                FCMUtil.setListToSharedPreference(alarmList)
                initAlarmAdapter()
            }

        }
        binding.rvAlarm.adapter = alarmAdapter
    }

}