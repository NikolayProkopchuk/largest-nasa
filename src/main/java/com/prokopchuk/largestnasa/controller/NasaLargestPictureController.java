package com.prokopchuk.largestnasa.controller;

import com.prokopchuk.largestnasa.exception.WrongUrlException;
import com.prokopchuk.largestnasa.service.LargestNasaPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NasaLargestPictureController {

    private final LargestNasaPictureService largestNasaPictureService;

    @Cacheable("largestPictureCache")
    @GetMapping("/mars/pictures/largest")
    public ResponseEntity<byte[]> getLargestPicture(@RequestParam int sol, @RequestParam String camera) {

        return largestNasaPictureService.getLargestNasaPicture(sol, camera);
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(WrongUrlException wrongUrlException) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(wrongUrlException.getMessage());
    }
}
