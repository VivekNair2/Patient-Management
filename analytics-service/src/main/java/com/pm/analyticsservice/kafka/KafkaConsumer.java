// java
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

    @KafkaListener(topics = "patient", groupId = "analytics-service")
    public void consumeEvent(byte[] eventBytes) {
        if (eventBytes == null || eventBytes.length == 0) {
            log.warn("RECEIVED EMPTY KAFKA PAYLOAD");
            return;
        }

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(eventBytes);
            log.info("RECEIVED PATIENT EVENT: ID={} NAME={} TYPE={}",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEventType());
            // ... perform business logic here
        } catch (InvalidProtocolBufferException e) {
            log.error("FAILED TO PARSE PATIENTEVENT PROTOBUF", e);
        } catch (Exception e) {
            log.error("UNEXPECTED ERROR PROCESSING PATIENT EVENT", e);
        }
    }
}
