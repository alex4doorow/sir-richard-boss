package ru.sir.richard.boss.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import ru.sir.richard.boss.web.service.UserService;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.*;

// https://www.baeldung.com/spring-security-basic-authentication
// https://www.baeldung.com/spring-security-login-error-handling-localization
// https://www.baeldung.com/spring-security-custom-filter
// https://www.baeldung.com/spring-security-expressions
// https://www.baeldung.com/spring-security-csrf
// https://www.baeldung.com/spring-security-redirect-login
// https://github.com/eugenp/tutorials/tree/master/spring-security-modules

// https://spring.io/guides/gs/securing-web/
// https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html

// https://www.digitalocean.com/community/tutorials/spring-4-security-mvc-login-logout-example
// https://docs.spring.io/spring-security/site/docs/5.4.11/guides/helloworld-xml.html
	
// https://habr.com/ru/post/482552/
// https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-access
// https://www.baeldung.com/spring-security-jdbc-authentication

@Slf4j
@Configuration
@EnableWebSecurity
public class MvcWebSecurityConfig {

	@Autowired
	UserService userService;

	@Autowired
	protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http
	        .csrf().disable()
	        .authorizeRequests()
		        .antMatchers("/orders/*").permitAll()
	    		.antMatchers("/wiki/*").permitAll()
	    		.antMatchers("/ajax/*").permitAll()
	    		.antMatchers("/errors/*").permitAll()
	    		.antMatchers("/", "/resources/**").permitAll()
	        	.antMatchers("/anonymous*").anonymous()
	        	.antMatchers("/login*","/invalid-session*", "/session-expired*", "/index-logout*").permitAll()
	        	.anyRequest().authenticated()
	        .and()
	        	.formLogin()
	        	.loginPage("/login")
	        	.loginProcessingUrl("/login")
				//.successHandler(successHandler())
	        	.failureUrl("/login?error=true")
	        .and()
	        	.logout().deleteCookies("JSESSIONID")
	        	.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(CACHE, COOKIES, STORAGE)))
	        .and()
	        	.rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400)
	        .and()
	        	.sessionManagement()
	        	.sessionFixation().migrateSession()
	        	.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
	        	.invalidSessionUrl("/login")
	        	.maximumSessions(2)
	        	.expiredUrl("/session-expired");
    	 return http.build();   
    }
    
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }   
    
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler();
    }  
    
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
    private AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler();
    	//return new RefererRedirectionAuthenticationSuccessHandler();
    }
}
