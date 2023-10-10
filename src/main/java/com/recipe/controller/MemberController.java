package com.recipe.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.recipe.dto.MemberDto;
import com.recipe.dto.SocialMemberDto;
import com.recipe.entity.Member;
import com.recipe.service.FileService;
import com.recipe.service.MemberService;
import com.recipe.service.RamdomPassword;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;
	private final RamdomPassword randomPassword;
	private final FileService fileService;
	private String profileImgLocation = "C:/yummy/member";

	// 로그인 화면
	@GetMapping(value = "/members/login")
	public String loginMember() {

		return "member/loginForm";
	}

	// 약관 동의
	@GetMapping(value = "/members/agree")
	public String agreeMember() {
		return "member/agreeForm";
	}

	// 일반 회원가입 화면
	@GetMapping(value = "/members/newMember")
	public String newMemberForm(Model model) {
		model.addAttribute("memberDto", new MemberDto());
		return "member/newMemberForm";
	}

	// sns 회원가입 화면
	@GetMapping(value = "/members/snsMember")
	public String snsMemberForm( HttpServletResponse response,
			HttpServletRequest request, Model model) {
		SocialMemberDto socialMemberDto = new SocialMemberDto();

		Cookie[] cookies = request.getCookies();
		String email = null;
		String provider = null;
		String providerId = null;
		String name = null;

			for (Cookie cookie : cookies) {
				switch (cookie.getName()) {
				case "email":
					email = cookie.getValue();
					break;
				case "provider":
					provider = cookie.getValue();
					break;
				case "providerId":
					providerId = cookie.getValue();
					break;
				case "name":
					name = cookie.getValue();
					break;
				}
			}
			
		socialMemberDto.setEmail(email);
		socialMemberDto.setProvider(provider);
		socialMemberDto.setProviderId(providerId);
		socialMemberDto.setName(name);
		
//		쿠키정보를 저장했으면 쿠키를 삭제한다 삭제안하면 웹사이트개발자도구에 남아있음
		Cookie emailCookie = new Cookie("email", null);
		emailCookie.setMaxAge(0); 
		emailCookie.setPath("/"); 
		response.addCookie(emailCookie); 
		Cookie providerCookie = new Cookie("provider", null); 
		providerCookie.setMaxAge(0); 
		providerCookie.setPath("/"); 
		response.addCookie(providerCookie); 
		Cookie providerIdCookie = new Cookie("providerId", null); 
		providerIdCookie.setMaxAge(0);
		providerIdCookie.setPath("/"); 
		response.addCookie(providerIdCookie); 
		Cookie nameCookie = new Cookie("name", null); 
		nameCookie.setMaxAge(0); 
		nameCookie.setPath("/"); 
		response.addCookie(nameCookie); 
		

		model.addAttribute("socialMemberDto", socialMemberDto);

		return "member/snsMemberForm";
	}
	
	

	
	
	
	
	
	
	

	// 일반 회원가입 기능
	@PostMapping(value = "/members/newMember")
	public String newMemberForm(@RequestParam("profileImgFile") MultipartFile profileImgFile,
			@Valid MemberDto memberDto, BindingResult bindingResult, Model model) {

		// 회원가입 에러처리
		if (!memberDto.getPassword().equals(memberDto.getPasswordConfirm()) || memberDto.getEmailConfirm2().isEmpty()) {
			return "member/newMemberForm";
		}

		// 자기소개 기본값 넣기
		if (memberDto.getIntroduce() == null || memberDto.getIntroduce().isEmpty()) {
			memberDto.setIntroduce("자기소개가 없습니다.");
		}

		// 에러처리
		if (bindingResult.hasErrors()) {
			return "member/newMemberForm";
		}

		String imgName = profileImgFile.getOriginalFilename();
		String imgUrl = "";

		// 프로필 사진처리
		if (!StringUtils.isEmpty(imgName)) {
			try {
				// 프로필 이미지 파일을 저장
				String nickname = memberDto.getNickname();
				Path path = Paths.get("C:/yummy/member/" + nickname);
				if (!Files.exists(path)) {
					Files.createDirectories(path);
				}
				Path path1 = Paths.get("C:/yummy/member/" + nickname + "/profile");
				if (!Files.exists(path1)) {
					Files.createDirectories(path1);
				}
				Path path2 = Paths.get("C:/yummy/member/" + nickname + "/recipe");
				if (!Files.exists(path2)) {
					Files.createDirectories(path2);
				}
				Path path3 = Paths.get("C:/yummy/member/" + nickname + "/recipe/ingre");
				if (!Files.exists(path3)) {
					Files.createDirectories(path3);
				}
				StringBuilder sb = new StringBuilder();
				sb.append(profileImgLocation + "/" + nickname + "/profile");
				String result = sb.toString();
				
				String savedImgName = fileService.profileImgFile(result, imgName,
						profileImgFile.getBytes());
				imgUrl = "/img/member/" + nickname + "/profile/" + savedImgName;

				memberDto.setOriImgName(imgName);
				memberDto.setImgName(savedImgName);
				memberDto.setImgUrl(imgUrl);
				// 멤버생성 DB에저장
				Member member = Member.createMember(memberDto, passwordEncoder);
				member.setProviderId("default");
				
				
				memberService.saveMember(member);

			} catch (Exception e) {
				e.printStackTrace();
				// 회원가입 실패시

				System.out.println("error ::::" + e.getMessage());

				model.addAttribute("errorMessage", e.getMessage());
				return "member/newMemberForm";
			}

		} else {

			// imgName이 비어있거나 null일 때 처리 고양이 이미지 추가
			try {
				String nickname = memberDto.getNickname();
				Path path = Paths.get("C:/yummy/member/" + nickname);
				if (!Files.exists(path)) {
					Files.createDirectories(path);
				}
				Path path1 = Paths.get("C:/yummy/member/" + nickname + "/profile");
				if (!Files.exists(path1)) {
					Files.createDirectories(path1);
				}
				Path path2 = Paths.get("C:/yummy/member/" + nickname + "/recipe");
				if (!Files.exists(path2)) {
					Files.createDirectories(path2);
				}
				Path path3 = Paths.get("C:/yummy/member/" + nickname + "/recipe/ingre");
				if (!Files.exists(path3)) {
					Files.createDirectories(path3);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				// 회원가입 실패시

				System.out.println("error ::::" + e.getMessage());

				model.addAttribute("errorMessage", e.getMessage());
			}
			
			
			
			imgName = "비회원이미지.jpg";
			imgUrl = "/img/" + imgName;

			memberDto.setImgName(imgName);
			memberDto.setImgUrl(imgUrl);

			// 멤버생성 DB에저장
			Member member = Member.createMember(memberDto, passwordEncoder);
			member.setProviderId("default");
			memberService.saveMember(member);
		}

		// 회원가입 성공시
		return "redirect:/";
	}

	// sns 회원가입 기능
	@PostMapping(value = "/members/snsMember")
	public String snsMemberForm(@RequestParam("snsProfileImgFile") MultipartFile snsProfileImgFile,
			@Valid SocialMemberDto socialMemberDto, BindingResult bindingResult, Model model) {

		// 자기소개 기본값 넣기
		if (socialMemberDto.getIntroduce() == null || socialMemberDto.getIntroduce().isEmpty()) {
			socialMemberDto.setIntroduce("자기소개가 없습니다.");
		}

		// 에러처리
		if (bindingResult.hasErrors()) {
			return "member/snsMemberForm";
		}

		String imgName = snsProfileImgFile.getOriginalFilename();
		String imgUrl = "";

		// 프로필 사진처리
		if (!StringUtils.isEmpty(imgName)) {
			try {
				
				String nickname = socialMemberDto.getNickname();
				Path path = Paths.get("C:/yummy/member/" + nickname);
				if (!Files.exists(path)) {
					Files.createDirectories(path);
				}
				Path path1 = Paths.get("C:/yummy/member/" + nickname + "/profile");
				if (!Files.exists(path1)) {
					Files.createDirectories(path1);
				}
				Path path2 = Paths.get("C:/yummy/member/" + nickname + "/recipe");
				if (!Files.exists(path2)) {
					Files.createDirectories(path2);
				}
				Path path3 = Paths.get("C:/yummy/member/" + nickname + "/recipe/ingre");
				if (!Files.exists(path3)) {
					Files.createDirectories(path3);
				}
				StringBuilder sb = new StringBuilder();
				sb.append(profileImgLocation + "/" + nickname + "/profile");
				String result = sb.toString();
				
				// 프로필 이미지 파일을 저장
				String savedImgName = fileService.profileImgFile(result, imgName,
						snsProfileImgFile.getBytes());
				imgUrl = "/img/member/" + nickname + "/profile/" + savedImgName;

				socialMemberDto.setOriImgName(imgName);
				socialMemberDto.setImgName(savedImgName);
				socialMemberDto.setImgUrl(imgUrl);

				// 멤버생성 DB에저장
				Member member = Member.createSnsMember(socialMemberDto);
				memberService.saveMember(member);

			} catch (Exception e) {
				e.printStackTrace();
				// 회원가입 실패시
				model.addAttribute("errorMessage", e.getMessage());
				return "member/newMemberForm";
			}

		} else {
			try {
				
				String nickname = socialMemberDto.getNickname();
				Path path = Paths.get("C:/yummy/member/" + nickname);
				if (!Files.exists(path)) {
					Files.createDirectories(path);
				}
				Path path1 = Paths.get("C:/yummy/member/" + nickname + "/profile");
				if (!Files.exists(path1)) {
					Files.createDirectories(path1);
				}
				Path path2 = Paths.get("C:/yummy/member/" + nickname + "/recipe");
				if (!Files.exists(path2)) {
					Files.createDirectories(path2);
				}
				Path path3 = Paths.get("C:/yummy/member/" + nickname + "/recipe/ingre");
				if (!Files.exists(path3)) {
					Files.createDirectories(path3);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				// 회원가입 실패시

				System.out.println("error ::::" + e.getMessage());

				model.addAttribute("errorMessage", e.getMessage());
			}
			
			
			// oriImgName이 비어있거나 null일 때 처리 고양이 이미지 추가
			imgName = "비회원이미지.jpg";
			imgUrl = "/img/" + imgName;

			socialMemberDto.setImgName(imgName);
			socialMemberDto.setImgUrl(imgUrl);

			// 멤버생성 DB에저장
			Member member = Member.createSnsMember(socialMemberDto);
			memberService.saveMember(member);
		}

		// 회원가입 성공시
		return "redirect:/";
	}

	// 로그인 실패
	@GetMapping(value = "/members/login/error")
	public String loginError(Model model) {
		model.addAttribute("loginErrorMsg", "로그인 실패 아이디 또는 비밀번호를 확인해주세요");
		return "member/loginForm";
	}

	// 아이디/비밀번호찾기 페이지
	@GetMapping(value = "/members/findIDPW")
	public String findIDPW() {
		return "member/findIDPW";
	}

	// 비밀번호 찾고 난수생성기로 랜덤비밀번호 생성 , 아이디/비밀번호 찾기
	@PostMapping(value = "/findPw")
	@ResponseBody
	public HashMap<String, String> memberps(@RequestBody Map<String, Object> psdata, Principal principal) {
		String email = (String) psdata.get("email");
		HashMap<String, String> msg = new HashMap<>();
		
		boolean snsCkeck = memberService.findMember(email);
		
		if(!snsCkeck) {
			msg.put("error", "소셜회원입니다. 비밀번호를 찾을수 없습니다.");
			return msg;
		}
		
		String memberEmail = randomPassword.findMember(email);

		// email이 DB에 없는 경우
		if (memberEmail == null) {
			msg.put("error", "입력하신 이메일 주소가 존재하지 않습니다.");
			return msg;
		}
		
		

		String ramdomps = randomPassword.getRamdomPassword(12); // 임시 비밀번호 12자리생성
		System.out.println("ramdomps===" + ramdomps);

		// 임시 비밀번호를 DB에 저장해서 바꿈 => 임시 비밀번호로 저장해야 로그인성공
		String password = randomPassword.updatePassword(ramdomps, email, passwordEncoder);

		String emailBody = "<h3>요청하신 임시 비밀번호입니다.</h3>" + "<h1>" + ramdomps + "</h1>" + "<h3>감사합니다.</h3>";

		randomPassword.sendEmail(email, "임시 비밀번호", emailBody);

		String asd1 = "이메일로 임시 비밀번호가 발송되었습니다.";
		String asd2 = "임시 비밀번호를 사용해 로그인 해주십시오.";
		msg.put("message1", asd1);
		msg.put("message2", asd2);
		return msg;
	}

	// 이메일 찾기
	@PostMapping(value = "/findEmail")
	public  @ResponseBody ResponseEntity findEmail(@RequestBody Map<String, Object> psdata) {
		
		String memberEmail = memberService.findEmailSearch(psdata);
		
		if(memberEmail == null) {
			return new ResponseEntity<>("가입된 이메일이 없습니다.", HttpStatus.BAD_REQUEST);
		}
		

		return new ResponseEntity<>(memberEmail, HttpStatus.OK);

	}

}