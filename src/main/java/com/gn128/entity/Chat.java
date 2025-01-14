package com.gn128.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gn128.enums.ChatStatus;
import com.gn128.enums.ChatType;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: Chat
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Chat {

    @Id
    private String chatId;
    private String chatKey;
    private String receiverId;
    private String senderId;
    private String message;
    private Date dateCreated;

    @Enumerated(EnumType.STRING)
    private ChatStatus chatStatus;
    private Date deliveredOn;
    private Date readOn;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;
}
