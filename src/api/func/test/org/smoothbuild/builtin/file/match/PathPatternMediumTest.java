package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.PathPattern.pathPattern;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;

import org.junit.Test;

public class PathPatternMediumTest {
  @Test
  public void all_generated_patterns_are_valid() throws Exception {
    generatePatterns(6, (pattern) -> pathPattern(pattern));
  }
}
