package org.smoothbuild.app.layout;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.stream;

import org.junit.jupiter.api.Test;

public class SmoothSpaceTest {
  @Test
  void all_prefixes_are_unique() {
    var prefixes = stream(SmoothSpace.values()).map(SmoothSpace::prefix).toList();
    assertThat(prefixes).containsNoDuplicates();
  }
}
