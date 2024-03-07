package org.smoothbuild.common.log;

import static java.util.Arrays.asList;
import static org.smoothbuild.common.collect.List.listOfAll;

import org.smoothbuild.common.collect.List;

public record Label(List<String> parts) {
  private static final String DELIMITER = "::";

  public static Label label(String... parts) {
    return new Label(listOfAll(asList(parts)));
  }

  public Label append(Label suffix) {
    return new Label(parts.appendAll(suffix.parts));
  }

  @Override
  public String toString() {
    return parts.isEmpty() ? DELIMITER : parts.toString(DELIMITER, DELIMITER, "");
  }
}
