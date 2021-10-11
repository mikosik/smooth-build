package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class SpecKindTest {
  @ParameterizedTest
  @MethodSource("marker_to_spec_kind_map")
  public void marker(int marker, SpecKind specKind) {
    assertThat(specKind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_spec_kind_map")
  public void from_marker(int marker, SpecKind specKind) {
    assertThat(SpecKind.fromMarker((byte) marker))
        .isEqualTo(specKind);
  }

  private static Collection<Arguments> marker_to_spec_kind_map() {
    return list(
        Arguments.of(0, SpecKind.ARRAY),
        Arguments.of(1, SpecKind.BLOB),
        Arguments.of(2, SpecKind.BOOL),
        Arguments.of(3, SpecKind.LAMBDA),
        Arguments.of(4, SpecKind.INT),
        Arguments.of(6, SpecKind.NOTHING),
        Arguments.of(7, SpecKind.RECORD),
        Arguments.of(8, SpecKind.STRING),

        Arguments.of(9, SpecKind.CALL),
        Arguments.of(10, SpecKind.CONST),
        Arguments.of(11, SpecKind.ARRAY_EXPR),
        Arguments.of(12, SpecKind.SELECT),
        Arguments.of(13, SpecKind.NULL),
        Arguments.of(14, SpecKind.REF),
        Arguments.of(15, SpecKind.RECORD_EXPR),
        Arguments.of(16, SpecKind.ABSENT),
        Arguments.of(17, SpecKind.VARIABLE),
        Arguments.of(18, SpecKind.ANY),
        Arguments.of(19, SpecKind.INVOKE)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 20})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(SpecKind.fromMarker((byte) marker))
        .isNull();
  }
}
