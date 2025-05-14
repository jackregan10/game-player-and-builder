package oogasalad.model.parser;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 * A generic parser class for a JSON node, converting to an object of type T
 *
 * @param <T> - the type of object produced by the parser
 * @author Justin Aronwald
 */
public interface Parser<T> {

  /**
   * Parse a JsonNode into a desired output
   *
   * @param node - the JSON node given to parse
   * @return - an instance of type T based on the parsed data
   * @throws ParsingException - a specific exception designed to catch errors with parsing
   */
  T parse(JsonNode node) throws ParsingException;

  /**
   * Creates the given output to an output stream
   *
   * @param data - the data object to serialize
   * @throws IOException - the exception if writing fails
   */
  JsonNode write(T data) throws IOException;

}
