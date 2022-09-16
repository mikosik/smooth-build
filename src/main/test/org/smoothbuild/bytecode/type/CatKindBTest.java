package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        arguments(0, CatKindB.BLOB),
        arguments(1, CatKindB.BOOL),
        arguments(2, CatKindB.INT),
        arguments(3, CatKindB.STRING),
        arguments(4, CatKindB.ARRAY),
        arguments(5, CatKindB.TUPLE),
        arguments(7, CatKindB.METHOD),
        arguments(8, CatKindB.ORDER),
        arguments(9, CatKindB.COMBINE),
        arguments(10, CatKindB.SELECT),
        arguments(11, CatKindB.CALL),
        arguments(12, CatKindB.INVOKE),
        arguments(13, CatKindB.IF),
        arguments(14, CatKindB.PARAM_REF),
        arguments(15, CatKindB.MAP)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 21})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(CatKindB.fromMarker((byte) marker))
        .isNull();
  }
}
