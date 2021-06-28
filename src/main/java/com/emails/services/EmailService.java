package com.emails.services;

import com.emails.configurations.PubSubConfig;
import com.emails.dtos.EmailListResponse;
import com.emails.dtos.EmailRequest;
import com.emails.models.Email;
import com.emails.repositories.EmailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    PubSubConfig.responderTodosLosMailsPubsubOutboundGateway responderTodosLosMailsPubsubOutboundGateway;

    @Autowired
    private EmailRepository emailRepository;

    public void saveNewEmail(Email email) {

        emailRepository.save(email);
    }

    public void findAllMailsByUser(EmailRequest emailRequest) throws JsonProcessingException {

        EmailListResponse emailListResponse = EmailListResponse.builder()
                .receivedEmails(emailRepository.findAllByReceiver(emailRequest.getEmail()))
                .sentEmails(emailRepository.findAllBySender(emailRequest.getEmail()))
                .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(emailListResponse);

        responderTodosLosMailsPubsubOutboundGateway.sendToPubsub(json);

    }
}
