package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        Arguments.of(0, SpecKind.INT),
        Arguments.of(1, SpecKind.NOTHING),
        Arguments.of(2, SpecKind.TUPLE),
        Arguments.of(3, SpecKind.ARRAY),
        Arguments.of(4, SpecKind.BLOB),
        Arguments.of(5, SpecKind.BOOL),
        Arguments.of(6, SpecKind.STRING)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 7})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(SpecKind.fromMarker((byte) marker))
        .isNull();
  }
}
