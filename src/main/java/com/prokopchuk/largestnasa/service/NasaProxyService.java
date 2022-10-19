package com.prokopchuk.largestnasa.service;

import com.prokopchuk.largestnasa.client.NasaFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NasaProxyService {

    private final NasaFeignClient nasaFeignClient;

    @Cacheable("pictures-cache")
    public byte[] getLargestPicture(int sol, String camera) {
        return nasaFeignClient.getLargestPicture(sol, camera);
    }
}
