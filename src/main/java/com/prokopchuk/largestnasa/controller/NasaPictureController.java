package com.prokopchuk.largestnasa.controller;

import com.prokopchuk.largestnasa.exception.NasaPicturesServiceException;
import com.prokopchuk.largestnasa.service.NasaPicturesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NasaPictureController {

    private final NasaPicturesService nasaPicturesService;

    @GetMapping("/mars/pictures/largest")
    @Cacheable("largestPictureCache")
    public ResponseEntity<byte[]> getLargestPicture(@RequestParam int sol, @RequestParam String camera) {
        log.info("handle request /mars/pictures/largest?sol={}&camera={}", sol, camera);
        return nasaPicturesService.getLargestPicture(sol, camera);
    }

    @ExceptionHandler
    ResponseEntity<String> handler(NasaPicturesServiceException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exception.getMessage());
    }
}
