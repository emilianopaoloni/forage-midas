package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

	// Esta etiqueta lee el nombre del topic y un groupId desde application.yml
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        
        System.out.println("Transacción recibida"); //marco debugg en esta linea
    }
    
}
