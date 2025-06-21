// package org.cheonyakplanet.be.application.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
//
// import java.time.LocalDate;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;
//
// import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionLikeDTO;
// import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
// import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLike;
// import org.cheonyakplanet.be.domain.entity.user.User;
// import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
// import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
// import org.cheonyakplanet.be.domain.repository.SubscriptionLikeRepository;
// import org.cheonyakplanet.be.domain.repository.UserRepository;
// import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
// import org.cheonyakplanet.be.domain.exception.CustomException;
// import org.cheonyakplanet.be.domain.exception.ErrorCode;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
//
// @ExtendWith(MockitoExtension.class)
// @DisplayName("청약 찜하기 서비스 테스트")
// class SubscriptionLikeServiceTest {
//
// 	@Mock
// 	private SubscriptionLikeRepository subscriptionLikeRepository;
//
// 	@Mock
// 	private SubscriptionInfoRepository subscriptionInfoRepository;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	@InjectMocks
// 	private SubscriptionLikeService subscriptionLikeService;
//
// 	private User testUser;
// 	private UserDetailsImpl userDetails;
// 	private SubscriptionInfo testSubscriptionInfo;
// 	private SubscriptionLike testSubscriptionLike;
//
// 	@BeforeEach
// 	void setUp() {
// 		// Given: 테스트용 데이터 설정
// 		testUser = User.builder()
// 			.email("test@test.com")
// 			.password("password")
// 			.role(UserRoleEnum.USER)
// 			.username("테스트사용자")
// 			.build();
//
// 		userDetails = new UserDetailsImpl(testUser);
//
// 		testSubscriptionInfo = SubscriptionInfo.builder()
// 			.id(1L)
// 			.houseNm("테스트아파트")
// 			.houseSecd("APT")
// 			.houseDtlSecd("민영")
// 			.rentSecd("분양")
// 			.specltRdnEarthAt("N")
// 			.totSuplyHshldco(100)
// 			.hssplyAdres("서울특별시 강남구")
// 			.totSuplyHshldco(500)
// 			.rcritPblancDe(LocalDate.parse("20231201"))
// 			.rcritPblancDe(LocalDate.parse("20231207"))
// 			.przwnerPresnatnDe(LocalDate.parse("20240115"))
// 			.cntrctCnclsBgnde(LocalDate.parse("20240130"))
// 			.hmpgAdres("http://test.com")
// 			.bsnsMbyNm("테스트건설")
// 			.mdhsTelno("010-1234-5678")
// 			.build();
//
// 		testSubscriptionLike = SubscriptionLike.builder()
// 			.id(1L)
// 			.subscriptionId(testSubscriptionInfo.getId())
// 			.detail(testUser.getEmail())
// 			.build();
// 	}
//
// 	@Test
// 	@DisplayName("청약 찜하기 성공 테스트")
// 	void givenValidSubscriptionIdAndUser_whenLikeSubscription_thenReturnSuccessMessage() {
// 		// Given: 유효한 청약 정보와 사용자, 기존 찜하기 없음
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail()))
// 			.willReturn(Optional.empty());
// 		given(subscriptionLikeRepository.save(any(SubscriptionLike.class)))
// 			.willReturn(testSubscriptionLike);
//
// 		// When: 청약 찜하기 메서드 호출
// 		String result = subscriptionLikeService.likeSubscription(subscriptionId, userDetails);
//
// 		// Then: 성공 메시지가 반환되고 찜하기 데이터가 저장됨
// 		assertThat(result).isEqualTo("찜하기 완료");
// 		then(subscriptionInfoRepository).should().findById(subscriptionId);
// 		then(userRepository).should().findByEmailAndDeletedAtIsNull(testUser.getEmail());
// 		then(subscriptionLikeRepository).should().findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail());
// 		then(subscriptionLikeRepository).should().save(any(SubscriptionLike.class));
// 	}
//
// 	@Test
// 	@DisplayName("청약 찜하기 실패 테스트 - 존재하지 않는 청약")
// 	void givenNonExistentSubscriptionId_whenLikeSubscription_thenThrowCustomException() {
// 		// Given: 존재하지 않는 청약 ID
// 		Long nonExistentSubscriptionId = 999L;
//
// 		given(subscriptionInfoRepository.findById(nonExistentSubscriptionId))
// 			.willReturn(Optional.empty());
//
// 		// When & Then: 청약 찜하기 호출 시 CustomException 발생
// 		CustomException exception = assertThrows(CustomException.class,
// 			() -> subscriptionLikeService.likeSubscription(nonExistentSubscriptionId, userDetails));
//
// 		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO001);
// 		assertThat(exception.getMessage()).isEqualTo("청약 정보를 찾을 수 없습니다.");
// 		then(subscriptionLikeRepository).should(never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("청약 찜하기 실패 테스트 - 존재하지 않는 사용자")
// 	void givenNonExistentUser_whenLikeSubscription_thenThrowCustomException() {
// 		// Given: 존재하지 않는 사용자
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.empty());
//
// 		// When & Then: 청약 찜하기 호출 시 CustomException 발생
// 		CustomException exception = assertThrows(CustomException.class,
// 			() -> subscriptionLikeService.likeSubscription(subscriptionId, userDetails));
//
// 		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER001);
// 		assertThat(exception.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
// 		then(subscriptionLikeRepository).should(never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("중복 찜하기 실패 테스트 - 이미 찜한 청약")
// 	void givenAlreadyLikedSubscription_whenLikeSubscription_thenThrowCustomException() {
// 		// Given: 이미 찜한 청약에 대한 중복 찜하기 시도
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail()))
// 			.willReturn(Optional.of(testSubscriptionLike));
//
// 		// When & Then: 청약 찜하기 호출 시 CustomException 발생
// 		CustomException exception = assertThrows(CustomException.class,
// 			() -> subscriptionLikeService.likeSubscription(subscriptionId, userDetails));
//
// 		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO004);
// 		assertThat(exception.getMessage()).isEqualTo("이미 찜한 청약입니다.");
// 		then(subscriptionLikeRepository).should(never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("청약 찜하기 취소 성공 테스트")
// 	void givenValidLikedSubscription_whenUnlikeSubscription_thenReturnSuccessMessage() {
// 		// Given: 찜한 청약에 대한 취소 요청
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail()))
// 			.willReturn(Optional.of(testSubscriptionLike));
//
// 		// When: 청약 찜하기 취소 메서드 호출
// 		String result = subscriptionLikeService.unlikeSubscription(subscriptionId, userDetails);
//
// 		// Then: 성공 메시지가 반환되고 찜하기 데이터가 삭제됨
// 		assertThat(result).isEqualTo("찜하기 취소 완료");
// 		then(subscriptionLikeRepository).should().delete(testSubscriptionLike);
// 	}
//
// 	@Test
// 	@DisplayName("청약 찜하기 취소 실패 테스트 - 찜하지 않은 청약")
// 	void givenNotLikedSubscription_whenUnlikeSubscription_thenThrowCustomException() {
// 		// Given: 찜하지 않은 청약에 대한 취소 요청
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail()))
// 			.willReturn(Optional.empty());
//
// 		// When & Then: 청약 찜하기 취소 호출 시 CustomException 발생
// 		CustomException exception = assertThrows(CustomException.class,
// 			() -> subscriptionLikeService.unlikeSubscription(subscriptionId, userDetails));
//
// 		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO005);
// 		assertThat(exception.getMessage()).isEqualTo("찜하지 않은 청약입니다.");
// 		then(subscriptionLikeRepository).should(never()).delete(any());
// 	}
//
// 	@Test
// 	@DisplayName("사용자 찜한 청약 목록 조회 성공 테스트")
// 	void givenUserWithLikedSubscriptions_whenGetUserLikedSubscriptions_thenReturnSubscriptionList() {
// 		// Given: 찜한 청약이 있는 사용자
// 		int page = 0;
// 		int size = 10;
// 		Pageable pageable = PageRequest.of(page, size);
//
// 		List<SubscriptionLike> likedSubscriptions = Arrays.asList(testSubscriptionLike);
// 		Page<SubscriptionLike> likedPage = new PageImpl<>(likedSubscriptions, pageable, 1);
//
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.findByUserEmailAndDeletedAtIsNull(
// 			testUser.getEmail(), pageable))
// 			.willReturn(likedPage);
//
// 		// When: 사용자 찜한 청약 목록 조회 메서드 호출
// 		Page<SubscriptionLikeDTO> result = subscriptionLikeService.getUserLikedSubscriptions(
// 			page, size, userDetails);
//
// 		// Then: 찜한 청약 목록이 반환됨
// 		assertThat(result).isNotNull();
// 		assertThat(result.getContent()).hasSize(1);
// 		assertThat(result.getContent().get(0).getHouseNm()).isEqualTo("테스트아파트");
// 		assertThat(result.getContent().get(0).getUserEmail()).isEqualTo(testUser.getEmail());
// 		then(subscriptionLikeRepository).should().findByUserEmailAndDeletedAtIsNull(
// 			testUser.getEmail(), pageable);
// 	}
//
// 	@Test
// 	@DisplayName("사용자 찜한 청약 목록 조회 테스트 - 찜한 청약 없음")
// 	void givenUserWithNoLikedSubscriptions_whenGetUserLikedSubscriptions_thenReturnEmptyList() {
// 		// Given: 찜한 청약이 없는 사용자
// 		int page = 0;
// 		int size = 10;
// 		Pageable pageable = PageRequest.of(page, size);
//
// 		Page<SubscriptionLike> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);
//
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.findByUserEmailAndDeletedAtIsNull(
// 			testUser.getEmail(), pageable))
// 			.willReturn(emptyPage);
//
// 		// When: 사용자 찜한 청약 목록 조회 메서드 호출
// 		Page<SubscriptionLikeDTO> result = subscriptionLikeService.getUserLikedSubscriptions(
// 			page, size, userDetails);
//
// 		// Then: 빈 목록이 반환됨
// 		assertThat(result).isNotNull();
// 		assertThat(result.getContent()).isEmpty();
// 		assertThat(result.getTotalElements()).isEqualTo(0);
// 	}
//
// 	@Test
// 	@DisplayName("찜한 청약 개수 조회 성공 테스트")
// 	void givenUserWithLikedSubscriptions_whenGetLikedSubscriptionCount_thenReturnCount() {
// 		// Given: 찜한 청약이 3개 있는 사용자
// 		long expectedCount = 3L;
//
// 		given(userRepository.findByEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(Optional.of(testUser));
// 		given(subscriptionLikeRepository.countByUserEmailAndDeletedAtIsNull(testUser.getEmail()))
// 			.willReturn(expectedCount);
//
// 		// When: 찜한 청약 개수 조회 메서드 호출
// 		long result = subscriptionLikeService.getLikedSubscriptionCount(userDetails);
//
// 		// Then: 정확한 개수가 반환됨
// 		assertThat(result).isEqualTo(expectedCount);
// 		then(subscriptionLikeRepository).should().countByUserEmailAndDeletedAtIsNull(testUser.getEmail());
// 	}
//
// 	@Test
// 	@DisplayName("찜한 청약 여부 확인 성공 테스트 - 찜한 경우")
// 	void givenLikedSubscription_whenIsSubscriptionLiked_thenReturnTrue() {
// 		// Given: 사용자가 찜한 청약
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(subscriptionLikeRepository.findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail()))
// 			.willReturn(Optional.of(testSubscriptionLike));
//
// 		// When: 찜한 청약 여부 확인 메서드 호출
// 		boolean result = subscriptionLikeService.isSubscriptionLiked(subscriptionId, userDetails);
//
// 		// Then: true가 반환됨
// 		assertThat(result).isTrue();
// 	}
//
// 	@Test
// 	@DisplayName("찜한 청약 여부 확인 성공 테스트 - 찜하지 않은 경우")
// 	void givenNotLikedSubscription_whenIsSubscriptionLiked_thenReturnFalse() {
// 		// Given: 사용자가 찜하지 않은 청약
// 		Long subscriptionId = 1L;
//
// 		given(subscriptionInfoRepository.findById(subscriptionId))
// 			.willReturn(Optional.of(testSubscriptionInfo));
// 		given(subscriptionLikeRepository.findBySubscriptionInfoAndUserEmailAndDeletedAtIsNull(
// 			testSubscriptionInfo, testUser.getEmail()))
// 			.willReturn(Optional.empty());
//
// 		// When: 찜한 청약 여부 확인 메서드 호출
// 		boolean result = subscriptionLikeService.isSubscriptionLiked(subscriptionId, userDetails);
//
// 		// Then: false가 반환됨
// 		assertThat(result).isFalse();
// 	}
// }