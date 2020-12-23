package ch.keepcalm.demo.security

import java.lang.Exception
import ch.keepcalm.security.ROLE_ACTUATOR
import ch.keepcalm.security.ROLE_ADMIN
import ch.keepcalm.security.ROLE_USER
import ch.keepcalm.security.jwt.JwtSecurityProperties
import ch.keepcalm.security.jwt.JwtTokenFilter
import ch.keepcalm.security.jwt.JwtTokenVerifier
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableConfigurationProperties(JwtSecurityProperties::class, SecurityProperties::class)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
class SecurityConfiguration(
    private val jwtSecurityProperties: JwtSecurityProperties,
    private val securityProperties: SecurityProperties
) : WebSecurityConfigurerAdapter() {

    companion object {
        private val API_DOCUMENT = "/api/document/**"
        private val API_SALARY = "/api/salary/**"
        private val FAKE_TOKEN = "/faketoken/**"
        private val WHOAMI = "/whoami/**"
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)
        http
            .addFilterBefore(JwtTokenFilter(JwtTokenVerifier(jwtSecurityProperties)), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .authorizeRequests()
            .antMatchers(FAKE_TOKEN).permitAll()
            .antMatchers(WHOAMI).permitAll()
            .antMatchers(API_DOCUMENT).hasAnyAuthority(ROLE_USER)
            .antMatchers(API_SALARY).hasAnyAuthority(ROLE_ADMIN)
            .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyRole(*getAdminRoles(securityProperties).toTypedArray())

    }
    private fun getAdminRoles(securityProperties: SecurityProperties) =
        if (securityProperties.user.roles.isNotEmpty()) securityProperties.user.roles else listOf(ROLE_ACTUATOR)
}

