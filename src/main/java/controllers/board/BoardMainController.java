package controllers.board;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import models.board.BoardDataDto;
import models.board.service.BoardListService;

@Controller
public class BoardMainController {
	
	@Autowired
	private BoardListService boardListService;

	@RequestMapping("/board/{boardId}")
	public String form(@PathVariable(name = "boardId") String boardId, @RequestParam(required = false, defaultValue = "1") int page, Model model) {
		
		List<BoardDataDto> boards = boardListService.gets(boardId, model, page);
		model.addAttribute("boards", boards);
		model.addAttribute("addCss", new String[] {"/board/index"});
		
		return "board/index";
	}
	
}
