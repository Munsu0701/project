# BookShoppingMall

게시판 구현(파일 업로드 )

<details><summary style="color:skyblue">메인(리스트 및 검색)</summary>

~~~
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
~~~

- 기능 구현
	
~~~
@Service
public class BoardListService {
	
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private AdminBoardDao adminBoardDao;
	@Autowired
	private HttpServletRequest request;
	
	public List<BoardDataDto> gets(String boardId, Model model, int page) {
		
		AdminBoardDto boardConfig = adminBoardDao.searchToId(boardId);
		
		String select = request.getParameter("select");
		String search = request.getParameter("search");
		
		
		List<BoardDataDto> boards = boardDataDao.gets(boardId, page, boardConfig.getPageCount(), select, search);
		
		Long _total = boardDataDao.total(boardId);
		int total = _total.intValue();
		
		Pagination pagination = new Pagination(page, total, 5, boardConfig.getPageCount());
		if(select == null || search == null) {
			select = "";
			search = "";
		}
		model.addAttribute("select", select);
		model.addAttribute("search", search);
		model.addAttribute("pagination", pagination);
		model.addAttribute("boardId", boardId);
		model.addAttribute("boardName", boardConfig.getBoardName());
		
		
		return boards;
	}

}
~~~
	
</details>

<details><summary style="color:skyblue">비밀글일 경우</summary>
	
~~~
package controllers.board;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import common.error.CommonException;
import models.board.BoardDataDto;
import models.board.BoardPrivateRequest;
import models.board.service.BoardPrivateService;

@Controller
@RequestMapping("/board")
public class BoardPrivateController {
	
	@Autowired
	private BoardPrivateService boardPrivateService;

	@GetMapping("/private/{id}")
	public String form(@PathVariable(name = "id") Long id, Model model) {
		
		BoardPrivateRequest boardPrivateRequest = new BoardPrivateRequest();
		boardPrivateRequest.setId(id);
		model.addAttribute("boardPrivateRequest", boardPrivateRequest);
		
		return "board/private";
	}
	
	@PostMapping("/private")
	public String process(@Valid BoardPrivateRequest boardPrivateRequest, Errors errors) {
		
		if(errors.hasErrors()) {
			return "board/private";
		}
		BoardDataDto board = null;
		try {
			board = boardPrivateService.sucess(boardPrivateRequest);
		} catch(CommonException e) {
			errors.rejectValue("password", e.getMessage());
			return "board/private";
		}
		
		return "redirect:/board/view/" + board.getId();
	}
}
~~~

	
	
- 기능 구현(접속권한부여)
	
~~~
package models.board.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.error.CommonException;
import models.board.BoardDataDao;
import models.board.BoardDataDto;
import models.board.BoardPrivateRequest;

@Service
public class BoardPrivateService {
	
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private HttpServletResponse response;

	public BoardDataDto sucess(BoardPrivateRequest boardPrivateRequest) {
		
		BoardDataDto board = boardDataDao.get(boardPrivateRequest.getId());
		
		if(!BCrypt.checkpw(boardPrivateRequest.getPassword(), board.getPrivatePassword())) {
			throw new CommonException("비밀번호가 일치하지 않습니다.");
		}
		
		Cookie cookie = new Cookie("board" + board.getId(), "true");
		cookie.setMaxAge(60 * 60 * 24 * 7);
		response.addCookie(cookie);
		
		return board;
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">게시글 개별 조회</summary>

~~~
package controllers.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import models.board.BoardDataDto;
import models.board.service.BoardViewService;

@Controller
@RequestMapping("/board")
public class BoardViewController {
	
	@Autowired
	private BoardViewService boardViewService;
	
	@GetMapping("/view/{id}")
	public String form(@PathVariable(name = "id") Long id, Model model) {
		
		BoardDataDto boardDataDto = boardViewService.view(id, model);
		model.addAttribute("boardDataDto", boardDataDto);
		model.addAttribute("addCss", new String[] { "/board/view" });
		model.addAttribute("addJs", new String[] { "/board/filelist", "/board/comment" });
		
		return "board/view";
	}

}
~~~


</details>
	
<details><summary style="color:skyblue">게시글 작성</summary>
	
~~~
package controllers.board;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.board.BoardDataDto;
import models.board.BoardDataRequest;
import models.board.service.BoardWriteService;
import models.user.UserDto;

@Controller
@RequestMapping("/board")
public class BoardWriteController {
	
	@Autowired
	private BoardWriteService service;
	
	@GetMapping("/write/{boardId}")
	public String form(@PathVariable(name = "boardId") String boardId, Model model, HttpSession session) {
		model.addAttribute("addCss", new String[] { "/board/write" });
		model.addAttribute("addJs", new String[] { "/ckeditor/ckeditor", "/board/board", "/file/fileupload" });
		
		BoardDataRequest boardDataRequest = new BoardDataRequest();
		UserDto user = (UserDto)session.getAttribute("user");
		if(user != null) {
			boardDataRequest.setPoster(user.getFakeName());
		}
		boardDataRequest.setBoardId(boardId);
		model.addAttribute("boardDataRequest", boardDataRequest);
		model.addAttribute("mode", "insert");
		
		return "board/write";
	}
	
	@PostMapping("/write")
	public String process(@Valid BoardDataRequest request, Errors errors, Model model, HttpSession session) {
		model.addAttribute("addCss", new String[] { "/board/write" });
		model.addAttribute("addJs", new String[] { "/ckeditor/ckeditor", "/board/board", "/file/fileupload" });
	
		BoardDataDto board = service.register(request, errors, session);
		
		if(errors.hasErrors()) {
			return "board/write";
		}
		
		
		return "redirect:/board/view/" + board.getId();
	}

}
~~~
	
- 기능 구현
	
~~~
package models.board.service;

import javax.servlet.http.HttpSession;

import org.mindrot.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import common.error.CommonException;
import models.admin.board.AdminBoardDao;
import models.admin.board.AdminBoardDto;
import models.board.BoardDataDao;
import models.board.BoardDataDto;
import models.board.BoardDataRequest;
import models.file.FileInfoDao;
import models.user.UserDto;

@Service
public class BoardWriteService {
	
	@Autowired
	private AdminBoardDao adminBoardDao;
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private FileInfoDao fileInfoDao;
	

	public BoardDataDto register(BoardDataRequest request, Errors errors, HttpSession session) {
		
		if(errors.hasErrors()) {
			return null;
		}
		
		UserDto user = (UserDto)session.getAttribute("user");
		BoardDataDto param = new BoardDataDto();
		if(request.getBoardId() != null) {
			AdminBoardDto adminBoard = adminBoardDao.searchToId(request.getBoardId());
			param.setBoard(adminBoard);
		}
		param.setPrivate(request.isPrivate());
		if(request.isPrivate()) {
			String pass = request.getPrivatePassword();
			if(pass == null || pass.isBlank()) {
				throw new CommonException("비밀글 전용 비밀번호를 입력해주세요.", "privatePassword");
			}
			pass = BCrypt.hashpw(pass, BCrypt.gensalt(12));
			param.setPrivatePassword(pass);
		}
		param.setContents(request.getContents());
		param.setGid(request.getGid());
		param.setPoster(request.getPoster());
		param.setSubject(request.getSubject());
		param.setUser(user);
		
		if(request.getId() != null) {
			param.setId(request.getId());
		}
		
		BoardDataDto board = boardDataDao.register(param);
		fileInfoDao.doneFinish(request.getGid());
		
		
		return board;
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">게시글 수정</summary>

~~~
package controllers.board;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.board.BoardDataDao;
import models.board.BoardDataDto;
import models.board.BoardDataRequest;
import models.board.service.BoardWriteService;
import models.user.UserDto;

@Controller
@RequestMapping("/board")
public class BoardUpdateController {
	
	@Autowired
	private BoardWriteService service;
	@Autowired
	private BoardDataDao boardDataDao;

	@GetMapping("/update/{id}")
	public String form(@PathVariable(name = "id") Long id, Model model, HttpSession session) {
		
		model.addAttribute("addCss", new String[] { "/board/write" });
		model.addAttribute("addJs", new String[] { "/ckeditor/ckeditor", "/board/board", "/file/fileupload" });
		
		BoardDataRequest boardDataRequest = BoardDataDto.toRequest(boardDataDao.get(id));
		
		model.addAttribute("boardDataRequest", boardDataRequest);
		model.addAttribute("mode", "update");
		
		return "board/write";
	}
	
	@PostMapping("/update")
	public String process(@Valid BoardDataRequest request, Errors errors, Model model, HttpSession session) {
		
		model.addAttribute("addCss", new String[] { "/board/write" });
		model.addAttribute("addJs", new String[] { "/ckeditor/ckeditor", "/board/board", "/file/fileupload" });
	
		BoardDataDto board = service.register(request, errors, session);
		
		if(errors.hasErrors()) {
			return "board/update";
		}
		return "redirect:/board/view/" + board.getId();
	}
	
}
~~~

- 기능 구현
	
~~~
@Service
public class BoardWriteService {
	
	@Autowired
	private AdminBoardDao adminBoardDao;
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private FileInfoDao fileInfoDao;
	

	public BoardDataDto register(BoardDataRequest request, Errors errors, HttpSession session) {
		
		if(errors.hasErrors()) {
			return null;
		}
		
		UserDto user = (UserDto)session.getAttribute("user");
		BoardDataDto param = new BoardDataDto();
		if(request.getBoardId() != null) {
			AdminBoardDto adminBoard = adminBoardDao.searchToId(request.getBoardId());
			param.setBoard(adminBoard);
		}
		param.setPrivate(request.isPrivate());
		if(request.isPrivate()) {
			String pass = request.getPrivatePassword();
			if(pass == null || pass.isBlank()) {
				throw new CommonException("비밀글 전용 비밀번호를 입력해주세요.", "privatePassword");
			}
			pass = BCrypt.hashpw(pass, BCrypt.gensalt(12));
			param.setPrivatePassword(pass);
		}
		param.setContents(request.getContents());
		param.setGid(request.getGid());
		param.setPoster(request.getPoster());
		param.setSubject(request.getSubject());
		param.setUser(user);
		
		if(request.getId() != null) {
			param.setId(request.getId());
		}
		
		BoardDataDto board = boardDataDao.register(param);
		fileInfoDao.doneFinish(request.getGid());
		
		
		return board;
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">게시판 삭제</summary>

~~~
package controllers.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import models.board.BoardDataDao;
import models.board.BoardDataDto;
import models.file.FileInfoDao;

@Controller
public class BoardDeleteController {
	
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private FileInfoDao fileInfoDao;

	@RequestMapping("/board/delete/{id}")
	public String form(@PathVariable(name = "id") Long id) {
		
		BoardDataDto board = boardDataDao.delete(id);
		fileInfoDao.deletes(board.getGid());
		
		return "redirect:/";
	}
	
}
~~~


</details>
	
<details><summary style="color:skyblue">관련 예시 이미지</summary>
	
- 게시판 메인 화면
	
<img src="https://user-images.githubusercontent.com/105355765/210729973-37356d1a-4bdb-401d-a9dc-11b8b409bc85.png">

- 게시글 보기
	
<img src="https://user-images.githubusercontent.com/105355765/210730299-bd93ebb8-8af0-4ac6-99e1-24d9b3c0f897.png">
	
	
</details>
