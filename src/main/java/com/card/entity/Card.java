package com.card.entity;

import com.card.entity.enums.CardType;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Card extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "card_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "card_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="provider_reference_id",nullable = false)
    private String providerReferenceId;

    @Enumerated(EnumType.STRING)
    private CardType type;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Account account;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column
    private String info;

    public Card() {
    }

    public Card(CardType type, Customer customer, Account account) {
        this.type = type;
        this.customer = customer;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderReferenceId() {
        return providerReferenceId;
    }

    public void setProviderReferenceId(String providerReferenceId) {
        this.providerReferenceId = providerReferenceId;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
