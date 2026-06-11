package com.niuma.remembereverythingapp.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

class JsonUtilsTest {

  private val log = LoggerFactory.getLogger(JsonUtils::class.java)

  data class TestData(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
  )

  @BeforeEach
  fun setUp() {
  }

  @AfterEach
  fun tearDown() {
    unmockkAll()
  }

  @Nested
  inner class ToJsonTests {

    @Test
    fun `should return null string when object is null`() {
      assertThat(JsonUtils.DEFAULT.toJson(null)).isEqualTo("")
    }

    @Test
    fun `should serialize object to json string`() {
      val testData = TestData(1, "Test")
      val json = JsonUtils.DEFAULT.toJson(testData)

      assertThat(json).contains(""""id":1""")
      assertThat(json).contains(""""name":"Test"""")
    }

    @Test
    fun `should throw exception when serialization fails`() {
      val mockMapper = mockk<ObjectMapper>()
      val realJsonProcessingException = object : JsonProcessingException("Mocked failure") {}
      every { mockMapper.writeValueAsString(any()) } throws realJsonProcessingException
      assertThatThrownBy { JsonUtils(mockMapper).toJson(TestData(1, "Test")) }
        .isInstanceOf(RuntimeException::class.java)
        .hasMessageContaining("Failed to serialize")
    }
  }

  @Nested
  inner class ToPrettyJsonTests {

    @Test
    fun `should return null string when object is null`() {
      assertThat(JsonUtils.DEFAULT.toPrettyJson(null)).isEqualTo("")
    }

    @Test
    fun `should return formatted json string`() {
      val testData = TestData(1, "Test")
      val json = JsonUtils.DEFAULT.toPrettyJson(testData)
      assertThat(json).contains("  \"id\" : 1,")
      assertThat(json).contains("  \"name\" : \"Test\"")
    }
  }

  @Nested
  inner class FromJsonWithTypeReferenceTests {

    @Test
    fun `should deserialize json to map`() {
      val json = """{"key1":"value1","key2":2}"""
      val result = JsonUtils.DEFAULT.fromJson(json, object : TypeReference<Map<String, Any>>() {})

      assertThat(result).isNotNull
      assertThat(result?.get("key1")).isEqualTo("value1")
      assertThat(result?.get("key2")).isEqualTo(2)
    }
  }

  @Nested
  inner class FromInputStreamTests {

    @Test
    fun `should deserialize input stream to object`() {
      val json = """{"id":1,"name":"Test"}"""
      val inputStream = ByteArrayInputStream(json.toByteArray())
      val result = JsonUtils.DEFAULT.fromJson(inputStream, TestData::class.java)

      assertThat(result.id).isEqualTo(1)
      assertThat(result.name).isEqualTo("Test")
    }
  }

  @Nested
  inner class ToMapTests {

    @Test
    fun `should convert json to map`() {
      val json = """{"name":"John","age":30}"""
      val map = JsonUtils.DEFAULT.toMap(json)

      assertThat(map).containsExactlyInAnyOrderEntriesOf(
        mapOf("name" to "John", "age" to 30)
      )
    }
  }

  @Nested
  inner class ToListTests {

    @Test
    fun `should convert json to list`() {
      val json = """[{"id":1,"name":"One"},{"id":2,"name":"Two"}]"""
      val list = JsonUtils.DEFAULT.toList(json, TestData::class.java)

      assertThat(list).hasSize(2)
      assertThat(list?.get(0)?.id).isEqualTo(1)
      assertThat(list?.get(1)?.name).isEqualTo("Two")
    }
  }

  @Nested
  inner class DeepCopyTests {

    @Test
    fun `should create deep copy of object`() {
      val original = TestData(1, "Original")
      val copy = JsonUtils.DEFAULT.deepCopy(original)

      assertThat(copy).isNotSameAs(original)
      assertThat(copy).isEqualTo(original)
    }
  }

  @Nested
  inner class ToBytesTests {

    @Test
    fun `should convert object to byte array`() {
      val testData = TestData(1, "Test")
      val bytes = JsonUtils.DEFAULT.toBytes(testData)

      assertThat(bytes).isNotEmpty
      assertThat(String(bytes)).contains("\"id\":1")
    }
  }
}