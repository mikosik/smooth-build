package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.file.match.testing.MatchingPathsGenerator.generatePaths;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Ignore;
import org.junit.Test;
import org.smoothbuild.builtin.file.match.testing.Consumer;
import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.io.fs.base.Path;

/**
 * This test is not automatically run by ant as it takes more than 1 hour to
 * complete.
 */
@Ignore
public class PathMatcherTestLarge {

  @Test
  public void test_generated_patterns() throws Exception {
    generatePatterns(5, doTestPatternConsumer());
  }

  private static Consumer<String> doTestPatternConsumer() {
    return new Consumer<String>() {
      private int count = 0;

      @Override
      public void consume(String pattern) {
        count++;
        System.out.println(count + ": " + pattern);
        generatePaths(pattern, assertThatPathMatchesPatternConsumer(pattern));
      }
    };
  }

  private static Consumer<String> assertThatPathMatchesPatternConsumer(String pattern) {
    final Predicate<Path> matcher = pathMatcher(pattern);
    return new Consumer<String>() {
      @Override
      public void consume(String path) {
        matcher.test(path(path));
      }
    };
  }
}
