package com.prokopchuk.largestnasa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Photo(@JsonProperty("img_src") String imgSrc) {
}
