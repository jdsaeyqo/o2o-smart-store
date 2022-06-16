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
import com.ssafy.smartstore.dto.BoardList

class BoardAdapter(val context : Context) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    var boardList = mutableListOf<BoardList>()
    var myId = ""

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivGrade: ImageView = itemView.findViewById(R.id.iv_board_grade)
        val tvUserId: TextView = itemView.findViewById(R.id.tv_board_user_id)
        val tvContent : TextView = itemView.findViewById(R.id.tv_board_content)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btn_board_remove)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_board_item, parent, false)
        return BoardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val resName = boardList[position].img.substring(0,boardList[position].img.length-4)
        val packageName = "com.ssafy.smartstore"
        val resId = context.resources.getIdentifier(resName,"drawable",packageName)
        holder.ivGrade.setImageResource(resId)

        holder.tvUserId.text = boardList[position].userId
        holder.tvContent.text = boardList[position].content

        if(myId == boardList[position].userId){
            holder.btnRemove.visibility = View.VISIBLE
        }

        holder.btnRemove.setOnClickListener {
            onRemoveButtonClick.onRemoveClick(holder.itemView,position)
        }


    }

    override fun getItemCount(): Int = boardList.size


    lateinit var onRemoveButtonClick: OnRemoveButtonClick

    interface OnRemoveButtonClick{
        fun onRemoveClick(view: View, position: Int)
    }

}