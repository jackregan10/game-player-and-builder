package oogasalad.model.serialization.serializable;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerializedFieldTest {

  static class TestData {
    private int number;
    public String text;

    public int getNumber() {
      return number;
    }

    public void setNumber(int number) {
      this.number = number;
    }
  }

  private TestData testData;

  @BeforeEach
  void setUp() {
    testData = new TestData();
    testData.setNumber(42);
    testData.text = "Hello";
  }

  @Test
  void getValue_WithGetter_ReturnsCorrectValue() throws Exception {
    Field field = TestData.class.getDeclaredField("number");
    Method getter = TestData.class.getMethod("getNumber");
    Method setter = TestData.class.getMethod("setNumber", int.class);
    Type type = field.getGenericType();

    SerializedField serialized = new SerializedField(testData, field, type, getter, setter);
    assertEquals(42, serialized.getValue());
  }

  @Test
  void setValue_WithSetter_UpdatesValue() throws Exception {
    Field field = TestData.class.getDeclaredField("number");
    Method getter = TestData.class.getMethod("getNumber");
    Method setter = TestData.class.getMethod("setNumber", int.class);
    Type type = field.getGenericType();

    SerializedField serialized = new SerializedField(testData, field, type, getter, setter);
    serialized.setValue(99);
    assertEquals(99, testData.getNumber());
  }

  @Test
  void getValue_WithoutGetter_ReturnsDirectFieldValue() throws Exception {
    Field field = TestData.class.getDeclaredField("text");
    Type type = field.getGenericType();

    SerializedField serialized = new SerializedField(testData, field, type, null, null);
    assertEquals("Hello", serialized.getValue());
  }

  @Test
  void setValue_WithoutSetter_SetsDirectFieldValue() throws Exception {
    Field field = TestData.class.getDeclaredField("text");
    Type type = field.getGenericType();

    SerializedField serialized = new SerializedField(testData, field, type, null, null);
    serialized.setValue("World");
    assertEquals("World", testData.text);
  }

  @Test
  void getFieldName_ReturnsCorrectName() throws Exception {
    Field field = TestData.class.getDeclaredField("text");
    SerializedField serialized = new SerializedField(testData, field, field.getGenericType(), null, null);
    assertEquals("text", serialized.getFieldName());
  }

  @Test
  void getFieldType_ReturnsCorrectType() throws Exception {
    Field field = TestData.class.getDeclaredField("text");
    SerializedField serialized = new SerializedField(testData, field, field.getGenericType(), null, null);
    assertEquals(String.class, serialized.getFieldType());
  }

  @Test
  void toString_Always_ContainsFieldName() throws Exception {
    Field field = TestData.class.getDeclaredField("number");
    SerializedField serialized = new SerializedField(testData, field, field.getGenericType(), null, null);
    assertTrue(serialized.toString().contains("number"));
  }
}
