package org.smoothbuild.builtin.file.match;

import static org.junit.Assert.assertTrue;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.builtin.file.match.testing.MatchingNamesGenerator.generateNames;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.smoothbuild.builtin.file.match.testing.NamePatternGenerator;

public class NameMatcherMediumTest {
  @Test
  public void test_generated_patterns() throws Exception {
    NamePatternGenerator.generatePatterns(5, doTestPatternConsumer());
  }

  private static Consumer<String> doTestPatternConsumer() {
    return (pattern) -> generateNames(pattern, assertThatNameMatchesPatternConsumer(pattern));
  }

  private static Consumer<String> assertThatNameMatchesPatternConsumer(String pattern) {
    final NameMatcher matcher = new NameMatcher(namePattern(pattern));
    return (name) -> assertTrue(matcher.test(path(name)));
  }
}
