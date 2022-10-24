package com.prokopchuk.largestnasa;

import java.util.Map;
import java.util.Optional;

import com.prokopchuk.largestnasa.dto.Photo;
import com.prokopchuk.largestnasa.dto.Photos;
import com.prokopchuk.largestnasa.exception.NasaServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NasaPicturesService {

    @Value("${nasa.url}")
    private String nasaUrl;

    @Value("${nasa.apiKey}")
    private String apiKey;

    private final WebClient webClient;

    public Mono<byte[]> getLargestPicture(int sol, String camera) {
        var uri = UriComponentsBuilder.fromUriString(nasaUrl)
          .queryParam("sol", sol)
          .queryParam("api_key", apiKey)
          .queryParamIfPresent("camera", Optional.ofNullable(camera))
          .build().toUri();

        return webClient
          .get()
          .uri(uri)
          .retrieve()
          .bodyToMono(Photos.class)
          .map(Photos::photos)
          .flatMapMany(Flux::fromIterable)
          .mapNotNull(photo -> webClient.head()
            .uri(photo.imgSrc())
            .retrieve()
            .toBodilessEntity()
            .map(responseEntity -> getImageUrlSizeEntry(photo, responseEntity.getHeaders().getContentLength())))
          .flatMap(Mono::flux)
          .reduce((e1, e2) -> e1.getValue() >= e2.getValue() ? e1 : e2)
          .switchIfEmpty(Mono.error(new NasaServiceException("Photos not found with such params")))
          .flatMap(e -> webClient.get()
            .uri(e.getKey())
            .exchangeToMono(clientResponse -> clientResponse.bodyToMono(byte[].class)));
    }

    private Map.Entry<String, Long> getImageUrlSizeEntry(Photo photo, Long size) {
        return Map.entry(photo.imgSrc(), size);
    }
}
