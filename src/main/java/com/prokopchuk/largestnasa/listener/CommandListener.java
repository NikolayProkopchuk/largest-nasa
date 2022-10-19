package com.prokopchuk.largestnasa.listener;

import com.prokopchuk.largestnasa.dto.CommandMessage;
import com.prokopchuk.largestnasa.dto.ResultMessage;
import com.prokopchuk.largestnasa.service.NasaPicturesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandListener {

    private final AmqpTemplate amqpTemplate;

    private final NasaPicturesService nasaPicturesService;

    @RabbitListener(queues = "prokopchuk-picture-request-queue")
    public void requestMessageListener(CommandMessage commandMessage) {
        log.info("process message: {}", commandMessage);
        var result = nasaPicturesService.getLargestPicture(commandMessage);
        amqpTemplate.convertAndSend("picture-result-queue", result);
    }

    @RabbitListener(queues = "prokopchuk-picture-request-dlq")
    public void requestDeadMessageListener(CommandMessage commandMessage) {
        log.info("dead message: {}", commandMessage);
    }

    @RabbitListener(queues = "picture-result-queue")
    public void resultMessageListener(ResultMessage resultMessage) {
        log.info(String.valueOf(resultMessage));
    }

}
