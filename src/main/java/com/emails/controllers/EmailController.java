package com.emails.controllers;

import com.emails.configurations.PubSubConfig;
import com.emails.dtos.EmailRequest;
import com.emails.models.Email;
import com.emails.services.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Bean
    @ServiceActivator(inputChannel = "guardarNuevoEmail")
    public  synchronized MessageHandler recivirNuevoEmail() {
        return message -> {
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            originalMessage.ack();

            String messagePayload = new String((byte[]) message.getPayload());
            Gson g = new Gson();

            emailService.saveNewEmail(g.fromJson(messagePayload, Email.class));
            System.out.println("Email guardado");
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "consultaTodosLosMails")
    public  synchronized MessageHandler consultarTodosLosMails() {
        return message -> {
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            originalMessage.ack();

            String messagePayload = new String((byte[]) message.getPayload());
            Gson g = new Gson();

            EmailRequest emailRequest = g.fromJson(messagePayload, EmailRequest.class);

            try {
                emailService.findAllMailsByUser(emailRequest);
                System.out.println("EmailsConsultados");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
    }



}
