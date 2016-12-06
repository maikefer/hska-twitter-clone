package de.hska.lkit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import de.hska.lkit.sessions.CookieInterceptor;


@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(cookieInterceptor()).excludePathPatterns("/login/**").excludePathPatterns("/registration/**").excludePathPatterns("/error/**")/*.addPathPatterns("/auth/**")*/;
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
