package com.prokopchuk.largestnasa.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prokopchuk.largestnasa.exception.NoPictureFoundException;
import com.prokopchuk.largestnasa.service.NasaPicturesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mars/pictures/largest")
public class NasaPicturesController {

    private final NasaPicturesService nasaPicturesService;

    @PostMapping
    public ResponseEntity<Void> postMessage(@RequestBody ObjectNode body) {
        var messageId = nasaPicturesService.postMessage(body.get("sol").asInt(), body.get("camera").asText());
        var location =
          ServletUriComponentsBuilder.fromCurrentRequestUri()
            .pathSegment(messageId)
            .build()
            .toUri();

        return ResponseEntity.status(HttpStatus.CREATED)
          .location(location)
          .build();
    }

    @GetMapping("/{commandId}")
    public ResponseEntity<byte[]> getLargestPicture(@PathVariable String commandId) {
        return ResponseEntity.ok()
          .contentType(MediaType.IMAGE_PNG)
          .body(nasaPicturesService.getLargestPicture(commandId));
    }

    @ExceptionHandler
    public ResponseEntity<String> handleWrongCommand(NoPictureFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
          .body(ex.getMessage());
    }
}
