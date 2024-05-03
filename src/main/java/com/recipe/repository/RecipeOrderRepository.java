package com.recipe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.recipe.entity.RecipeOrder;

public interface RecipeOrderRepository extends JpaRepository<RecipeOrder, Long> {
	@Query(value = "select * from recipe_order where order_number = ?1", nativeQuery = true)
	List<RecipeOrder> getRecipeOrderByid(Long id);
	List<RecipeOrder> findByRecipeId(Long recipeId);
}
