package org.smoothbuild.exec.java;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.BASE_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.INT;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.testing.TestingContext;

public class MapTypeToJTypeTest extends TestingContext {
  @Test
  public void verify_all_types_are_tested_below() {
    assertThat(BASE_TYPES.size())
        .isEqualTo(5);
  }

  @ParameterizedTest
  @MethodSource("mapTypeToJType_test_data")
  public void map_type_to_jType(Type type, Class<?> clazz) {
    assertThat(MapTypeToJType.mapTypeToJType(type))
        .isEqualTo(clazz);
  }

  private static Stream<Arguments> mapTypeToJType_test_data() {
    return Stream.of(
        arguments(A, Val.class),
        arguments(BLOB, Blob.class),
        arguments(BOOL, Bool.class),
        arguments(INT, Int.class),
        arguments(NOTHING, Val.class),
        arguments(STRING, Str.class),
        arguments(PERSON, Struc_.class),

        arguments(a(A), Array.class),
        arguments(a(BLOB), Array.class),
        arguments(a(BOOL), Array.class),
        arguments(a(INT), Array.class),
        arguments(a(NOTHING), Array.class),
        arguments(a(STRING), Array.class),
        arguments(a(PERSON), Array.class),

        arguments(a(a(A)), Array.class),
        arguments(a(a(BLOB)), Array.class),
        arguments(a(a(BOOL)), Array.class),
        arguments(a(a(INT)), Array.class),
        arguments(a(a(NOTHING)), Array.class),
        arguments(a(a(STRING)), Array.class),
        arguments(a(a(PERSON)), Array.class)
    );
  }
}
