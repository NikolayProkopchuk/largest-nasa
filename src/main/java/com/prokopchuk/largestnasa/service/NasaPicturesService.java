package com.prokopchuk.largestnasa.service;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.prokopchuk.largestnasa.dto.Message;
import com.prokopchuk.largestnasa.exception.NasaPictureException;
import com.prokopchuk.largestnasa.exception.NoPictureFoundException;
import com.prokopchuk.largestnasa.storage.NasaPictureStorage;
import com.prokopchuk.largestnasa.storage.NasaPictureStorageInMemory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NasaPicturesService {

    private final NasaPictureStorage nasaPictureStorage = new NasaPictureStorageInMemory();

    private final RabbitTemplate rabbitTemplate;

    private final RestTemplate restTemplate;

    @Value("${nasa.url}")
    private String nasaUrl;

    @Value("${nasa.apiKey}")
    private String apiKey;

    public String postMessage(int sol, String camera) {
        String messageId = RandomStringUtils.randomAlphanumeric(6);
        Message message = new Message(messageId, sol, camera);
        rabbitTemplate.setExchange("message-fanout");
        rabbitTemplate.convertAndSend(message);

        return messageId;
    }

    public void saveLargestPicture(Message message) {
        var uri = UriComponentsBuilder.fromUriString(nasaUrl)
          .queryParam("sol", message.sol())
          .queryParam("api_key", apiKey)
          .queryParam("camera", message.camera())
          .build()
          .toUri();
        var largestImage = Objects.requireNonNull(restTemplate.getForObject(uri, JsonNode.class))
          .findValuesAsText("img_src")
          .parallelStream()
          .map(this::getActualImageUrlSizePair)
          .max(Map.Entry.comparingByValue())
          .map(entry -> restTemplate.getForObject(entry.getKey(), byte[].class))
          .orElseThrow(() -> new NasaPictureException("No images were found with such params"));

        nasaPictureStorage.save(message.id(), largestImage);
    }

    private Map.Entry<String, Long> getActualImageUrlSizePair(String imgUrl) {
        var responseEntity = restTemplate.exchange(imgUrl, HttpMethod.HEAD, null, void.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return Map.entry(imgUrl, responseEntity.getHeaders().getContentLength());
        }
        if (responseEntity.getStatusCode().is3xxRedirection()) {
            return getActualImageUrlSizePair(String.valueOf(responseEntity.getHeaders().getLocation()));
        }
        throw new NasaPictureException("Wrong image url: " + imgUrl);
    }

    public byte[] getLargestPicture(String commandId) {
        return nasaPictureStorage.getPictureByKey(commandId)
          .orElseThrow(() -> new NoPictureFoundException("No pictures found"));
    }
}
