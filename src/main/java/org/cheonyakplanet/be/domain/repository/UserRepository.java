package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("select u.interestLocal1 from User u group by u.interestLocal1 " +
            "order by count(u.interestLocal1) desc ")
    List<String> findInterestLocal1TopByInterestLocal1(Pageable pageable);

    @Query("select u.interestLocal1,u.interestLocal2,u.interestLocal3,u.interestLocal4,u.interestLocal5 from User u where u.email = :email")
    List<Object[]> myInterestLocals(@Param("email") String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByEmailAndUsernameAndDeletedAtIsNull(String email, String username);

}
