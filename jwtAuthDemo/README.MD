### What is JWT Authentication?

JWT (JSON Web Token) Authentication is a widely used method for securely transmitting information between a client and a server. 
JWT is a compact, URL-safe token format that is often used for handling authentication and authorization in web applications and services.

In the context of **authentication**, JWT is used to verify the identity of a user based on a token, rather than relying on traditional session-based authentication (where the server stores user sessions). 
It allows for stateless authentication, which is especially beneficial for distributed systems like microservices.

### How JWT Authentication Works

JWT Authentication typically involves three main steps:

1. **User Login**:  
   The user submits their credentials (username and password) to the authentication server (e.g., via a REST API).

2. **Token Generation**:  
   If the credentials are valid, the server generates a **JWT** containing user information (e.g., user ID, roles, permissions). This JWT is then signed using a secret key (HMAC) or a private key (RSA). The signed token is sent back to the client.

3. **Token Validation**:  
   In subsequent requests, the client sends the JWT (usually in the `Authorization` header as a `Bearer` token) along with each API request. The server validates the token, checks if it's valid (using the signature), and if valid, grants access to the protected resources.

#### Structure of a JWT Token

A JWT token consists of three parts:
1. **Header**:
    - Specifies the algorithm used to sign the token (e.g., `HS256` for HMAC with SHA-256).
    - Typically looks like: `{ "alg": "HS256", "typ": "JWT" }`

2. **Payload**:
    - Contains claims or data about the user (like `sub` for subject, `iat` for issued at, `exp` for expiration time).
    - Example payload could include user ID, roles, etc.

3. **Signature**:
    - A cryptographic signature used to verify the integrity of the token and the authenticity of the sender.
    - The signature is created by combining the **header** and **payload**, and then signing it with a secret key or private key.

The resulting JWT structure looks like this:
```
<HEADER>.<PAYLOAD>.<SIGNATURE>
```

### Example of a JWT

Here’s a sample JWT token:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

- **Header** (base64-encoded):
  ```json
  {
      "alg": "HS256",
      "typ": "JWT"
  }
  ```
- **Payload** (base64-encoded):
  ```json
  {
      "sub": "1234567890",
      "name": "John Doe",
      "iat": 1516239022
  }
  ```
- **Signature**:
  The signature is computed by signing the header and payload using a secret key (e.g., `HMACSHA256`).

### JWT Authentication Flow
In a monolithic Spring Boot application, you can handle JWT authentication without needing a separate authorization server.

You only need an Auth Server if your architecture is more complex, such as when:
- `You have multiple applications` (e.g., multiple microservices, front-end, and back-end apps), and you want to centralize authentication.
- `You're following OAuth2 or OpenID Connect (OIDC) standards`. If you’re using a library like Spring Security OAuth2 or Keycloak, you would typically run a dedicated authentication server to handle token issuance and validation for multiple services.
- `You want Single Sign-On (SSO)`, where users authenticate once and get access to multiple services or applications.

Since we are managing our own JWT tokens, we'll want to create the token ourselves (using a library like `jjwt` or `Nimbus`), 
and then `parse` and `validate` it on every request. We’ll also need to configure Spring Security to use your custom JWT decoding logic.


- Here’s the typical flow without auth server:
1. **User Login**:  
   The user provides their credentials (e.g., username and password) via a login API endpoint (e.g., `/login`). The server verifies these credentials against a database.

2. **Generate JWT Token**:  
   Upon successful authentication, the server creates a JWT that contains claims like user ID, roles, and expiration time. This JWT is signed and sent to the client.

3. **Client Stores the Token**:  
   The client (usually a web or mobile app) stores the JWT (typically in localStorage or sessionStorage for web applications, or a secure storage solution for mobile apps).

4. **Client Makes Requests**:  
   For each subsequent request to a protected resource, the client includes the JWT in the `Authorization` header as a `Bearer` token.

   Example:
   ```
   Authorization: Bearer <JWT_TOKEN>
   ```

5. **Server Validates the Token**:  
   The server extracts the JWT from the `Authorization` header, verifies its authenticity using the secret key (HMAC) or public key (RSA), and checks whether the token is expired. If the token is valid, the server grants access to the requested resource. If invalid, the server responds with an error (e.g., `401 Unauthorized`).

### Why Use JWT Authentication?

#### 1. **Stateless Authentication**:
- With JWT, the server does not need to store session information. Since the token contains all the necessary information (like user details, roles, etc.), it can be used to authenticate subsequent requests.
- This stateless nature makes JWT ideal for distributed systems and microservices architectures, where managing session data on the server would be cumbersome.

#### 2. **Decentralized Authentication**:
- JWT can be used for **single sign-on (SSO)**, allowing users to authenticate once and then access multiple services without needing to log in again.

#### 3. **Scalability**:
- Since tokens are self-contained and stateless, they can be used across multiple servers or instances, making it easier to scale applications horizontally.

#### 4. **Flexibility and Extensibility**:
- The payload can carry custom data such as roles, user preferences, and other claims, making JWT highly customizable for different use cases.
- JWTs are **platform-agnostic** and can be used across different technologies and services.

#### 5. **Security**:
- JWT uses strong cryptographic signing algorithms like **HMAC** or **RSA** to ensure the integrity of the token, making it hard to tamper with.
- Tokens can also have an **expiration date** (`exp` claim), limiting the lifespan of a token and mitigating the risk of long-lived tokens being compromised.

### Do we need a  database to handle user roles?
No, the JWT approach does not necessarily require saving roles in a separate table if you are simply using JWTs for authentication and authorization.

However, whether or not you store roles in a separate table depends on how you want to manage user roles in your application. so in summary:
`Yes`, if you want to manage roles in your application (e.g., assigning multiple roles to users, managing role-based permissions).
`No`, if you are simply hardcoding roles into the application or want to avoid querying a database for role data.

We will go with database the JWT with Roles Stored in the Database approach

### JWT with Roles Stored in the Database
We will have JPA entities to have many-to-many relationship between users and roles table.

```kotlin
package com.dmrhimali.auth.jwtauth.model

import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String  // Role name, e.g., "ROLE_USER", "ROLE_ADMIN"
) {
    // Default constructor required by Hibernate
    constructor() : this(0, "")
}
```

```kotlin
package com.dmrhimali.auth.jwtauth.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class User(
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   val id: Long = 0,

   @Column(name = "username") // Make sure this is correctly mapped to the "username" column
   private val username: String,  // Private backing field for username

   @Column(name = "password")
   private var password: String,  // Private backing field for password

   @ManyToMany(fetch = FetchType.EAGER) // Fetch roles eagerly so they are available for authorization
   @JoinTable(
      name = "user_roles",
      joinColumns = [JoinColumn(name = "user_id")],
      inverseJoinColumns = [JoinColumn(name = "role_id")]
   )
   var roles: Set<Role> = emptySet()
) : UserDetails {

   // Default constructor required by Hibernate
   constructor() : this(0, "", "", emptySet())

   override fun getAuthorities(): Collection<GrantedAuthority> =
      roles.map { role -> org.springframework.security.core.authority.SimpleGrantedAuthority(role.name) }

   override fun getPassword(): String = password  // Return the private _password field

   override fun getUsername(): String = username  // Return the private _username field

   override fun isAccountNonExpired(): Boolean = true

   override fun isAccountNonLocked(): Boolean = true

   override fun isCredentialsNonExpired(): Boolean = true

   override fun isEnabled(): Boolean = true

}
```

And we have the respective repositories in repository package.

And an some predefined users:

```kotlin
package com.dmrhimali.auth.jwtauth.repository

import com.dmrhimali.auth.jwtauth.model.Role
import com.dmrhimali.auth.jwtauth.model.User
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class UserRoleInitializer(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val roleRepository: RoleRepository,
    @Autowired private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct
    fun init() {
        // Ensure roles :user and admin exist
        val userRole = roleRepository.save(Role(name = "ROLE_USER"))
        val adminRole = roleRepository.save(Role(name = "ROLE_ADMIN"))

        // Ensure the admin user exists
        var adminUser = userRepository.findByUsername("admin").getOrNull()
        if (adminUser == null) {
                adminUser = User(
                    username = "admin",
                    password = passwordEncoder.encode("adminpassword"), // encode the password
                    roles = mutableSetOf(adminRole)
                )
            userRepository.save(adminUser)
        }

        // Ensure a regular user exists
        var regularUser = userRepository.findByUsername("user").getOrNull()
        if (regularUser == null) {
            regularUser = User(
                username = "user",
                password = passwordEncoder.encode("userpassword"), // encode the password
                roles = mutableSetOf(userRole)
            )
            userRepository.save(regularUser)
        }

        adminUser = userRepository.findByUsername("admin").getOrNull()
        if (adminUser != null && adminUser.roles.none { it.name == "ROLE_ADMIN" }) {
            adminUser.roles = mutableSetOf(adminRole)
            userRepository.save(adminUser)
        }

        regularUser = userRepository.findByUsername("user").getOrNull()
        if (regularUser != null && regularUser.roles.none { it.name == "ROLE_USER" }) {
            regularUser.roles = mutableSetOf(userRole)
            userRepository.save(regularUser)
        }
    }
}
```

### Endpoints
 [insomnia endpoints](Insomnia_endpoints.yaml)

See `AuthController.kt` and `SecurityConfig.kt`:
- `/api/register`:Public endpoint, no authentication required. Users can register with a username, password, and a list of roles.
- `/api/login`: login using username and password and get a jwt token
- `/api/greet`:Only accessible by authenticated users. This method retrieves the username from the JWT token (assuming you're using JWT-based authentication).
- `/api/users`:This endpoint is protected by @PreAuthorize("hasRole('ADMIN')"), so only users with the ROLE_ADMIN role can access it. Non-admin users will get a 403 Forbidden response.


#### /api/register:

```shell
curl --request POST \
  --url http://localhost:8080/api/login \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/10.3.0' \
  --cookie JSESSIONID=69D7AD28264CAA4115C267ECB8EC4F9D \
  --data '{
  "username": "newUser2",
  "password": "password123"
}'
```

response:
```json
{
	"token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuZXdVc2VyMiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzM4MTIzMDk2LCJleHAiOjE3MzgxMjM0NTZ9.J9jUzyjBAACTN14PacUThy4iSEV2Elr6J-Sec0owsICMRAZ05CQDL8o0ubfB6VnflpmKi495sBvDEjIfKLhFRA"
}
```
![img](img/img01.png)
![img](img/img02.png)

#### /api/login:
```shell
curl --request POST \
  --url http://localhost:8080/api/login \
  --header 'Content-Type: application/json' \
  --header 'User-Agent: insomnia/10.3.0' \
  --cookie JSESSIONID=69D7AD28264CAA4115C267ECB8EC4F9D \
  --data '{
  "username": "admin",
  "password": "adminpassword"
}'
```

![img](img/img03.png)

#### /api/refresh:
```shell
curl --request POST \
  --url http://localhost:8080/api/refresh \
  --header 'Authorization: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzM4MTIxNjQ3LCJleHAiOjE3MzgxMjIwMDd9.UC5GDeAtRgQeuFmrqT3bMm_EyWW4FVQxExdKnaqbKsT2ygICEtPcP4ZJlhV14Yi-In95-mTbo1eFBEjL9zgwng' \
  --header 'User-Agent: insomnia/10.3.0' \
  --cookie JSESSIONID=69D7AD28264CAA4115C267ECB8EC4F9D
```

![img](img/img04.png)

#### /api/greet (Authenticated):
```shell
curl --request GET \
  --url http://localhost:8080/api/greet \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuZXdVc2VyMiIsInJvbGVzIjpbXSwiaWF0IjoxNzM4MTIxODQ3LCJleHAiOjE3MzgxMjIyMDd9.OV0aCnLjACyWLmJCOYwFNTMlfXZnUMzqy04ed5mOVXWx2tA0BS4Q3MJJJu5Azq7lskvvnaQYXcyzrBt4lplG5g' \
  --header 'User-Agent: insomnia/10.3.0' \
  --cookie JSESSIONID=69D7AD28264CAA4115C267ECB8EC4F9D
```

![img](img/img05.png)


#### /api/users (Authenticated, only available to ADMINs):
```shell
curl --request GET \
  --url http://localhost:8080/api/users \
  --header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzM4MTI0MDA0LCJleHAiOjE3MzgxMjQzNjR9.fU5dgZq9NyLMqbpOKch0okLKeTOQK6VkanW-c27tJO4CHjnAxB2t7MUBDbsiCSgi7HfEeXb2H54u5bNLgsvk3Q' \
  --header 'User-Agent: insomnia/10.3.0' \
  --cookie JSESSIONID=DB5406ACF3DE29A61B70066BEDCFB174
```
![img](img/img06.png)

if non-admin try to access, you get a 403 code (Forbidden) error.

![img](img/img07.png)


### References
https://tienisto.medium.com/securing-spring-boot-with-jwt-kotlin-7b529f99ca47
https://codersee.com/spring-boot-3-spring-security-6-with-kotlin-jwt/
https://www.baeldung.com/kotlin/spring-security-jwt