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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "account")
@org.hibernate.annotations.Table(appliesTo = "account", comment = "Table for storing accounts")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SequenceGenerator(name = "default_gen", sequenceName = "account_seq", allocationSize = 1)
public class AccountEntity extends BaseDatedEntity {
    @Serial
	@Transient
	private static final long serialVersionUID = 4164818603740725715L;

	@Column(name = "balance", nullable = false, columnDefinition = "DECIMAL (19,4)")
	private BigDecimal balance = new BigDecimal(0);

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserEntity user;


	@Column(name = "init_balance", nullable = false, columnDefinition = "DECIMAL (19,4)")
	private BigDecimal initBalance = new BigDecimal(0);

	@Override
	@PrePersist
	protected void onPrePersist()  {
		initBalance = balance;
	}
}
