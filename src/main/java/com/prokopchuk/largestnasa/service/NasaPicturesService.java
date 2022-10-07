package com.prokopchuk.largestnasa.service;

import java.util.Map;

import com.prokopchuk.largestnasa.client.GetPicturesClient;
import com.prokopchuk.largestnasa.exception.NasaPicturesServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NasaPicturesService {

    private final GetPicturesClient getPicturesClient;
    private final RestTemplate restTemplate;

    @Value("${nasa.api.key}")
    private String apiKey;

    public ResponseEntity<byte[]> getLargestPicture(int sol, String camera) {
        return getPicturesClient.getAllPictures(sol, apiKey, camera)
          .photos()
          .parallelStream()
          .map(picture -> mapToActualUrlImageSizeEntry(picture.ImgSrc()))
          .max(Map.Entry.comparingByValue())
          .map(entry -> restTemplate.getForEntity(entry.getKey(), byte[].class))
          .orElseThrow(() -> new NasaPicturesServiceException(
            String.format("No images found with such query params sol: %d, camera: %s", sol, camera)));
    }

    private Map.Entry<String, Long> mapToActualUrlImageSizeEntry(String url) {
        var resp = restTemplate.exchange(url, HttpMethod.HEAD, null, Void.class);
        if (resp.getStatusCode().is3xxRedirection()) {
            return mapToActualUrlImageSizeEntry(String.valueOf(resp.getHeaders().getLocation()));
        }
        if (resp.getStatusCode().is2xxSuccessful()) {
            return Map.entry(url, resp.getHeaders().getContentLength());
        }
        throw new NasaPicturesServiceException("Wrong image url: " + url);
    }
}
