package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.PathPattern.pathPattern;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;

import org.junit.Test;

import com.google.common.base.Function;

public class PathPatternMediumTest {
  @Test
  public void allGeneratedPatternsAreValid() throws Exception {
    Function<String, Void> consumer = new Function<String, Void>() {
      public Void apply(String pattern) {
        pathPattern(pattern);
        return null;
      }
    };

    generatePatterns(6, consumer);
  }
}
