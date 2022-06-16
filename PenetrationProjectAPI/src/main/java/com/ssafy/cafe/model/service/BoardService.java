package com.ssafy.cafe.model.service;

import java.util.List;

import com.ssafy.cafe.model.dto.Board;

public interface BoardService {
	
	void insertBoard(Board board);
	
	void deleteBoard(Integer id);
	
	List<Board> selectBoards();
	
	
	

}
