package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.PathPattern.pathPattern;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;

import org.junit.Test;
import org.smoothbuild.builtin.file.match.testing.Consumer;

public class PathPatternMediumTest {
  @Test
  public void allGeneratedPatternsAreValid() throws Exception {
    Consumer<String> consumer = new Consumer<String>() {
      public void consume(String pattern) {
        pathPattern(pattern);
      }
    };

    generatePatterns(6, consumer);
  }
}
