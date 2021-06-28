package com.emails.dtos;

import com.emails.models.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmailListResponse {

    List<Email> receivedEmails;

    List<Email> sentEmails;
}
