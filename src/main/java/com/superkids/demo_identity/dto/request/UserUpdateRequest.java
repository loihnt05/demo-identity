package com.superkids.demo_identity.dto.request;

import com.superkids.demo_identity.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    @DobConstraint(min = 18, message = "DOB_INVALID")
    LocalDate dob;
    List<String> roles;
}
