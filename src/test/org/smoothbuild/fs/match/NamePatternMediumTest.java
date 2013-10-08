package org.smoothbuild.fs.match;

import static org.smoothbuild.testing.fs.match.NamePatternGenerator.generatePatterns;

import org.junit.Test;
import org.smoothbuild.fs.match.NamePattern;

import com.google.common.base.Function;

public class NamePatternMediumTest {
  @Test
  public void all_generated_patterns_are_accepted() throws Exception {
    Function<String, Void> consumer = new Function<String, Void>() {
      public Void apply(String pattern) {
        NamePattern.namePattern(pattern);
        return null;
      }
    };
    generatePatterns(5, consumer);
  }
}
