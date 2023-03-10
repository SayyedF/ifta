package com.jilani.ifta.config;

import com.jilani.ifta.users.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*http.authorizeRequests()
                //.antMatchers("/", "/error").permitAll()
                .antMatchers("/test").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/css/**", "/js/**", "/images/**","/icons/**","/plugins/**").permitAll()
                .antMatchers("/user").hasAnyAuthority("USER","MUFTI","MAINMUFTI","ADMIN")
                .antMatchers("/admin").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()//.loginPage("/login").loginProcessingUrl("/authenticate").permitAll()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                //.and()
                //.oauth2Login()
        ;*/

        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/ask/**").authenticated()
                .antMatchers("/asked/**").hasAnyAuthority("ADMIN", "MUFTI", "MAINMUFTI")
                .antMatchers("/user/**").authenticated()
                .antMatchers(HttpMethod.POST,"/sample").permitAll()
                .antMatchers("/modal/**").permitAll()
                .antMatchers("/remove").permitAll()
                .antMatchers("/**").permitAll().and()
                .formLogin().permitAll()//.loginPage("/login").loginProcessingUrl("/authenticate").permitAll()
                .and()
                .cors()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(HttpMethod.POST,"/sample");
    }
}
