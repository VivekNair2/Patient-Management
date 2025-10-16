package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics="patient",groupId="analytics-service")
    public void consumeEvent(byte[] eventBytes) {
        // Here you would typically deserialize the eventBytes and process the event.
        // For simplicity, we'll just log that we've received an event.
        System.out.println("Received Kafka event: " + new String(eventBytes));
        try {
            PatientEvent patientEvent=PatientEvent.parseFrom(eventBytes);
            // ... perform any bussiness related to analytics here
            log.info("Received Patient Event: " + patientEvent.toString());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error while parsing Kafka event", e.getMessage());
        }


    }
}
