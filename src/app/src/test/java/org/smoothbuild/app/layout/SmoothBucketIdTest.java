package org.smoothbuild.app.layout;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.stream;

import org.junit.jupiter.api.Test;

public class SmoothBucketIdTest {
  @Test
  void all_prefixes_are_unique() {
    var prefixes = stream(SmoothBucketId.values()).map(SmoothBucketId::get).toList();
    assertThat(prefixes).containsNoDuplicates();
  }
}
