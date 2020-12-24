@file:JvmName("FakeTokenMain")
package ch.keepcalm.security.faketoken

fun main() {
    println("Welcome to JWT token generator....")
    println("subject : [john.doe@ch.keepcalm.foo.ch.keepcalm.bar.ch]")
    val subject = readLine()?.ifBlank { Token().subject }
    println("vorname : [John]")
    val firstName = readLine()?.ifBlank { Token().firstName }
    println("name : [Doe]")
    val name = readLine()?.ifBlank { Token().name }
    println("roles : [keepcalm.user, keepcalm.admin]")
    val roles = readLine()?.ifBlank { Token().roles }
    println("issuer : [Keepcalm Auth]")
    val issuer = readLine()?.ifBlank { Token().issuer }
    println("audience : [Keepcalm]")
    val audience = readLine()?.ifBlank { Token().audience }
    println("secret : [s3cretP@ssw0rd]")
    val secret = readLine()?.ifBlank { Token().secret }.toString()
    println("userEmail : [joh.doe@ch.keepcalm.foo.ch.keepcalm.bar.ch]")
    val userEmail = readLine()?.ifBlank { Token().userEmail }
    println("language : [de]")
    val language = readLine()?.ifBlank { Token().language }
    println("expiration : [3600000]")
    val expiration = readLine()?.toIntOrNull() ?: Token().expiration

    val token = Token(
        subject = subject,
        firstName = firstName,
        language = language,
        name = name,
        roles = roles,
        issuer = issuer,
        audience = audience,
        secret = secret,
        userEmail = userEmail,
        expiration = expiration
    )

    val generatedToken = generateToken(token)
    println("-----------------")
    println("export DEMO_TOKEN=${generatedToken} \n")
    println(" http :8080/api/document/1 \"Authorization:Bearer  \$DEMO_TOKEN\" -v \n")
    println("-----------------")
    println("###############################")
    println("export DEMO_TOKEN=${generatedToken} \n")
    println(
        "\ncurl http://localhost:8080/api/document/1 " +
                "-H \"Authorization:Bearer  \$DEMO_TOKEN\" -v  | python -m json.tool \n"
    )
    println("###############################")
}
