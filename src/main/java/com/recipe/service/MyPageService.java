package com.recipe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.recipe.dto.MyPageDto;
import com.recipe.entity.BookMark;
import com.recipe.entity.Comment;
import com.recipe.entity.Follow;
import com.recipe.entity.Member;
import com.recipe.entity.Recipe;
import com.recipe.entity.RecipeOrder;
import com.recipe.entity.Review;
import com.recipe.repository.BookMarkRepository;
import com.recipe.repository.CommentRepository;
import com.recipe.repository.FollowRepository;
import com.recipe.repository.MemberRepository;
import com.recipe.repository.RecipeRepository;
import com.recipe.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

	private final String imgLocation = "C:/yummy/";
	private final MemberRepository memberRepository;
	private final RecipeRepository recipeRepository;
	private final BookMarkRepository bookMarkRepository;
	private final CommentRepository commentRepository;
	private final ReviewRepository reviewRepository;
	private final FollowRepository followRepository;
	
	

	//회원정보가져오기 
	@Transactional(readOnly = true)
	public MyPageDto getMemberInfo(Long id){
		
		Member member = memberRepository.findById(id)
										.orElseThrow(EntityNotFoundException::new);
		
		MyPageDto myPageDto = MyPageDto.of(member);


		return myPageDto;
	}
	
	
	//회원정보수정하기
	public Long editMember(MyPageDto myPageDto, MultipartFile imgFile) throws Exception {
		
		Member member = memberRepository.findById(myPageDto.getId())
										.orElseThrow(EntityNotFoundException::new);
		
		Long memberId = myPageDto.getId();
		
		updateImg(memberId,imgFile);
		member.editMember(myPageDto);
		
		System.out.println(imgFile);
		
		
		
		return member.getId();
	}
	
	
	
	//이미지!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public void saveImage(Member member,MultipartFile imgFile) throws Exception {
		String oriImgName = imgFile.getOriginalFilename();    //파일이름 -> 이미지1.jpg
		String imgUrl = "";
		String imgName = "";
		    
		    // 1. 파일을 itemImgLocation에 저장
		if(!StringUtils.isEmpty(oriImgName)) {
			//oriImgName이 빈문자열이 아니라면 이미지 파일 업로드
			imgName = uploadFile(imgLocation, 
					oriImgName, imgFile.getBytes());
			imgUrl = "/img/" + imgName;
		}
		    
		member.updateImg(oriImgName, imgName, imgUrl);
		memberRepository.save(member);
	}
	
	public void updateImg(Long id, MultipartFile imgFile)
	throws Exception{
		
		if(!imgFile.isEmpty()) {
			
			Member memberId = memberRepository.findById(id)
											  .orElseThrow(EntityNotFoundException::new);
			
			//기존 이미지 파일 C:/hotel/room 폴더에서 삭제
			if(!StringUtils.isEmpty(memberId.getImgName())) {
				deleteFile(imgLocation + "/" + memberId.getImgName()); 
			} 
			//수정된 이미지 파일 C:/shop/item 에 업로드
			
			String oriImgName = imgFile.getOriginalFilename();
			String imgName = uploadFile(imgLocation, oriImgName, imgFile.getBytes());
			String imgUrl = "/img/" + imgName;
			//update쿼리문 실행
			/* ★★★ 한번 insert를 진행하면 엔티티가 영속성 컨텍스트에 저장이 되므로 
			그 이후에는 변경감지 기능이 동작하기 때문에 개발자는 update쿼리문을 쓰지 않고
			엔티티 데이터만 변경해주면 된다. */
			
			memberId.updateImg(oriImgName, imgName, imgUrl);
		}
		
		
	}

	
	
	//파일업로드
	public String uploadFile(String uploadPath, String originalFileName,
			byte[] fileData) throws Exception {
		UUID uuid = UUID.randomUUID(); //중복되지 않은 이름을 만든다
		
		// 이미지1.jpg -> 이미지의 확장자 명을 구한다
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
	
		// 파일이름 생성
		String savedFileName = uuid.toString() + extension;
		
		// C:/shop/item/ERSFHG4FDGD454.jpg
		String fileUploadFullUrl = uploadPath + "/" + savedFileName;
		
		//파일업로드
		FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
		fos.write(fileData);
		fos.close();
		
		return savedFileName;
	}
	
	//파일삭제
	public void deleteFile(String filePath) throws Exception{
		// filePath ->  C:/shop/item/ERSFHG4FDGD454.jpg
		File deleteFile = new File(filePath); //파일이 저장된 경로를 이용해서 파일 객체를 생성
		
		//파일삭제
		if(deleteFile.exists()) {//해당 경로에 파일이 있으면
			deleteFile.delete(); //파일삭제
			log.info("파일을 삭제하였습니다."); //로그기록을 저장한다
		}else {
			log.info("파일이 존재하지 않습니다.");
		}
		
	}
	
	
	//회원탈퇴
	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
										.orElseThrow(EntityNotFoundException::new);
		
		//delete
		memberRepository.delete(member);
		
	}
	
	
	//마이페이지 레시피리스트
	@Transactional(readOnly = true)
	public List<Recipe> getRecipeList(Long id){
		List<Recipe> recipes = recipeRepository.findRecipe(id);

		
		return recipes;

	}
	//모든레시피리스트
	@Transactional(readOnly = true)
	public List<Recipe> getAllRecipeList(Long id){
		List<Recipe> recipes = recipeRepository.findAllRecipe(id);
		
		
		return recipes;
		
	}
	//인기레시피리스트
	@Transactional(readOnly = true)
	public List<Recipe> getPopularRecipeList(Long id){
		List<Recipe> recipes = recipeRepository.getPopularRecipe(id);
		
		
		return recipes;
		
	}
	//레시피 삭제
	public void deleteRecipe(Long recipeId) {
		Recipe recipe = recipeRepository.findById(recipeId)
										  .orElseThrow(EntityNotFoundException::new);
		
		recipeRepository.delete(recipe);
	}
	//찜목록 불러오기
	@Transactional(readOnly = true)
	public List<MyPageDto> getBookmark(Long id){
		List<BookMark> bookmarks = bookMarkRepository.getBookmarks(id);
		List<MyPageDto> bookmarkDtos = new ArrayList<>();
		
		for(BookMark bookmark : bookmarks) {
			
			Member member = bookmark.getMember();
			Recipe recipe = bookmark.getRecipe();
			
			MyPageDto bookmarkDto = new MyPageDto(member , recipe, bookmark);
			bookmarkDtos.add(bookmarkDto);
		}
		
		return bookmarkDtos;
	}
	
	//찜 삭제
	public void deleteBookmark(Long bookmarkId) {
		BookMark bookmark = bookMarkRepository.findById(bookmarkId)
											  .orElseThrow(EntityNotFoundException::new);
		bookmark.setIsDelete(true);
		bookMarkRepository.save(bookmark); // 상태 변경 저장
	}
	//찜 삭제취소
	public void undeleteBookmark(Long bookmarkId) {
		BookMark bookmark = bookMarkRepository.findById(bookmarkId)
				.orElseThrow(EntityNotFoundException::new);
		bookmark.setIsDelete(false);
		bookMarkRepository.save(bookmark); // 상태 변경 저장
	}
	public void deleteMarkedBookmarks() {
	    List<BookMark> bookmarksToDelete = bookMarkRepository.findByIsDeleteTrue();
	    bookMarkRepository.deleteAll(bookmarksToDelete);
	}
	
	//댓글목록 불러오기
	@Transactional(readOnly = true)
	public List<MyPageDto> getMyComment(Long id){

		List<Comment> comments = commentRepository.getMyComment(id);
		List<MyPageDto> commentDtos = new ArrayList<>();
		
		for(Comment comment : comments) {
			
			Member member = comment.getMember();

			MyPageDto commentDto = new MyPageDto(member , comment);
			commentDtos.add(commentDto);
		}
		
		return commentDtos;
	}
	//댓글 삭제
	public void deleteComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(EntityNotFoundException::new);
		commentRepository.delete(comment);
	}
	
	//내후기불러오기
	@Transactional(readOnly = true)
	public List<MyPageDto> getMyReview(Long id){
		
		List<Review> reviews = reviewRepository.getMyReview(id);
		List<MyPageDto> reviewDtos = new ArrayList<>();
		
		for(Review review : reviews) {
			Member member = review.getMember();
			
			MyPageDto reviewDto = new MyPageDto(member, review);
			reviewDtos.add(reviewDto);
		}
		return reviewDtos;
	}
	//받은후기불러오기
	@Transactional(readOnly = true)
	public List<MyPageDto> getReceivedReview(Long id){
		
		List<Review> reviews = reviewRepository.getReceivedReview(id);
		List<MyPageDto> reviewDtos = new ArrayList<>();
		
		for(Review review : reviews) {
			Member member = review.getMember();
			
			MyPageDto reviewDto = new MyPageDto(member, review);
			reviewDtos.add(reviewDto);
		}
		return reviewDtos;
	}
	
	//후기삭제
	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
										.orElseThrow(EntityNotFoundException::new);
		reviewRepository.delete(review);
	}
	
	//팔로우
	public void saveFollow(Long toMemberId, Principal fromMemberPrincipal) {
		  Member toMember = memberRepository.findById(toMemberId).orElseThrow(EntityNotFoundException::new);
		    Member fromMember = memberRepository.findByEmail(fromMemberPrincipal.getName());

		    Follow follow = new Follow();
		    follow.setToMember(toMember.getId());
		    follow.setMember(fromMember);

		    followRepository.save(follow);
	}
	
	//언팔로우
	@Transactional
	public boolean unfollow(Long toMemberId, Long fromMemberId) {
	    Member toMember = memberRepository.findById(toMemberId)
	        .orElseThrow(() -> new EntityNotFoundException("To member not found"));
	    Member fromMember = memberRepository.findById(fromMemberId)
	        .orElseThrow(() -> new EntityNotFoundException("From member not found"));

	    // 언팔로우 관계를 제거합니다.
	    followRepository.deleteByToMemberAndMember(toMember.getId() , fromMember);

	    // 언팔로우 성공 여부를 반환합니다.
	    return true;
	}
	
	//로그인한 사용자가 자기를 팔로우한 팔로워삭제
	public void unfollowTofollower(Long toMemberId, Long fromMemberId) {
		Member toMember = memberRepository.findById(toMemberId)
				.orElseThrow(() -> new EntityNotFoundException("To member not found"));
		Member fromMember = memberRepository.findById(fromMemberId)
				.orElseThrow(() -> new EntityNotFoundException("From member not found"));
		
		followRepository.deleteByToMemberAndMember(toMember.getId(), fromMember);
	}
	
	//팔로우확인
    public boolean isFollowing(Long toMemberId, Long fromMemberId) {
        // 팔로우 테이블에서 toUserId와 fromUserId로 데이터가 존재하는지 확인
    	Member toMember = memberRepository.findById(toMemberId)
    	        .orElseThrow(() -> new EntityNotFoundException("To member not found"));
    	    Member fromMember = memberRepository.findById(fromMemberId)
    	        .orElseThrow(() -> new EntityNotFoundException("From member not found"));
        return followRepository.existsByToMemberAndMember(toMember.getId() , fromMember);
    }
    
 // 특정 사용자가 팔로우한 사용자 수 조회
    public void getFollowingCount(Long fromMemberId){
    	int following = followRepository.countFromMember(fromMemberId);
    	int follower = followRepository.countToMember(fromMemberId);
    	
    	Member member = memberRepository.findById(fromMemberId) .orElseThrow(() -> new EntityNotFoundException("To member not found"));

    }
    
    @Transactional(readOnly = true)
    public List<Member> getFollowerList(Long toMember){
    	List<Member> follows = memberRepository.getFollowerList(toMember);
    	
    	
    	return follows;
    	
    }
    @Transactional(readOnly = true)
    public List<Member> getFollowingList(Long fromMember){
    	List<Member> follows = memberRepository.getFollowingList(fromMember);
    	
    	
    	return follows;
    	
    }
}

