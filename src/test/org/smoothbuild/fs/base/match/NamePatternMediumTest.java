package org.smoothbuild.fs.base.match;

import static org.smoothbuild.fs.base.match.NamePatternGenerator.generatePatterns;

import org.junit.Test;

import com.google.common.base.Function;

public class NamePatternMediumTest {
  @Test
  public void all_generated_patterns_are_accepted() throws Exception {
    Function<String, Void> function = new Function<String, Void>() {
      public Void apply(String pattern) {
        NamePattern.namePattern(pattern);
        return null;
      }
    };
    generatePatterns(5, function);
  }
}
