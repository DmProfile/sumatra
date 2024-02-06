package com.example.sumatra.service.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationRequest {
    @JsonProperty("userName")
    private UserName userName;

    @JsonProperty("initBalance")
    private InitBalance initBalance;

    @JsonProperty("phoneNumbers")
    private List<PhoneNumber> phoneNumbers;

    @JsonProperty("emailAddresses")
    private List<EmailAddress> emailAddresses;

    @JsonProperty("dateOfBirth")
    private DateOfBirth dateOfBirth;

    @JsonProperty("userPassword")
    private UserPassword userPassword;

    @JsonCreator
    public UserRegistrationRequest(@JsonProperty("userName") UserName userName,
                                   @JsonProperty("initBalance") InitBalance initBalance,
                                   @JsonProperty("phoneNumbers") List<PhoneNumber> phoneNumbers,
                                   @JsonProperty("emailAddresses") List<EmailAddress> emailAddresses,
                                   @JsonProperty("dateOfBirth") DateOfBirth dateOfBirth,
                                   @JsonProperty("userPassword") UserPassword userPassword) {
        this.userName = userName;
        this.initBalance = initBalance;
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
        this.dateOfBirth = dateOfBirth;
        this.userPassword = userPassword;
    }

    @JsonCreator
    public static UserRegistrationRequest userRegistrationRequest(String json) {
        return JsonParser.parse(json);
     }

    public static UserRegistrationRequestBuilder builder() {
        return new UserRegistrationRequestBuilder();
    }

    public static class JsonParser {
        public static UserRegistrationRequest parse(String json) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(json, UserRegistrationRequest.class);
            } catch (IOException e) {
                throw new RuntimeException("Error parsing JSON", e);
            }
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DateOfBirth {
        @Schema(description = "Дата рождения", example = "1990-01-01")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата рождения должна быть в формате YYYY-MM-DD")
        @JsonProperty("value")
        private String value;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhoneNumber {
        @Schema(description = "Номер телефона", example = "79139152356")
        @Pattern(regexp = "^\\d{11}$", message = "Номер телефона должен состоять из 11 цифр")
        @JsonProperty("value")
        private String value;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserName {
        @Schema(description = "Имя пользователя", example = "Иван")
        @JsonProperty("value")
        private String value;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailAddress {
        @Schema(description = "Адрес электронной почты", example = "ivan@mail.ru")
        @Email
        @JsonProperty("value")
        private String value;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InitBalance {
        @Pattern(regexp = "^\\d{16}.\\d{2}$", message = "Начальный баланс должен быть в формате 1234567890.00")
        @JsonProperty("value")
        private String value;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserPassword {
        @Length(min = 8, max = 500, message = "Пароль должен содержать не менее 8 символов")
        @JsonProperty("value")
        private String value;
    }

    public static class UserRegistrationRequestBuilder {
        private final UserRegistrationRequest instance = new UserRegistrationRequest();

        public UserRegistrationRequestBuilder userName(String userName) {
            instance.setUserName(new UserName(userName));
            return this;
        }

        public UserRegistrationRequestBuilder initBalance(String initBalance) {
            instance.setInitBalance(new InitBalance(initBalance));
            return this;
        }

        public UserRegistrationRequestBuilder phoneNumbers(List<PhoneNumber> phoneNumbers) {
            instance.setPhoneNumbers(phoneNumbers);
            return this;
        }

        public UserRegistrationRequestBuilder emailAddresses(List<EmailAddress> emailAddresses) {
            instance.setEmailAddresses(emailAddresses);
            return this;
        }

        public UserRegistrationRequestBuilder dateOfBirth(String dateOfBirth) {
            instance.setDateOfBirth(new DateOfBirth(dateOfBirth));
            return this;
        }

        public UserRegistrationRequestBuilder userPassword(String userPassword) {
            instance.setUserPassword(new UserPassword(userPassword));
            return this;
        }

        public UserRegistrationRequest build() {
            return instance;
        }
    }

}



