package com.jpmc.midascore.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jpmc.midascore.entity.TransactionRecord;

@Repository
public interface TransactionRecordRepository extends CrudRepository<TransactionRecord, Long>{
	// JPA se encarga de programar los métodos de guardado.
}

