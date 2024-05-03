package com.recipe.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.recipe.dto.RecipeCategoryDto;
import com.recipe.dto.RecipeMainDto;
import com.recipe.dto.RecipeSearchDto;
import com.recipe.entity.Recipe;

import jakarta.transaction.Transactional;


public interface RecipeRepository extends JpaRepository<Recipe , Long>  ,  RecipeRepositoryCustom
										{
	
	@Transactional
	@Modifying
	@Query(value = "update recipe set count = count + 1 where recipe_id = ?1", nativeQuery = true)
	int setaddview(Long id);
	@Query(value = "select * from recipe where recipe_id = ?1", nativeQuery = true)
	Recipe getRecipeDetailByid(Long id);
	
	@Query(value = "SELECT * FROM recipe WHERE member_id = ?1  ORDER BY recipe_id DESC" , nativeQuery = true)
	List<Recipe> findRecipe(Long id);
	
	@Query(value = "SELECT * FROM recipe WHERE member_id = ?1 AND writing_status = 'PUBLISHED' ORDER BY recipe_id DESC ", nativeQuery = true)
	List<Recipe> findAllRecipe(Long id);
	
	
	@Query(value = "select r.* from recipe r where r.member_id = ?1 AND writing_status = 'PUBLISHED' ORDER BY r.count DESC LIMIT 5", nativeQuery = true)
List<Recipe> getPopularRecipe(Long id);
	
	
	
	
//	조회수 가장 높은순으로 모든레시피 가져옴 (4개)
//	메인
	@Query ( value = "select recipe.recipe_id id , recipe.title title , recipe.sub_title subTitle , "
			+ " recipe.intro intro , recipe.dur_time durTime , recipe.image_url imageUrl ,  "
			+ " recipe.level level , recipe.count count "
			+ " from recipe "
			+ " where recipe.writing_status = 'PUBLISHED'"
			+ " order by count desc, RAND() limit 4" 
			, nativeQuery = true)
	List<RecipeMainDto> getRecipeHeaderBestList();

//	최근 등록순으로 모든레시피 가져옴 (제한 15개)
//	메인
	@Query ( value = "SELECT \r\n"
			+ "  r.recipe_id id, \r\n"
			+ "  r.title title, \r\n"
			+ "  r.sub_title subTitle, \r\n"
			+ "  r.intro intro, \r\n"
			+ "  r.dur_time durTime, \r\n"
			+ "  r.level level, \r\n"
			+ "  r.count count, \r\n"
			+ "  m.nickname nickname, \r\n"
			+ "  r.image_url imageUrl , \r\n"
			+ "  m.img_url imgUrl , "
			+ "  m.member_id memberId "
			+ " FROM recipe r\r\n"
			+ " JOIN member m ON r.member_id = m.member_id\r\n"
			+ " where r.writing_status = 'PUBLISHED'"
			+ " ORDER BY r.reg_time DESC limit 15"
			, nativeQuery = true)
	List<RecipeMainDto> getRecipeNewList();
	
	
	
	@Query ( value = "SELECT \r\n"
			+ "    ifnull(bm_count , 0) bookCount ,"
			+ "    ifnull(rv_count , 0) reviewCount ,\r\n"
			+ "    COALESCE(rv_avg, 0) retingAvg ,\r\n"
			+ "    r.recipe_id id , r.count count , r.dur_time durTime , r.image_url imageUrl , "
			+ "    r.level level , r.sub_title subTitle, r.title title , r.member_id memberId ,"
			+ "    r.reg_time regTime , r.intro intro ,\r\n"
			+ "    m.nickname nickname ,\r\n"
			+ "    m.img_url imgUrl \r\n"
			+ "FROM recipe r "
			+ "LEFT JOIN ( "
			+ "    SELECT recipe_id, COUNT(*) AS bm_count\r\n"
			+ "    FROM book_mark\r\n"
			+ "    GROUP BY recipe_id\r\n"
			+ ") bm ON r.recipe_id = bm.recipe_id "
			+ "LEFT JOIN (\r\n"
			+ "    SELECT recipe_id, COUNT(*) AS rv_count, COALESCE(AVG(reting), 0) AS rv_avg\r\n"
			+ "    FROM review\r\n"
			+ "    GROUP BY recipe_id\r\n"
			+ ") rv ON r.recipe_id = rv.recipe_id\r\n"
			+ " JOIN member m ON r.member_id = m.member_id\r\n"
			+ " where r.writing_status = 'PUBLISHED'"
			+ " ORDER BY rv_count DESC limit 10 "
			, nativeQuery = true)
	List<RecipeMainDto> getRecipeBestList();
	

}
