package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.object.type.base.ObjKind;

public class ObjKindTest {
  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void marker(int marker, ObjKind objKind) {
    assertThat(objKind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void from_marker(int marker, ObjKind objKind) {
    assertThat(ObjKind.fromMarker((byte) marker))
        .isEqualTo(objKind);
  }

  private static Collection<Arguments> marker_to_obj_kind_map() {
    return list(
        Arguments.of(0, ObjKind.ARRAY),
        Arguments.of(1, ObjKind.BLOB),
        Arguments.of(2, ObjKind.BOOL),
        Arguments.of(3, ObjKind.LAMBDA),
        Arguments.of(4, ObjKind.INT),
        Arguments.of(6, ObjKind.NOTHING),
        Arguments.of(7, ObjKind.TUPLE),
        Arguments.of(8, ObjKind.STRING),

        Arguments.of(9, ObjKind.CALL),
        Arguments.of(10, ObjKind.CONST),
        Arguments.of(11, ObjKind.ORDER),
        Arguments.of(12, ObjKind.SELECT),
        Arguments.of(14, ObjKind.REF),
        Arguments.of(15, ObjKind.CONSTRUCT),
        Arguments.of(17, ObjKind.VARIABLE),
        Arguments.of(18, ObjKind.ANY),
        Arguments.of(19, ObjKind.NATIVE_METHOD),
        Arguments.of(20, ObjKind.INVOKE)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 5, 21})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(ObjKind.fromMarker((byte) marker))
        .isNull();
  }
}
