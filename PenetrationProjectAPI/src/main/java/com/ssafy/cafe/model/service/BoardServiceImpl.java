package com.ssafy.cafe.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssafy.cafe.model.dao.BoardDao;
import com.ssafy.cafe.model.dto.Board;
@Service
public class BoardServiceImpl implements BoardService {

	
	@Autowired
	BoardDao bDao;
	
	@Override
	public void insertBoard(Board board) {
		bDao.insert(board);
		
	}

	@Override
	public void deleteBoard(Integer id) {
		bDao.delete(id);
		
	}

	@Override
	public List<Board> selectBoards() {
		
		return bDao.selectAll();
	}

}
