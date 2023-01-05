# BookShoppingMall


댓글 및 대댓글 작성

<details><summary style="color:skyblue">기본 작성</summary>

~~~
package controllers.comment;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.board.BoardDataDto;
import models.comment.service.CommentWriteService;

@Controller
@RequestMapping("/board/comment")
public class CommentWriteController {
	
	@Autowired
	private CommentWriteService commentWriteService;

	@PostMapping("/write")
	public String process(Model model, HttpServletRequest request) {
		
		BoardDataDto board = commentWriteService.regist(request);
		
		
		return "redirect:/board/view/" + board.getId();
	}
	
}
~~~

- 기능 부분

~~~
@Service
public class CommentWriteService {
	
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private HttpSession session;

	public BoardDataDto regist(HttpServletRequest request) {
		
		String gid = request.getParameter("gid");
		String comments = request.getParameter("comments");
		UserDto user = (UserDto)session.getAttribute("user");
		BoardDataDto board = boardDataDao.searchGid(gid);
		
		CommentDto param = new CommentDto();
		param.setBoard(board);
		param.setUser(user);
		param.setPoster(user.getFakeName());
		param.setComments(comments);
		param.setFirstComment(true);
		param.setGid(gid);
		
		commentDao.register(param);
		
		return board;
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">댓글 수정</summary>

~~~
package controllers.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.comment.CommentDto;
import models.comment.service.CommentUpdateService;

@Controller
@RequestMapping("/board/comment")
public class CommentUpdateController {
	
	@Autowired
	private CommentUpdateService service;

	@PostMapping("/update/{id}")
	public String process(@PathVariable(name = "id") Long id) {
		
		CommentDto comment = service.update(id);
		return "redirect:/board/view/" + comment.getBoard().getId();
	}
	
}
~~~

- 기능

~~~
package models.comment.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import models.comment.CommentDao;
import models.comment.CommentDto;

@Service
public class CommentUpdateService {

	@Autowired
	private CommentDao commentDao;
	@Autowired
	private HttpServletRequest request;
	
	public CommentDto update(Long id) {
		
		CommentDto param = new CommentDto();
		param.setId(id);
		param.setComments(request.getParameter("comments"));
		
		CommentDto comment = commentDao.update(param);
		
		
		return comment;
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">댓글 삭제</summary>

~~~
package controllers.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import models.comment.CommentDao;
import models.comment.CommentDto;

@Controller
public class CommentDeleteController {
	
	@Autowired
	private CommentDao commentDao;

	@RequestMapping("/board/comment/delete/{id}")
	public String process(@PathVariable(name = "id") Long id) {
		
		CommentDto comment = commentDao.delete(id);
		
		return "redirect:/board/view/" + comment.getBoard().getId();
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">대댓글</summary>

~~~
package controllers.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.board.BoardDataDto;
import models.comment.service.ReplyWriteService;

@Controller
@RequestMapping("/board/reply")
public class ReplyWriteController {
	
	@Autowired
	private ReplyWriteService replyWriteService;

	@PostMapping("/write/{id}")
	public String process(@PathVariable(name = "id") Long id) {
		
		BoardDataDto board = replyWriteService.regist(id);
		
		
		return "redirect:/board/view/" + board.getId();
	}
	
}
~~~

- 기능

~~~
@Service
public class ReplyWriteService {

	@Autowired
	private CommentDao commentDao;
	@Autowired
	private BoardDataDao boardDataDao;
	@Autowired
	private HttpSession session;
	@Autowired
	private HttpServletRequest request;

	public BoardDataDto regist(Long id) {
		
		CommentDto com = commentDao.get(id);
		String comments = request.getParameter("comments");
		
		UserDto user = (UserDto)session.getAttribute("user");
		BoardDataDto board = boardDataDao.searchGid(com.getGid());
		
		CommentDto param = new CommentDto();
		param.setBoard(board);
		param.setUser(user);
		param.setPoster(user.getFakeName());
		param.setMatchComment(id);
		param.setComments(comments);
		param.setFirstComment(false);
		param.setGid(com.getGid());
		
		commentDao.replyInsert(param);
		
		return board;
	}
	
}
~~~

</details>

<details><summary style="color:skyblue">관련 예시 이미지</summary>

- 댓글 작성 및 대댓글 작성
<img src="https://user-images.githubusercontent.com/105355765/210731295-26d8e7b1-7dd2-450d-8389-8e574ca64dcb.png">
	
</details>
