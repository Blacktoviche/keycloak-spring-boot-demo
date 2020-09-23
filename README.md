# keycloak-spring-boot-demo

[Keycloak](https://www.keycloak.org) is well known open source Identity and Access Management for securing applications
You start thinking of using keycloak when you have so many web apps and most of them need to be secured
I used to implement my rest apps security by myself using JWT Token and normal login using the spring boot security.
But in my case when you have several spring boot apps in a tomcat server it's not wisly to implement security for each of the app individually.
 It's a waste of time and effort.
 
 It's a better practice to have a central security for your apps and very easy to be implemented in not time.
 It's only the first time you install keycloak than every new app's security take some clicks
 
 This repository I wanted to share is simple implementation of keycloak in spring boot app.
 You can see the most of operations you need in your app ( register user, grap the token, reset password, update user info)
 
 # Before running this repository
 
 You have to setup keycloak first and add realm, users and roles.
 Please follow [Server Admin](https://www.keycloak.org/docs/latest/server_admin/) guide to setup keycloak
 You should add client roles ( admin, user, manager) these roles will be used in the spring boot app.
 And you should add realm roles ( app-admin, app-manager, app-user) and composite them with the client roles so you can add roles for the new registered user.
 
 
 # Run
 Import this project in IntelliJ IDEA or you can run it using command line
 
```bash
# Clone this repository
git clone https://github.com/blacktoviche/keycloak-spring-boot-demo
# Go into the repository
cd stm-java-backend
# Install dependencies
mvn install
# Compile the app
mvn compile
# Package the app
mvn package
# Run the app
mvn spring-boot:run
``` 




## License
- [MIT](LICENSE)

Twitter [@SyrianDeveloper](https://www.twitter.com/SyrianDeveloper)