package org.smoothbuild.testing.io.fs.match;

import static org.smoothbuild.builtin.file.match.Constants.DOUBLE_STAR;
import static org.smoothbuild.builtin.file.match.Constants.SINGLE_STAR;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.testing.io.fs.match.HelpTester.ALL;
import static org.smoothbuild.testing.io.fs.match.HelpTester.ALL_DOUBLE_STARS;
import static org.smoothbuild.testing.io.fs.match.HelpTester.ALL_WITH_EMPTY;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class MatchingPathsGenerator {

  public static void generatePaths(String pattern, Function<String, Void> consumer) {
    List<List<String>> template = createGeneratorTemplate(pattern);
    generatePaths("", template, 0, consumer);
  }

  private static void generatePaths(String path, List<List<String>> template, int index,
      Function<String, Void> consumer) {
    if (index == template.size()) {
      consumer.apply(path);
    } else {
      List<String> t = template.get(index);
      for (int i = 0; i < t.size(); i++) {
        String suffix = t.get(i);
        if (!(suffix.equals("/") && (path.endsWith("/") || path.isEmpty()))) {
          generatePaths(path + suffix, template, index + 1, consumer);
        }
      }
    }
  }

  private static List<List<String>> createGeneratorTemplate(String pattern) {
    List<List<String>> result = Lists.newArrayList();

    if (pattern.endsWith("**")) {
      pattern = pattern + "/*";
    }

    ImmutableList<String> parts = ImmutableList.copyOf(Splitter.on('/').split(pattern));
    for (int i = 0; i < parts.size(); i++) {
      if (i != 0) {
        result.add(ImmutableList.of("/"));
      }

      addNameGenerators(result, parts.get(i));
    }
    return result;
  }

  private static void addNameGenerators(List<List<String>> result, String name) {
    if (name.equals(DOUBLE_STAR)) {
      result.add(ALL_DOUBLE_STARS);
    } else if (name.equals(SINGLE_STAR)) {
      result.add(ALL);
    } else {
      ImmutableList<String> parts = namePattern(name).parts();

      for (String part : parts) {
        if (part.equals(SINGLE_STAR)) {
          result.add(ALL_WITH_EMPTY);
        } else {
          result.add(ImmutableList.of(part));
        }
      }
    }
  }
}
