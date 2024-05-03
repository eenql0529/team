package com.recipe.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.recipe.constant.CategoryEnum;
import com.recipe.constant.WritingStatus;
import com.recipe.dto.RecipeNewDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="recipe") //테이블 이름 지정
@Getter
@Setter
@ToString
public class Recipe extends BaseEntity {
	
	
    @Id
    @Column(name="recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id

    private String title; //제목
    
    private String subTitle; //부제목
    
    @Column(length = 1000)
    private String intro; //레시피 소개
    
    @Column(length = 1000)
    private String description;
    //소요시간
    private String durTime;
    
    //난이도
    private String level;
    private int bookmarkCount; //북마크
    
    private int count = 0; //조회수
    
    private String imageUrl; // 메인이미지 (이미지 URL필드 추가)
    
    private String imgName; //이미지 이름
    
    @Enumerated(EnumType.STRING) //레시피타입별 카테고리
	private CategoryEnum categoryEnum;
    
    @Enumerated(EnumType.STRING) //레시피 등록,임시저장
    private WritingStatus writingStatus;
    
    
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Member member;
	
	
	
	public void updateRecipe(RecipeNewDto recipeNewDto) {
		this.title = recipeNewDto.getTitle();
		this.subTitle = recipeNewDto.getSubTitle();
		this.intro = recipeNewDto.getIntro();
		this.durTime = recipeNewDto.getDurTime();
		this.level = recipeNewDto.getLevel();
		this.imageUrl = recipeNewDto.getImageUrl();
		this.categoryEnum = recipeNewDto.getCategoryEnum();
		this.writingStatus = recipeNewDto.getWritingStatus();
	}
	

	public void updateRecipeImg(String imageUrl , String imgName) {
		this.imageUrl = imageUrl;
		this.imgName = imgName;
	}
}