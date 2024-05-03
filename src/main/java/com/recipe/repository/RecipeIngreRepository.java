package com.recipe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.recipe.entity.RecipeIngre;

public interface RecipeIngreRepository extends JpaRepository<RecipeIngre, Long>{
	@Query(value = "select * from ingredient where recipe_count = ?1", nativeQuery = true)
	List<RecipeIngre> getRecipeIngreByid(Long id);
	List<RecipeIngre> findByRecipeId(Long recipeId);
}