package com.gn128.entity;

/*
  Developer: Rohit Parihar
  Project: gabriel-project
  GitHub: github.com/rohit-zip
  File: Notification
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gn128.enums.NotificationFor;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification {

    @Id
    private String notificationId;
    private String receiverId;
    private String senderId;
    private Date dateCreated;

    @Enumerated(EnumType.STRING)
    private NotificationFor notificationFor;
    private String message;
}
