package oogasalad.model.engine.datastore;

import java.util.Optional;
import oogasalad.model.config.GameConfig;

/**
 * Enum representing basic mathematical operations that can be applied
 * to values stored in the {@link ScriptableDataStore}.
 *
 * Supported operations:
 * - SUB: Subtract one value from another
 * - ADD: Add two values
 * - MUL: Multiply two values
 * - DIV: Divide one value by another (with zero division check)
 * Each operation can be dynamically parsed from a string using {@code fromString}.
 * This is used when parsing operation chains in dynamic field references (e.g., "op:add,player.score").
 *
 * @author - Logan Dracos
 */
public enum DataStoreOp {

  /**
   * Subtracts argValue from current value.
   */
  SUB {
    @Override
    public Object apply(Object current, Object argValue) {
      return toDouble(current) - toDouble(argValue);
    }
  },

  /**
   * Adds current value and argValue.
   */
  ADD {
    @Override
    public Object apply(Object current, Object argValue) {
      return toDouble(current) + toDouble(argValue);
    }
  },

  /**
   * Multiplies current value and argValue.
   */
  MUL {
    @Override
    public Object apply(Object current, Object argValue) {
      return toDouble(current) * toDouble(argValue);
    }
  },

  /**
   * Divides current value by argValue.
   * Throws {@link ArithmeticException} if division by zero is attempted.
   */
  DIV {
    @Override
    public Object apply(Object current, Object argValue) {
      double b = toDouble(argValue);
      if (b == 0) throw new ArithmeticException(GameConfig.getText("divisionByZeroError"));
      return toDouble(current) / b;
    }
  };

  /**
   * Applies the operation to two values.
   *
   * @param current the starting value
   * @param argValue the value to operate with
   * @return result of the operation
   */
  public abstract Object apply(Object current, Object argValue);

  /**
   * Helper method to convert an Object into a double.
   * - Returns 0.0 if the object is null
   * - Throws an exception if the object is not a numeric type
   *
   * @param o the object to convert
   * @return the object's value as a double
   */
  protected static double toDouble(Object o) {
    if (o == null) return 0.0;
    if (!(o instanceof Number)) {
      throw new IllegalArgumentException(GameConfig.getText("expectedNumericValue", o));
    }
    return ((Number) o).doubleValue();
  }

  /**
   * Parses a string into a {@link DataStoreOp}.
   * Supports inputs like "add", "sub", "mul", "div", and "op:add" (truncates prefix).
   *
   * @param raw the string to parse
   * @return an {@link Optional} containing the matching {@code DataStoreOp}, or empty if invalid
   */
  public static Optional<DataStoreOp> fromString(String raw) {
    String name = raw.contains(":") ? raw.substring(0, raw.indexOf(":")) : raw;
    try {
      return Optional.of(DataStoreOp.valueOf(name.toUpperCase()));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}