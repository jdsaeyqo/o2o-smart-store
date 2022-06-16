package com.ssafy.smartstore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.OrderDetail

class OrderDetailAdapter(val context : Context) :
    RecyclerView.Adapter<OrderDetailAdapter.RecentOrderDetailViewHolder>() {

    var orderDetailList = mutableListOf<OrderDetail>()

    inner class RecentOrderDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductDetail: ImageView = itemView.findViewById(R.id.item_iv_product_detail)
        val tvOrderMenuDetail: TextView = itemView.findViewById(R.id.item_tv_order_menu_detail)
        val tvPriceDetail: TextView = itemView.findViewById(R.id.item_tv_price_detail)
        val tvOrderQuantityDetail: TextView =
            itemView.findViewById(R.id.item_tv_order_quantity_detail)
        val tvOrderTotalPriceDetail: TextView =
            itemView.findViewById(R.id.item_tv_total_price_detail)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentOrderDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recent_order_detail_item, parent, false)
        return RecentOrderDetailViewHolder(itemView)

    }
//    data class RecentOrderDetail(val imgDetail: Int, val orderMenuDetail : String, val priceDetail : String,val quantityDetail : String,val totalPriceDetail:String)

    override fun onBindViewHolder(holder: RecentOrderDetailViewHolder, position: Int) {
        val resName = orderDetailList[position].img.substring(0,orderDetailList[position].img.length-4)

        val packageName = "com.ssafy.smartstore"
        val resId = context.resources.getIdentifier(resName,"drawable",packageName)
        holder.ivProductDetail.setImageResource(resId)


        holder.tvOrderMenuDetail.text = orderDetailList[position].name
        holder.tvPriceDetail.text = "${orderDetailList[position].price} 원"
        holder.tvOrderQuantityDetail.text = "${orderDetailList[position].quantity} 잔"
        holder.tvOrderTotalPriceDetail.text = "${orderDetailList[position].totalPrice} 원"

    }

    override fun getItemCount(): Int {
        return orderDetailList.size
    }

}