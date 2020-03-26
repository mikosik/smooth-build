package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PathPatternGeneratorTest {
  @Test
  public void all_possible_patterns_are_generated() {
    final List<String> generatedPatterns = new ArrayList<>();
    generatePatterns(2, generatedPatterns::add);
    assertThat(generatedPatterns)
        .containsExactly("a", "*", "**", "aa", "ab", "a*", "a/a", "a/b",
        "a/*", "a/**", "*a", "*/a", "*/*", "*/**", "**/a", "**/*");
  }
}
