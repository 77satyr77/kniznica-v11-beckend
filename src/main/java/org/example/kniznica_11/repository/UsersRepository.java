package org.example.kniznica_11.repository;

import org.example.kniznica_11.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByLoginName(String loginName);
}
