//package com.RootBuildUp.oauth2jwtspringboot.model;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.google.gson.FieldNamingPolicy;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import lombok.SneakyThrows;
//import org.codehaus.jackson.annotate.JsonIgnoreProperties;
//import org.springframework.security.oauth2.common.util.SerializationUtils;
//
//import java.io.*;
//import java.util.Base64;
//
//public class SerializableObjectConverter {
//
//	public static <T> String serialize(T object) {
//		try {
//			byte[] bytes = SerializationUtils.serialize(object);
//			return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//
//	public static <T> T deserialize(String encodedObject) {
//		try {
//			byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(encodedObject);
//			return SerializationUtils.deserialize(bytes);
//		} catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//	}
//
//	@SneakyThrows
//	public static <T> String serializeObjectToString(T o) {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = new ObjectOutputStream(baos);
//		oos.writeObject(o);
//		oos.close();
//
//		return Base64.getEncoder().encodeToString(baos.toByteArray());
//	}
//
//	@SneakyThrows
//	public static Object deSerializeObjectFromString(String s){
//
//		byte[] data = Base64.getDecoder().decode(s);
//		ObjectInputStream ois = new ObjectInputStream(
//				new ByteArrayInputStream(data));
//		Object o = ois.readObject();
//		ois.close();
//		return o;
//	}
//private static Gson gson = new GsonBuilder()
//		.setPrettyPrinting()
//		.serializeNulls()
//		.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//		.create();
//
//	public static <T> String objectToString(T o) {
//		return gson.toJson(o);
//	}
//
//	@SneakyThrows
//	public static <T> T stringToObject(String s,Class<T> responseType){
//		return gson.fromJson(s,responseType);
//	}
//
//	@SneakyThrows
//	public static String asJsonString(final Object obj){
//		return newObjectMapper().writeValueAsString(obj);
//	}
//
//	@SneakyThrows
//	public static <T> T asObject(final String json, Class<T> type) {
//		return newObjectMapper().readValue(json, type);
//	}
//
//	public static ObjectMapper newObjectMapper() {
//		ObjectMapper mapper = new ObjectMapper()
//				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//				.configure(SerializationFeature.INDENT_OUTPUT, true)
//				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//		        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
//		        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
//				.registerModule(new JavaTimeModule())
//				.registerModule(new Jdk8Module());
//		mapper.addMixIn(Object.class, IgnoreHibernatePropertiesInJackson.class);
//		return mapper;
//	}
//	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//	private abstract class IgnoreHibernatePropertiesInJackson {
//
//	}
//
//}
