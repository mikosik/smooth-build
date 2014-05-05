package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.io.fs.match.MatchingPathsGenerator.generatePaths;
import static org.smoothbuild.testing.io.fs.match.PathPatternGenerator.generatePatterns;

import org.junit.Ignore;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * This test is not automatically run by ant as it takes more than 1 hour to
 * complete.
 */
@Ignore
public class PathMatcherTestLarge {

  @Test
  public void testGeneratedPatterns() throws Exception {
    generatePatterns(5, doTestPatternConsumer());
  }

  private static Function<String, Void> doTestPatternConsumer() {
    return new Function<String, Void>() {
      private int count = 0;

      @Override
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
      @Override
      public Void apply(String path) {
        matcher.apply(path(path));
        return null;
      }
    };
  }
}
