package com.example.sumatra.service;

import com.example.sumatra.service.request.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.sumatra.Tables.EMAIL_DATA;
import static com.example.sumatra.Tables.PHONE_DATA;

@Service
@RequiredArgsConstructor
public class RequestHandler {

    private final DSLContext dslContext;

    List<String> extractEmailAddressForSaving(List<UserRegistrationRequest.EmailAddress> emailAddresses) {
        List<String> emailList = emailAddresses.stream().map(UserRegistrationRequest.EmailAddress::getValue).toList();

        List<String> existedNumbers = dslContext.select(EMAIL_DATA.EMAIL)
                .from(EMAIL_DATA)
                .where(EMAIL_DATA.EMAIL.in(emailList))
                .fetchInto(String.class);

        List<String> creationEmailList = emailList.stream().filter(p -> !existedNumbers.contains(p)).toList();

        return creationEmailList;
    }

    List<String> extractPhonesForSaving(List<UserRegistrationRequest.PhoneNumber> phoneNumbers) {
        List<String> phones = phoneNumbers.stream().map(UserRegistrationRequest.PhoneNumber::getValue).toList();

        List<String> existedNumbers = dslContext.select(PHONE_DATA.PHONE)
                .from(PHONE_DATA)
                .where(PHONE_DATA.PHONE.in(phoneNumbers))
                .fetchInto(String.class);

        List<String> creationPhoneList = phones.stream().filter(p -> !existedNumbers.contains(p)).toList();

        return creationPhoneList;
    }
}
