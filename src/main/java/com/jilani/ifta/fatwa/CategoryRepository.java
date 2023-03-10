package com.jilani.ifta.fatwa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category getCategoryByName(String name);
}
