package com.recipe.dto;

import java.time.format.DateTimeFormatter;
import org.modelmapper.ModelMapper;
import com.recipe.entity.BookMark;
import com.recipe.entity.Comment;
import com.recipe.entity.Member;
import com.recipe.entity.Recipe;
import com.recipe.entity.RecipeOrder;
import com.recipe.entity.Review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageDto {
	
	public MyPageDto() {
		
	}
	//회원정보수정
	private Long id;
	private String phoneNumber;
	private String nickname;
	private String name;
	private String postCode;
	private String address;
	private String detailAddress;
	private String password;
	private String imgUrl;
	private String imgName;
	private String oriImgName;
	private String introduce;

	private static ModelMapper modelMapper = new ModelMapper();
	
	public static MyPageDto of(Member member) {
		
		return modelMapper.map(member, MyPageDto.class);
	}
	
	
	//레시피목록
	private Recipe recipeId;
	private RecipeOrder recipeOrderId;
	private int bookmarkCount;

	public MyPageDto(Recipe recipeId, int bookmarkCount) {
		this.recipeId = recipeId;
		this.bookmarkCount = bookmarkCount;
	}
	
	//찜목록
	private Member member;
	private BookMark bookmark;

	//찜목록
	public MyPageDto(Member member, Recipe recipe, BookMark bookmark) {
		this.member = member;
		this.recipeId = recipe;
		this.bookmark = bookmark;

		
	}

	private Comment comment;
	//댓글
	public MyPageDto(Member member, Comment comment) {
		this.member = member;
		this.comment = comment;
		this.recipeId = comment.getRecipe();
		
	}
	
	private Review review;
	private String regTime;
	//리뷰
	public MyPageDto(Member member, Review review) {
		this.member = member;
		this.review = review;
		this.recipeId = review.getRecipe();
	    if (review.getRegTime() != null) {
	        this.regTime = review.getRegTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
	    } else {
	        this.regTime = ""; // 또는 다른 값으로 표시하고 싶은 내용을 넣으세요
	    }
	}
	
	
}