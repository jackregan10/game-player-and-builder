package oogasalad.model.engine.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ParsedKey encapsulates a parsed and interpretable representation of a string key passed to
 * {@link ScriptableDataStore} methods.
 * A ParsedKey extracts and stores:
 * - The base object-field key (e.g., "player.score")
 * - Whether the key is marked with /save for long-term storage
 * - Any operation chains (e.g., op:add,bonus;sub,penalty) to be applied during retrieval
 * Example accepted formats:
 * - "player.score"
 * - "player.health/save"
 * - "enemy.hp/save/op:sub,damage;add,regen"
 * Parsing happens once during construction. Operation chains are stored as Operation objects.
 *
 * @author - Logan Dracos
 */
public class ParsedKey {

  /**
   * Represents a single operation (e.g., ADD or SUB) to apply on the base value,
   * with its associated argument (either a literal number or a dynamic key).
   */
  private static class Operation {
    private final DataStoreOp op;
    private final String arg;          // the original string argument (e.g., "3" or "bonus.score")
    private final boolean isLiteral;   // true if the arg is a literal numeric value

    /**
     * Constructor for private Operation class
     *
     * @param op - Operation to be performed
     * @param arg - Argument for the operation
     */
    public Operation(DataStoreOp op, String arg) {
      this.op = op;
      this.arg = arg;
      this.isLiteral = isNumeric(arg); // Decide if this is a literal or a dynamic reference
    }

    /**
     * Checks whether a string can be parsed as a numeric literal.
     */
    private static boolean isNumeric(String str) {
      try {
        Double.parseDouble(str);
        return true;
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  private static final String SAVE_MARKER = "/save";
  private static final String OP_PREFIX = "op:";
  private static final String OP_DELIMITER = ";";
  private static final String ARG_DELIMITER = ",";
  private static final int SAVE_STRING_LENGTH = 5;

  private final String baseKeyPath;  // The main object.field identifier
  private final String original;     // The full raw input string
  private String saveAlias;
  private boolean shouldSave;  // Whether this key was marked with /save
  private final List<Operation> operations;  // List of operations to apply when retrieving value

  /**
   * Constructs a ParsedKey by analyzing the raw key string.
   * Parses out /save flag and any operation chain after "/".
   *
   * @param raw the raw input string (e.g., "dino.hp/save,score/op:sub,damage;add,regen")
   */
  public ParsedKey(String raw) {
    this.original = raw;

    String working = handleSaveAndAlias(raw); // Extract and clean the raw string

    String[] opSplit = working.split(Pattern.quote("/"), 2);
    this.baseKeyPath = opSplit[0];
    this.operations = new ArrayList<>();

    computeOperations(opSplit);
  }

  /**
   * Parses and handles the /save marker and optional alias.
   *
   * @param raw the original input string
   * @return the cleaned string without save markers, ready for op parsing
   */
  private String handleSaveAndAlias(String raw) {
    boolean save = false;
    String alias = null;
    String working = raw;

    if (working.contains(SAVE_MARKER)) {
      save = true;

      String[] values = handleSavingAlias(working);
      working = values[0];
      alias = values[1];
    }

    this.shouldSave = save;
    this.saveAlias = alias;

    return working;
  }

  private String[] handleSavingAlias(String working) {
    String[] ret = new String[2];
    int saveIndex = working.indexOf(SAVE_MARKER);
    char comma = ',';

    if (working.length() > saveIndex + SAVE_STRING_LENGTH && working.charAt(saveIndex + SAVE_STRING_LENGTH) == comma) {
      int commaIndex = saveIndex + 6;
      int nextSlash = working.indexOf("/", commaIndex);
      if (nextSlash == -1) nextSlash = working.length();
      ret[0] = working.substring(0, saveIndex) + working.substring(nextSlash);
      ret[1] = working.substring(commaIndex, nextSlash);
    } else {
      ret[0] = working.replace(SAVE_MARKER, "");
      ret[1] = null;
    }
    return ret;
  }


  /**
   * Parses any operations present after "/op:" in the input string.
   * Each segment is parsed into an Operation object.
   *
   * @param opSplit the result of the split between baseKeyPath and operations
   */
  private void computeOperations(String[] opSplit) {
    int one = 1;
    int two = 2;
    if (opSplit.length > one && opSplit[one].startsWith(OP_PREFIX)) {
      String allOps = opSplit[1].substring(OP_PREFIX.length());

      for (String segment : allOps.split(OP_DELIMITER)) {
        String[] parts = segment.split(ARG_DELIMITER, 2);

        if (parts.length == two) {
          DataStoreOp.fromString(parts[0]).ifPresent(op ->
              operations.add(new Operation(op, parts[1])));
        }
      }
    }
  }

  /**
   * @return the base object-field key (without /save or operations)
   */
  public String getBaseKeyPath() {
    return baseKeyPath;
  }

  /**
   * @return the original raw input string
   */
  public String getOriginal() {
    return original;
  }

  /**
   * @return the save alias of the value
   */
  public String getSaveAlias() {
    return saveAlias;
  }

  /**
   * @return true if this key was marked with /save
   */
  public boolean shouldSave() {
    return shouldSave;
  }

  /**
   * Applies all parsed operations sequentially on the given base value,
   * using the provided {@link ScriptableDataStore} for dynamic argument resolution.
   * If the final result is numeric, it is rounded to the nearest integer.
   *
   * @param baseValue the initial retrieved value
   * @param store the datastore used to resolve dynamic argument keys
   * @return the final computed value after operations (rounded if numeric)
   */
  public Object applyOperations(Object baseValue, ScriptableDataStore store) {
    Object current = baseValue;
    for (Operation op : operations) {
      Object argValue = op.isLiteral
          ? Double.parseDouble(op.arg) // Use literal number
          : store.getValue(op.arg);    // Or resolve dynamic field

      current = op.op.apply(current, argValue);
    }

    if (current instanceof Number) {
      return Math.round(((Number) current).doubleValue());
    }

    return current;
  }
}
