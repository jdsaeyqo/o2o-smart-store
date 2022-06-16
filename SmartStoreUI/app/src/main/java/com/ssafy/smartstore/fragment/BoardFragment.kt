package com.ssafy.smartstore.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.smartstore.IntentApplication
import com.ssafy.smartstore.adapter.BoardAdapter
import com.ssafy.smartstore.databinding.DialogBoardBinding
import com.ssafy.smartstore.databinding.FragmentBoardBinding
import com.ssafy.smartstore.dto.Board
import com.ssafy.smartstore.dto.BoardList
import com.ssafy.smartstore.service.BoardService
import com.ssafy.smartstore.service.UserService
import com.ssafy.smartstore.util.RecyclerDecoration
import com.ssafy.smartstore.viewmodel.BoardViewModel
import com.ssafy.smartstore.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "BoardFragment"

class BoardFragment : Fragment() {

    private lateinit var binding: FragmentBoardBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var ctx: Context
    private lateinit var boards: MutableList<Board>
    private lateinit var boardList: MutableList<BoardList>
    private lateinit var boardListAdapter: BoardAdapter

    private val boardViewModel: BoardViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = ctx.getSharedPreferences("user_info", AppCompatActivity.MODE_PRIVATE)
        userId = prefs.getString("ID", "")!!

        boards = mutableListOf()
        boardList = mutableListOf()

        selectAllBoards()

        boardViewModel.boardList.observe(viewLifecycleOwner) {
            if (boardViewModel.boardList.value != null) {
                boards = it
                boards.forEach { board ->
                    userViewModel.getInfo(board.userId)
                }
                initBoardAdapter()
            }
        }

        userViewModel.userInfo.observe(viewLifecycleOwner) {
            if (userViewModel.userInfo.value != null) {
                val info = it
                val grade = info["grade"] as Map<String, Any>
                val img = grade["img"] as String
                val user = info["user"] as Map<String,Any>
                val userId = user["id"] as String
                boardList.clear()
                boards.forEach {  board ->
                    if(board.userId == userId){
                        boardList.add(BoardList(board.id, board.userId, img, board.content))
                    }
                }
                initBoardAdapter()

            }
        }

        boardViewModel.insertResult.observe(viewLifecycleOwner) {
            if (boardViewModel.insertResult.value != null) {
                if (it == true) {
                    selectAllBoards()
                    Toast.makeText(ctx, "등록이 완료되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(ctx, "등록에 실패하였습니다", Toast.LENGTH_SHORT).show()

                }
            }
        }

        boardViewModel.deleteResult.observe(viewLifecycleOwner){
            if(boardViewModel.deleteResult.value != null){
                if (it == true) {
                    selectAllBoards()
                    Toast.makeText(ctx, "삭제가 완료되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(ctx, "삭제에 실패하였습니다", Toast.LENGTH_SHORT).show()

                }
            }
        }

        binding.btnRegist.setOnClickListener {

            val binding1 = DialogBoardBinding.inflate(layoutInflater)

            val dialog = AlertDialog.Builder(requireActivity())
                .setView(binding1.root)
                .setPositiveButton("등록") { dialog, id ->

                    if (binding1.etBoard.text.toString() == "") {
                        Toast.makeText(ctx, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val board = Board(0, userId, (binding1.etBoard.text.toString()))
                    boardViewModel.insertBoard(board)
                    dialog.dismiss()

                }
                .setNegativeButton("취소") { dialog, id ->
                    dialog.dismiss()

                }
            dialog.show()

        }

    }

    private fun selectAllBoards() {
        boardList.clear()
        boardViewModel.selectAllBoards()
    }

    private fun initBoardAdapter() {

        binding.rvBoard.layoutManager =
            LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)


        boardListAdapter = BoardAdapter(ctx)
        boardListAdapter.myId = userId
        boardListAdapter.boardList = boardList

        boardListAdapter.onRemoveButtonClick = object : BoardAdapter.OnRemoveButtonClick {
            override fun onRemoveClick(view: View, position: Int) {

                val id = boardList[position].id
                boardViewModel.deleteBoard(id)


            }
        }
        binding.rvBoard.adapter = boardListAdapter
//        binding.rvBoard.addItemDecoration(RecyclerDecoration())

    }



}