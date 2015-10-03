package org.smoothbuild.builtin.file.match;

import static org.junit.Assert.assertTrue;
import static org.smoothbuild.builtin.file.match.NamePattern.namePattern;
import static org.smoothbuild.builtin.file.match.testing.MatchingNamesGenerator.generateNames;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.match.testing.Consumer;
import org.smoothbuild.builtin.file.match.testing.NamePatternGenerator;

public class NameMatcherMediumTest {

  @Test 
  public void test_generated_patterns() throws Exception {
    NamePatternGenerator.generatePatterns(5, doTestPatternConsumer());
  }

  private static Consumer<String> doTestPatternConsumer() {
    return new Consumer<String>() {
      public void consume(String pattern) {
        generateNames(pattern, assertThatNameMatchesPatternConsumer(pattern));
      }
    };
  }

  private static Consumer<String> assertThatNameMatchesPatternConsumer(String pattern) {
    final NameMatcher matcher = new NameMatcher(namePattern(pattern));
    return new Consumer<String>() {
      public void consume(String name) {
        assertTrue(matcher.test(path(name)));
      }
    };
  }
}
