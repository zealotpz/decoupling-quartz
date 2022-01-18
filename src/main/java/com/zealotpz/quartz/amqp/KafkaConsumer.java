package com.zealotpz.quartz.amqp;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

/**
 * description: 测试-> kafka 消费者
 *
 * @author:
 * create: 2020-09-29 14:33
 **/

@Slf4j
@Component
public class KafkaConsumer {

//    @KafkaListener(topics = "test-topic")
    public void listenMsg(ConsumerRecord<?, String> record) {
        String value = record.value();
        log.info("ConsumerMsg====>>" + value);
    }

}
