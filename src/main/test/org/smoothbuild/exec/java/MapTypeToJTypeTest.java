package org.smoothbuild.exec.java;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypesS.A;
import static org.smoothbuild.lang.base.type.TestingTypesS.BASE_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypesS.INT;
import static org.smoothbuild.lang.base.type.TestingTypesS.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypesS.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.TestingTypesS.a;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.testing.TestingContext;

public class MapTypeToJTypeTest extends TestingContext {
  @Test
  public void verify_all_types_are_tested_below() {
    assertThat(BASE_TYPES.size())
        .isEqualTo(5);
  }

  @ParameterizedTest
  @MethodSource("mapTypeToJType_test_data")
  public void map_type_to_jType(TypeS type, Class<?> clazz) {
    assertThat(MapTypeToJType.mapTypeToJType(type))
        .isEqualTo(clazz);
  }

  private static Stream<Arguments> mapTypeToJType_test_data() {
    return Stream.of(
        arguments(A, ValueH.class),
        arguments(BLOB, BlobH.class),
        arguments(BOOL, BoolH.class),
        arguments(INT, IntH.class),
        arguments(NOTHING, ValueH.class),
        arguments(STRING, StringH.class),
        arguments(PERSON, TupleH.class),

        arguments(a(A), ArrayH.class),
        arguments(a(BLOB), ArrayH.class),
        arguments(a(BOOL), ArrayH.class),
        arguments(a(INT), ArrayH.class),
        arguments(a(NOTHING), ArrayH.class),
        arguments(a(STRING), ArrayH.class),
        arguments(a(PERSON), ArrayH.class),

        arguments(a(a(A)), ArrayH.class),
        arguments(a(a(BLOB)), ArrayH.class),
        arguments(a(a(BOOL)), ArrayH.class),
        arguments(a(a(INT)), ArrayH.class),
        arguments(a(a(NOTHING)), ArrayH.class),
        arguments(a(a(STRING)), ArrayH.class),
        arguments(a(a(PERSON)), ArrayH.class)
    );
  }
}
