package com.jpmc.midascore.component;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
// Asegúrate de importar tu User y UserRepository correctamente

// 1. @RestController le dice a Java: "Esta clase es una ventanilla de API"
@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    // 2. @GetMapping le dice: "Cuando alguien pregunte por la ruta /balance, ejecuta este método"
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam(name = "userId") Long userId) {
        
        // 3. Usamos la herramienta que ya conoces para buscar en la base de datos
    	UserRecord user = userRepository.findById(userId).orElse(null);
        // 4. Aplicamos la regla de negocio que pidieron las instrucciones
        if (user != null) {
            // Si existe, devolvemos un objeto Balance con su saldo real
            return new Balance(user.getBalance());
        } else {
            // Si no existe, devolvemos un objeto Balance con 0
            return new Balance(0);
        }
    }
}