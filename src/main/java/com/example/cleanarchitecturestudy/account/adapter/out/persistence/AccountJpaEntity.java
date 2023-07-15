package com.example.cleanarchitecturestudy.account.adapter.out.persistence;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "account")
public class AccountJpaEntity {

    @Id
    @GeneratedValue
    private Long id;
}
