// ============================================
// UserRepository.java
// ============================================
package com.provectus.formula.repository;

import com.provectus.formula.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.birthday < :date")
    List<User> findUsersBornBefore(@Param("date") LocalDate date);
}
