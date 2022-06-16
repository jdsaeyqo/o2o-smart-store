package com.ssafy.smartstore.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.MyOrder

class MyOrderAdapter(val context : Context) : RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder>() {

    var myOrderList = mutableListOf<MyOrder>()

    inner class MyOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.item_iv_my_product)
        val tvOrderMenu: TextView = itemView.findViewById(R.id.item_tv_my_order_menu)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.item_tv_my_total_price)
        val tvOrderDate: TextView = itemView.findViewById(R.id.item_tv_my_order_date)
        val tvStatus: TextView = itemView.findViewById(R.id.item_tv_status)

        fun setClickListener(myOrder: MyOrder){
            itemView.setOnClickListener {
                itemClickListener.onClick(it,myOrder,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_my_order_item, parent, false)

        return MyOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {

        val resName = myOrderList[position].img.substring(0,myOrderList[position].img.length-4)

        val packageName = "com.ssafy.smartstore"
        val resId = context.resources.getIdentifier(resName,"drawable",packageName)
        holder.ivProduct.setImageResource(resId)

        val quantity = myOrderList[position].totalQuantity-1
        if(quantity == 0){
            holder.tvOrderMenu.text = "${myOrderList[position].Name}"
        }else{
            holder.tvOrderMenu.text = "${myOrderList[position].Name} 외 ${quantity}잔"
        }

        holder.tvTotalPrice.text = myOrderList[position].totalPrice.toString()+"원"

        holder.tvOrderDate.text = myOrderList[position].date.split("T")[0]
        holder.tvStatus.text = "픽업 완료"

        holder.setClickListener(myOrderList[position])
    }

    override fun getItemCount(): Int {

        return myOrderList.size
    }

    interface ItemClickListener{
        fun onClick(view : View, myOrder: MyOrder, position: Int)
    }
    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}