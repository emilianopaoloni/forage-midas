package com.jpmc.midascore.entity;

import org.apache.catalina.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;



@Entity
public class TransactionRecord {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private float incentive;

    // Relación de muchos a uno: Muchas transacciones pueden pertenecer a un solo usuario
    @ManyToOne
    private UserRecord sender;

    @ManyToOne
    private UserRecord recipient;

    public float getIncentive() {
		return incentive;
	}

	public void setIncentive(float incentive) {
		this.incentive = incentive;
	}

	private float amount;

    // Constructor vacío obligatorio para JPA
    public TransactionRecord() {}

    // Constructor para facilitarnos la vida
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserRecord getSender() {
		return sender;
	}

	public void setSender(UserRecord sender) {
		this.sender = sender;
	}

	public UserRecord getRecipient() {
		return recipient;
	}

	public void setRecipient(UserRecord recipient) {
		this.recipient = recipient;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

}
