package com.prokopchuk.largestnasa.dto;

import lombok.Builder;

@Builder
public record ResultMessage(User user, CommandMessage request, Picture picture) {
}
