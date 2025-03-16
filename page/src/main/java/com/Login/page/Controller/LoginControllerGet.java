package com.Login.page.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;



@Controller
public class LoginControllerGet {
	//Anasayfayı gösteren method
	@GetMapping("/Anasayfa")
	public String ShowHomepage() {
		return "homepage";
	}
	
	//Giriş Ekranını gösteren method
	@GetMapping("/Giris")
	public String showLogin(
			){	 
		return "login";
	}
	//Kayıt ekranını gösteren method
	@GetMapping("/Kayıt")
	public String showKayıt() {
		return "register";
	}
	@GetMapping("/SifreYenile")
	public String showSifreYenile(Model model) {
		
	        model.addAttribute("emailGonderildi", false);
		return "passwordReset";
	}
}
