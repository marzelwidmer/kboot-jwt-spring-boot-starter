package ch.keepcalm.security.web

import ch.keepcalm.security.ROLE_ACTUATOR
import ch.keepcalm.security.ROLE_ADMIN
import ch.keepcalm.security.ROLE_USER
import ch.keepcalm.security.jwt.JwtSecurityConfigurer
import ch.keepcalm.security.jwt.JwtTokenFilter
import ch.keepcalm.security.jwt.JwtTokenVerifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
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
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(WebSecurityConfigurer::class, JwtSecurityConfigurer::class)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
class WebSecurityConfiguration(private val webSecurityConfigurer: WebSecurityConfigurer,
                               private val jwtSecurityConfigurer: JwtSecurityConfigurer, private val securityProperties: SecurityProperties) : WebSecurityConfigurerAdapter() {

    val log: Logger = LoggerFactory.getLogger(WebSecurityConfiguration::class.java)

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)
        http
            .addFilterBefore(JwtTokenFilter(JwtTokenVerifier(jwtSecurityConfigurer)), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .authorizeRequests()
            .antMatchers(*getUserEndpoints()).hasAnyAuthority(ROLE_USER)
            .antMatchers(*getAdminEndpoints()).hasAnyAuthority(ROLE_ADMIN)
            .antMatchers(*getUnsecuredEndpoints()).permitAll()
            .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyRole(*getAdminRoles(securityProperties).toTypedArray())
    }
    private fun getUserEndpoints() = webSecurityConfigurer.user.toTypedArray()
        .also {
            log.debug("Configure (User Endpoints) with [${it.joinToString()}]")
        }
    private fun getAdminEndpoints() = webSecurityConfigurer.admin.toTypedArray()
        .also {
            log.debug("Configure (Admin Endpoints) with [${it.joinToString()}]")
        }
    private fun getUnsecuredEndpoints() = webSecurityConfigurer.unsecured.toTypedArray()
        .also {
            log.debug("Configure (Unsecured Endpoints) with [${it.joinToString()}]")
        }

    private fun getAdminRoles(securityProperties: SecurityProperties) = if (securityProperties.user.roles.isNotEmpty()) securityProperties.user.roles else listOf(ROLE_ACTUATOR)
}
