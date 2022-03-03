package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.bytecode.type.base.CatKindB;

public class CatKindBTest {
  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void marker(int marker, CatKindB kind) {
    assertThat(kind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void from_marker(int marker, CatKindB kind) {
    assertThat(CatKindB.fromMarker((byte) marker))
        .isEqualTo(kind);
  }

  private static Collection<Arguments> marker_to_obj_kind_map() {
    return list(
        Arguments.of(0, CatKindB.ARRAY),
        Arguments.of(1, CatKindB.BLOB),
        Arguments.of(2, CatKindB.BOOL),
        Arguments.of(3, CatKindB.METHOD),
        Arguments.of(4, CatKindB.INT),
        Arguments.of(5, CatKindB.IF),
        Arguments.of(6, CatKindB.NOTHING),
        Arguments.of(7, CatKindB.TUPLE),
        Arguments.of(8, CatKindB.STRING),
        Arguments.of(9, CatKindB.CALL),
        Arguments.of(10, CatKindB.FUNC),
        Arguments.of(11, CatKindB.ORDER),
        Arguments.of(12, CatKindB.SELECT),
        Arguments.of(14, CatKindB.PARAM_REF),
        Arguments.of(15, CatKindB.COMBINE),
        Arguments.of(17, CatKindB.VAR),
        Arguments.of(18, CatKindB.ANY),
        Arguments.of(19, CatKindB.INVOKE),
        Arguments.of(20, CatKindB.MAP)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 21})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(CatKindB.fromMarker((byte) marker))
        .isNull();
  }
}
