package com.RootBuildUp.oauth2jwtspringboot.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
	public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

		private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		@Override
		public String convertToDatabaseColumn(LocalDateTime locDate) {
			return (locDate == null ? null : formatter.format(locDate));
		}

		@Override
		public LocalDateTime convertToEntityAttribute(String dateValue) {
			return (dateValue == null ? null : LocalDateTime.parse(dateValue, formatter));
		}
	}