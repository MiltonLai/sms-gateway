package com.rockbb.sms.commons;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JacksonUtils.class);
	private static final ObjectMapper mapper = createObjectMapper();
	private JacksonUtils() {}

	private static ObjectMapper createObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return objectMapper;
	}

	/**
	 * 当需要屏蔽结果的对象中某些字段时, 需要用特定的view去过滤
	 *
	 * @param o 待序列化的实例, 可以是list, map或者某个对象
	 * @param clazz 自己定义的jackson view对象
	 * @return 序列化后产生的字符串
	 */
	public static String compressByView(Object o, Class<?> clazz) {
		try {
			return mapper.writerWithView(clazz).writeValueAsString(o);
		} catch (JsonGenerationException e) {
			logger.error("JsonGenerationException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return null;
	}

	public static <T> T extractObject(String json, Class<T> valueType) {
		if (json == null || json.equals("")) return null;
		try {
			return mapper.readValue(json, valueType);
		} catch (JsonParseException e) {
			logger.error("JsonParseException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return null;
	}

	public static <T> T extractByTypeRef(String json, TypeReference<T> typeRef) {
		if (json == null || json.equals("")) return null;
		try {
			return mapper.readValue(json, typeRef);
		} catch (JsonParseException e) {
			logger.error("JsonParseException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return null;
	}

	public static <T> String compressObject(T object) {
		String json = "";
		try {
			json = mapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			logger.error("JsonGenerationException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return json;
	}

	public static <T> List<T> extractList(String json, List<T> list) {
		if (json == null || json.length() == 0) return list;
		List<T> output = null;
		try {
			output = mapper.readValue(json, list.getClass());
		} catch (JsonParseException e) {
			logger.error("JsonParseException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return (output == null) ? list : output;
	}

	public static <T> List<T> extractList(String json, Class c) {
		if (StringUtils.isEmpty(json)) return null;
		JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class, c);
		try {
			return mapper.readValue(json, javaType);
		} catch (JsonParseException e) {
			logger.error("JsonParseException");
		} catch (IOException e) {
			logger.error("IOException");
		}
		return null;
	}

	public static <T> String compressList(List<T> list) {
		String json = "[]";
		if (list == null || list.size() == 0) return json;

		try {
			json = mapper.writeValueAsString(list);
		} catch (JsonGenerationException e) {
			logger.error("JsonGenerationException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return json;
	}

	public static <T, S> Map<T, S> extractMap(String json, Map<T, S> map) {
		if (json == null || json.length() == 0) return map;
		Map<T, S> output = null;
		try {
			output = mapper.readValue(json, map.getClass());
		} catch (JsonParseException e) {
			logger.error("JsonParseException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return (output == null) ? map : output;
	}

	public static <T, S> String compressMap(Map<T, S> map) {
		String json = "{}";
		if (map == null || map.size() == 0) return json;

		try {
			json = mapper.writeValueAsString(map);
		} catch (JsonGenerationException e) {
			logger.error("JsonGenerationException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		}
		return json;
	}

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		ArrayList< String> al = new ArrayList<>();
		al.add("123123");
		al.add("5555");
		String output2  = compressList(al);
		map.put("html_content", "1");
		map.put("css_content", "2");
		String output = compressMap(map);
		System.out.println(output);
	}
}
