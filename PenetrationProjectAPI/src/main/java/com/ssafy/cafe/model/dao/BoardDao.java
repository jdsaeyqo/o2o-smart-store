package com.ssafy.cafe.model.dao;

import java.util.List;

import com.ssafy.cafe.model.dto.Board;


public interface BoardDao {

	
	List<Board> selectAll();
	
	int insert(Board board);
	
	int delete(Integer orderId);
}
