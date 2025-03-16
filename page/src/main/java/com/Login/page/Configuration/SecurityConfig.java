package com.Login.page.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/Kayıt","/UyeGiris", "/SifreYenile","/SifreYenilemeKod", "/KodDogrulama", "/SifreGuncelle","/Dogrulama",
                		"/TekrarGonder","/SifreKodYenileme","/AnasayfaGiris"
                		).permitAll()
                .anyRequest().authenticated()
                
            )
            
            .formLogin(form -> form
                .loginPage("/Giris") // Kendi login sayfan
                .loginProcessingUrl("/AnasayfaGiris") // Kendi login işlem URL'in
                .defaultSuccessUrl("/Anasayfa", true)
                .failureUrl("/Giris?error=true") // Başarısız giriş sonrası yönlendirme
                .permitAll()
            )
           
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/Giris")
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}