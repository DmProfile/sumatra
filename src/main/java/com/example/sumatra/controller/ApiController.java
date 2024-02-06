package com.example.sumatra.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.example.sumatra.Tables.ACCOUNT;
import static com.example.sumatra.Tables.EMAIL_DATA;
import static com.example.sumatra.Tables.PHONE_DATA;
import static com.example.sumatra.Tables.USER;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Работа с пользователями", description = "API для работы с пользователями")
public class ApiController{

    private final DSLContext dslContext;

    @GetMapping("/users/feed/")
    public ResponseEntity<List<UserDetailsDto>> userScrollingFeed(@RequestParam UserSearchRequestDto requestDto,
                                                                  @RequestParam Long lastSeenId,
                                                                  @RequestParam Integer limit) {
        Condition condition = buildCondition(requestDto);

        List<UserDetailsDto> userDetailsDtos = dslContext.select(USER.ID, USER.NAME, EMAIL_DATA.EMAIL, PHONE_DATA.PHONE)
                .from(USER)
                .leftJoin(ACCOUNT).on(USER.ID.eq(ACCOUNT.USER_ID))
                .leftJoin(EMAIL_DATA).on(EMAIL_DATA.USER_ID.eq(ACCOUNT.USER_ID))
                .leftJoin(PHONE_DATA).on(PHONE_DATA.USER_ID.eq(ACCOUNT.USER_ID))
                .where(condition)
                .orderBy(USER.ID)
                .seek(lastSeenId)
                .limit(limit)
                .fetchInto(UserDetailsDto.class);

        return ResponseEntity.ok().body(userDetailsDtos);
    }

    @GetMapping("/users/search/")
    public ResponseEntity<List<UserDetailsDto>> getUsersByFilters(@RequestParam UserSearchRequestDto requestDto,
                                                                  @RequestParam @Positive Integer pageNumber,
                                                                  @RequestParam @Positive Integer limit) {
        int offset = (pageNumber - 1) * limit;

        Condition condition = buildCondition(requestDto);

        List<UserDetailsDto> userDetailsDtos = dslContext.select(USER.ID, USER.NAME, EMAIL_DATA.EMAIL, PHONE_DATA.PHONE)
                .from(USER)
                .leftJoin(ACCOUNT).on(USER.ID.eq(ACCOUNT.USER_ID))
                .leftJoin(EMAIL_DATA).on(EMAIL_DATA.USER_ID.eq(ACCOUNT.USER_ID))
                .leftJoin(PHONE_DATA).on(PHONE_DATA.USER_ID.eq(ACCOUNT.USER_ID))
                .where(condition)
                .orderBy(USER.ID)
                .offset(offset)
                .limit(limit)
                .fetchInto(UserDetailsDto.class);

        return ResponseEntity.ok().body(userDetailsDtos);
    }

    @GetMapping("/users/{id}")
    public String user(@PathVariable Long id) {
        return "user";
    }


    @PostMapping("/users/{id}/mail")
    public ResponseEntity<Long> addMail(@PathVariable Long id, @RequestBody String email) {
        return null;
    }

    @PutMapping("/users/{id}/mail/{email}")
    public ResponseEntity<Void> updateMail(@RequestBody String newMail, @PathVariable String email, @PathVariable String id) {
        return null;
    }

    private Condition buildCondition(UserSearchRequestDto requestDto) {
        return Stream.<Supplier<Optional<Condition>>>of(
                        () -> requestDto.getEmailAddress().map(emailAddress -> EMAIL_DATA.EMAIL.eq(emailAddress.getValue())),
                        () -> requestDto.getUserName().map(userName -> USER.NAME.like(userName.getValue())),
                        () -> requestDto.getPhoneNumber().map(phoneNumber -> PHONE_DATA.PHONE.eq(phoneNumber.getValue())),
                        () -> requestDto.getDateOfBirth().map(dateOfBirth -> USER.DATE_OF_BIRTH.gt(LocalDate.parse(dateOfBirth.getValue())))
                )
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(DSL.trueCondition(), Condition::and);
    }

    @Schema(description = "Поиск пользователя по параметрам")
    @Data
    public static class UserSearchRequestDto {
        @Schema(description = "Дата рождения")
        private Optional<DateOfBirth> dateOfBirth;
        @Schema(description = "Номер телефона")
        private Optional<PhoneNumber> phoneNumber;
        @Schema(description = "Имя пользователя")
        private Optional<UserName> userName;
        @Schema(description = "Адрес электронной почты")
        private Optional<EmailAddress> emailAddress;
        

    }

    @Data
    public static class DateOfBirth {
        @Schema(description = "Дата рождения", example = "1990-01-01")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата рождения должна быть в формате YYYY-MM-DD")
        private String value;
    }

    @Data
    public static class PhoneNumber {
        @Schema(description = "Номер телефона", example = "79139152356")
        @Pattern(regexp = "^\\d{11}$", message = "Номер телефона должен состоять из 11 цифр")
        private String value;
    }

    @Data
    public static class UserName {
        @Schema(description = "Имя пользователя", example = "Иван")
        private String value;
    }

    @Data
    public static class EmailAddress {
        @Schema(description = "Адрес электронной почты", example = "ivan@mail.ru")
        @Email
        private String value;
    }

    @Data
    public static class UserDetailsDto {

        private Long id;
        @Schema(description = "Имя пользователя", example = "Иван")
        private String userName;
        @Schema(description = "Адрес электронной почты", example = "ivan@mail.ru")
        private String emailAddress;
        @Schema(description = "Номер телефона", example = "79139152356")
        private String phoneNumber;

    }
}
