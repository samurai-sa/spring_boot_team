package com.example.eventapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		// パスワードの暗号化クラスを戻り値とする
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	//WebSecurity の設定
	@Override
	public void configure(WebSecurity web) throws Exception {
		// images, js, css フォルダ以下のファイルはアクセス制限の対象外とする
		web
			.debug(false)
			.ignoring()
			.antMatchers("/images/**", "/js/**", "/css/**");
	}
	
	// HttpSecurity の設定
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.mvcMatchers("/store/signUp", "/store/login").permitAll()
				.anyRequest().authenticated()
			.and()
				.formLogin()
				.loginPage("/store/login")
				.loginProcessingUrl("/store/login")
				.usernameParameter("id")
				.passwordParameter("password")
				.defaultSuccessUrl("/event/", true)
				.failureUrl("/store/login?error")
				.permitAll()
			.and()
				.logout()
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessUrl("/store/login");
	}
	
}
