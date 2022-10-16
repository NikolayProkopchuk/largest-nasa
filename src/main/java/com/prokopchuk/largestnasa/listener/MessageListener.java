package com.prokopchuk.largestnasa.listener;

import com.prokopchuk.largestnasa.dto.Message;
import com.prokopchuk.largestnasa.service.NasaPicturesService;
import com.prokopchuk.largestnasa.storage.NasaPictureStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageListener {

    private final NasaPicturesService nasaPicturesService;

    @RabbitListener(queues = "largest-picture-command-queue")
    public void printMessage(Message message) {
        log.info("received message: {}", message.toString());
        nasaPicturesService.saveLargestPicture(message);
    }
}
