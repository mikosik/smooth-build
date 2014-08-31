package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.builtin.file.match.testing.NamePatternGenerator.generatePatternsImpl;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;

public class NamePatternGeneratorTest {

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
    generatePatternsImpl(3, collectingConsumer);

    // then
    List<String> expected = newArrayList();
    expected.add("aaa");
    expected.add("aab");
    expected.add("aa*");

    expected.add("aba");
    expected.add("abb");
    expected.add("abc");
    expected.add("ab*");

    expected.add("a*a");
    expected.add("a*b");

    expected.add("*aa");
    expected.add("*ab");
    expected.add("*a*");

    assertThat(generatedPatterns).containsOnly(expected.toArray(new String[expected.size()]));
  }
}
