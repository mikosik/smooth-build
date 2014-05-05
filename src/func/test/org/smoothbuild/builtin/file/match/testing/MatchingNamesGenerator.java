package org.smoothbuild.builtin.file.match.testing;

import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class MatchingNamesGenerator {
  public static void generateNames(String pattern, Function<String, Void> consumer) {
    List<List<String>> template = createGeneratorTemplate(pattern);
    generateNames("", template, 0, consumer);
  }

  private static void generateNames(String name, List<List<String>> template, int index,
      Function<String, Void> consumer) {
    if (index == template.size()) {
      consumer.apply(name);
    } else {
      List<String> t = template.get(index);
      for (int i = 0; i < t.size(); i++) {
        generateNames(name + t.get(i), template, index + 1, consumer);
      }
    }
  }

  private static List<List<String>> createGeneratorTemplate(String pattern) {
    List<List<String>> result = Lists.newArrayList();
    addNameGenerators(result, pattern);
    return result;
  }

  private static void addNameGenerators(List<List<String>> result, String namePattern) {
    if (namePattern.equals(SINGLE_STAR)) {
      result.add(HelpTester.ALL);
    }

    ImmutableList<String> parts = namePattern(namePattern).parts();

    for (String part : parts) {
      if (part.equals(SINGLE_STAR)) {
        result.add(HelpTester.ALL_WITH_EMPTY);
      } else {
        result.add(ImmutableList.of(part));
      }
    }
  }
}
