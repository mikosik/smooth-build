package org.smoothbuild.virtualmachine.bytecode.type;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class KindIdTest {
  @Test
  void marker_values_are_correct() {
    var values = KindId.values();
    for (int i = 0; i < values.length; i++) {
      assertThat(values[i].byteMarker()).isEqualTo(i);
    }
  }

  @Nested
  class _from_ordinal {
    @Test
    void returns_enum_value_for_valid_ordinal() {
      for (KindId value : KindId.values()) {
        var ordinal = value.ordinal();
        assertThat(KindId.fromOrdinal(ordinal)).isEqualTo(value);
      }
    }

    @Test
    void returns_null_for_negative_ordinal() {
      assertThat(KindId.fromOrdinal(-1)).isNull();
    }

    @Test
    void returns_null_for_ordinal_greater_than_max_ordinal() {
      assertThat(KindId.fromOrdinal(KindId.values().length)).isNull();
    }
  }
}
