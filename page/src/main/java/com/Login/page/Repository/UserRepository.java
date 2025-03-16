package com.Login.page.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Login.page.Entity.User;
@Repository
public interface UserRepository  extends JpaRepository<User,Long>{
	 // E-posta adresine göre kullanıcıyı getir
	User findByEmail(String email);
	 User findByUsername(String username);
}
