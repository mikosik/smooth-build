package org.smoothbuild.vm.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CategoryKindBTest {
  @ParameterizedTest
  @MethodSource("marker_to_kind_map")
  public void marker(int marker, CategoryKindB kind) {
    assertThat(kind.marker())
        .isEqualTo(marker);
  }

  @ParameterizedTest
  @MethodSource("from_marker_cases")
  public void from_marker(int marker, CategoryKindB kind) {
    assertThat(CategoryKindB.fromMarker((byte) marker))
        .isEqualTo(kind);
  }

  private static Collection<Arguments> from_marker_cases() {
    var illegalMarkers = list(
        arguments(-2, null),
        arguments(-1, null),
        arguments(19, null),
        arguments(20, null)
    );
    return concat(marker_to_kind_map(), illegalMarkers);
  }

  private static Collection<Arguments> marker_to_kind_map() {
    return list(
        arguments(0, CategoryKinds.BLOB),
        arguments(1, CategoryKinds.BOOL),
        arguments(2, CategoryKinds.INT),
        arguments(3, CategoryKinds.STRING),
        arguments(4, CategoryKinds.ARRAY),
        arguments(5, CategoryKinds.TUPLE),
        arguments(7, CategoryKinds.NATIVE_FUNC),
        arguments(8, CategoryKinds.ORDER),
        arguments(9, CategoryKinds.COMBINE),
        arguments(10, CategoryKinds.SELECT),
        arguments(11, CategoryKinds.CALL),
        arguments(12, CategoryKinds.PICK),
        arguments(13, CategoryKinds.IF_FUNC),
        arguments(14, CategoryKinds.VAR),
        arguments(15, CategoryKinds.MAP_FUNC),
        arguments(16, CategoryKinds.FUNC),
        arguments(17, CategoryKinds.CLOSURIZE),
        arguments(18, CategoryKinds.EXPR_FUNC)
    );
  }
}
