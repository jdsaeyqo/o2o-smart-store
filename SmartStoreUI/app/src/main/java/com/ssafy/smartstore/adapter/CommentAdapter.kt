package com.ssafy.smartstore.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.R
import com.ssafy.smartstore.databinding.DialogCommentBinding
import com.ssafy.smartstore.dto.Comment
import com.ssafy.smartstore.service.CommentService
import com.ssafy.smartstore.viewmodel.CommentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentAdapter(private val commentViewModel: CommentViewModel) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    lateinit var ctx: Context
    var commentList = mutableListOf<Comment>()

    var userId = ""

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvComment: TextView = itemView.findViewById(R.id.item_tv_comment)
        val btnModify: ImageButton = itemView.findViewById(R.id.btn_modify)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btn_remove)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_comment_item, parent, false)
        return CommentViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        holder.tvComment.text = commentList[position].comment

        if (commentList[position].userId == userId) {
            holder.btnModify.visibility = View.VISIBLE
            holder.btnRemove.visibility = View.VISIBLE
        } else {
            holder.btnModify.visibility = View.GONE
            holder.btnRemove.visibility = View.GONE
        }

        holder.btnRemove.setOnClickListener {
            commentViewModel.deleteComment(commentList[position].id)
            commentList.remove(commentList[position])
            notifyDataSetChanged()

        }

        holder.btnModify.setOnClickListener {

            var modifyComment = commentList[position]
            val layout = View.inflate(ctx, R.layout.dialog_comment, null)

            val dialog = AlertDialog.Builder(ctx)
                .setView(layout)
                .setPositiveButton("수정") { dialog, id ->
                    val comment = layout.findViewById<EditText>(R.id.dialog_et_comment)
                    modifyComment.comment = comment.text.toString()

                    commentViewModel.updateComment(modifyComment)
                    commentList[position] = modifyComment
                    notifyDataSetChanged()

                    dialog.dismiss()
                }
                .setNegativeButton("취소") { dialog, id ->
                    dialog.dismiss()
                }

            dialog.show()

        }

    }

    override fun getItemCount(): Int {
        return commentList.size
    }

}