package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TransactionListener {

	@Autowired
    private UserRepository userRepository; // Herramienta para buscar usuarios en H2

    @Autowired
    private TransactionRecordRepository transactionRecordRepository; // Herramienta para guardar la transacción
	
	
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        
        // 1. Buscar si el remitente y el destinatario existen en la base de datos
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // 2. Validar las 3 reglas de negocio
        	//validar que el monto de la transferencia es mayor o igual al monto del usuario emisor
        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            
            // 3. Actualizar los saldos
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount());

            // 4. Guardar los usuarios con sus nuevos saldos en la BD
            userRepository.save(sender);
            userRepository.save(recipient);

            // 5. Crear el registro histórico y guardarlo
            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
            transactionRecordRepository.save(record);
            
            System.out.println("Transacción VÁLIDA procesada por: " + transaction.getAmount());
        } else {
            // Si no cumple las reglas, se descarta (no hacemos nada con la BD)
            System.out.println("Transacción INVÁLIDA descartada.");
        }
    }
    
    
	
	// Esta etiqueta lee el nombre del topic y un groupId desde application.yml
    //@KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    //public void listen(Transaction transaction) {
        
    //    System.out.println("Transacción recibida"); //marco debugg en esta linea
    //}
    
}
