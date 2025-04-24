package org.cheonyakplanet.be.presentation.controller;

import java.util.List;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.infra.InfraResponseDTO;
import org.cheonyakplanet.be.application.dto.infra.PublicFacilityDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionLikeDTO;
import org.cheonyakplanet.be.application.service.InfoService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/info")
@RequiredArgsConstructor
public class InfoController {

	private final InfoService infoService;

	/**
	 * 모든 청약 불러오기
	 * @param page
	 * @param size
	 * @return
	 */
	@GetMapping("/subscription")
	@Operation(summary = "모든 청약 불러오기", description = "간단한 정보만 제공, 마감일 순으로 정렬",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					  {
					  "status": "success",
					  "data": [
					    {
					      "id": 3,
					      "region": "서울특별시",
					      "city": "서초구",
					      "district": "방배동",
					      "houseManageNo": "2025000001",
					      "houseNm": "래미안 원페를라",
					      "bsnsMbyNm": "방배6구역주택재건축정비사업조합",
					      "houseSecdNm": null,
					      "rceptBgnde": "2025-02-03",
					      "rceptEndde": "2025-02-06",
					      "totSuplyHshldco": null
					    },
					    {}
					  ]
					}"""))
			)
		}
	)
	public ResponseEntity<?> getSubscriptions(@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "10") int size) {
		return ResponseEntity.ok(new ApiResponse<>("success", infoService.getSubscriptions(page, size)));
	}

	/**
	 * 1건의 청약 정보를 불러오기
	 *
	 * @return
	 */
	@GetMapping("/subscription/{id}")
	@Operation(summary = "id로 1건의 청약 물건 조회", description = "요소 클릭시 사용",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					 {
					 "status": "success",
					  "data": {
					    "id": 1,
					    "bsnsMbyNm": "울산온양발리스타지역주택조합",
					    "cnstrctEntrpsNm": "(주)유림E&C",
					    "cntrctCnclsBgnde": "2025-02-24",
					    "cntrctCnclsEndde": "2025-02-26",
					    "gnrlRnk1CrspareaEndde": "2025-02-04",
					    "gnrlRnk1CrspareaRcptde": "2025-02-04",
					    "gnrlRnk1EtcAreaEndde": "2025-02-04",
					    "gnrlRnk1EtcAreaRcptde": "2025-02-04",
					    "gnrlRnk1EtcGgEndde": null,
					    "gnrlRnk1EtcGgRcptde": null,
					    "gnrlRnk2CrspareaEndde": null,
					    "gnrlRnk2CrspareaRcptde": null,
					    "gnrlRnk2EtcAreaEndde": null,
					    "gnrlRnk2EtcAreaRcptde": null,
					    "gnrlRnk2EtcGgEndde": null,
					    "gnrlRnk2EtcGgRcptde": null,
					    "hmpgAdres": null,
					    "houseDtlSecd": "01",
					    "houseDtlSecdNm": "민영",
					    "houseManageNo": "2024000750",
					    "houseNm": "남울산 노르웨이숲",
					    "houseSecd": null,
					    "houseSecdNm": null,
					    "hssplyAdres": "울산광역시 울주군 온양읍 발리 440-1번지 일원",
					    "hssplyZip": "44976",
					    "imprmnBsnsAt": null,
					    "lrsclBldlndAt": null,
					    "mdatTrgetAreaSecd": null,
					    "mdhsTelno": null,
					    "mvnPrearngeYm": null,
					    "nplnPrvoprPublicHouseAt": null,
					    "parcprcUlsAt": null,
					    "pblancNo": null,
					    "pblancUrl": "https://www.applyhome.co.kr/ai/aia/selectAPTLttotPblancDetail.do?houseManageNo=2024000750&pblancNo=2024000750",
					    "przwnerPresnatnDe": "2025-02-11",
					    "publicHouseEarthAt": null,
					    "publicHouseSpclwApplcAt": null,
					    "rceptBgnde": "2025-02-03",
					    "rceptEndde": "2025-02-05",
					    "rcritPblancDe": null,
					    "rentSecd": "0",
					    "rentSecdNm": "분양주택",
					    "specltRdnEarthAt": null,
					    "spsplyRceptBgnde": "2025-02-03",
					    "spsplyRceptEndde": "2025-02-03",
					    "subscrptAreaCode": "680",
					    "subscrptAreaCodeNm": "울산",
					    "totSuplyHshldco": null,
					    "region": "울산광역시",
					    "city": "울주군",
					    "district": "온양읍 ",
					    "detail": "발리 440-1번지 일원",
					    "latitude": "35.4101054673879",
					    "longitude": "129.299084772191",
					    "priceInfo": [
					      {
					        "id": 1,
					        "houseManageNo": "2024000750",
					        "housingType": "059.9957A",
					        "supplyPrice": 34100,
					        "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					        "moveInMonth": "2026.07"
					      },
					      {
					        "id": 2,
					        "houseManageNo": "2024000750",
					        "housingType": "059.9734B",
					        "supplyPrice": 33700,
					        "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					        "moveInMonth": "2026.07"
					      },
					      {
					        "id": 3,
					        "houseManageNo": "2024000750",
					        "housingType": "074.5847A",
					        "supplyPrice": 41800,
					        "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					        "moveInMonth": "2026.07"
					      },
					      {
					        "id": 4,
					        "houseManageNo": "2024000750",
					        "housingType": "074.9026B",
					        "supplyPrice": 41100,
					        "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					        "moveInMonth": "2026.07"
					      },
					      {
					        "id": 5,
					        "houseManageNo": "2024000750",
					        "housingType": "084.9350A",
					        "supplyPrice": 48900,
					        "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					        "moveInMonth": "2026.07"
					      },
					      {
					        "id": 6,
					        "houseManageNo": "2024000750",
					        "housingType": "084.4955B",
					        "supplyPrice": 47500,
					        "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					        "moveInMonth": "2026.07"
					      }
					    ],
					    "specialSupplyTarget": [
					      {
					        "id": 1,
					        "houseManageNo": "2024000750",
					        "housingType": "059.9957A",
					        "supplyCountMultichild": 9,
					        "supplyCountNewlywed": 12,
					        "supplyCountFirst": 6,
					        "supplyCountYouth": 0,
					        "supplyCountElderly": 2,
					        "supplyCountNewborn": 0,
					        "supplyCountInstitutionRecommend": 6,
					        "supplyCountPreviousInstitution": 0,
					        "supplyCountOthers": 0,
					        "supplyCountTotal": 35
					      },
					      {
					        "id": 2,
					        "houseManageNo": "2024000750",
					        "housingType": "059.9734B",
					        "supplyCountMultichild": 11,
					        "supplyCountNewlywed": 15,
					        "supplyCountFirst": 8,
					        "supplyCountYouth": 0,
					        "supplyCountElderly": 2,
					        "supplyCountNewborn": 0,
					        "supplyCountInstitutionRecommend": 8,
					        "supplyCountPreviousInstitution": 0,
					        "supplyCountOthers": 0,
					        "supplyCountTotal": 44
					      },
					      {
					        "id": 3,
					        "houseManageNo": "2024000750",
					        "housingType": "074.5847A",
					        "supplyCountMultichild": 7,
					        "supplyCountNewlywed": 10,
					        "supplyCountFirst": 5,
					        "supplyCountYouth": 0,
					        "supplyCountElderly": 2,
					        "supplyCountNewborn": 0,
					        "supplyCountInstitutionRecommend": 5,
					        "supplyCountPreviousInstitution": 0,
					        "supplyCountOthers": 0,
					        "supplyCountTotal": 29
					      },
					      {
					        "id": 4,
					        "houseManageNo": "2024000750",
					        "housingType": "074.9026B",
					        "supplyCountMultichild": 7,
					        "supplyCountNewlywed": 9,
					        "supplyCountFirst": 5,
					        "supplyCountYouth": 0,
					        "supplyCountElderly": 1,
					        "supplyCountNewborn": 0,
					        "supplyCountInstitutionRecommend": 5,
					        "supplyCountPreviousInstitution": 0,
					        "supplyCountOthers": 0,
					        "supplyCountTotal": 27
					      },
					      {
					        "id": 5,
					        "houseManageNo": "2024000750",
					        "housingType": "084.9350A",
					        "supplyCountMultichild": 9,
					        "supplyCountNewlywed": 12,
					        "supplyCountFirst": 6,
					        "supplyCountYouth": 0,
					        "supplyCountElderly": 2,
					        "supplyCountNewborn": 0,
					        "supplyCountInstitutionRecommend": 6,
					        "supplyCountPreviousInstitution": 0,
					        "supplyCountOthers": 0,
					        "supplyCountTotal": 35
					      },
					      {
					        "id": 6,
					        "houseManageNo": "2024000750",
					        "housingType": "084.4955B",
					        "supplyCountMultichild": 7,
					        "supplyCountNewlywed": 9,
					        "supplyCountFirst": 4,
					        "supplyCountYouth": 0,
					        "supplyCountElderly": 1,
					        "supplyCountNewborn": 0,
					        "supplyCountInstitutionRecommend": 4,
					        "supplyCountPreviousInstitution": 0,
					        "supplyCountOthers": 0,
					        "supplyCountTotal": 25
					      }
					    ],
					    "supplyTarget": [
					      {
					        "id": 1,
					        "houseManageNo": "2024000750",
					        "housingCategory": "민영",
					        "housingType": "059.9957A",
					        "supplyArea": 81.0764,
					        "supplyCountNormal": 24,
					        "supplyCountSpecial": 35,
					        "supplyCountTotal": 59,
					        "houseManageNoDetail": "2024000750(01)"
					      },
					      {
					        "id": 2,
					        "houseManageNo": "2024000750",
					        "housingCategory": "민영",
					        "housingType": "059.9734B",
					        "supplyArea": 81.7591,
					        "supplyCountNormal": 32,
					        "supplyCountSpecial": 44,
					        "supplyCountTotal": 76,
					        "houseManageNoDetail": "2024000750(02)"
					      },
					      {
					        "id": 3,
					        "houseManageNo": "2024000750",
					        "housingCategory": "민영",
					        "housingType": "074.5847A",
					        "supplyArea": 99.6395,
					        "supplyCountNormal": 20,
					        "supplyCountSpecial": 29,
					        "supplyCountTotal": 49,
					        "houseManageNoDetail": "2024000750(03)"
					      },
					      {
					        "id": 4,
					        "houseManageNo": "2024000750",
					        "housingCategory": "민영",
					        "housingType": "074.9026B",
					        "supplyArea": 100.9108,
					        "supplyCountNormal": 19,
					        "supplyCountSpecial": 27,
					        "supplyCountTotal": 46,
					        "houseManageNoDetail": "2024000750(04)"
					      },
					      {
					        "id": 5,
					        "houseManageNo": "2024000750",
					        "housingCategory": "민영",
					        "housingType": "084.9350A",
					        "supplyArea": 113.0813,
					        "supplyCountNormal": 24,
					        "supplyCountSpecial": 35,
					        "supplyCountTotal": 59,
					        "houseManageNoDetail": "2024000750(05)"
					      },
					      {
					        "id": 6,
					        "houseManageNo": "2024000750",
					        "housingCategory": "민영",
					        "housingType": "084.4955B",
					        "supplyArea": 113.0982,
					        "supplyCountNormal": 18,
					        "supplyCountSpecial": 25,
					        "supplyCountTotal": 43,
					        "houseManageNoDetail": "2024000750(06)"
					      }
					    ]
					  }
					}
					"""))
			)
		})
	public ResponseEntity<ApiResponse> getSubscription(@PathVariable("id") Long id) {
		Object result = infoService.getSubscriptionById(id);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	/**
	 * 특정 지역의 청약물건 검색
	 *
	 * @param region
	 * @param city
	 * @return
	 */
	@GetMapping("/subscription/list")
	@Operation(summary = "지역으로 청약 검색", description = "시, 도 list에서 선택",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "status": "success",
					  "data": [
					    {
					      "createdAt": null,
					      "createdBy": null,
					      "updatedAt": null,
					      "updatedBy": null,
					      "deletedAt": null,
					      "deletedBy": null,
					      "id": 3,
					      "bsnsMbyNm": "방배6구역주택재건축정비사업조합",
					      "cnstrctEntrpsNm": "삼성물산(주)",
					      "cntrctCnclsBgnde": "2025-02-24",
					      "cntrctCnclsEndde": "2025-02-27",
					      "gnrlRnk1CrspareaEndde": "2025-02-04",
					      "gnrlRnk1CrspareaRcptde": "2025-02-04",
					      "gnrlRnk1EtcAreaEndde": "2025-02-05",
					      "gnrlRnk1EtcAreaRcptde": "2025-02-05",
					      "gnrlRnk1EtcGgEndde": null,
					      "gnrlRnk1EtcGgRcptde": null,
					      "gnrlRnk2CrspareaEndde": null,
					      "gnrlRnk2CrspareaRcptde": null,
					      "gnrlRnk2EtcAreaEndde": null,
					      "gnrlRnk2EtcAreaRcptde": null,
					      "gnrlRnk2EtcGgEndde": null,
					      "gnrlRnk2EtcGgRcptde": null,
					      "hmpgAdres": null,
					      "houseDtlSecd": "01",
					      "houseDtlSecdNm": "민영",
					      "houseManageNo": "2025000001",
					      "houseNm": "래미안 원페를라",
					      "houseSecd": null,
					      "houseSecdNm": null,
					      "hssplyAdres": "서울특별시 서초구 방배동 818-14번지 일대",
					      "hssplyZip": "06562",
					      "imprmnBsnsAt": null,
					      "lrsclBldlndAt": null,
					      "mdatTrgetAreaSecd": null,
					      "mdhsTelno": null,
					      "mvnPrearngeYm": null,
					      "nplnPrvoprPublicHouseAt": null,
					      "parcprcUlsAt": null,
					      "pblancNo": null,
					      "pblancUrl": "https://www.applyhome.co.kr/ai/aia/selectAPTLttotPblancDetail.do?houseManageNo=2025000001&pblancNo=2025000001",
					      "przwnerPresnatnDe": "2025-02-12",
					      "publicHouseEarthAt": null,
					      "publicHouseSpclwApplcAt": null,
					      "rceptBgnde": "2025-02-03",
					      "rceptEndde": "2025-02-06",
					      "rcritPblancDe": null,
					      "rentSecd": "0",
					      "rentSecdNm": "분양주택",
					      "specltRdnEarthAt": null,
					      "spsplyRceptBgnde": "2025-02-03",
					      "spsplyRceptEndde": "2025-02-03",
					      "subscrptAreaCode": "100",
					      "subscrptAreaCodeNm": "서울",
					      "totSuplyHshldco": null,
					      "region": "서울특별시",
					      "city": "서초구",
					      "district": "방배동 ",
					      "detail": "818-14번지 일대",
					      "subscriptionPriceInfo": [
					        {
					          "createdAt": null,
					          "createdBy": null,
					          "updatedAt": null,
					          "updatedBy": null,
					          "deletedAt": null,
					          "deletedBy": null,
					          "id": 12,
					          "subscriptionInfoId": 3,
					          "houseManageNo": "2025000001",
					          "housingType": "059.9800A",
					          "supplyPrice": 172470,
					          "secondPriorityPayment": "청약통장으로 청약(청약금 없음)",
					          "moveInMonth": "2025.11"
					        },{}] } ] }
					"""))
			)
		})
	public ResponseEntity<?> getSubscriptionsByRegion(
		@Parameter(description = "지역", example = "서울특별시")
		@RequestParam("region") String region,
		@Parameter(description = "구", example = "서초구")
		@RequestParam("city") String city) {
		Object subscriptions = infoService.getSubscriptionsByRegion(region, city);

		return ResponseEntity.ok(new ApiResponse("success", subscriptions));
	}

	/**
	 * 청약 물건의 주변 인프라를 불러오는 메서드
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/subscription/{id}/detail/infra")
	@Operation(summary = "청약 물건의 주변 인프라", description = "반경 1km내의 학교와 역을 조회",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "status": "success",
					  "data": {
					    "stations": [
					      {
					        "number": "432",
					        "name": "총신대입구(이수)",
					        "line": "4호선",
					        "operator": "서울교통공사",
					        "latitude": 37.487521,
					        "longitude": 126.982309,
					        "distance": 0.62,
					        "transfer": false
					      }
					    ],
					    "schools": [
					      {
					        "schoolId": "B000012058",
					        "schoolName": "서문여자고등학교",
					        "category": "고등학교",
					        "type": "사립",
					        "address": "0",
					        "latitude": 37.48891973,
					        "longitude": 126.9845458,
					        "distance": 0.37
					      }
					    ]
					  }
					}
					"""))
			)
		})
	public ResponseEntity<?> getSubscriptionDetailInfra(
		@PathVariable(name = "id") Long id) {
		InfraResponseDTO response = infoService.getNearbyInfrastructure(id);
		return ResponseEntity.ok(new ApiResponse("success", response));
	}

	/**
	 * 청약의 주변 공공시설을 불러오는 메서드
	 * @param id
	 * @return
	 */
	@GetMapping("/subscription/{id}/detail/facilities")
	@Operation(summary = "청약 물건의 주변 공공시설", description = "반경 1km내의 주변 공공시설 조회",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "status": "success",
					  "data": [
					       {
					         "dgmNm": "서문여자중,고등학교",
					         "longitude": 126.9850997358138,
					         "latitude": 37.48889848416472
					       },
					       {
					         "dgmNm": "사회복지시설",
					         "longitude": 126.9877484286745,
					         "latitude": 37.48983331692146
					       },
					       {
					         "dgmNm": "사당제2동사무소",
					         "longitude": 126.97757856778836,
					         "latitude": 37.48838552736948
					       }
					       ]
					}
					
					"""))
			)
		})
	public ResponseEntity<?> getSubscriptionDetailFacilities(
		@PathVariable(name = "id") Long id) {
		List<PublicFacilityDTO> response = infoService.getNearbyPublicFacility(id);
		return ResponseEntity.ok(new ApiResponse("success", response));
	}

	/**
	 * 대한민국의 특별시,도 리스트
	 *
	 * @return
	 */
	@GetMapping("/subscription/regionlist")
	@Operation(summary = "대한민국의 특별시,도 리스트", description = "입력을 위한 보기",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """ 
					{
					  "status": "success",
					  "data": [
					    "서울특별시",
					    "부산광역시",
					    "대구광역시",
					    "인천광역시",
					    "광주광역시",
					    "대전광역시",
					    "울산광역시",
					    "경기도",
					    "충청북도",
					    "충청남도",
					    "전라남도",
					    "경상북도",
					    "경상남도",
					    "제주특별자치도",
					    "강원특별자치도",
					    "전북특별자치도"
					  ]
					}
					"""))
			)
		})
	public ResponseEntity<?> getRegionList() {
		List<?> result = infoService.getReginList();
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	/**
	 * 대한민국의 군, 구 리스트
	 *
	 * @param region
	 * @return
	 */
	@GetMapping("/subscription/citylist")
	@Operation(summary = "대한민국의 군,구 리스트", description = "입력을 위한 보기",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "status": "success",
					  "data": [
					    "종로구",
					    "중구",
					    "용산구",
					    "성동구",
					    "광진구",
					    "동대문구",
					    "중랑구",
					    "성북구",
					    "강북구",
					    "도봉구",
					    "노원구",
					    "은평구",
					    "서대문구",
					    "마포구",
					    "양천구",
					    "강서구",
					    "구로구",
					    "금천구",
					    "영등포구",
					    "동작구",
					    "관악구",
					    "서초구",
					    "강남구",
					    "송파구",
					    "강동구"
					  ]
					} 
					"""))
			)
		})
	public ResponseEntity<?> getCityList(@RequestParam("region") String region) {
		List<?> result = infoService.getCityList(region);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	/**
	 * 나의 관심지역 청약 리스트
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/subscription/mysubscriptions")
	@Operation(summary = "나의 관심지역 청약 리스트", description = "관심지역이 제대로 등록되어 있어야 함",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "status": "success",
					  "data": [
					    {
					      "id": 3,
					      "region": "서울특별시",
					      "city": "서초구",
					      "district": "방배동 ",
					      "houseManageNo": "2025000001",
					      "houseNm": "래미안 원페를라",
					      "bsnsMbyNm": "방배6구역주택재건축정비사업조합",
					      "houseSecdNm": null,
					      "rceptBgnde": "2025-02-03",
					      "rceptEndde": "2025-02-06",
					      "totSuplyHshldco": null
					    },
					    {
					      "id": 40,
					      "region": "서울특별시",
					      "city": "서초구",
					      "district": "방배동 ",
					      "houseManageNo": "2024000672",
					      "houseNm": "아크로 리츠카운티",
					      "bsnsMbyNm": "방배삼익아파트 주택재건축정비사업조합",
					      "houseSecdNm": null,
					      "rceptBgnde": "2024-12-09",
					      "rceptEndde": "2024-12-12",
					      "totSuplyHshldco": null
					    }
					  ]
					}
					"""))
			)
		})
	public ResponseEntity<?> getMySubscriptions(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(new ApiResponse<>("success", infoService.getMySubscriptions(userDetails)));
	}

	@Operation(summary = "관심 청약 추가")
	@PostMapping("/subscription/like/{subscriptionId}")
	public ResponseEntity<?> createSubscriptionLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam("subscriptionId") Long id) {
		infoService.createSubscriptionLike(userDetails, id);
		return ResponseEntity.ok(new ApiResponse<>("success", "관심지역 추가 성공"));
	}

	@PatchMapping("/subscription/like/{subscriptionLikeId}")
	public ResponseEntity<?> updateSubscriptionLike(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam("subscriptionLikeId") Long id) {
		infoService.deleteSubscriptionLike(userDetails, id);
		return ResponseEntity.ok(new ApiResponse<>("success", "관심지역 삭제 성공"));
	}

	@Operation(summary = "관심 청약 조회")
	@GetMapping("subscription/like")
	public ResponseEntity<?> getLikeSubscription(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		List<SubscriptionLikeDTO> result = infoService.getLikeSubscription(userDetails);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}
}
