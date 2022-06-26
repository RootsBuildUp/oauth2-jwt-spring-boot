package com.RootBuildUp.oauth2jwtspringboot.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class AccessToken{

	@Id
	private String id;
	private String username;
	private String clientId;
	@Column(columnDefinition = "TEXT")
	private String token;
	private String oAuth2AccessToken;
	private String authentication;
	private String refreshToken;
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime expiredDateAndTime;


}