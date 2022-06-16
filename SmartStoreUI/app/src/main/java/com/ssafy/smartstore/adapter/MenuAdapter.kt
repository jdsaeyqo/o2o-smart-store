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
import com.ssafy.smartstore.dto.ProductDTO

class MenuAdapter(val context: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    var menuList = mutableListOf<ProductDTO>()

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProduct: ImageView = itemView.findViewById(R.id.item_iv_menu_product)
        val tvProductName : TextView = itemView.findViewById(R.id.tv_product_name)

        fun setClickListener(product: ProductDTO){
            itemView.setOnClickListener {
                itemClickListener.onClick(it,product,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_menu_list_item, parent, false)

        return MenuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        val resName = menuList[position].img.substring(0,menuList[position].img.length-4)

        Log.d("onBindViewHolder", "onBindViewHolder: $resName")
        val packageName = "com.ssafy.smartstore"
        val resId = context.resources.getIdentifier(resName,"drawable",packageName)
        holder.ivProduct.setImageResource(resId)
        holder.tvProductName.text = menuList[position].name

        holder.setClickListener(menuList[position])

    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    interface ItemClickListener{
        fun onClick(view : View, product : ProductDTO, position: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }

}