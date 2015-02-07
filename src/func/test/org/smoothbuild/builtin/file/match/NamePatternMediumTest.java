package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.testing.NamePatternGenerator.generatePatterns;

import org.junit.Test;
import org.smoothbuild.builtin.file.match.testing.Consumer;

public class NamePatternMediumTest {
  @Test
  public void all_generated_patterns_are_accepted() throws Exception {
    Consumer<String> consumer = new Consumer<String>() {
      public void consume(String pattern) {
        NamePattern.namePattern(pattern);
      }
    };
    generatePatterns(5, consumer);
  }
}
