package com.jilani.ifta.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u from User u where u.username = :username")
    public User getUserByUsername(@Param("username") String username);

    List<User> findAllByRolesContaining(Role role);
}
