package com.jty.pf.bindasynctransaction.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

public class TypeConverter {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> typeRefMap = new TypeReference<>() {};
    private static final TypeReference<List<Object>> typeRefList = new TypeReference<>() {};

    static {
            MAPPER
                //JSON에 있는 propety가 Mapping될 Object에 primitive인데 null 값이 전달을 무시해야하는 경우
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                //JSON에는 있지만 Mapping될 Object에는 없는 필드를 무시해야하는 경우
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // LocalDateTime 지원
                .registerModule(new JavaTimeModule())
        ;
    }

    public static <T> T convert(String json, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(json, clazz);
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    public static Map<String,Object> toMap(String json){
        return MAPPER.convertValue(json, typeRefMap);
    }

    public static List<Object> toList(String json){
        return MAPPER.convertValue(json, typeRefList);
    }

}
