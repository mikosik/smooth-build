package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.bytecode.type.CategoryKinds.ARRAY;
import static org.smoothbuild.bytecode.type.CategoryKinds.BLOB;
import static org.smoothbuild.bytecode.type.CategoryKinds.BOOL;
import static org.smoothbuild.bytecode.type.CategoryKinds.CALL;
import static org.smoothbuild.bytecode.type.CategoryKinds.COMBINE;
import static org.smoothbuild.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.INT;
import static org.smoothbuild.bytecode.type.CategoryKinds.MAP_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.NAT_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.ORDER;
import static org.smoothbuild.bytecode.type.CategoryKinds.PICK;
import static org.smoothbuild.bytecode.type.CategoryKinds.REF;
import static org.smoothbuild.bytecode.type.CategoryKinds.SELECT;
import static org.smoothbuild.bytecode.type.CategoryKinds.STRING;
import static org.smoothbuild.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CategoryKindBTest {
  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void marker(int marker, CategoryKindB kind) {
    assertThat(kind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("marker_to_obj_kind_map")
  public void from_marker(int marker, CategoryKindB kind) {
    assertThat(CategoryKindB.fromMarker((byte) marker))
        .isEqualTo(kind);
  }

  private static Collection<Arguments> marker_to_obj_kind_map() {
    return list(
        arguments(0, BLOB),
        arguments(1, BOOL),
        arguments(2, INT),
        arguments(3, STRING),
        arguments(4, ARRAY),
        arguments(5, TUPLE),
        arguments(7, NAT_FUNC),
        arguments(8, ORDER),
        arguments(9, COMBINE),
        arguments(10, SELECT),
        arguments(11, CALL),
        arguments(12, PICK),
        arguments(13, IF_FUNC),
        arguments(14, REF),
        arguments(15, MAP_FUNC),
        arguments(16, FUNC)
    );
  }

  @ParameterizedTest
  @ValueSource(bytes = {-1, 21})
  public void from_marker_returns_null_for_illegal_marker(int marker) {
    assertThat(CategoryKindB.fromMarker((byte) marker))
        .isNull();
  }
}
