package org.smoothbuild.testing.fs.match;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.testing.fs.match.MatchingNamesGenerator.generateNames;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;

public class MatchingNamesGeneratorTest {

  @Test
  public void test() {
    // given
    final List<String> generatedNames = newArrayList();
    Function<String, Void> collectingConsumer = new Function<String, Void>() {
      public Void apply(String name) {
        generatedNames.add(name);
        return null;
      }
    };

    // when
    generateNames("x*z", collectingConsumer);

    // then
    List<String> expected = newArrayList();
    expected.add("xz");

    expected.add("xaz");
    expected.add("xbz");
    expected.add("xcz");

    expected.add("xaaz");
    expected.add("xabz");
    expected.add("xacz");

    expected.add("xbaz");
    expected.add("xbbz");
    expected.add("xbcz");

    expected.add("xcaz");
    expected.add("xcbz");
    expected.add("xccz");

    assertThat(generatedNames).containsOnly(expected.toArray(new String[] {}));
  }
}
