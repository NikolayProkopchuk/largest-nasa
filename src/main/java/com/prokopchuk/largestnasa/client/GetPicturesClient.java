package com.prokopchuk.largestnasa.client;

import com.prokopchuk.largestnasa.dto.Photos;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "getPicturesClient", url = "${nasa.url}")
public interface GetPicturesClient {

    @GetMapping("${nasa.path}")
    Photos getAllPictures(@RequestParam int sol,
                          @RequestParam("api_key") String apiKey,
                          @RequestParam String camera);

}
