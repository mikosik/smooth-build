package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.object.type.base.SpecKindH;

public class SpecKindHTest {
  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void marker(int marker, SpecKindH kind) {
    assertThat(kind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void from_marker(int marker, SpecKindH kind) {
    assertThat(SpecKindH.fromMarker((byte) marker))
        .isEqualTo(kind);
  }

  private static Collection<Arguments> marker_to_obj_kind_map() {
    return list(
        Arguments.of(0, SpecKindH.ARRAY),
        Arguments.of(1, SpecKindH.BLOB),
        Arguments.of(2, SpecKindH.BOOL),
        Arguments.of(3, SpecKindH.ABSTRACT_FUNCTION),
        Arguments.of(4, SpecKindH.INT),
        Arguments.of(5, SpecKindH.IF_FUNCTION),
        Arguments.of(6, SpecKindH.NOTHING),
        Arguments.of(7, SpecKindH.TUPLE),
        Arguments.of(8, SpecKindH.STRING),

        Arguments.of(9, SpecKindH.CALL),
        Arguments.of(11, SpecKindH.ORDER),
        Arguments.of(12, SpecKindH.SELECT),
        Arguments.of(14, SpecKindH.REF),
        Arguments.of(15, SpecKindH.CONSTRUCT),
        Arguments.of(17, SpecKindH.VARIABLE),
        Arguments.of(18, SpecKindH.ANY),
        Arguments.of(19, SpecKindH.NATIVE_FUNCTION),
        Arguments.of(21, SpecKindH.MAP_FUNCTION),
        Arguments.of(22, SpecKindH.DEFINED_FUNCTION)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 23})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(SpecKindH.fromMarker((byte) marker))
        .isNull();
  }
}
