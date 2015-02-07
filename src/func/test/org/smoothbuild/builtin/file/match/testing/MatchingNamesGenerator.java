package org.smoothbuild.builtin.file.match.testing;

import static java.util.Arrays.asList;
import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;

import java.util.ArrayList;
import java.util.List;

public class MatchingNamesGenerator {
  public static void generateNames(String pattern, Consumer<String> consumer) {
    List<List<String>> template = createGeneratorTemplate(pattern);
    generateNames("", template, 0, consumer);
  }

  private static void generateNames(String name, List<List<String>> template, int index,
      Consumer<String> consumer) {
    if (index == template.size()) {
      consumer.consume(name);
    } else {
      for (String elem : template.get(index)) {
        generateNames(name + elem, template, index + 1, consumer);
      }
    }
  }

  private static List<List<String>> createGeneratorTemplate(String pattern) {
    List<List<String>> result = new ArrayList<>();
    addNameGenerators(result, pattern);
    return result;
  }

  private static void addNameGenerators(List<List<String>> result, String namePattern) {
    if (namePattern.equals(SINGLE_STAR)) {
      result.add(HelpTester.ALL);
    }

    List<String> parts = namePattern(namePattern).parts();

    for (String part : parts) {
      if (part.equals(SINGLE_STAR)) {
        result.add(HelpTester.ALL_WITH_EMPTY);
      } else {
        result.add(asList(part));
      }
    }
  }
}
