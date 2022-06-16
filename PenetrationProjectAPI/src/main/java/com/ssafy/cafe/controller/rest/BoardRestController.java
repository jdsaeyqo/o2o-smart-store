package com.ssafy.cafe.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.cafe.model.dto.Board;
import com.ssafy.cafe.model.service.BoardService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/board")
@CrossOrigin(allowCredentials = "true", originPatterns = { "*" })
public class BoardRestController {
	
	@Autowired
	BoardService bService;
	
	@PostMapping
	@Transactional
	@ApiOperation(value = "board 객체를 추가한다.", response = Boolean.class)
	public Boolean insert(@RequestBody Board board) {
		bService.insertBoard(board);;
		return true;
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@ApiOperation(value = "{id}에 해당하는 Board 정보를 삭제한다.", response = Boolean.class)
	public Boolean delete(@PathVariable Integer id) {
		bService.deleteBoard(id);
		return true;
	}
	
	@GetMapping("/all")
	@ApiOperation(value = "Board 전체 가져오기", response = List.class)
	public List<Board> selectAll() {
		return bService.selectBoards();
		
	}

}
