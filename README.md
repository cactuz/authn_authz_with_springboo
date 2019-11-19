# eCommerce Application

This project is a demo of authorization and logging with springboot.

## How to interact with the API
You may refer to src/test/http/ for sample requests
1) First, create a new user: with a POST request:
http://localhost:8080/api/user/create 

```
{
"username" : "bob",
"password" : "password_at_least_7_characters",
  "confirmPassword" : "password_at_least_7_characters"
}
```

2) Then login, with a POST request: http://localhost:8080/login using the credentials set-up in step 1

```
{
    "username": "test",
    "password": "password_at_least_7_characters"
}
```

From the response header copy the JWT from Authorization value, example:
```
Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2IiLCJleHAiOjE1NzQyMTM5MjZ9.1jrsdy6X4mnGtjTEdhvG4f-0d7RwjmhA1St7xIq542u42KoV6PTzugA0jjCQORC_NhQdFISLk9h2NByT3b7g2A
```

3) One can then go ahead and access the other api end points using the JWT in the request header

## References
For the Auth implementation: https://auth0.com/blog/implementing-jwt-authentication-on-spring-boot/