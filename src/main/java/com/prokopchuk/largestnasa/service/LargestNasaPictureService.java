package com.prokopchuk.largestnasa.service;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prokopchuk.largestnasa.exception.WrongUrlException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LargestNasaPictureService {

    private final RestTemplate restTemplate;

    public ResponseEntity<byte[]> getLargestNasaPicture(int sol, String camera) {

        var body = restTemplate.getForObject(
          "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol={sol}&api_key={apiKey}&camera={camera}",
          ObjectNode.class, sol, "DEMO_KEY", camera);

        assert body != null;

        return body.findValuesAsText("img_src").stream()
          .map(this::getActualImgUrlSizeEntry)
          .max(Map.Entry.comparingByValue())
          .map(entry -> restTemplate.getForEntity(entry.getKey(), byte[].class))
          .orElseThrow(() -> new  WrongUrlException(String.format("Images weren't found by such sol=%d, camera=%s", sol, camera)));
    }

    private Map.Entry<String, Long> getActualImgUrlSizeEntry(String url) {
        var response = restTemplate.exchange(url, HttpMethod.HEAD, null, Void.class);
        if (response.getStatusCode().is3xxRedirection()) {
            return getActualImgUrlSizeEntry(String.valueOf(response.getHeaders().getLocation()));
        }
        if (response.getStatusCode().is2xxSuccessful()) {
            return Map.entry(url, response.getHeaders().getContentLength());
        }
        throw new WrongUrlException("Wrong image url " + url);
    }
}
