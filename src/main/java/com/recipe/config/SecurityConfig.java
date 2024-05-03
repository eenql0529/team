package com.recipe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import com.recipe.handler.LoginSuccessHandler;
import com.recipe.oauth.PrincipalOauth2UserService;




@Configuration //Bean 객체를 싱글톤으로 공유할 수 있게 해준다.
@EnableWebSecurity //spring security filterChain이 자동으로 포함되게 한다
public class SecurityConfig {
	
	private final PrincipalOauth2UserService principalOauth2UserService;
	
	@Autowired
	private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
	
	@Autowired
	private LoginSuccessHandler LoginSuccessHandler;
	
	
	@Lazy
	public SecurityConfig(PrincipalOauth2UserService principalOauth2UserService) {
	    this.principalOauth2UserService = principalOauth2UserService;
	}
	
	
	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,  MvcRequestMatcher.Builder mvc ) throws Exception {
		//람다로 변경됨
		   
		http
		.authorizeHttpRequests(authorize -> authorize //1. 페이지 접근에 관한 설정
				//모든 사용자가 로그인(인증) 없이 접근할 수 있도록 설정
				.requestMatchers(mvc.pattern("/myPage/**")).authenticated()
				.requestMatchers(mvc.pattern("/css/**"), mvc.pattern("/js/**"), mvc.pattern("/img/**"), mvc.pattern("/image/**"), mvc.pattern("/fonts/**")).permitAll()
				.requestMatchers(mvc.pattern("/"),mvc.pattern("/members/**"),mvc.pattern("/oauth/**"),mvc.pattern("/findPw/**"),mvc.pattern("/findEmail/**"),mvc.pattern("/recipe/**")).permitAll()
				.requestMatchers(mvc.pattern("/category"),mvc.pattern("/item/**"),mvc.pattern("/profile/**")).permitAll()
				.requestMatchers(mvc.pattern("/favicon.ico"), mvc.pattern("/error") ,mvc.pattern("/test"),mvc.pattern("/email/**")).permitAll()
				//'admin'으로 시작하는 경로는 관리자만 접근가능하도록 설정
				.requestMatchers(mvc.pattern("/admin/**")).hasRole("ADMIN")
				//그 외의 페이지는 모두 로그인(인증을 받아야 한다)
				.anyRequest().authenticated() 
				)
		.oauth2Login(oauth2 -> oauth2 
				.loginPage("/members/login")
				.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
				.userService(principalOauth2UserService))

				.successHandler(LoginSuccessHandler)
				.failureUrl("/members/login/error")
				.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
						.userService(principalOauth2UserService))
				)
		.formLogin(formLogin -> formLogin //2. 로그인에 관련된 설정
				.loginPage("/members/login") //로그인 페이지 URL 설정
				.successHandler(myAuthenticationSuccessHandler)
				.usernameParameter("email") //로그인시 id로 사용할 파라메터 이름
				.failureUrl("/members/login/error") //로그인 실패시 이동할 URL
				) 
		.logout(logout -> logout //3. 로그아웃에 관련된 설정
				.logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //로그아웃시 이동할 URL
				//.logoutSuccessHandler(logoutSuccessHandler)
				.logoutSuccessUrl("/") //로그아웃 성공시 이동할 URL
				)   //4.인증되지 않은 사용자가 리소스에 접근했을때 설정(ex. 로그인 안했는데 cart페이지에 접근..)
		.exceptionHandling(handling -> handling
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				)
		.rememberMe(Customizer.withDefaults());
		   
		
	    return http.build();
	}
	

	
}