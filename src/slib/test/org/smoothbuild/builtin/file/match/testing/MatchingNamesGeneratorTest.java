package org.smoothbuild.builtin.file.match.testing;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.file.match.testing.MatchingNamesGenerator.generateNames;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

public class MatchingNamesGeneratorTest {
  @Test
  public void test() {
    CollectingConsumer collectingConsumer = new CollectingConsumer();
    generateNames("x*z", collectingConsumer);
    assertThat(collectingConsumer.generatedNames)
        .containsExactly("xz", "xaz", "xbz", "xcz", "xaaz", "xabz", "xacz", "xbaz", "xbbz", "xbcz",
            "xcaz", "xcbz", "xccz");
  }

  private static class CollectingConsumer implements Consumer<String> {
    private final List<String> generatedNames = new ArrayList<>();

    @Override
    public void accept(String name) {
      generatedNames.add(name);
    }
  }
}
