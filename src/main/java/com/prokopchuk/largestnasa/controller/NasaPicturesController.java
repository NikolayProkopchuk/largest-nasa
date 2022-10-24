package com.prokopchuk.largestnasa.controller;

import com.prokopchuk.largestnasa.NasaPicturesService;
import com.prokopchuk.largestnasa.exception.NasaServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class NasaPicturesController {

    private final NasaPicturesService nasaPicturesService;

    @GetMapping(value = "/mars/pictures/largest", produces = MediaType.IMAGE_JPEG_VALUE)
    public Mono<byte[]> getLargestPicture(int sol, String camera) {
        return nasaPicturesService.getLargestPicture(sol, camera);
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(NasaServiceException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
