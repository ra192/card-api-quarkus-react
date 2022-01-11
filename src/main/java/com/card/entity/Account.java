package com.card.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;

import javax.persistence.*;

@Entity
public class Account extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "account_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "account_id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private Boolean active;

    @Column
    private String currency;

    @ManyToOne
    private Merchant merchant;

    public static Uni<Account> findActive(Long id) {
        return find("id=?1 and active = true",id).firstResult();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
