package org.smoothbuild.fs.base.match;

import static org.junit.Assert.assertTrue;
import static org.smoothbuild.fs.base.match.NamePattern.namePattern;
import static org.smoothbuild.testing.fs.base.match.MatchingNamesGenerator.generateNames;

import org.junit.Test;
import org.smoothbuild.testing.fs.base.match.NamePatternGenerator;

import com.google.common.base.Function;

public class NameMatcherMediumTest {

  @Test
  public void testGeneratedPatterns() throws Exception {
    NamePatternGenerator.generatePatterns(5, doTestPatternConsumer());
  }

  private static Function<String, Void> doTestPatternConsumer() {
    return new Function<String, Void>() {
      public Void apply(String pattern) {
        generateNames(pattern, assertThatNameMatchesPatternConsumer(pattern));
        return null;
      }
    };
  }

  private static Function<String, Void> assertThatNameMatchesPatternConsumer(String pattern) {
    final NameMatcher matcher = new NameMatcher(namePattern(pattern));
    return new Function<String, Void>() {
      public Void apply(String name) {
        assertTrue(matcher.apply(name));
        return null;
      }
    };
  }
}
