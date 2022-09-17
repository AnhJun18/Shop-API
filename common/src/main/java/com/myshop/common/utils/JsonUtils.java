package com.craw.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class JsonUtils {

  private static ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

  public static String writeToString(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] writeToBytes(Object obj) {
    try {
      return objectMapper.writeValueAsBytes(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object> readToMap(String json) {
    try {
      return objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> List<T> readToList(String json, Class<T> cls) {
    try {
      return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, cls));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object> readToMap(byte[] json) {
    try {
      return objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T readToObject(String json, Class<T> cls) {
    try {
      return objectMapper.readValue(json, cls);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
