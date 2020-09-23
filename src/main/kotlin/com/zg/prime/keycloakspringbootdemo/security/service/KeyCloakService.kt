package com.zg.prime.keycloakspringbootdemo.security.service


import com.zg.prime.keycloakspringbootdemo.security.model.AppUser
import com.zg.prime.keycloakspringbootdemo.security.model.TokenRepresentation
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.util.*
import java.util.function.Consumer


@Component
class KeyCloakService {
    @Value("\${keycloak.credentials.secret}")
    private val CREDENTIALS_SECRET: String? = null

    @Value("\${keycloak.resource}")
    private val CLIENT_ID: String? = null

    @Value("\${keycloak.auth-server-url}")
    private val AUTH_SERVER_URL: String? = null

    @Value("\${keycloak.realm}")
    private val REALM: String? = null


    var logger = LoggerFactory.getLogger(KeyCloakService::class.java)
    fun generateToken(appUser: AppUser): TokenRepresentation? {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("grant_type", "password")
        map.add("client_id", CLIENT_ID)
        map.add("username", appUser.username)
        map.add("password", appUser.password)
        map.add("client_secret", CREDENTIALS_SECRET)
        val entity = HttpEntity(map, headers)
        return post(entity)
    }

    fun getByRefreshToken(refreshToken: String?): TokenRepresentation? {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("grant_type", "refresh_token")
        map.add("client_id", CLIENT_ID)
        map.add("refresh_token", refreshToken)
        map.add("client_secret", CREDENTIALS_SECRET)
        val entity = HttpEntity(map, headers)
        return post(entity)
    }

    //@Throws(Exception::class)
    fun updateUser(userId:String, appUser: AppUser){
        var userResource = usersResource[userId]

        var user = userResource.toRepresentation()

        user.email = appUser.email
        user.firstName = appUser.firstName
        user.lastName = appUser.lastName

        userResource.update(user)
    }

    fun saveUser(appUser: AppUser): UserRepresentation? {
        val usersResource = usersResource
        val user = UserRepresentation()
        user.username = appUser.username
        user.email = appUser.email
        user.firstName = appUser.firstName
        user.lastName = appUser.lastName
        user.isEnabled = true
        val result = usersResource.create(user)
        if (result.status == HttpStatus.CREATED.value()) {
            val userId = CreatedResponseUtil.getCreatedId(result)
            val userResource = usersResource[userId]
            val credentialRepresentation = CredentialRepresentation()
            credentialRepresentation.isTemporary = false
            credentialRepresentation.type = CredentialRepresentation.PASSWORD
            credentialRepresentation.value = appUser.password
            userResource.resetPassword(credentialRepresentation)
            //RealmResource realmResource = getRealmResource();
            userResource.roles().realmLevel().add(getRolesRepresentation(appUser.roles))
            return user
        }
        return null
    }

    fun logoutUser(userId: String?) {
        val userResource = usersResource
        userResource[userId].logout()
    }

    fun resetPassword(newPassword: String, userId: String?) {
        val userResource = usersResource
        val credentialRepresentation = CredentialRepresentation()
        credentialRepresentation.isTemporary = false
        credentialRepresentation.type = CredentialRepresentation.PASSWORD
        credentialRepresentation.value = newPassword
        userResource[userId].resetPassword(credentialRepresentation)
    }

    private val usersResource: UsersResource
        private get() = realmResource.users()
    private val realmResource: RealmResource
        private get() = buildKeycloakAdminClient().realm(REALM)

    private fun getRolesRepresentation(userRoles: List<String>): List<RoleRepresentation> {
        val roleRepresentationList: MutableList<RoleRepresentation> = LinkedList()
        val rolesResource = realmResource.roles()
        userRoles.forEach(Consumer { s: String? -> roleRepresentationList.add(rolesResource[s].toRepresentation()) })
        return roleRepresentationList
    }

    @Throws(RestClientException::class)
    private fun post(entity: HttpEntity<*>): TokenRepresentation? {
        val restTemplate = RestTemplate()
        val uri = "$AUTH_SERVER_URL/realms/$REALM/protocol/openid-connect/token"
        val response: ResponseEntity<TokenRepresentation> = restTemplate.postForEntity(uri, entity, TokenRepresentation::class.java)
        return response.body
    }

    fun getUserProfile(userId: String?): UserRepresentation {
        val usersResource = realmResource.users()
        val userResource = usersResource[userId]
        return userResource.toRepresentation()
    }

    private fun buildKeycloakAdminClient(): Keycloak {
        return KeycloakBuilder.builder().serverUrl(AUTH_SERVER_URL).realm("master").username("admin").password("admin")
                .clientId("admin-cli").resteasyClient(ResteasyClientBuilder().connectionPoolSize(10).build())
                .build()
    }
}