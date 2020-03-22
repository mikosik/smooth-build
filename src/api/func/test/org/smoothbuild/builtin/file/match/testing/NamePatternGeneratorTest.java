package org.smoothbuild.builtin.file.match.testing;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.builtin.file.match.testing.NamePatternGenerator.generatePatternsImpl;
import static org.testory.Testory.then;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class NamePatternGeneratorTest {
  @Test
  public void all_possible_patterns_are_generated() {
    final List<String> generatedPatterns = new ArrayList<>();
    generatePatternsImpl(3, (pattern) -> generatedPatterns.add(pattern));
    then(generatedPatterns, containsInAnyOrder("aaa", "aab", "aa*", "aba", "abb", "abc", "ab*",
        "a*a", "a*b", "*aa", "*ab", "*a*"));
  }
}
