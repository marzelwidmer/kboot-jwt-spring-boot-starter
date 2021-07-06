package ch.keepcalm.security.faketoken

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@RestController
class FakeTokenController() {
    @GetMapping(value = ["/faketoken"])
    fun faketoken() = generateToken(Token())
}
fun generateToken(token: Token) =
    Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .setSubject(token.subject)
        .setIssuedAt(Date())
        .setExpiration(
            Date.from(token.expiration.toLong().let {
                LocalDateTime.now().plusSeconds(it).atZone(ZoneId.systemDefault()).toInstant()
            })
        )
        .setIssuer(token.issuer)
        .setAudience(token.audience)
        .addClaims(
            mapOf(
                Pair("language", token.language),
                Pair("name", token.name),
                Pair("firstName", token.firstName),
                Pair("email", token.userEmail),
                Pair("roles", token.roles)
            )
        )
        .signWith(
            SignatureAlgorithm.HS256,
            Base64.getEncoder().encodeToString(token.secret.toByteArray(StandardCharsets.UTF_8))
        ).compact()


data class Token(
    var language: String? = "de",
    var firstName: String? = "John",
    var name: String? = "Doe",
    var subject: String? = "john.doe@ch.keepcalm.foo.ch.keepcalm.bar.ch",
    var roles: String? = "keepcalm.user",
    var issuer: String? = "Keepcalm Auth",
    var audience: String? = "Keepcalm",
    var secret: String = "SuperSecretTestPasswordThatIsUsedOnlyForTests",
    var userEmail: String? = "joh.doe@ch.keepcalm.foo.ch.keepcalm.bar.ch",
    var expiration: Int = 3600000
)
