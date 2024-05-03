package com.recipe.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.recipe.dto.MemberMainDto;
import com.recipe.dto.RecipeMainDto;
import com.recipe.service.MemberService;
import com.recipe.service.RecipeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final RecipeService recipeService;
	
	private final MemberService memberService;
	
	
	
	@GetMapping(value = "/")
	public String main(Model model) {
		
	    List<RecipeMainDto> headerBestList = recipeService.getRecipeHeaderBestList(); // 헤더 레시피
	    model.addAttribute("headerBestList" , headerBestList);
	    
	    List<RecipeMainDto> mainBestList = recipeService.getRecipeBestList(); // 베스트 레시피
	    model.addAttribute("mainBestList" , mainBestList);
	    
	    
	    List<MemberMainDto> rankMemberList = memberService.getMemberBestList(); //베스트 쉐프 
	    model.addAttribute("rankMemberList",rankMemberList);
	    
	
		return "main";
	}
	
//	팔로우 하기
	@PostMapping(value="/followReq")
	public @ResponseBody ResponseEntity inqReq(@RequestBody Map<String, Object> requestBody
			, @AuthenticationPrincipal Object principal) {
		
//		현재 로그인 여부
		if(!isAuthenticated()) {
			return new ResponseEntity<String>("로그인이 필요한 기능입니다 \n 로그인 페이지로 이동하시겠습니까?" , HttpStatus.BAD_REQUEST);
		}
		
	    String email = "";

	    if (principal instanceof OAuth2User) {
	        email = ((OAuth2User) principal).getAttribute("email");
	    } else if (principal instanceof UserDetails) {
	        email = ((UserDetails) principal).getUsername();
	    }
		
		String followOk = memberService.followReg(requestBody ,email);
		
		return new ResponseEntity<>(followOk , HttpStatus.OK);
	}
	
	
	
	private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }
	
	
}
