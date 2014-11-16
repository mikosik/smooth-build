package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.builtin.file.match.testing.MatchingPathsGenerator.generatePaths;
import static org.testory.Testory.*;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;

public class MatchingPathsGeneratorTest {

  @Test
  public void single_star_set_for_whole_name() {
    // given
    final List<String> generatedPaths = newArrayList();
    Function<String, Void> consumer = new Function<String, Void>() {
      public Void apply(String string) {
        generatedPaths.add(string);
        return null;
      }
    };

    generatePaths("x/*/z", consumer);
    then(generatedPaths, containsInAnyOrder("x/a/z", "x/b/z", "x/c/z", "x/aa/z", "x/ab/z", "x/ac/z",
        "x/ba/z", "x/bb/z", "x/bc/z", "x/ca/z", "x/cb/z", "x/cc/z"));
  }

  @Test
  public void single_star_set_as_part_of_name() {
    // given
    final List<String> generatedPaths = newArrayList();
    Function<String, Void> consumer = new Function<String, Void>() {
      public Void apply(String string) {
        generatedPaths.add(string);
        return null;
      }
    };

    generatePaths("x/1*9/z", consumer);
    then(generatedPaths, containsInAnyOrder("x/19/z", "x/1a9/z", "x/1b9/z", "x/1c9/z", "x/1aa9/z",
        "x/1ab9/z", "x/1ac9/z", "x/1ba9/z", "x/1bb9/z", "x/1bc9/z", "x/1ca9/z", "x/1cb9/z",
        "x/1cc9/z"));
  }
}
