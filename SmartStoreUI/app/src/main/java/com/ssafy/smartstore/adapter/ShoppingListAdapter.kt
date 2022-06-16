package com.ssafy.smartstore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.ShoppingList

class ShoppingListAdapter(val context: Context) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>(){

    var shoppingList = mutableListOf<ShoppingList>()

    inner class ShoppingListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val ivProduct : ImageView = itemView.findViewById(R.id.item_iv_shop_product)
        val tvName : TextView = itemView.findViewById(R.id.item_tv_shop_name)
        val tvPrice : TextView = itemView.findViewById(R.id.item_tv_shop_price)
        val tvQuantity : TextView = itemView.findViewById(R.id.item_tv_quantity)
        val tvTotalPrice : TextView = itemView.findViewById(R.id.item_tv_shop_total_price)
        val btnRemove : ImageButton = itemView.findViewById(R.id.btn_shop_remove)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_shopping_list_item,parent,false)
        return ShoppingListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {

        val resName = shoppingList[position].img.substring(0,shoppingList[position].img.length-4)

        val packageName = "com.ssafy.smartstore"
        val resId = context.resources.getIdentifier(resName,"drawable",packageName)
        holder.ivProduct.setImageResource(resId)

        holder.tvName.text = shoppingList[position].name
        holder.tvPrice.text = shoppingList[position].price.toString()
        holder.tvQuantity.text = shoppingList[position].quantity.toString()
        holder.tvTotalPrice.text = shoppingList[position].totalPrice.toString()
        holder.btnRemove.setImageResource(R.drawable.minus)

        holder.btnRemove.setOnClickListener {
            onRemoveButtonClick.onRemoveClick(holder.itemView,position)
        }
    }

    override fun getItemCount(): Int {

        return shoppingList.size
    }

    lateinit var onRemoveButtonClick: OnRemoveButtonClick

    interface OnRemoveButtonClick{
        fun onRemoveClick(view: View, position: Int)
    }

}