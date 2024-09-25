package com.crm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

@GetMapping("/generate")
public ResponseEntity<byte[]> getCaptcha() {
    String url = "http://localhost:8000/get/auth_service/generate_captcha";
    RestTemplate restTemplate = new RestTemplate();

    try {
        // Call the Nameko service to get the CAPTCHA
        String rawResponse = restTemplate.getForObject(url, String.class);
        logger.info("Raw CAPTCHA response: {}", rawResponse);
        
        // Map the raw response to the CaptchaResponse class
        ObjectMapper objectMapper = new ObjectMapper();
        CaptchaResponse captchaResponse = objectMapper.readValue(rawResponse, CaptchaResponse.class);

        if (captchaResponse != null && captchaResponse.getCaptchaImageBase64() != null) {
            byte[] imageBytes = Base64.getDecoder().decode(captchaResponse.getCaptchaImageBase64());

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/png");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            logger.error("CAPTCHA response is null or does not contain image data.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } catch (Exception e) {
        logger.error("Error while calling the Nameko service: {}", e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}




static class CaptchaResponse {
    @JsonProperty("captcha_image_base64")
    private String captchaImageBase64;

    @JsonProperty("captcha_text")
    private String captchaText;

    private int status;

    // Getters and setters
    public String getCaptchaImageBase64() {
        return captchaImageBase64;
    }

    public void setCaptchaImageBase64(String captchaImageBase64) {
        this.captchaImageBase64 = captchaImageBase64;
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


}