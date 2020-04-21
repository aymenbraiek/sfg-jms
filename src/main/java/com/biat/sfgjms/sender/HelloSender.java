package com.biat.sfgjms.sender;

import com.biat.sfgjms.config.JmsConfig;
import com.biat.sfgjms.model.HelloWorldMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HelloSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;


    @Scheduled(fixedRate = 2000)
    public void sendMessage() {
        // System.out.println("i am Sending a message");
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("hello world!!!!")
                .build();
        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
        //System.out.println("Message sent !!!!!");
    }

    @Scheduled(fixedRate = 2000)
    public void sendAndReceiveMessage() throws JMSException {
        // System.out.println("i am Sending a message");
        HelloWorldMessage message = HelloWorldMessage.builder()
                .id(UUID.randomUUID())
                .message("hello world!!!!")
                .build();
        Message recevieMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException, JmsException {
                Message helloMessage = null;
                try {
                    helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
                    helloMessage.setStringProperty("_type", "com.biat.sfgjms.model.HelloWorldMessage");
                    return helloMessage;
                } catch (JsonProcessingException e) {
                    //  e.printStackTrace();
                    throw new JMSException("boom");
                }

            }
        });
        System.out.println(recevieMsg.getBody(String.class));
    }
}
