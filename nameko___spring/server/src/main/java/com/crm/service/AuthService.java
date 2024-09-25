package com.crm.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.crm.dto.AuthResponse;
import com.crm.model.RegisterRequest;
import com.crm.model.SigninRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AuthService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<AuthResponse> registerUser(RegisterRequest request) {
        String namekoServiceUrl = "http://localhost:8000/get/auth_service/register";
        AuthResponse response = executeRequest(namekoServiceUrl, request);
        return createResponseWithCookies(response);
    }

    public ResponseEntity<AuthResponse> signinUser(SigninRequest request) {
        String namekoServiceUrl = "http://localhost:8000/get/auth_service/signin";
        AuthResponse response = executeRequest(namekoServiceUrl, request);
        return createResponseWithCookies(response);
    }

    public ResponseEntity<AuthResponse> logoutUser() {
        try {
            // Create expired cookies to remove tokens
            ResponseCookie accessTokenCookie = createExpiredCookie("access_token");
            ResponseCookie refreshTokenCookie = createExpiredCookie("refresh_token");

            AuthResponse response = new AuthResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Logout successful");

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Logout failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    private AuthResponse executeRequest(String url, Object request) {
        try {
            ResponseEntity<AuthResponse> responseEntity = restTemplate.postForEntity(url, request, AuthResponse.class);
            AuthResponse response = responseEntity.getBody();
            if (response != null) {
                response.setStatus(responseEntity.getStatusCodeValue());
            }
            return response;
        } catch (HttpClientErrorException ex) {
            try {
                return objectMapper.readValue(ex.getResponseBodyAsString(), AuthResponse.class);
            } catch (Exception e) {
                return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()).getBody();
            }
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage()).getBody();
        }
    }

    private ResponseEntity<AuthResponse> createResponseWithCookies(AuthResponse response) {
        if (response.getStatus() == HttpStatus.OK.value() && response.getAccess_token() != null && response.getRefresh_token() != null) {
            ResponseCookie accessTokenCookie = createCookie("access_token", response.getAccess_token(), 3600);
            ResponseCookie refreshTokenCookie = createCookie("refresh_token", response.getRefresh_token(), 7 * 24 * 3600);

            response.setAccess_token(null);
            response.setRefresh_token(null);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                    .body(response);
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

    private ResponseCookie createExpiredCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    private ResponseEntity<AuthResponse> createErrorResponse(HttpStatus status, String errorMessage) {
        AuthResponse errorResponse = new AuthResponse();
        errorResponse.setStatus(status.value());
        errorResponse.setError(errorMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }
}





