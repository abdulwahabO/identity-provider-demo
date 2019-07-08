[![Build Status](https://travis-ci.org/abdulwahabO/identity-provider-demo.svg?branch=master)](https://travis-ci.org/abdulwahabO/identity-provider-demo)

# About

A small demo showing how redirection-based OAuth2 flows could support passwordless authentication using one-time passwords(OTP) sent via SMS.

# Modules

`server`: a simple OAuth2 server implemented with Spring Boot. During startup data for clients and users is dumped into it's database from `src\main\resources\data.sql`.

`client`: a simple OAuth2 client implemented with Spring Boot. It uses the Authorization code flow to interact with the server.

# How it works

The client redirects to the OAuth2 server's login page using their client id as request param. An  OTP is sent to the user via text. The OAuth2 server provides a form for the OTP, checks it for validity and redirects back to the client with either an access token or an authorization code depending on the grant type in use. 

The OAuth2 server supports only two grant types, Implicit and Authorization code. For the `Implicit` grant type, the server redirects to the client's specified callback with a JWT access token in the URL's hash segment. For `Authorization code`, the server redirects to the client with an authorization code in the URL params. The client then uses the code and their client secret to obtain a JWT access token via an API endpoint.

Notes: 

* The SMS gateway used in this demo only supports Nigerian numbers.

* TOTP(Time-Based One-Time Password) and HOTP(HMAC-Based One-Time Password)
are (perhaps more secure) alternatives to using SMS passcodes. SMS was used in this demo for the sake of simplicity.

# Tech Stack

* Java with Spring Boot, Spring Data JPA etc.
* Apache HTTP Library for exchanging auth code for access token.
* Thymeleaf for UI in both modules.
* Auth0's [Java JWT Library](https://github.com/auth0/java-jwt)
* Unit testing is done with JUnit 4 and Mockito.
* Maven for dependency management.

# Development

To build and run the project locally, use `./mvnw spring-boot:run -P{profile-name}` at the root of both modules. Client and server will startup on the ports specified in the Maven profile for the project. Both applications are served from embedded Tomcat containers.

`./mvnw test` will run all unit tests.

Sample Maven profile showing the properties needed for the project to deploy successfully.

```xml
<profile>
	<id>oauth2-demo</id>
	
	<properties>
	
		<!-- Spring Boot embedded Server details -->
		<client.port>8080</client.port>
		<authserver.server.port>8081</authserver.server.port>

		<client.id>id</client.id>
		<client.secret>secret</client.secret>
		
		<authserver.baseUrl>http://localhost:8081</authserver.baseUrl>				
		<bulksmsng.apiToken>{SMS gateway token obtainable from https://www.bulksmsnigeria.com/}</bulksmsng.apiToken>
			
		<!-- DB config -->
		<database.url>jdbc:mysql://localhost:3306/{DB_NAME}?useJDBCCompliantTimezoneShift=true&amp;useLegacyDatetimeCode=false&amp;serverTimezone=CET</database.url>
		
		<database.password>password</database.password>
		<database.username>username</database.username>			
	</properties>
  </profile>
```

# User Login Demo

![User Login Demo](demo.gif)