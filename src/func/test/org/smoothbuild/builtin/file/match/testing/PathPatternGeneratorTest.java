package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.builtin.file.match.testing.PathPatternGenerator.generatePatterns;
import static org.testory.Testory.then;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;

public class PathPatternGeneratorTest {

  @Test
  public void all_possible_patterns_are_generated() {
    final List<String> generatedPatterns = newArrayList();
    Function<String, Void> collectingConsumer = new Function<String, Void>() {
      public Void apply(String pattern) {
        generatedPatterns.add(pattern);
        return null;
      }
    };

    generatePatterns(2, collectingConsumer);
    then(generatedPatterns, containsInAnyOrder("a", "*", "**", "aa", "ab", "a*", "a/a", "a/b",
        "a/*", "a/**", "*a", "*/a", "*/*", "*/**", "**/a", "**/*"));
  }
}
