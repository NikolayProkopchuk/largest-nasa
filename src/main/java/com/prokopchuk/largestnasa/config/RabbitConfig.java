package com.prokopchuk.largestnasa.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
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
    public Queue largestPictureCommandQueue() {
        return new Queue("largest-picture-command-queue");
    }

    @Bean
    public Exchange messageExchangeFanout() {
        return new FanoutExchange("message-fanout");
    }

    @Bean
    public Binding springQueueBinding() {
        return BindingBuilder
          .bind(largestPictureCommandQueue())
          .to(messageExchangeFanout())
          .with("")
          .noargs();
    }
}
