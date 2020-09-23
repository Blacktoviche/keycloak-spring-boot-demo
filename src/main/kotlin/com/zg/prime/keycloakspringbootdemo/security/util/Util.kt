package com.zg.prime.keycloakspringbootdemo.security.util

import org.keycloak.KeycloakPrincipal
import org.keycloak.KeycloakSecurityContext
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

object Util {
    val loggedInUserToken: String
        get() {
            val authentication: KeycloakAuthenticationToken = SecurityContextHolder.getContext()
                    .authentication as KeycloakAuthenticationToken
            val keycloakPrincipal = authentication
                    .principal as KeycloakPrincipal<KeycloakSecurityContext>
            return keycloakPrincipal.keycloakSecurityContext.tokenString
        }
    val loggedInUserId: String
        get() {
            val simpleKeycloakAccount: SimpleKeycloakAccount = SecurityContextHolder.getContext().authentication.details as SimpleKeycloakAccount
            return simpleKeycloakAccount.principal.name
        }
}