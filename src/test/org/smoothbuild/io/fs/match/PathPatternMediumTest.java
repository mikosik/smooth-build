package org.smoothbuild.io.fs.match;

import static org.smoothbuild.io.fs.match.PathPattern.pathPattern;
import static org.smoothbuild.testing.io.fs.match.PathPatternGenerator.generatePatterns;

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
