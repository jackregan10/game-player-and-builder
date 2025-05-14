package oogasalad.model.engine.datastore;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class DataStoreOpTest {


  @Test
  void apply_ADDWithValidNumbers_ReturnsCorrectSum() {
    Object result = DataStoreOp.ADD.apply(10, 5);
    assertEquals(15.0, result);
  }

  @Test
  void apply_SUBWithValidNumbers_ReturnsCorrectDifference() {
    Object result = DataStoreOp.SUB.apply(10, 5);
    assertEquals(5.0, result);
  }

  @Test
  void apply_MULWithValidNumbers_ReturnsCorrectProduct() {
    Object result = DataStoreOp.MUL.apply(10, 5);
    assertEquals(50.0, result);
  }

  @Test
  void apply_DIVWithValidNumbers_ReturnsCorrectQuotient() {
    Object result = DataStoreOp.DIV.apply(10, 5);
    assertEquals(2.0, result);
  }

  @Test
  void apply_DIVByZero_ThrowsArithmeticException() {
    assertThrows(ArithmeticException.class, () -> DataStoreOp.DIV.apply(10, 0));
  }

  @Test
  void apply_WithNullInput_TreatsAsZero() {
    Object result = DataStoreOp.ADD.apply(null, 5);
    assertEquals(5.0, result);
  }

  @Test
  void apply_WithNonNumericInput_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> DataStoreOp.ADD.apply("string", 5));
  }

  // ----- Tests for toDouble() indirectly through apply -----

  @Test
  void toDouble_NullInput_ReturnsZero() {
    assertEquals(0.0, DataStoreOp.ADD.apply(null, 0));
  }

  @Test
  void toDouble_NonNumberInput_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> DataStoreOp.ADD.apply("abc", 1));
  }

  // ----- Tests for fromString() parsing -----

  @Test
  void fromString_ValidLowercaseWithoutPrefix_ReturnsMatchingOp() {
    Optional<DataStoreOp> op = DataStoreOp.fromString("add");
    assertTrue(op.isPresent());
    assertEquals(DataStoreOp.ADD, op.get());
  }

  @Test
  void fromString_InvalidString_ReturnsEmptyOptional() {
    Optional<DataStoreOp> op = DataStoreOp.fromString("invalid");
    assertFalse(op.isPresent());
  }

  @Test
  void fromString_EmptyString_ReturnsEmptyOptional() {
    Optional<DataStoreOp> op = DataStoreOp.fromString("");
    assertFalse(op.isPresent());
  }
}
