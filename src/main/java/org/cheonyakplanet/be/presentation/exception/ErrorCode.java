package org.cheonyakplanet.be.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 계정
	SIGN000("SIGN000", "로그인이 필요한 서비스입니다."),
	SIGN001("SIGN001", "일치하는 이메일 없음"),
	SIGN002("SIGN002", "중복된 이메일 존재"),
	SIGN003("SIGN003", "관리자 가입 토큰 불일치"),
	SIGN004("SIGN004", "로그인 정보 불일치"),
	SIGN005("SIGN005", "사용자를 찾을 수 없음"),
	SIGN006("SIGN006", "탈퇴한 회원"),

	// JWT
	AUTH001("AUTH001", "유효하지 않은 JWT 서명"),
	AUTH002("AUTH002", "만료된 토큰"),
	AUTH003("AUTH003", "지원되지 않는 토큰"),
	AUTH004("AUTH004", "claim is empty"),
	AUTH005("AUTH005", "유효하지 않은 토큰"),
	AUTH006("AUTH006", "로그아웃된 토큰"),
	AUTH010("AUTH010", "토큰 없음"),

	//community
	COMU001("COMU001", "게시글 없음"),
	COMU002("COMU002", "게시글에 수정 권한 없음"),
	COMU003("COMU003", "이미 반응 함"),

	// Info
	INFO001("INFO001", "해당 아이디의 청약건 없음"),
	INFO002("INFO002", "해당 지역의 청약건 없음"),
	INFO003("INFO003", "위치 정보가 없는 청약건입니다."),

	INFO005("INFO005", "지역 테이블 없음, DB확인"),
	INFO006("INFO006", "관심지역 아님"),
	INFO007("INFO007", "이미 추가한 관심 청약"),

	// Other
	OTHER001("OTHER001", "필터 부분 에러"),

	// Mypage
	USER001("USER001", "사용자를 찾을 수 없습니다."),
	USER002("USER002", "유효하지 않은 입력값입니다."),
	USER003("USER003", "이미 탈퇴한 사용자입니다."),
	USER004("USER004", "회원 탈퇴 시 토큰 삭제에 실패했습니다."),
	LOCATION001("LOC001", "이미 등록된 관심 지역입니다."),
	LOCATION002("LOC002", "최대 5개의 관심 지역만 등록할 수 있습니다."),
	LOCATION003("LOC003", "등록되지 않은 관심 지역입니다."),

	UNKNOWN_ERROR("UNK000", "알 수 없는 에러 발생");

	private final String code;
	private final String message;

}
