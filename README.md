# Kotlin Spring Boot Starter for JWT validation
![MasterCI](https://github.com/marzelwidmer/kboot-jwt-spring-boot-starter/workflows/MasterCI/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=marzelwidmer_kboot-jwt-spring-boot-starter&metric=alert_status)](https://sonarcloud.io/dashboard?id=marzelwidmer_kboot-jwt-spring-boot-starter)

## Configure JWT
```yaml
keepcalm:
  security:
    jwt:
      issuer: Keepcalm Auth
      audience: Keepcalm
      secret: s3cretP@ssw0rd
```
## Configure Protected EndPoints
To override the default configuration of access of a User Token `/api` add the following configuration in you `application.yaml` file
and also if you configure a Admin Endpoints.
```yaml
keepcalm:
  security:
    endpoints:
      admin:
        - "/api/salary/**"
      user:
        - "/api/document/**"
      unsecured:
        - "/faketoken"
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
