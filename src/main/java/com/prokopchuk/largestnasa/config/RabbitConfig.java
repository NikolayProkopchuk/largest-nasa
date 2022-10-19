package com.prokopchuk.largestnasa.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue commandQueue() {

        return QueueBuilder
          .nonDurable("prokopchuk-picture-request-queue")
          .autoDelete()
          .build();
    }

    @Bean
    public Queue resultQueue() {
        return QueueBuilder.nonDurable("picture-result-queue")
          .autoDelete()
          .build();
    }

    @Bean
    public Exchange pictureRequestFanout() {
        return new FanoutExchange("picture-request-fanout");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
          .bind(commandQueue())
          .to(pictureRequestFanout())
          .with("")
          .noargs();
    }
}
