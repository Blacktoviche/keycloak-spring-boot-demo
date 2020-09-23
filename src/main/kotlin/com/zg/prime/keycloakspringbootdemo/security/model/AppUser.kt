package com.zg.prime.keycloakspringbootdemo.security.model

import java.io.Serializable

class AppUser : Serializable {
    var username: String? = null
    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var password: String? = null
    var refreshToken: String? = null
    var roles: List<String> = mutableListOf()
}