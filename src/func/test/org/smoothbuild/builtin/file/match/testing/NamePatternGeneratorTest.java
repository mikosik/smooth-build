package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.builtin.file.match.testing.NamePatternGenerator.generatePatternsImpl;
import static org.testory.Testory.*;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;

public class NamePatternGeneratorTest {

  @Test
  public void all_possible_patterns_are_generated() {
    final List<String> generatedPatterns = newArrayList();
    Function<String, Void> collectingConsumer = new Function<String, Void>() {
      public Void apply(String pattern) {
        generatedPatterns.add(pattern);
        return null;
      }
    };

    generatePatternsImpl(3, collectingConsumer);
    then(generatedPatterns, containsInAnyOrder("aaa", "aab", "aa*", "aba", "abb", "abc", "ab*",
        "a*a", "a*b", "*aa", "*ab", "*a*"));
  }
}
