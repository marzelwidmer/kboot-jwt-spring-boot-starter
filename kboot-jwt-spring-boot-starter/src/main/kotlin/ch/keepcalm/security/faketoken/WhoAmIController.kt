package ch.keepcalm.security.faketoken

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class WhoAmIController() {
    @GetMapping("/whoami")
    fun me(principal: Principal): Principal {
        return principal
    }
}
