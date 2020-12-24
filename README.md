# Kotlin Spring Boot Starter for JWT validation ![MasterCI](https://github.com/marzelwidmer/kboot-jwt-spring-boot-starter/workflows/MasterCI/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=marzelwidmer_kboot-jwt-spring-boot-starter&metric=alert_status)](https://sonarcloud.io/dashboard?id=marzelwidmer_kboot-jwt-spring-boot-starter)

## application.yaml
```yaml
security:
  jwt:
    issuer: Keepcalm Auth
    audience: Keepcalm
    secret: s3cretP@ssw0rd

```

## Maven

```xml
<dependency>
	<groupId>ch.keepcalm.security</groupId>
	<artifactId>kboot-jwt-spring-boot-starter</artifactId>
	<version>VERSION</version>
</dependency>
```

## Gradle
```kotlin
repositories {
    maven {
        url = uri("http://nexusvm.cloudapp.net/artifactory/maven-public")
    }
}

dependencies {
    //  Keepcalm Starter
    implementation("ch.keepcalm.security", "kboot-jwt-spring-boot-starter", "0.0.1-20201220.205452-1")

}
```

## Configuration Class
```kotlin

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
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)
        http
            .addFilterBefore(JwtTokenFilter(JwtTokenVerifier(jwtSecurityProperties)), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement().sessionCreationPolicy(STATELESS).and()
            .authorizeRequests()
            .antMatchers(FAKE_TOKEN).permitAll()
            .antMatchers(API_DOCUMENT).hasAnyAuthority(ROLE_USER)
            .antMatchers(API_SALARY).hasAnyAuthority(ROLE_ADMIN)
            .requestMatchers(EndpointRequest.to(HealthEndpoint::class.java, InfoEndpoint::class.java)).permitAll()
            .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyRole(*getAdminRoles(securityProperties).toTypedArray())

    }
    private fun getAdminRoles(securityProperties: SecurityProperties) =
        if (securityProperties.user.roles.isNotEmpty()) securityProperties.user.roles else listOf(ROLE_ACTUATOR)
}
```

# Test Support

```xml
<!-- Test Support -->
<dependency>
    <groupId>ch.keepcalm.security</groupId>
    <artifactId>kboot-jwt-spring-boot-starter</artifactId>
    <version>VERSION</version>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>
```

```kotlin


@SpringBootTest
@SpringBootConfiguration
class WithMockCustomerTest {

	@WithMockCustomUser(username = "jane@doe.ch", authorities = ["keepcalm.user"], firstname = "jane", lastname = "doe")
	@Test
	fun `test SecurityContext of Jane Doe`() {
		assertThat(getPrincipalFirstName()).isEqualTo("jane")
		assertThat(getPrincipalLastName()).isEqualTo("doe")
		assertThat(getCurrentUsername()).isEqualTo("jane@doe.ch")
	}
}


```
