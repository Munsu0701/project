# BookShoppingMall


Q&A 고객센터 문의글 구현

- 메인 고객센터(리스트)
~~~
package controllers.qna;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import models.qna.QnADto;
import models.qna.service.QnAListService;

@Controller
@RequestMapping("/QnA")
public class QnAMainController {
	
	@Autowired
	private QnAListService listService;

	@GetMapping
	public String form(@RequestParam(required = false, defaultValue = "1") int page, Model model) {
		
		model.addAttribute("addCss", new String[] { "/qna/style" });
		
		List<QnADto> qnas = listService.list(page, model);
		model.addAttribute("qnas", qnas);
		
		return "qna/index";
	}
	
}
~~~
- 기능 구현
~~~
package models.qna.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import common.page.Pagination;
import models.qna.QnADao;
import models.qna.QnADto;

@Service
public class QnAListService {
	
	@Autowired
	private QnADao dao;
	
	public List<QnADto> list(int page, Model model) {
		
		Long count = dao.count(page, 20);
		
		List<QnADto> qnas = dao.gets(page, 20);
		
		int total = count.intValue();
		
		Pagination pagination = new Pagination(page, total, 5, 20);
		
		model.addAttribute("pagination", pagination);
		
		return qnas;
	}

}
~~~

- 문의글 작성
~~~
package controllers.qna;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.qna.QnADto;
import models.qna.QnARequest;
import models.qna.service.QnAWriteService;
import models.user.UserDto;
import models.user.UserType;

@Controller
@RequestMapping("/QnA")
public class QnAWriteController {
	
	@Autowired
	private QnAWriteService service;

	@GetMapping("/write")
	public String form(Model model, HttpSession session) {
		QnARequest qnARequest = new QnARequest();
		UserDto user = (UserDto)session.getAttribute("user");
		qnARequest.setPoster(user.getMemId());
		if(user.getUserType() == UserType.ADMIN) {
			qnARequest.setAdmin(true);
		} else {
			qnARequest.setAdmin(false);
		}
		
		model.addAttribute("qnARequest", qnARequest);
		model.addAttribute("addCss", new String[] { "/qna/write" });
		
		return "qna/write";
	}
	
	@PostMapping("/write")
	public String process(@Valid QnARequest request, Errors errors, Model model) {
		model.addAttribute("addCss", new String[] { "/qna/write" });
		
		QnADto qna = service.register(request, errors);
		
		if(errors.hasErrors()) {
			return "qna/write";
		}
		
		return "redirect:/QnA/view/" + qna.getId();
	}
}
~~~
- 기능
~~~
@Service
public class QnAWriteService {
	
	@Autowired
	private QnADao dao;
	@Autowired
	private HttpSession session;
	
	public QnADto register(QnARequest request, Errors errors) {
		if(errors.hasErrors()) {
			return null;
		}
		
		QnADto param = new QnADto();
		param.setAdmin(request.isAdmin());
		param.setAnswered(false);
		param.setPoster(request.getPoster());
		param.setQuestion(request.getQuestion());
		param.setSubject(request.getSubject());
		
		UserDto user = (UserDto)session.getAttribute("user");
		param.setUser(user);
		if(user.getUserType() == UserType.ADMIN) {
			param.setAdmin(true);
		} else {
			param.setAdmin(false);
		}
		
		QnADto dto = dao.register(param);
		
		
		return dto;
	}

}
~~~

- 문의글 답변 작성
~~~
package controllers.qna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import models.qna.QnADto;
import models.qna.service.QnACommentService;

@Controller
@RequestMapping("/QnA")
public class QnACommentController {
	
	@Autowired
	private QnACommentService service;

	@PostMapping("/comment/{id}")
	public String process(@PathVariable(name = "id") Long id, QnADto dto) {
		
		service.comment(id, dto);
		
		return "redirect:/QnA/view/" + id;
	}
}
~~~
- 기능 구현
~~~
package models.qna.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import models.qna.QnADao;
import models.qna.QnADto;

@Service
public class QnACommentService {
	
	@Autowired
	private QnADao dao;

	public void comment(Long id, QnADto param) {
		
		QnADto dto = dao.get(id);
		dto.setAnswer(param.getAnswer());
		dto.setAdminName(param.getAdminName());
		
		dao.adminAnswer(dto);
		
	}
	
}
~~~

- 문의글 보기 구현
~~~
package controllers.qna;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import models.qna.QnADao;
import models.qna.QnADto;

@Controller
@RequestMapping("/QnA")
public class QnAViewController {
	
	@Autowired
	private QnADao dao;
	
	@GetMapping("/view/{id}")
	public String form(@PathVariable(name = "id") Long id, Model model) {
		
		QnADto dto = dao.get(id);
		
		model.addAttribute("qnADto", dto);
		model.addAttribute("addCss", new String[] { "/qna/view" });
		
		return "qna/view";
	}

}
~~~
