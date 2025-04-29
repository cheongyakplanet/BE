package org.cheonyakplanet.be.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cheonyakplanet.be.application.dto.infra.InfraResponseDTO;
import org.cheonyakplanet.be.application.dto.infra.PublicFacilityDTO;
import org.cheonyakplanet.be.application.dto.infra.SchoolDTO;
import org.cheonyakplanet.be.application.dto.infra.StationDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDetailDTO;
import org.cheonyakplanet.be.application.service.InfoService;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InfoControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private InfoService infoService;

	private UserDetailsImpl userDetails;
	private User user;
	private SubscriptionDTO subscriptionDTO;
	private SubscriptionDetailDTO subscriptionDetailDTO;
	private SubscriptionInfo subscriptionInfo;
	private InfraResponseDTO infraResponseDTO;

	@BeforeEach
	void setUp() {
		// 테스트용 User 객체 생성
		user = new User("test@test.com", "password", UserRoleEnum.USER, "tester");
		userDetails = new UserDetailsImpl(user);

		// SecurityContext 설정
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		// DTO 객체 초기화
		subscriptionDTO = SubscriptionDTO.builder()
			.id(3L)
			.region("서울특별시")
			.city("서초구")
			.district("방배동")
			.houseManageNo("2025000001")
			.houseNm("래미안 원페를라")
			.bsnsMbyNm("방배6구역주택재건축정비사업조합")
			.rceptBgnde(LocalDate.of(2025, 2, 3))
			.rceptEndde(LocalDate.of(2025, 2, 6))
			.build();

		subscriptionDetailDTO = SubscriptionDetailDTO.builder()
			.id(1L)
			.bsnsMbyNm("울산온양발리스타지역주택조합")
			.cnstrctEntrpsNm("(주)유림E&C")
			.region("울산광역시")
			.city("울주군")
			.district("온양읍")
			.detail("발리 440-1번지 일원")
			.houseNm("남울산 노르웨이숲")
			.houseManageNo("2024000750")
			.rceptBgnde(LocalDate.of(2025, 2, 3))
			.rceptEndde(LocalDate.of(2025, 2, 5))
			.latitude("35.4101054673879")
			.longitude("129.299084772191")
			.build();

		// SubscriptionInfo 객체 초기화
		subscriptionInfo = SubscriptionInfo.builder()
			.id(3L)
			.bsnsMbyNm("방배6구역주택재건축정비사업조합")
			.cnstrctEntrpsNm("삼성물산(주)")
			.region("서울특별시")
			.city("서초구")
			.district("방배동")
			.houseNm("래미안 원페를라")
			.houseManageNo("2025000001")
			.rceptBgnde(LocalDate.of(2025, 2, 3))
			.rceptEndde(LocalDate.of(2025, 2, 6))
			.build();

		// InfraResponseDTO 초기화
		StationDTO stationDTO = StationDTO.builder()
			.number("432")
			.name("총신대입구(이수)")
			.line("4호선")
			.operator("서울교통공사")
			.latitude(37.487521)
			.longitude(126.982309)
			.distance(0.62)
			.isTransfer(false)
			.build();

		SchoolDTO schoolDTO = SchoolDTO.builder()
			.schoolId("B000012058")
			.schoolName("서문여자고등학교")
			.category("고등학교")
			.type("사립")
			.address("0")
			.latitude(37.48891973)
			.longitude(126.9845458)
			.distance(0.37)
			.build();

		infraResponseDTO = InfraResponseDTO.builder()
			.stations(List.of(stationDTO))
			.schools(List.of(schoolDTO))
			.build();
	}

	@Test
	@DisplayName("모든 청약 불러오기 테스트")
	void getSubscriptionsTest() throws Exception {
		// given
		List<SubscriptionDTO> content = List.of(subscriptionDTO);
		Page<SubscriptionDTO> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);
		Map<String, Object> response = new HashMap<>();
		response.put("content", content);
		response.put("totalPages", page.getTotalPages());
		response.put("totalElements", page.getTotalElements());
		response.put("currentPage", page.getNumber() + 1);
		response.put("size", page.getSize());

		given(infoService.getSubscriptions(anyInt(), anyInt())).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/info/subscription")
				.param("page", "0")
				.param("size", "10"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.content[0].id").value(3))
			.andExpect(jsonPath("$.data.content[0].region").value("서울특별시"))
			.andExpect(jsonPath("$.data.content[0].city").value("서초구"))
			.andExpect(jsonPath("$.data.content[0].houseNm").value("래미안 원페를라"));
	}

	@Test
	@DisplayName("id로 1건의 청약 물건 조회 테스트")
	void getSubscriptionTest() throws Exception {
		// given
		given(infoService.getSubscriptionById(anyLong())).willReturn(subscriptionDetailDTO);

		// when & then
		mockMvc.perform(get("/api/info/subscription/{id}", 1L))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.bsnsMbyNm").value("울산온양발리스타지역주택조합"))
			.andExpect(jsonPath("$.data.region").value("울산광역시"))
			.andExpect(jsonPath("$.data.city").value("울주군"))
			.andExpect(jsonPath("$.data.latitude").value("35.4101054673879"))
			.andExpect(jsonPath("$.data.longitude").value("129.299084772191"));
	}

	@Test
	@DisplayName("지역으로 청약 검색 테스트")
	void getSubscriptionsByRegionTest() throws Exception {
		// given
		List<SubscriptionInfo> subscriptions = List.of(subscriptionInfo);
		given(infoService.getSubscriptionsByRegion(anyString(), anyString())).willReturn(subscriptions);

		// when & then
		mockMvc.perform(get("/api/info/subscription/list")
				.param("region", "서울특별시")
				.param("city", "서초구"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].id").value(3))
			.andExpect(jsonPath("$.data[0].region").value("서울특별시"))
			.andExpect(jsonPath("$.data[0].city").value("서초구"))
			.andExpect(jsonPath("$.data[0].houseNm").value("래미안 원페를라"));
	}

	@Test
	@DisplayName("청약 물건의 주변 인프라 조회 테스트")
	void getSubscriptionDetailInfraTest() throws Exception {
		// given
		given(infoService.getNearbyInfrastructure(anyLong())).willReturn(infraResponseDTO);

		// when & then
		mockMvc.perform(get("/api/info/subscription/{id}/detail/infra", 1L))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.stations[0].number").value("432"))
			.andExpect(jsonPath("$.data.stations[0].name").value("총신대입구(이수)"))
			.andExpect(jsonPath("$.data.stations[0].distance").value(0.62))
			.andExpect(jsonPath("$.data.schools[0].schoolId").value("B000012058"))
			.andExpect(jsonPath("$.data.schools[0].schoolName").value("서문여자고등학교"))
			.andExpect(jsonPath("$.data.schools[0].distance").value(0.37));
	}

	@Test
	@DisplayName("청약 물건의 주변 공공시설 조회 테스트")
	void getSubscriptionDetailFacilitiesTest() throws Exception {
		// given
		List<PublicFacilityDTO> facilities = Arrays.asList(
			PublicFacilityDTO.builder()
				.dgmNm("서문여자중,고등학교")
				.longitude(126.9850997358138)
				.latitude(37.48889848416472)
				.build(),
			PublicFacilityDTO.builder()
				.dgmNm("사회복지시설")
				.longitude(126.9877484286745)
				.latitude(37.48983331692146)
				.build()
		);

		given(infoService.getNearbyPublicFacility(anyLong())).willReturn(facilities);

		// when & then
		mockMvc.perform(get("/api/info/subscription/{id}/detail/facilities", 1L))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].dgmNm").value("서문여자중,고등학교"))
			.andExpect(jsonPath("$.data[0].longitude").value(126.9850997358138))
			.andExpect(jsonPath("$.data[0].latitude").value(37.48889848416472))
			.andExpect(jsonPath("$.data[1].dgmNm").value("사회복지시설"));
	}

	@Test
	@DisplayName("대한민국의 특별시,도 리스트 조회 테스트")
	void getRegionListTest() throws Exception {
		// given
		List<String> regions = Arrays.asList(
			"서울특별시", "부산광역시", "대구광역시", "인천광역시",
			"광주광역시", "대전광역시", "울산광역시", "경기도"
		);
		given(infoService.getReginList()).willReturn(regions);

		// when & then
		mockMvc.perform(get("/api/info/subscription/regionlist"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0]").value("서울특별시"))
			.andExpect(jsonPath("$.data[7]").value("경기도"));
	}

	@Test
	@DisplayName("대한민국의 군,구 리스트 조회 테스트")
	void getCityListTest() throws Exception {
		// given
		List<String> cities = Arrays.asList(
			"종로구", "중구", "용산구", "성동구", "광진구",
			"동대문구", "중랑구", "성북구", "강북구", "도봉구"
		);
		given(infoService.getCityList(anyString())).willReturn(cities);

		// when & then
		mockMvc.perform(get("/api/info/subscription/citylist")
				.param("region", "서울특별시"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0]").value("종로구"))
			.andExpect(jsonPath("$.data[2]").value("용산구"));
	}

	@Test
	@DisplayName("나의 관심지역 청약 리스트 조회 테스트")
	void getMySubscriptionsTest() throws Exception {
		// given
		List<SubscriptionDTO> mySubscriptions = Arrays.asList(
			SubscriptionDTO.builder()
				.id(3L)
				.region("서울특별시")
				.city("서초구")
				.district("방배동")
				.houseManageNo("2025000001")
				.houseNm("래미안 원페를라")
				.rceptBgnde(LocalDate.of(2025, 2, 3))
				.rceptEndde(LocalDate.of(2025, 2, 6))
				.build(),
			SubscriptionDTO.builder()
				.id(40L)
				.region("서울특별시")
				.city("서초구")
				.district("방배동")
				.houseManageNo("2024000672")
				.houseNm("아크로 리츠카운티")
				.rceptBgnde(LocalDate.of(2024, 12, 9))
				.rceptEndde(LocalDate.of(2024, 12, 12))
				.build()
		);

		given(infoService.getMySubscriptions(any(UserDetailsImpl.class))).willReturn(mySubscriptions);

		// when & then
		mockMvc.perform(get("/api/info/subscription/mysubscriptions")
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data[0].id").value(3))
			.andExpect(jsonPath("$.data[0].houseNm").value("래미안 원페를라"))
			.andExpect(jsonPath("$.data[1].id").value(40))
			.andExpect(jsonPath("$.data[1].houseNm").value("아크로 리츠카운티"));
	}
}