package com.emails.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class PubSubConfig {

    @Bean
    public MessageChannel consultaTodosLosMails() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter consultaTodosLosMailsAdapter(
            @Qualifier("consultaTodosLosMails") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "consulta-todos-los-emails-sub");
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }

    @Bean
    public MessageChannel guardarNuevoEmail() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter guardarNuevoEmailAdapter(
            @Qualifier("guardarNuevoEmail") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "new-email-suscriptor");
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }




    @Bean
    public MessageChannel responderTodosLosMails() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "responderTodosLosMails")
    public MessageHandler responderTodosLosMailsSender(PubSubTemplate pubsubTemplate) {
        return new PubSubMessageHandler(pubsubTemplate, "respuesta-todos-los-emails");
    }

    @MessagingGateway(defaultRequestChannel = "responderTodosLosMails")
    public interface responderTodosLosMailsPubsubOutboundGateway {
        void sendToPubsub(String text);
    }



}
