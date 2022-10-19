package com.prokopchuk.largestnasa.controller;

import com.prokopchuk.largestnasa.service.NasaProxyService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/mars/pictures/largest")
@RequiredArgsConstructor
@Slf4j
public class NasaProxyController {

    private final NasaProxyService nasaProxyService;

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getLargestPicture(int sol, @RequestParam(required = false) String camera) {
        return nasaProxyService.getLargestPicture(sol, camera);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleFeignClientException(FeignException ex) {
        log.error("Feign exception occurred. Status code: {}, message: {}", ex.status(), ex.getMessage());
        return ResponseEntity.badRequest().body("Arguments are invalid. Please check");
    }
}
