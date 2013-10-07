package org.smoothbuild.testing.fs.base.match;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

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

    // when
    MatchingPathsGenerator.generatePaths("x/*/z", consumer);

    // then
    List<String> expected = newArrayList();
    expected.add("x/a/z");
    expected.add("x/b/z");
    expected.add("x/c/z");

    expected.add("x/aa/z");
    expected.add("x/ab/z");
    expected.add("x/ac/z");

    expected.add("x/ba/z");
    expected.add("x/bb/z");
    expected.add("x/bc/z");

    expected.add("x/ca/z");
    expected.add("x/cb/z");
    expected.add("x/cc/z");

    assertThat(generatedPaths).containsOnly(expected.toArray(new String[] {}));
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

    // when
    MatchingPathsGenerator.generatePaths("x/1*9/z", consumer);

    // then
    List<String> expected = newArrayList();
    expected.add("x/19/z");

    expected.add("x/1a9/z");
    expected.add("x/1b9/z");
    expected.add("x/1c9/z");

    expected.add("x/1aa9/z");
    expected.add("x/1ab9/z");
    expected.add("x/1ac9/z");

    expected.add("x/1ba9/z");
    expected.add("x/1bb9/z");
    expected.add("x/1bc9/z");

    expected.add("x/1ca9/z");
    expected.add("x/1cb9/z");
    expected.add("x/1cc9/z");

    assertThat(generatedPaths).containsOnly(generatedPaths.toArray(new String[] {}));
  }
}
