package com.zg.prime.keycloakspringbootdemo.security.model

class TokenRepresentation {
    var access_token: String? = null
    var expires_in = 0
    var refresh_expires_in = 0
    var refresh_token: String? = null
    var token_type: String? = null
    var session_state: String? = null
    var scope: String? = null
}