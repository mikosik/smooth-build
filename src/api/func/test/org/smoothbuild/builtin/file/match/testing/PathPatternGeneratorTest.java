package org.smoothbuild.builtin.file.match.testing;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;
import static org.testory.Testory.then;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PathPatternGeneratorTest {
  @Test
  public void all_possible_patterns_are_generated() {
    final List<String> generatedPatterns = new ArrayList<>();
    generatePatterns(2, (pattern) -> generatedPatterns.add(pattern));
    then(generatedPatterns, containsInAnyOrder("a", "*", "**", "aa", "ab", "a*", "a/a", "a/b",
        "a/*", "a/**", "*a", "*/a", "*/*", "*/**", "**/a", "**/*"));
  }
}
