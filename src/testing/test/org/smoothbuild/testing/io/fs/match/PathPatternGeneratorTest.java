package org.smoothbuild.testing.io.fs.match;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;

public class PathPatternGeneratorTest {

  @Test
  public void all_possible_patterns_are_generated() {
    // given
    final List<String> generatedPatterns = newArrayList();
    Function<String, Void> collectingConsumer = new Function<String, Void>() {
      public Void apply(String pattern) {
        generatedPatterns.add(pattern);
        return null;
      }
    };

    // when
    PathPatternGenerator.generatePatternsImpl(2, collectingConsumer);

    // then
    List<String> expected = newArrayList();
    expected.add("aa");
    expected.add("ab");
    expected.add("a*");
    expected.add("a/a");
    expected.add("a/b");
    expected.add("a/*");
    expected.add("a/**");

    expected.add("*a");
    expected.add("*/a");
    expected.add("*/*");
    expected.add("*/**");

    expected.add("**/a");
    expected.add("**/*");

    assertThat(generatedPatterns).containsOnly(expected.toArray(new String[] {}));
  }
}
