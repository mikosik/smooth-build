package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.testing.NamePatternGenerator.generatePatterns;

import org.junit.jupiter.api.Test;

public class NamePatternMediumTest {
  @Test
  public void all_generated_patterns_are_accepted() throws Exception {
    generatePatterns(5, (pattern) -> {
      NamePattern.namePattern(pattern);
    });
  }
}
