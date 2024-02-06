package com.example.sumatra.domain.entity;

import com.example.sumatra.domain.entity.base.BaseDatedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;
import java.io.Serial;

@Entity
@Table(name = "phone_data",  uniqueConstraints = {@UniqueConstraint(columnNames = {"phone"})})
@org.hibernate.annotations.Table(appliesTo = "phone_data", comment = "Table for storing phones")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SequenceGenerator(name = "default_gen", sequenceName = "phone_data_seq", allocationSize = 1)
public class PhoneDataEntity extends BaseDatedEntity {

	@Serial
	@Transient
	private static final long serialVersionUID = 5465025999817557453L;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(name = "phone", nullable = false, columnDefinition = "varchar(13)")
	@Pattern(regexp = "7\\d{10}", message = "Номер телефона должен соответствовать формату 79999999999")
	private  String phone;
}
