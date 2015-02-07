package org.smoothbuild.builtin.file.match.testing;

import static java.util.Arrays.asList;
import static org.smoothbuild.builtin.file.match.Constants.DOUBLE_STAR;
import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.builtin.file.match.testing.HelpTester.ALL;
import static org.smoothbuild.builtin.file.match.testing.HelpTester.ALL_DOUBLE_STARS;
import static org.smoothbuild.builtin.file.match.testing.HelpTester.ALL_WITH_EMPTY;

import java.util.ArrayList;
import java.util.List;

public class MatchingPathsGenerator {

  public static void generatePaths(String pattern, Consumer<String> consumer) {
    List<List<String>> template = createGeneratorTemplate(pattern);
    generatePaths("", template, 0, consumer);
  }

  private static void generatePaths(String path, List<List<String>> template, int index,
      Consumer<String> consumer) {
    if (index == template.size()) {
      consumer.consume(path);
    } else {
      for (String suffix : template.get(index)) {
        if (!(suffix.equals("/") && (path.endsWith("/") || path.isEmpty()))) {
          generatePaths(path + suffix, template, index + 1, consumer);
        }
      }
    }
  }

  private static List<List<String>> createGeneratorTemplate(String pattern) {
    List<List<String>> result = new ArrayList<>();

    if (pattern.endsWith("**")) {
      pattern = pattern + "/*";
    }

    String[] parts = pattern.split("/");
    for (int i = 0; i < parts.length; i++) {
      if (i != 0) {
        result.add(asList("/"));
      }

      addNameGenerators(result, parts[i]);
    }
    return result;
  }

  private static void addNameGenerators(List<List<String>> result, String name) {
    if (name.equals(DOUBLE_STAR)) {
      result.add(ALL_DOUBLE_STARS);
    } else if (name.equals(SINGLE_STAR)) {
      result.add(ALL);
    } else {
      List<String> parts = namePattern(name).parts();

      for (String part : parts) {
        if (part.equals(SINGLE_STAR)) {
          result.add(ALL_WITH_EMPTY);
        } else {
          result.add(asList(part));
        }
      }
    }
  }
}
