package com.prokopchuk.largestnasa.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "largestPictureClient", url = "${thirdParty.service.url}")
public interface NasaFeignClient {

    @GetMapping("/mars/pictures/largest")
    byte[] getLargestPicture(int sol, @RequestParam(required = false) String camera);
}
