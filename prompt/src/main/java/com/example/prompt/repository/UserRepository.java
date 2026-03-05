package com.example.prompt.repository;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.repository.Repository;

interface UserRepository extends Repository<AbstractPersistable, PK> {
}
