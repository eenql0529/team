package com.recipe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.recipe.entity.Follow;
import com.recipe.entity.Member;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	@Modifying
	@Query(value = "insert into Follow(member_id, from_member) VALUES(?1,?2)", nativeQuery = true)
	void saveFollow(Member toMember, String fromMember);

	void deleteByToMemberAndMember(Long toMember, Member fromMember);

	boolean existsByToMemberAndMember(Long toMember, Member fromMember);
	
	 // 특정 사용자를 팔로우한 사용자 수 조회
 	@Query(value="SELECT COUNT(*) FROM Follow WHERE member_id = ?1" , nativeQuery = true)
    int countToMember(Long memberId);
 	
 	// 특정 사용자가 팔로우한 사용자 수 조회
 	@Query(value="SELECT COUNT(*) FROM Follow WHERE to_member = ?1" , nativeQuery = true)
 	int countFromMember(Long fromMember);
	
	Follow findByMemberIdAndToMember(Long memberId, Long id);

	

}
