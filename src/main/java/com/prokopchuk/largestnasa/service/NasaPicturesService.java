package com.prokopchuk.largestnasa.service;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.prokopchuk.largestnasa.dto.CommandMessage;
import com.prokopchuk.largestnasa.dto.Picture;
import com.prokopchuk.largestnasa.dto.ResultMessage;
import com.prokopchuk.largestnasa.dto.User;
import com.prokopchuk.largestnasa.exception.NasaPictureServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NasaPicturesService {

    @Value("${nasa.url}")
    private String nasaUrl;

    @Value("${nasa.apiKey}")
    private String nasaApiKey;

    private final RestTemplate restTemplate;

    public ResultMessage getLargestPicture(CommandMessage commandMessage) {
        var uri =
          UriComponentsBuilder.fromUriString(nasaUrl)
            .queryParam("sol", commandMessage.sol())
            .queryParam("api_key", nasaApiKey)
            .queryParam("camera", commandMessage.camera())
            .build().toUri();

        return Objects.requireNonNull(restTemplate.getForObject(uri, JsonNode.class))
          .findValuesAsText("img_src")
          .parallelStream()
          .map(this::getActualImageUrlSizeEntry)
          .max(Map.Entry.comparingByValue())
          .map(entry -> ResultMessage.builder()
            .request(commandMessage)
            .user(new User("Nikolas", "Prokopchuk"))
            .picture(new Picture(entry.getKey()))
            .build())
          .orElseThrow(
            () -> new NasaPictureServiceException("Pictures were not found with such params: " + commandMessage));
    }

    private Map.Entry<String, Long> getActualImageUrlSizeEntry(String url) {
        var resp = restTemplate.exchange(url, HttpMethod.HEAD, null, Void.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return Map.entry(url, resp.getHeaders().getContentLength());
        }
        if (resp.getStatusCode().is3xxRedirection()) {
            return getActualImageUrlSizeEntry(String.valueOf(resp.getHeaders().getLocation()));
        }
        throw new NasaPictureServiceException("wrong image url: " + url);
    }
}
