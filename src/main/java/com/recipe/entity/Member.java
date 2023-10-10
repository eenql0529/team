package com.recipe.entity;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.recipe.constant.PrivateOk;
import com.recipe.constant.PromotionOk;
import com.recipe.constant.Role;
import com.recipe.constant.ServiceOk;
import com.recipe.dto.MemberDto;
import com.recipe.dto.MyPageDto;
import com.recipe.dto.SocialMemberDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true) // 중복된 값이 들어올 수 없다
	private String email;

	private String password; // 소셜 멤버 패스워드는 null값을 받아서 @Column(nullable = false)제거

	private String nickname;

	@Column(nullable = false)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Enumerated(EnumType.STRING) // 서비스 동의
	private ServiceOk serviceOk;

	@Enumerated(EnumType.STRING) // 개인정보 동의
	private PrivateOk privateOk;

	@Enumerated(EnumType.STRING) // 프로모션 동의
	private PromotionOk promotionOk;

	private String name; // 간편로그인시 주어지는 사이트마다 이름값, 일반로그인으로 회원가입시 null임

	private String provider; // google

	private String providerId; // google 기본키 id값

	private String postCode; // 우편번호

	private String address; // 주소

	private String detailAddress; // 상세주소

	private String introduce; // 자기소개

	private String imgUrl; // 업로드한 프로필 이미지 url

	private String oriImgName; // 원본이미지 이름

	private String imgName; // 바뀐 사진이름(보안을위해)

	
	// member 엔티티 수정
	public void editMember(MyPageDto myPageDto) {
		this.nickname = myPageDto.getNickname();
		this.phoneNumber = myPageDto.getPhoneNumber();
		this.name = myPageDto.getName();
		this.introduce = myPageDto.getIntroduce();
		this.detailAddress = myPageDto.getDetailAddress();
		this.postCode = myPageDto.getPostCode();
		this.address = myPageDto.getAddress();

	}

	// 일반로그인 회원가입 메소드
	public static Member createMember(MemberDto memberDto, PasswordEncoder passwordEncoder) {
		Member member = new Member();

		String password = passwordEncoder.encode(memberDto.getPassword());
		// 기본정보
		member.setNickname(memberDto.getNickname());
		member.setEmail(memberDto.getEmail());
		member.setPhoneNumber(memberDto.getPhoneNumber());
		member.setPassword(password);

		// 주소
		member.setPostCode(memberDto.getPostCode());
		member.setAddress(memberDto.getAddress());
		member.setDetailAddress(memberDto.getDetailAddress());

		// 자기소개, 프로필 이미지 등등
		member.setIntroduce(memberDto.getIntroduce());
		member.setImgUrl(memberDto.getImgUrl());
		member.setOriImgName(memberDto.getOriImgName());
		member.setImgName(memberDto.getImgName());

		// 약관동의
		member.setServiceOk(ServiceOk.Y);
		member.setPrivateOk(PrivateOk.Y);
		member.setPromotionOk(PromotionOk.Y);

		// 역할
		member.setRole(Role.ADMIN); // 현재 일반회원은 ADMIN으로 바꿈

		return member;
	}

	public static Member createSnsMember(SocialMemberDto socialMemberDto) {
		Member member = new Member();

		// 기본정보
		member.setNickname(socialMemberDto.getNickname());
		member.setEmail(socialMemberDto.getEmail());
		member.setPassword(socialMemberDto.getPassword());
		member.setPhoneNumber(socialMemberDto.getPhoneNumber());
		member.setName(socialMemberDto.getName()); // 간편로그인시 소셜회사마다 주는 회원이름값
		member.setProvider(socialMemberDto.getProvider()); // 구글인지 카카오인지 구별값
		member.setProviderId(socialMemberDto.getProviderId()); // 소셜 기본id값

		// 자기소개, 프로필 이미지 등등
		member.setIntroduce(socialMemberDto.getIntroduce());
		member.setImgUrl(socialMemberDto.getImgUrl());
		member.setOriImgName(socialMemberDto.getOriImgName());
		member.setImgName(socialMemberDto.getImgName());

		// 주소
		member.setPostCode(socialMemberDto.getPostCode());
		member.setAddress(socialMemberDto.getAddress());
		member.setDetailAddress(socialMemberDto.getDetailAddress());

		// 약관동의
		member.setServiceOk(ServiceOk.Y);
		member.setPrivateOk(PrivateOk.Y);
		member.setPromotionOk(PromotionOk.Y);

		// 역할
		member.setRole(Role.USER);

		return member;
	}

	// 간편로그인
	@Builder(builderClassName = "MemberDetailRegister", builderMethodName = "MemberDetailRegister")
	public Member(String name, String password, String email, Role role) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.role = role;
	}

	@Builder(builderClassName = "OAuth2Register", builderMethodName = "oauth2Register")
	public Member(String name, String password, String email, Role role, String provider, String providerId) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
	}

	public Member(String name, String email) {
		this.name = name;
		this.email = email;
	}

	// 가지고옴 비번을 임시 비밀번호로 업데이트
	public String updatePassword(String pass, PasswordEncoder passwordEncoder) {
		String password = passwordEncoder.encode(pass);
		this.password = password;

		return password;
	}

	public void updateProfileImg(String imgUrl, String imgName) {
		this.imgUrl = imgUrl;
		this.imgName = imgName;
	}
	
	//이미지 업데이트
		public void updateImg(String oriImgName, String imgName, String imgUrl) {
			this.imgUrl = imgUrl;
			this.imgName = imgName;
			this.oriImgName = oriImgName;
		}
		
	
}