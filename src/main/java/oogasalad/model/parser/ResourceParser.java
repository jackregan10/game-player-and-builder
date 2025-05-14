package oogasalad.model.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Map;

/**
 * Parses and serializes the resources with a name-to-path mapping
 *
 * @author Justin Aronwald
 */
public class ResourceParser implements Parser<Map.Entry<String, String>> {

  private final ObjectMapper mapper = new ObjectMapper();


  /**
   * Parses the resource array into a map of name to a path
   *
   * @param node - the JSON node given to parse
   * @return - a map where the key is the name and the value is the path of the file
   * @throws ParsingException - error thrown when parsing fails or is interrupted
   */
  @Override
  public Map.Entry<String, String> parse(JsonNode node) throws ParsingException {
    String name = node.get("Name").asText();
    String path = node.get("Path").asText();

    return Map.entry(name, path);
  }

  /**
   * Serializes a resource entry into a JSON node
   *
   * @param entry - a map of string to string object to serialize
   * @return - a JsonNode that holds all the configured Resource information
   * @throws IOException - an exception thrown if errors occur with input/output
   */
  @Override
  public JsonNode write(Map.Entry<String, String> entry) throws IOException {
    ObjectNode resourceNode = mapper.createObjectNode();
    resourceNode.put("Name", entry.getKey());
    resourceNode.put("Path", entry.getValue());
    return resourceNode;
  }
}
