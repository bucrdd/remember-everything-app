package com.niuma.remembereverythingapp.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.LoggerFactory
import java.io.InputStream

object JsonUtils {

  private val logger = LoggerFactory.getLogger(JsonUtils::class.java)

  private const val DEFAULT_STR_OF_NULL: String = "null"

  private val mapper: ObjectMapper = ObjectMapper().apply {
    registerModule(KotlinModule.Builder().build())
    registerModule(JavaTimeModule())
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
//    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
  }

  fun toJson(obj: Any?): String {
    return if (obj == null) {
      DEFAULT_STR_OF_NULL
    } else {
      try {
        mapper.writeValueAsString(obj)
      } catch (e: JsonProcessingException) {
        logger.error("Failed to serialize $obj, ${e.message}", e)
        throw RuntimeException("Failed to serialize $obj", e)
      }
    }
  }

  fun toPrettyJson(obj: Any?): String {
    return if (obj == null) {
      DEFAULT_STR_OF_NULL
    } else {
      try {
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
      } catch (e: JsonProcessingException) {
        logger.error("Failed to serialize $obj, ${e.message}", e)
        throw RuntimeException("Failed to serialize $obj", e)
      }
    }
  }

  fun <T> fromJson(json: String?, clazz: Class<T>): T? {
    if (json.isNullOrBlank()) {
      return null
    }
    return try {
      mapper.readValue(json, clazz)
    } catch (e: Exception) {
      logger.error("Failed to deserialize $json as ${clazz.typeName}: ${e.message}", e)
      throw RuntimeException("Failed to deserialize $json", e)
    }
  }

  fun <T> fromJson(json: String?, typeReference: TypeReference<T>): T? {
    if (json.isNullOrBlank()) {
      return null
    }
    return try {
      mapper.readValue(json, typeReference)
    } catch (e: Exception) {
      logger.error("Failed to deserialize $json as ${typeReference.type}: ${e.message}", e)
      throw RuntimeException("Failed to deserialize $json as", e)
    }
  }

  fun <T> fromJson(inputStream: InputStream, clazz: Class<T>): T {
    return try {
      mapper.readValue(inputStream, clazz)
    } catch (e: Exception) {
      logger.error("Failed to deserialize $inputStream as ${clazz.typeName}: ${e.message}", e)
      throw RuntimeException("Failed to deserialize $inputStream", e)
    }
  }

  fun toMap(json: String?): Map<String, Any>? {
    if (json.isNullOrBlank()) {
      return null
    }
    return try {
      mapper.readValue(json, object : TypeReference<Map<String, Any>>() {})
    } catch (e: Exception) {
      logger.error("Failed to deserialize $json as Map<String, Any>: ${e.message}", e)
      throw RuntimeException("Failed to deserialize $json", e)
    }
  }

  fun <K, Any> toMap(json: String?, keyClazz: Class<K>): Map<K, Any>? {
    if (json.isNullOrBlank()) {
      return null
    }
    return try {
      mapper.readValue(json, object : TypeReference<Map<K, Any>>() {})
    } catch (e: Exception) {
      logger.error("Failed to deserialize $json as Map<${keyClazz.typeName}, Any>: ${e.message}", e)
      throw RuntimeException("Failed to deserialize $json", e)
    }
  }

  fun <T> toList(json: String?, clazz: Class<T>): List<T>? {
    if (json.isNullOrBlank()) {
      return null
    }
    return try {
      mapper.readValue(json, object : TypeReference<List<T>>() {})
    } catch (e: Exception) {
      logger.error("Failed to deserialize $json as List<${clazz.typeName}>: ${e.message}", e)
      throw RuntimeException("Failed to deserialize $json", e)
    }
  }

  fun <T> deepCopy(obj: T): T {
    return fromJson(toJson(obj), obj!!::class.java)!!
  }

  fun toBytes(obj: Any): ByteArray {
    return try {
      mapper.writeValueAsBytes(obj)
    } catch (e: JsonProcessingException) {
      logger.error("Failed to serialize $obj to byte[]: ${e.message}", e)
      throw RuntimeException("Failed to serialize $obj", e)
    }
  }

}