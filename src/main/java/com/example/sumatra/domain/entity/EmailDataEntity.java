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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import java.io.Serial;

@Entity
@Table(name = "email_data",  uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
@org.hibernate.annotations.Table(appliesTo = "email_data", comment = "Table for storing emails")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SequenceGenerator(name = "default_gen", sequenceName = "email_data_seq", allocationSize = 1)

public class EmailDataEntity extends BaseDatedEntity {
	@Serial
	@Transient
	private static final long serialVersionUID = 5495025999817557453L;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(name = "email", nullable = false, columnDefinition = "varchar(200)")
	@Email
	@Length(max = 200, message = "Длина email должна быть до 200 символов")
	private  String email;
}
