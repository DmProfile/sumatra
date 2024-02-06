package com.example.sumatra.service;

import com.example.sumatra.domain.entity.AccountEntity;
import com.example.sumatra.domain.entity.UserEntity;
import com.example.sumatra.repository.AccountRepository;
import com.example.sumatra.service.request.UserRegistrationRequest;
import com.example.sumatra.tables.records.EmailDataRecord;
import com.example.sumatra.tables.records.PhoneDataRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.sumatra.Sequences.EMAIL_DATA_SEQ;
import static com.example.sumatra.Sequences.PHONE_DATA_SEQ;
import static com.example.sumatra.Sequences.USER_SEQ;
import static com.example.sumatra.Tables.EMAIL_DATA;
import static com.example.sumatra.Tables.PHONE_DATA;
import static com.example.sumatra.Tables.USER;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final DSLContext dslContext;
    private final RequestHandler requestHandler;
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public boolean registerUser(UserRegistrationRequest event) {
        try {
            Long userId = createUser(event);
            saveUserDetails(event, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Long createUser(UserRegistrationRequest event) {
        UserRegistrationRequest.UserName userName = event.getUserName();
        UserRegistrationRequest.UserPassword userPassword = event.getUserPassword();
        return createUserRecord(userName, event.getDateOfBirth().getValue(), userPassword);
    }

    private void saveUserDetails(UserRegistrationRequest event, Long userId) {
        saveUserPhones(event.getPhoneNumbers(), userId);
        saveUserMails(event.getEmailAddresses(), userId);
        UserRegistrationRequest.InitBalance initBalance = event.getInitBalance();
        saveUserAccount(userId, initBalance.getValue());
    }

    private void saveUserAccount(Long userId, String initBalance) {
        UserEntity managedUserEntity = entityManager.getReference(UserEntity.class, userId);
        AccountEntity accountEntity = AccountEntity.builder()
                .user(managedUserEntity)
                .balance(BigDecimal.valueOf(Double.parseDouble(initBalance)))
                .build();
        accountRepository.save(accountEntity); // Assume AccountRepository is a separate service/component
    }

    private void saveUserMails(List<UserRegistrationRequest.EmailAddress> emailAddresses, Long userId) {
        List<String> addressForSaving = requestHandler.extractEmailAddressForSaving(emailAddresses);
        List<EmailDataRecord> emailDataRecords = addressForSaving.stream()
                .map(s -> getEmailDataRecord(s, userId))
                .toList();

        dslContext.batchInsert(emailDataRecords)
                .execute();

    }

    private EmailDataRecord getEmailDataRecord(String email, Long userId) {
        EmailDataRecord emailDataRecord = EMAIL_DATA.newRecord();
        emailDataRecord.setCreateDate(LocalDateTime.now());
        emailDataRecord.setUpdateDate(LocalDateTime.now());
        emailDataRecord.setEmail(email);
        emailDataRecord.setUserId(userId);
        emailDataRecord.setId(dslContext.nextval(EMAIL_DATA_SEQ));
        return emailDataRecord;
    }

    private Long createUserRecord(UserRegistrationRequest.UserName userName, String birthDate, UserRegistrationRequest.UserPassword userPassword) {
        return dslContext.transactionResult(configuration -> {
            Long nextval = dslContext.nextval(USER_SEQ);
            return dslContext.insertInto(USER, USER.ID, USER.NAME, USER.DATE_OF_BIRTH, USER.PASSWORD, USER.CREATE_DATE, USER.UPDATE_DATE)
                    .values(nextval, userName.getValue(), LocalDate.parse(birthDate), userPassword.getValue(), LocalDateTime.now(), LocalDateTime.now())
                    .returningResult(USER.ID)
                    .fetchOneInto(Long.class);
        });
    }

    private void saveUserPhones(List<UserRegistrationRequest.PhoneNumber> phoneNumbers, Long userId) {
        List<String> newPhones = requestHandler.extractPhonesForSaving(phoneNumbers);
        List<PhoneDataRecord> phoneDataRecords = newPhones.stream()
                .map(phoneNumber -> getPhoneDataRecord(phoneNumber, userId))
                .toList();

        dslContext.batchInsert(phoneDataRecords)
                .execute();
    }

    private PhoneDataRecord getPhoneDataRecord(String e, Long newUserId) {
        PhoneDataRecord phoneDataRecord = PHONE_DATA.newRecord();
        phoneDataRecord.setCreateDate(LocalDateTime.now());
        phoneDataRecord.setUpdateDate(LocalDateTime.now());
        phoneDataRecord.setPhone(e);
        phoneDataRecord.setUserId(newUserId);
        phoneDataRecord.setId(dslContext.nextval(PHONE_DATA_SEQ));
        return phoneDataRecord;
    }
}
