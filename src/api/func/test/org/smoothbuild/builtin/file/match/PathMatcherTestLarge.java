package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.builtin.file.match.PathMatcher.pathMatcher;
import static org.smoothbuild.builtin.file.match.testing.MatchingPathsGenerator.generatePaths;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.Ignore;
import org.junit.Test;
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

      public void accept(String pattern) {
        count++;
        System.out.println(count + ": " + pattern);
        generatePaths(pattern, assertThatPathMatchesPatternConsumer(pattern));
      }
    };
  }

  private static Consumer<String> assertThatPathMatchesPatternConsumer(String pattern) {
    final Predicate<Path> matcher = pathMatcher(pattern);
    return (path) -> matcher.test(path(path));
  }
}
