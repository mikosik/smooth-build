package org.smoothbuild.fs.match;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.match.PathMatcher.pathMatcher;
import static org.smoothbuild.testing.fs.match.MatchingPathsGenerator.generatePaths;
import static org.smoothbuild.testing.fs.match.PathPatternGenerator.generatePatterns;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * This test is not automatically run by ant as it takes more than 1 hour to
 * complete.
 */
public class PathMatcherTestLarge {

  @Test
  public void testGeneratedPatterns() throws Exception {
    generatePatterns(5, doTestPatternConsumer());
  }

  private static Function<String, Void> doTestPatternConsumer() {
    return new Function<String, Void>() {
      private int count = 0;

      public Void apply(String pattern) {
        count++;
        System.out.println(count + ": " + pattern);
        generatePaths(pattern, assertThatPathMatchesPatternConsumer(pattern));
        return null;
      }
    };
  }

  private static Function<String, Void> assertThatPathMatchesPatternConsumer(String pattern) {
    final Predicate<Path> matcher = pathMatcher(pattern);
    return new Function<String, Void>() {
      public Void apply(String path) {
        matcher.apply(path(path));
        return null;
      }
    };
  }
}
