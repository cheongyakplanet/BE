package org.cheonyakplanet.be.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KakaoController {

	@GetMapping("loginpage")
	public String loginpage() {
		return "login";
	}
}
