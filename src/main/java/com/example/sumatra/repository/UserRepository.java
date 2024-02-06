package com.example.sumatra.repository;

import com.example.sumatra.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("""
    select count(e) > 0
    from EmailDataEntity e
    inner join UserEntity u on e.user.id = u.id
    where e.email = :email and u.password = :password
    """)
    Boolean existByEmailAndPassword(String email, String password);
}
