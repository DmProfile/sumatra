package com.example.sumatra.domain.entity;

import com.example.sumatra.domain.entity.base.BaseDatedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serial;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@org.hibernate.annotations.Table(appliesTo = "user", comment = "Table for storing clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SequenceGenerator(name = "default_gen", sequenceName = "user_seq", allocationSize = 1)
public class UserEntity extends BaseDatedEntity {
    @Serial
    @Transient
    private static final long serialVersionUID = 5495025999817557453L;

    @Column(nullable = false, columnDefinition = "VARCHAR(500)", unique = true)
    private String name;

    @Column(name = "date_of_birth", columnDefinition = "DATE")
    private LocalDate birthdate;

    @Column(nullable = false, name = "password")
    @Length(min = 8, max = 500, message = "Длина пароля должна быть от 8 до 500 символов")
    private String password;
}
