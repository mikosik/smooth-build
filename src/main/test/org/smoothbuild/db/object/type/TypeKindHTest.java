package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.object.type.base.TypeKindH;

public class TypeKindHTest {
  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void marker(int marker, TypeKindH kind) {
    assertThat(kind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void from_marker(int marker, TypeKindH kind) {
    assertThat(TypeKindH.fromMarker((byte) marker))
        .isEqualTo(kind);
  }

  private static Collection<Arguments> marker_to_obj_kind_map() {
    return list(
        Arguments.of(0, TypeKindH.ARRAY),
        Arguments.of(1, TypeKindH.BLOB),
        Arguments.of(2, TypeKindH.BOOL),
        Arguments.of(3, TypeKindH.FUNCTION),
        Arguments.of(4, TypeKindH.INT),
        Arguments.of(5, TypeKindH.IF),
        Arguments.of(6, TypeKindH.NOTHING),
        Arguments.of(7, TypeKindH.TUPLE),
        Arguments.of(8, TypeKindH.STRING),

        Arguments.of(9, TypeKindH.CALL),
        Arguments.of(10, TypeKindH.CONST),
        Arguments.of(11, TypeKindH.ORDER),
        Arguments.of(12, TypeKindH.SELECT),
        Arguments.of(14, TypeKindH.REF),
        Arguments.of(15, TypeKindH.CONSTRUCT),
        Arguments.of(17, TypeKindH.VARIABLE),
        Arguments.of(18, TypeKindH.ANY),
        Arguments.of(19, TypeKindH.NATIVE_METHOD),
        Arguments.of(20, TypeKindH.INVOKE),
        Arguments.of(21, TypeKindH.MAP)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 22})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(TypeKindH.fromMarker((byte) marker))
        .isNull();
  }
}
