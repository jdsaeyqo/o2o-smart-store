package com.ssafy.smartstore.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.MyOrder

class RecentOrderAdapter(val context : Context) : RecyclerView.Adapter<RecentOrderAdapter.RecentOrderViewHolder>(){

    var recentOrderList = mutableListOf<MyOrder>()

    inner class RecentOrderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivProduct : ImageView = itemView.findViewById(R.id.item_iv_product)
        val tvOrderMenu : TextView = itemView.findViewById(R.id.item_tv_order_menu)
        val tvTotalPrice : TextView = itemView.findViewById(R.id.item_tv_total_price)
        val tvOrderDate : TextView  = itemView.findViewById(R.id.item_tv_order_date)
        val ivShoppingList : ImageView = itemView.findViewById(R.id.item_btn_shoppingList)

        fun setClickListener(myOrder: MyOrder){
            itemView.setOnClickListener {
                itemClickListener.onClick(it,myOrder,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_recent_order_item,parent,false)
        return RecentOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecentOrderViewHolder, position: Int) {
        val resName = recentOrderList[position].img.substring(0,recentOrderList[position].img.length-4)

        val packageName = "com.ssafy.smartstore"
        val resId = context.resources.getIdentifier(resName,"drawable",packageName)
        holder.ivProduct.setImageResource(resId)

        val quantity = recentOrderList[position].totalQuantity-1
        if(quantity == 0){
            holder.tvOrderMenu.text = "${recentOrderList[position].Name}"
        }else{
            holder.tvOrderMenu.text = "${recentOrderList[position].Name} 외 ${quantity}잔"
        }

        holder.tvTotalPrice.text = recentOrderList[position].totalPrice.toString()+"원"
        holder.tvOrderDate.text = recentOrderList[position].date.split("T")[0]

        holder.ivShoppingList.setImageResource(R.drawable.ic_baseline_shopping_cart_24)

        holder.setClickListener(recentOrderList[position])
    }

    override fun getItemCount(): Int {
        return recentOrderList.size
    }

    interface ItemClickListener{
        fun onClick(view : View, myOrder: MyOrder, position: Int)
    }
    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}