package models.admin.product.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import models.seller.product.ProductRequestDao;
import models.seller.product.ProductRequestDto;
import models.seller.product.Progress;

@Service
public class AdminProductService {
	
	@Autowired
	private ProductRequestDao productDao;
	
	public void agree(HttpServletRequest request) {
		/** 유효성 검사 S */
		String[] productIds = request.getParameterValues("product");
		if (productIds == null) {
			throw new RuntimeException("상품을 선택 하세요.");
		}

		/** 유효성 검사 E */
		
		/** 수정 시작 S */
		for(String product : productIds) {
			ProductRequestDto dto = new ProductRequestDto();
			dto.setNum(Long.parseLong(product));
			dto.setProgress(Progress.Agree);
			productDao.argee(dto);
			
		}
		/** 수정 시작 E */
	}
	
	public void rejected(HttpServletRequest request) {
		/** 유효성 검사 S */
		String[] productIds = request.getParameterValues("product");
		if (productIds == null) {
			throw new RuntimeException("상품을 선택 하세요.");
		}

		/** 유효성 검사 E */
		
		/** 수정 시작 S */
		for(String product : productIds) {
			ProductRequestDto dto = new ProductRequestDto();
			dto.setNum(Long.parseLong(product));
			dto.setProgress(Progress.Rejected);
			productDao.argee(dto);
			
		}
		/** 수정 시작 E */
	}
	
}
