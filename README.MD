# Authentication
User authentication is a critical aspect of any web application. 
It ensures that users are who they say they are before granting access to protected resources.
User authentication is a critical part of web application security. In modern applications, there are various ways to authenticate users, each with its own use cases and benefits. In this tutorial, we will cover:

1. **Traditional authentication mechanisms** like Username-Password Authentication.
2. **Token-based authentication** (JWT - JSON Web Tokens).
3. **Social login** (Google, GitHub, etc.).
4. **Two-factor authentication (2FA)**.

In this tutorial, we will cover several popular user authentication methods and how to implement them in a Spring Boot application.

1. [Basic username/password Authentication](basicAuthDemo)
2. [JWT Authentication](jwtAuthDemo)

## Authentication using OpenID Connect(OIDC)

### OAuth 2.0
OAuth 2.0 is a widely used authorization framework that allows applications to access resources (such as user data) on behalf of a user without directly handling their credentials.
It is used by major web applications (like Google, Facebook, and GitHub) to provide secure authorization for third-party applications.


### Core Concepts of OAuth 2.0
OAuth 2.0 is centered around the concept of delegating authorization. The key components involved in the OAuth 2.0 flow are:

- `Resource Owner (User):`
  The entity that owns the protected resources (e.g., user’s data or account).
- `Client (Application):`
  The third-party application that wants to access the resource owner's resources.
- `Authorization Server:`
  The server that authenticates the user and issues access tokens.
- `Resource Server:`
  The server that hosts the protected resources and validates access tokens.
- `Access Token:`
  A token that represents the authorization granted to the client to access protected resources.
- `Refresh Token:`
  A token used to obtain new access tokens when the original access token expires.

### OAuth 2.0 Flow Overview
The flow generally involves the following steps:

- `Authorization Request:`
  The client redirects the user to the authorization server to request authorization for access to the user’s resources.
- `Authorization Grant:`
  After the user approves or denies the request, the authorization server sends an authorization code or token back to the client.
- `Token Request:`
  The client exchanges the authorization code for an access token by sending it to the authorization server’s token endpoint.
- `Accessing Resources:`
  The client uses the access token to make requests to the resource server on behalf of the user.
- `Refresh Token (Optional):`
  If the access token expires, the client can use the refresh token to obtain a new access token.

### OAuth 2.0 Grant Types
OAuth 2.0 defines several grant types, each used in different scenarios:

- `Authorization Code Grant (most common for web apps):`
  The client redirects the user to the authorization server, and after the user grants authorization, an authorization code is returned.
  The client exchanges this code for an access token.

- `Implicit Grant (for single-page applications):`
  The access token is directly returned in the URL fragment, skipping the authorization code exchange. This flow is considered less secure and is less common now due to security concerns.

- `Resource Owner Password Credentials Grant (legacy, not recommended):`
  The user provides their username and password directly to the client, which then exchanges these credentials for an access token. This is not recommended because it involves sharing sensitive credentials.

- `Client Credentials Grant (for machine-to-machine communication):`
  The client authenticates itself to the authorization server (usually with a client ID and client secret) to obtain an access token to access its own resources.

- `Device Code Grant (for devices without a browser):`
  The client prompts the user to visit a URL on a separate device (like a mobile phone) to approve the authorization request.

### Step-by-Step Example: Authorization Code Flow
Let’s walk through a typical OAuth 2.0 flow, focusing on the Authorization Code Grant type. Assume the client is a web app trying to access the user's Google account information.

- `Step 1: Authorization Request`
  The client (web app) redirects the user to Google’s authorization server with the following parameters:
    - `response_type`=code (to specify we want an authorization code)
    - `client_id`=your-client-id
    - `redirect_uri`=your-redirect-uri
    - `scope=email` (the permissions the app is requesting, e.g., email access)
    - `state`=random-state-string (used for preventing CSRF attacks)

      Example URL:
      ```shell
      https://accounts.google.com/o/oauth2/v2/auth?
      response_type=code&
      client_id=your-client-id&
      redirect_uri=https://yourapp.com/callback&
      scope=email&
      state=random-state-string
      ```

      The `user is redirected to Google’s login page` (if not already logged in), where they grant or deny the requested permissions.

      If the user approves the request, Google redirects the user back to the `redirect_uri` with an authorization code:

      `https://yourapp.com/callback?code=authorization-code&state=random-state-string`


- `Step 2: Token Request`
  The client (web app) takes the authorization code and sends it to Google’s token endpoint to exchange it for an access token.

    - POST request to the token endpoint:`POST https://oauth2.googleapis.com/token`
    - Request body (using application/x-www-form-urlencoded encoding):
        - `code`=authorization-code
        - `client_id`=your-client-id
        - `client_secret`=your-client-secret
        - `redirect_uri`=https://yourapp.com/callback
        - `grant_type`=authorization_code

        - If everything is valid, the authorization server responds with an `access token` (and optionally a `refresh token`):
          ```json
                  {
                  "access_token": "access-token-value",
                  "token_type": "bearer",
                  "expires_in": 3600,
                  "refresh_token": "refresh-token-value"
                  }
          ```  

- `Step 3: Access Resources` : The client uses the access token to access the user’s resources on the resource server. For example, to fetch the user’s profile from Google:

  GET request to the resource server (Google’s API):

    ```shell
    GET https://www.googleapis.com/oauth2/v2/userinfo`
    Authorization: Bearer access-token-value
    ```

  If the access token is valid, the resource server responds with the user’s data:

    ```json
    {
    "id": "1234567890",
    "email": "user@example.com",
    "name": "John Doe"
    }
    ```
    - `Step 4: Token Refresh (Optional)`If the access token expires, the client can use the refresh token (if provided) to obtain a new access token by making a request to the token endpoint.

      POST request to refresh the access token:

      ``POST https://oauth2.googleapis.com/token``

      Request body:
      ```shel
          grant_type=refresh_token
          refresh_token=refresh-token-value
          client_id=your-client-id
          client_secret=your-client-secret
      ```

  The server responds with a new access token and optionally a new refresh token.

### OpenID Connect (OIDC)
OpenID Connect (OIDC) is an identity layer `built on top of OAuth 2.0` that allows you to :
- authenticate users,
- obtain their profile information, and
- securely manage user sessions.


It is widely used for Single` Sign-On (SSO)` across different applications.

There are many OAuth 2.0 authorization servers like:
- Google,
- Auth0,
- Keycloak.
  etc.

We will use Google.

### OpenID Connect Flow
OpenID Connect uses a flow to authenticate users and obtain their identity. The flow can be broken down into several stages:

- `Authorization Request`: The client application (e.g., your web or mobile app) redirects the user to an OpenID provider (OP), asking for authentication.
- `Authorization Code Grant`: The OpenID provider sends an authorization code to the client.
- `Token Exchange`: The client exchanges the authorization code for tokens (ID Token, Access Token, Refresh Token) at the OP's token endpoint.
- `User Info`: The client may also request user information using the Access Token.

### Steps to Implement OpenID Connect Authentication
Set up an OpenID Provider

To get started with OpenID Connect, you'll need an OpenID Connect provider. You can use a hosted service like:
- Google Identity Platform
- Auth0
- Okta
- Keycloak