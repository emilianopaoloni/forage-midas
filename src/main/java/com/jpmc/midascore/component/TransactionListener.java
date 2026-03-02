package com.jpmc.midascore.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate; // Importamos RestTemplate

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive; // Importamos tu nueva clase
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
// Asegúrate de que estén los demás imports (User, Transaction, etc.)
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    // Instanciamos nuestro "navegador interno" para llamar a la API
    private final RestTemplate restTemplate = new RestTemplate();

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            
            //  Llamada a la API de Incentivos ---
            // enviar la transacción a la URL y le decimos que esperamos un objeto Incentive de vuelta
            Incentive incentive = restTemplate.postForObject(
                    "http://localhost:8080/incentive", 
                    transaction, 
                    Incentive.class
            );
            
            // extraer el número (si la API falla por algo, ponemos 0 como red de seguridad)
            float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

            // --- ACTUALIZADO: Nueva lógica de saldos ---
            // Al remitente SOLO se le descuenta lo que transfirió (no paga el incentivo)
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            
            // Al destinatario se le suma la transferencia + el regalo del banco (incentivo)
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

            userRepository.save(sender);
            userRepository.save(recipient);

            // --- ACTUALIZADO: Guardamos el registro con los 4 datos ---
            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
            transactionRecordRepository.save(record);
            
            System.out.println("VÁLIDA - Monto: " + transaction.getAmount() + " | Incentivo: " + incentiveAmount);
        } else {
            System.out.println("Transacción INVÁLIDA descartada.");
        }
    }
}