package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.object.type.base.CatKindH;

public class CatKindHTest {
  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void marker(int marker, CatKindH kind) {
    assertThat(kind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void from_marker(int marker, CatKindH kind) {
    assertThat(CatKindH.fromMarker((byte) marker))
        .isEqualTo(kind);
  }

  private static Collection<Arguments> marker_to_obj_kind_map() {
    return list(
        Arguments.of(0, CatKindH.ARRAY),
        Arguments.of(1, CatKindH.BLOB),
        Arguments.of(2, CatKindH.BOOL),
        Arguments.of(4, CatKindH.INT),
        Arguments.of(5, CatKindH.IF),
        Arguments.of(6, CatKindH.NOTHING),
        Arguments.of(7, CatKindH.TUPLE),
        Arguments.of(8, CatKindH.STRING),

        Arguments.of(9, CatKindH.CALL),
        Arguments.of(10, CatKindH.FUNC),
        Arguments.of(11, CatKindH.ORDER),
        Arguments.of(12, CatKindH.SELECT),
        Arguments.of(14, CatKindH.PARAM_REF),
        Arguments.of(15, CatKindH.COMBINE),
        Arguments.of(17, CatKindH.VARIABLE),
        Arguments.of(18, CatKindH.ANY),
        Arguments.of(19, CatKindH.INVOKE),
        Arguments.of(20, CatKindH.MAP)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 21})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(CatKindH.fromMarker((byte) marker))
        .isNull();
  }
}
