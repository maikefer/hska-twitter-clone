package de.hska.lkit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import de.hska.lkit.sessions.CookieInterceptor;

// TODO: BUG: kills the UI (css can't be found)

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.hska.lkit.*" })
public class WebConfig extends WebMvcConfigurerAdapter {
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(cookieInterceptor()).excludePathPatterns("/login/**").excludePathPatterns("/error/**")/*.addPathPatterns("/auth/**")*/;		
	}
	

	@Bean
	public CookieInterceptor cookieInterceptor() {
	    return new CookieInterceptor();
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    return new LocaleChangeInterceptor();
	}
}
