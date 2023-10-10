package com.recipe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.midi.SysexMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.recipe.constant.ItemSellStatus;
import com.recipe.dto.CartDto;
import com.recipe.entity.Cart;
import com.recipe.entity.Item;
import com.recipe.entity.ItemImg;
import com.recipe.service.OrderService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

//	장바구니 페이지
	@GetMapping(value = "/cart")
	public String Cart(Model model, HttpSession session) {
		
	
	List<CartDto> cartList = orderService.getCartList(session);
	
	
	int totalCount = 0; // 오리지널 총 금액
	int saleCount = 0; //할인받는 총 금액
	int deleveryCount = 4000;
	
	for(CartDto countPlus : cartList) {
		if(ItemSellStatus.SELL.equals(countPlus.getItemSellStatus())) {
			totalCount += countPlus.getPrice() * countPlus.getCount() ;
			saleCount  +=  (countPlus.getPrice() * countPlus.getSale() / 100) * countPlus.getCount();
		}
	}
	
//	주문 총금액(할인포함) 40000원 이상이면 배송비 4000원 추가 아니면 0 
	if((totalCount - saleCount) >= 40000) {
		deleveryCount = 0;
	}
	
//	최종금액 오리지널총금액+배송비-할인받는총금액
	int finalCount = totalCount+deleveryCount-saleCount;
	
	double totalDcFormat = ((double) saleCount / totalCount) * 100;
	System.out.println("totalDcFormat" + totalDcFormat);
	int totalDc = (int) Math.round(totalDcFormat);
	
	model.addAttribute("cartList",cartList);
	model.addAttribute("totalCount",totalCount);
	model.addAttribute("deleveryCount",deleveryCount);
	model.addAttribute("saleCount",saleCount);
	model.addAttribute("finalCount",finalCount);
	model.addAttribute("totalDc",totalDc);
	
	
	return "cart";

	}

//	카트 상품담기
	@PostMapping(value = "/cart/cartReq")
	public @ResponseBody ResponseEntity inqReq(@RequestBody Map<String, Object> requestBody, HttpSession session) {

		
		Long cartCount = orderService.cartReg(requestBody, session);
		
		if(cartCount==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(cartCount, HttpStatus.OK);
	}
	
//	카트 상품 개별삭제
	@PostMapping(value = "/cart/cartDeleteButton")
	public @ResponseBody ResponseEntity cartDeleteButton(@RequestBody Map<String, Object> requestBody, HttpSession session) {
		
	
		
		boolean check = orderService.cartDeleteButton(requestBody);
		
		if(!check) {
			return new ResponseEntity<>("장바구니에 해당상품이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("장바구니에서 삭제 되었습니다.", HttpStatus.OK);
	}
	
//	카트 상품 체크박스 선택삭제
	@PostMapping(value = "/cart/cartDeletCkeckBox")
	public @ResponseBody ResponseEntity cartDeletCkeckBox(@RequestBody List<Object> requestBody, HttpSession session) {
		
	
		
		boolean check = orderService.cartDeleteCheckBox(requestBody);
		
		if(!check) {
			return new ResponseEntity<>("삭제중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("장바구니에서 삭제 되었습니다.", HttpStatus.OK);
	}
	
//	카트 상품 품절 전체삭제
	@PostMapping(value = "/cart/cartDeleteSoldoutCheckBok")
	public @ResponseBody ResponseEntity cartDeleteSoldoutCheckBok(@RequestBody List<Object> requestBody, HttpSession session) {
	
		
		
		boolean check = orderService.cartDeleteSoldoutCheckBok(requestBody);
		
		if(!check) {
			return new ResponseEntity<>("삭제중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("장바구니에서 삭제 되었습니다.", HttpStatus.OK);
	}
	
	
//	카트 상품 개별수량 변경
	@PostMapping(value = "/cart/cartCountUpdateButton")
	public @ResponseBody ResponseEntity cartCountUpdateButton(@RequestBody Map<String, Object> requestBody, HttpSession session) {
		
	
		String check = orderService.cartCountUpdateButton(requestBody);
	
		if("error".equals(check)) {
			return new ResponseEntity<>("변경중 에러가 발생했습니다.", HttpStatus.BAD_REQUEST);
		}
		else if("stockError".equals(check)) {
			return new ResponseEntity<>("재고가 부족합니다.", HttpStatus.BAD_REQUEST);
		}
		
			
		return new ResponseEntity<>("수량 변경되었습니다.", HttpStatus.OK);
	}
	
	
	


}
