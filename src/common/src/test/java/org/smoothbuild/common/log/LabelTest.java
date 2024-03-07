package org.smoothbuild.common.log;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.Label.label;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LabelTest {
  @Nested
  class _toString {
    @Test
    void with_zero_parts() {
      var label = label();
      assertThat(label.toString()).isEqualTo("::");
    }

    @Test
    void with_one_part() {
      var label = label("name");
      assertThat(label.toString()).isEqualTo("::name");
    }

    @Test
    void with_two_parts() {
      var label = label("name").append(label("second"));
      assertThat(label.toString()).isEqualTo("::name::second");
    }
  }
}
