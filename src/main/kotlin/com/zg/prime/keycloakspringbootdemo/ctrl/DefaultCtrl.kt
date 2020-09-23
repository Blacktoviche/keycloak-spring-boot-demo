package com.zg.prime.keycloakspringbootdemo.ctrl

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.annotation.security.RolesAllowed


@RestController
@RequestMapping("/")
class DefaultCtrl {

    @get:GetMapping(value = ["test/anyone"])
    val anyone: ResponseEntity<String>?
        get() = ResponseEntity.ok("Access granted for anyone")

    @get:GetMapping(value = ["test/admin"])
    @get:RolesAllowed("admin")
    val admin: ResponseEntity<String>?
        get() = ResponseEntity.ok("Access granted for Admin")

    @get:GetMapping(value = ["test/user"])
    @get:RolesAllowed("user")
    val user: ResponseEntity<String>?
        get() = ResponseEntity.ok("Access granted for User")

    @RolesAllowed("manager")
    @RequestMapping(value = ["test/manager"], method = [RequestMethod.GET])
    fun manager(): ResponseEntity<String>? {
        return ResponseEntity.ok("Access granted for Manager")
    }

}

