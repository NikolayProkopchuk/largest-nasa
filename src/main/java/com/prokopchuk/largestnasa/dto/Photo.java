package com.prokopchuk.largestnasa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Photo(long id, @JsonProperty("img_src") String ImgSrc) {
}
