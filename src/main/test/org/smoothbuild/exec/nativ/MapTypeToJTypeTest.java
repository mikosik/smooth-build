package org.smoothbuild.exec.nativ;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.lang.base.type.Type;

public class MapTypeToJTypeTest {
  @Test
  public void verify_all_types_are_tested_below() {
    assertThat(BASE_TYPES.size())
        .isEqualTo(4);
  }

  @ParameterizedTest
  @MethodSource("mapTypeToJType_test_data")
  public void map_type_to_jType(Type type, Class<?> clazz) {
    assertThat(MapTypeToJType.mapTypeToJType(type))
        .isEqualTo(clazz);
  }

  private static Stream<Arguments> mapTypeToJType_test_data() {
    return Stream.of(
        arguments(A, Obj.class),
        arguments(BLOB, Blob.class),
        arguments(BOOL, Bool.class),
        arguments(NOTHING, Obj.class),
        arguments(STRING, Str.class),
        arguments(PERSON, Tuple.class),

        arguments(ARRAY_A, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_NOTHING, Array.class),
        arguments(ARRAY_STRING, Array.class),
        arguments(ARRAY_PERSON, Array.class),
        arguments(ARRAY2_A, Array.class),
        arguments(ARRAY2_BLOB, Array.class),
        arguments(ARRAY2_BOOL, Array.class),
        arguments(ARRAY2_NOTHING, Array.class),
        arguments(ARRAY2_STRING, Array.class),
        arguments(ARRAY2_PERSON, Array.class)
    );
  }
}
