# Getting the authorisation server started

## Getting Config Info
The below request returns some config info about the server, such as endpoints:
```
GET http://localhost:8080/.well-known/openid-configuration
```

## Requesting the token
The below will need to be pasted into the browser once the authorisation server has started:
```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=https://www.manning.com/authorized&code_challenge=iMnq5o6zALKXGivsnlom_0F5_WYda32GHkxlV7mq7hQ&code_challenge_method=S256
```
Note all the query params. Especially, the `code_challenge` which is the hashed version of the `code_verifier` used in 
the next step. The hashing algorithm is specified by the `code_challenge_method` param (e.g., SHA-256, in this case).

When sending the above request in the browser, you will be prompted to login with the user credentials (not the same as the 
registered client credentials). These user credentials are an individual user's details. The registered client credentials
are the client server's credentials. 

Once logged in, you should be redirected to the `redirect_url` with a query param called `code`.
This code refers to the code you will send to the authorisation server to get the JWT Token. 

E.g., `https://www.manning.com/authorized?code=ZFCnie_bmWBa9txy_ML0isLWOqsVr-5M5AaYj4ce4dje1PgemF-OdnnV2kCQQQI4xB3f2IIBs-CeYg2PzaXkCh7GgJi1E4tBJnlzbEfl6YDzr2GzX_7gBF9BG2eDmJ54`

You must copy and paste this code into the `code` field in the next section.

## Next
You will then need to send another HTTP request to the server using the basic auth credentials set in the registered client.

### URL
```
POST http://localhost:8080/oauth2/token
```
### Full cURL request
x-www-form-urlencoded (not JSON)
```
curl --location 'http://localhost:8080/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic Y2xpZW50OnNlY3JldA==' \
--data-urlencode 'client_id=client' \
--data-urlencode 'redirect_uri=https://www.manning.com/authorized' \
--data-urlencode 'grant_type=authorization_code' \
--data-urlencode 'code=ZFCnie_bmWBa9txy_ML0isLWOqsVr-5M5AaYj4ce4dje1PgemF-OdnnV2kCQQQI4xB3f2IIBs-CeYg2PzaXkCh7GgJi1E4tBJnlzbEfl6YDzr2GzX_7gBF9BG2eDmJ54' \
--data-urlencode 'code_verifier=verifier'
```

Alternatively, in Postman:

![token request example](docs/token_request.png)

This method uses basic auth with a username `client` and password `password`. These are the same credentials used to 
set up the registration client in the config (or, in the future, the registration client stored in the db).

Note, if you used a SHA-256 hashing algorithm on the `code_verifier` value, the result would be the `code_challenge` value
passed in to the earlier GET request.

The response from the above POST request gives you the access token needed for authorisation.

## Decoding the JWT

If you use a JWT decoder like [jwt.io](jwt.io), you can look at what's inside the token. You will be able to see 
the header, payload, and signature used to sign the token. For example:

### Header
```json
{
  "kid": "9e204893-8664-4ede-9aed-142448654d21",
  "alg": "RS256"
}
```

### Payload
```json
{
  "sub": "bill",
  "aud": "client",
  "nbf": 1734197141,
  "scope": [
    "openid"
  ],
  "iss": "http://localhost:8080",
  "exp": 1734197441,
  "iat": 1734197141,
  "jti": "870a2111-3bf6-490d-b36c-b8643578f24c"
}
```

### Verify Signature
```
RSASHA256(
base64UrlEncode(header) + "." +
base64UrlEncode(payload),

Public Key in SPKI, PKCS #1, X.509 Certificate, or JWK string format.
,

Private Key in PKCS #8, PKCS #1, or JWK string format. The key never leaves your browser.

)
```

Note that, because this project uses a RSA public/private key, the token gets signed with this.

No passwords are sent in this request, you can just see the usernames `sub` (bill) and the `aud` (client) which relates to the
user's credentials and the registered client's credentials respectively.

## Understanding RSA

With RSA, tokens are signed with the private key, and verified with the public key. Only the authorisation server knows
the private key. Whereas, other apps can use the public key to validate the token.

### Validation
Validation is a way of checking that the JWT was sent from the source you expect. The private/public key relationship
is how this works. The public key validates that the JWT was sent from somewhere that signed the token using the private key.

If the validation fails, the JWT may have been sent from an unknown source and so is rejected.

RSA Keys in JSON form look like this:
```json
{
    "p": "23mpJcBVZTvjdv66D1lNY36oMAYKnyrR3N3YzznlWQLKD_BbSmmFkpR1T4wCy5qcmbxeUXpG5XRdwkF-DQ0emY3tBwmeGz9Pf3N-QiVMIabT5y-Fvwy_3iqa1PID4DS96Aegt6tbJbRKpBCyBRmxwt5zMj2zIgwMFwWK0WBvgv0",
    "kty": "RSA",
    "q": "-oSDDZRfw-wLcjdIAOBSJapWwMcx_qYJgGJ1b1klF-ECGJeTnVoeVr_AvE1xKZGtbWEWLVXjbGBe4bNzIx1AesegYOdArQkbtKL2zeBTLSP2ypqVfZwHyW2SYlxWBXI4q3OCihvZvTPgn747h5V3_kE6CPg2tCCYjoOKMzFr5G8",
    "d": "CiwbnGjJwYEEOgNwb-3FWe8qdx--dUi7pdUbyFzH-ZUq6sC0NhLouiIGNwUBZ7Z5_mw0ujCOp8riaJwTastDyDZJq34eBMYVP08EjfiAStafxoXQlGmHEgOt5vVVfdfjZPNMlPVlM61g5e9I_5xNm2O5GMZTyt2fT6y4xrgO5jern6oHfsH4xkkLalyrh2WH2fy5SCc98QevUOueiP_fiXr5wEDHViojHIk9ug9ngTsNzC30n8QfN4W_c3QGsCKCtONOklZc3kjZ8LkKRxMK8XrSp3C8f47kj3No4VMuM_s-WpWF9vtS28YAyN0BIqxWybQaBbD3O5g5Gsvs5HlpAQ",
    "e": "AQAB",
    "kid": "1698b303-8646-48e6-84c0-62fee48687cf",
    "qi": "ZXa0Wl5bmg6vAWtQrqnxRx7BVS1AxLGe9DklGA-Fm0GHhnm6l129IhqTIOsOwh1ohDVzWqY8NqafSOu0i98XcZ5kc3pzU7KzyirF7dzEKWPJXxicopPs-CUX0RK4qR0uOjI6MadIvJhQFleSRuG-0lhsUnG21NSMnEXGVD6ftxQ",
    "dp": "ibBisJ9gM5mF05Js8dIpneDI7Q_tO_vmGzVGnoMI4nXAhI1bCGz48oOWsSf6XPeLhVtKpL0cmzkM1SC3TsLLTrReXornGNr4KdIwBJlXkMkTqbcDpl7-RAfgiPWn_tG3zkhyyKeFEDtylEkxFcgP4FwkUwGoTxTVxN-iPkbMzpU",
    "dq": "3DdkJKnLTSTQIwTpbKvESovkWu4_yoQWUh868DjC3KzU9N05y9aOWN-TA3RoB4yhkUcJAa1Aj-JfRbRDgBiUt9mH5-cg-XrKOg4POob5VvMIXK1qv9JLHgwN5B0bGQqBOmX92H4G98UErifBmLwRkRWxeHUJUWpd8Nk8wSeumk0",
    "n": "1sZqRPs66SLIKtvp2EO8BFTRA3sumapNvMNuowP8kOtB-Qp4pDdS49sUiimaHwAkCG3JGtPdD01gnjm78rIG4nrgegtXVMq5L9teww5fSSXS5hG9i2PqhNhnzh9gl4lW1QFyKmPOmprE5E3EhwkB9DvAXvZ60e5dd8FbVdeOX48vApj_RadEOo64qsxyNcB2pWoYyqQMBhq7ptDWtTgC0N7mKHjEtT8Rd4oG1-WSQZ0U6FJAmsiWFyAvRblIQuHz9GYajP0RPlVy5eDbP3aRJP_RpXAD8Pojf8MEO41ScvVy-NF83xC_3QYIVVfw8kD3uaWe_MH-zES0TMvBTcIfsw"
}
```
