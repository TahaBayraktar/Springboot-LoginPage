package com.Login.page.Controller;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Login.page.Entity.User;
import com.Login.page.Repository.UserRepository;
import com.Login.page.Service.EmailService;
import com.Login.page.Service.PasswordService;
@Controller
public class LoginControllerPost {
	
	int dogrulamakod;
	int sifredogrulamakod;
	
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmailService emailService;
	 @Autowired
	 private PasswordService passwordService;
	 User newUser=new User();
	
	//Kullanıcı bilgilerini kontrol edip anasayfaya gönderen metod
	@PostMapping("/AnasayfaGiris")
	public String KontrolLogin(Model model,
			@RequestParam("username") String username, @RequestParam("password") String password) {
		User user = userRepository.findByUsername(username.toLowerCase());
		if (user != null && passwordService.verifyPassword(password, user.getPassword())) {
	        // Eğer şifre doğruysa, anasayfaya yönlendir
	        return "homepage";
	    } 
		 model.addAttribute("error", "Kullanıcı adı veya şifre hatalı!");
		 model.addAttribute("username", username);
		 
		return "login";
	}
	
	//Kayıt oldaki bilgileri göndermek için metod 
	@PostMapping("/UyeGiris")
	public String GirisYonlendir(@RequestParam("username") String username, 
								@RequestParam("password") String password,
								@RequestParam("name") String name,
								@RequestParam("surname") String surname,
								@RequestParam("email") String email,
								@RequestParam("number") String number,
								@RequestParam("confirmPassword") String confirmPassword,
								Model model
								) {
		
		model.addAttribute("username", username);
		model.addAttribute("password", password);
		model.addAttribute("name", name);
		model.addAttribute("surname", surname);
		model.addAttribute("email", email);
		model.addAttribute("number", number);
		model.addAttribute("confirmPassword", confirmPassword);
		
		
		int nameUzunluk=50;
		if(nameUzunluk<name.length() || name.length()<=2) {
			model.addAttribute("error", "İsminiz 50 karakterden fazla ve 2 karakterden az olamaz");
			 return "register";
		}
		int surnameUzunluk=30;
		if(surnameUzunluk<surname.length() || surname.length()<=2) {
			model.addAttribute("error", "Soyadınız 30 karakterden fazla ve 2 karakterden az olamaz");
			 return "register";
		}
		int usernameUzunluk=30;
		if(usernameUzunluk<username.length() || username.length()<=2) {
			model.addAttribute("error", "Kullanıcı adı 30 karakterden fazla ve 2 karakterden az olamaz");
			 return "register";
		}
		int emailUzunluk=50;
		if(emailUzunluk<email.length() || email.length()<=10) {
			model.addAttribute("error", "Email 50 karakterden fazla ve 10 karakterden az olamaz");
			 return "register";
		}
		int numberUzunluk=11;
		if(numberUzunluk!=number.length()) {
			 model.addAttribute("error", "Telefon numarası 11 haneli olmalıdır.");
			 return "register";
		}
		int passwordUzunluk=30;
		if(passwordUzunluk<password.length()) {
			 model.addAttribute("error", "Şifreniz 30 karakterden fazla olamaz.");
			 return "register";
		}
		List<User> us=userRepository.findAll();
		for(User users:us) {
			if(users.getUsername().equals(username)) {
				 model.addAttribute("error", "Bu kullanıcı adı daha önceden alınmış");
				 return "register";
			}
			
			
			if(users.getEmail().equals(email)) {
				 model.addAttribute("error", "Bu email zaten kullanılıyor.");
				 return "register";
			}
			
			if(users.getNumber().equals(number)) {
				 model.addAttribute("error", "Bu telefon numarası zaten kullanılıyor.");
				 return "register";
			}
			
		}
		
		if(!password.equals(confirmPassword)) {
			 model.addAttribute("error", "Şifreler eşleşmedi.");
			 return "register";
		}
		
		if(!PasswordControl(password)) {
			model.addAttribute("error", "Lütfen en az 5 haneli bir büyük harf bir özel karakter içeren şifre giriniz.");
			return "register";
		}
		if (emailService == null) {
		    System.out.println("emailService null olduğu için mail gönderilemiyor!");
		}
		
		
		model.addAttribute("email", email);
		surname= surname.substring(0, 1).toUpperCase()+ surname.substring(1).toLowerCase();
		name=formatFullName(name);
		int kod=dogrulamaKodu();
		String body=String.format("""
	            Merhaba,

	            Hesabınızı doğrulamak için aşağıdaki doğrulama kodunu kullanabilirsiniz:

	            👉 KODUNUZ: %d 👈

	            Eğer bu işlemi siz yapmadıysanız, lütfen bizimle iletişime geçin.

	            Saygılar,
	            Thbayraktar Destek Ekibi
	        """, kod);
		
		dogrulamakod=kod;
		emailService.sendVerificationEmail(email, "Hesap Güvenliği İçin Onay Kodunuz",body);
		
		String hashedPassword = passwordService.hashPassword(password);
		
		
		 
		 
		 newUser.setEmail(email.toLowerCase());
		 newUser.setName(name);
		 newUser.setNumber(number);
		 newUser.setPassword(hashedPassword);
		 newUser.setSurname(surname);
		 newUser.setUsername(username.toLowerCase());
		 
		
		
		
		
		return "verification";
	}
	
	
	//Kod doğrulama işlemi yapan metod
	@PostMapping("/Dogrulama")
	public String DogrulamaKontrol(Model model,
								   @RequestParam("DogrulamaKodu") int DogrulamaKodu,
								   @RequestParam("email") String email
								   ) {
		if(DogrulamaKodu!=dogrulamakod) {
			model.addAttribute("error", "Doğrulama kodu hatalı.");
			model.addAttribute("DogrulamaKodu",DogrulamaKodu );
			model.addAttribute("email", email);
			
			return "verification";
		}
		userRepository.save(newUser);
		emailService.sendVerificationEmail(email, "Hesap İşlemi","Hesabınız başarıyla oluşturuldu artık giriş yapabilirsiniz.");

		return "login";
	}
	//Kodun tekrar gönderilmesini sağlayan method
	@PostMapping("/TekrarGonder")
	public String TekrarKodGonder(@RequestParam("email") String email,
								  Model model) {
		String body="Doğrulama kodunuz:";
		int kod=dogrulamaKodu();
		dogrulamakod=kod;
		emailService.sendVerificationEmail(email, "Email doğrulama kodu", body+"\n"+kod);
		model.addAttribute("email", email);
		return "verification";
	}
	String sifreYenilemeMail;
	@PostMapping("/SifreYenilemeKod")
	public String sifreYenileme(@RequestParam("email") String email,
			  Model model) {
		List<User> us=userRepository.findAll();
	
		for(User users:us) {
			if(users.getEmail().equals(email)) {
				String body="Şifrenizi yenilemek için doğrulama kodunuz:";
				int kod=dogrulamaKodu();
				sifredogrulamakod=kod;
				emailService.sendVerificationEmail(email, "Email doğrulama kodu", body+"\n"+kod);
				model.addAttribute("success", "Doğrulama kodu e-posta adresinize başarıyla gönderildi.");
				model.addAttribute("emailGonderildi", true);
		        model.addAttribute("kodDogru", false);
		       
				sifreYenilemeMail=email;
				return "passwordReset";
				
			}
		}
		model.addAttribute("email", email);
		model.addAttribute("emailGonderildi", false);
		model.addAttribute("error", "Girdiğiniz mail adresi bulunamadı.");
		return "passwordReset";
	}
	@PostMapping("/KodDogrulama")
	public String kodDogrulama(@RequestParam("verificationCode") int dogrulamakod,Model model) {
		if(sifredogrulamakod!=dogrulamakod) {
			 model.addAttribute("error", "Girilen kod hatalı.");
			 model.addAttribute("emailGonderildi", true); // E-posta alanı gizli kalacak
			 model.addAttribute("dogrulamakod", dogrulamakod);
		     model.addAttribute("kodDogru", false);
			
			 return "passwordReset";
		}
		 model.addAttribute("kodDogru", true);
		 model.addAttribute("emailGonderildi", true);
		 
		return "passwordReset";
	}
	@PostMapping("/SifreKodYenileme")
	public String sifreTekrarKod(Model model) {
		String body="Şifrenizi yenilemek için doğrulama kodunuz:";
		int kod=dogrulamaKodu();
		sifredogrulamakod=kod;
		emailService.sendVerificationEmail(sifreYenilemeMail, "Email doğrulama kodu", body+"\n"+kod);
		model.addAttribute("emailGonderildi", true);
        model.addAttribute("kodDogru", false);
		return "passwordReset";
		
	}
	@PostMapping("/SifreGuncelle")
	public String sifreGuncelle(@RequestParam("yeniSifre") String yeniSifre, 
								@RequestParam("sifreTekrar") String sifreTekrar,
								Model model) {
		if(!yeniSifre.equals(sifreTekrar)) {
			 model.addAttribute("error", "Şifreler uyuşmuyor.");
			 model.addAttribute("kodDogru", true);
		      model.addAttribute("emailGonderildi", true);
		      model.addAttribute("yeniSifre", yeniSifre);
		      model.addAttribute("sifreTekrar", sifreTekrar);
			 return "passwordReset";
		}
		if(!PasswordControl(yeniSifre)) {
			 model.addAttribute("kodDogru", true);
		     model.addAttribute("emailGonderildi", true);
		     model.addAttribute("yeniSifre", yeniSifre);
		      model.addAttribute("sifreTekrar", sifreTekrar);
			 model.addAttribute("error", "Lütfen en az 5 haneli bir büyük harf bir özel karakter içeren şifre giriniz.");
			return "passwordReset";
		}
		User us=userRepository.findByEmail(sifreYenilemeMail);
		if(us.getPassword().equals(yeniSifre)) {
			 model.addAttribute("kodDogru", true);
		     model.addAttribute("emailGonderildi", true);
		     model.addAttribute("yeniSifre", yeniSifre);
		      model.addAttribute("sifreTekrar", sifreTekrar);
		     model.addAttribute("error", "Yeni şifreniz eski şifrenizle aynı olamaz.");
		     return "passwordReset";
		}
		String hashedPassword = passwordService.hashPassword(yeniSifre);
		us.setPassword(hashedPassword);
		userRepository.save(us);
		emailService.sendVerificationEmail(sifreYenilemeMail, "Şifreniz başarılı bir şekilde değiştirilmiştir.","\n İyi Günler Dileriz");
		
		return "login";
	}
	
	
	
	//Doğrulama Kodu göndermek için metod
	private int dogrulamaKodu() {
		 SecureRandom random = new SecureRandom();
	        int code = 100000 + random.nextInt(900000); // 100000 ile 999999 arasında sayı üretir
	        return code;
		
	}
	//Şifre Kontrolü yapmak için metod
	private boolean PasswordControl(String password) {
		  if (password.length() < 5) {
	            return false;
	        }
		  // En az bir büyük harf içermeli
	        Pattern upperCasePattern = Pattern.compile("[A-Z]");
	        Matcher upperCaseMatcher = upperCasePattern.matcher(password);
	        if (!upperCaseMatcher.find()) {
	            return false;
	        }
	        // En az bir özel karakter içermeli
	        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*()_+=\\-\\[\\]{};':\"\\\\|,.<>/?]");
	        Matcher specialCharMatcher = specialCharPattern.matcher(password);
	        if (!specialCharMatcher.find()) {
	            return false;
	        }
	        return true;
	}
	private String formatFullName(String name) {
	    if (name == null || name.isEmpty()) {
	        return name; // Eğer boşsa veya null ise direkt döndür
	    }

	    String[] words = name.trim().split("\\s+"); // Birden fazla boşluğu tek boşluğa indir
	    StringBuilder formattedName = new StringBuilder();

	    for (String word : words) {
	        if (!word.isEmpty()) {
	            formattedName.append(Character.toUpperCase(word.charAt(0))) // İlk harfi büyük yap
	                          .append(word.substring(1).toLowerCase()) // Geri kalanını küçük yap
	                          .append(" "); // Kelimeleri ayırmak için boşluk ekle
	        }
	    }

	    return formattedName.toString().trim(); // Sonundaki boşluğu kaldır ve döndür
	}
}
