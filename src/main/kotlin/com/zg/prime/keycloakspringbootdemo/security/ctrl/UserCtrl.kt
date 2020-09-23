package com.zg.prime.keycloakspringbootdemo.security.ctrl


import com.zg.prime.keycloakspringbootdemo.security.model.AppUser
import com.zg.prime.keycloakspringbootdemo.security.model.TokenRepresentation
import com.zg.prime.keycloakspringbootdemo.security.service.KeyCloakService
import com.zg.prime.keycloakspringbootdemo.security.util.Util
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping(value = ["user"])
class UserCtrl {

    @Autowired
    var keyClockService: KeyCloakService? = null

    @PostMapping("token")
    fun token(@RequestBody appUser: AppUser): ResponseEntity<*> {
        var tokenRepresentation: TokenRepresentation?
        tokenRepresentation = try {
            keyClockService?.generateToken(appUser)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<Any>(tokenRepresentation, HttpStatus.OK)
    }


    @PostMapping("refreshToken")
    fun tokenByRefreshToken(@RequestBody appUser: AppUser): ResponseEntity<*> {
        var tokenRepresentation: TokenRepresentation?
        tokenRepresentation = try {
            keyClockService?.getByRefreshToken(appUser.refreshToken)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity<Any>(tokenRepresentation, HttpStatus.OK)
    }

    @RolesAllowed("admin", "manager", "user")
    @GetMapping("profile")
    fun profile(): ResponseEntity<UserRepresentation> {
        return ResponseEntity(keyClockService?.getUserProfile(Util.loggedInUserId), HttpStatus.OK)
    }

    @PostMapping("register")
    fun register(@RequestBody appUser: AppUser): ResponseEntity<*> {
        return try {
            keyClockService?.saveUser(appUser)
            ResponseEntity<Any>(HttpStatus.OK)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    //Update logged in user info (email,firstname,lastname)
    @RolesAllowed("user", "manager", "admin")
    @PutMapping("update")
    fun updateProfile(@RequestBody appUser: AppUser): ResponseEntity<*> {
        return try {
            keyClockService?.updateUser(Util.loggedInUserId, appUser)
            ResponseEntity<Any>(HttpStatus.OK)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("logout")
    fun logoutUser(): ResponseEntity<*> {
        keyClockService?.logoutUser(Util.loggedInUserId)
        return ResponseEntity<Any>(HttpStatus.OK)
    }

    @PostMapping("password")
    fun resetPassword(newPassword: String): ResponseEntity<*> {
        keyClockService?.resetPassword(newPassword, Util.loggedInUserId)
        return ResponseEntity<Any>(HttpStatus.OK)
    }
}