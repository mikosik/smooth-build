package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_DATA;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_FLAG;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.DATA;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_NON_GENERIC_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.FLAG;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Types.BASIC_TYPES;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.common.TestingLocation.loc;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class TypeTest {
  private static final Location LOCATION = internal();

  @Test
  public void verify_all_basic_types_are_tested() {
    assertThat(BASIC_TYPES)
        .hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Type type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Type type, String name) {
    assertThat(type.q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Type type, String name) {
    assertThat(type.toString())
        .isEqualTo("Type(`" + name + "`)");
  }

  public static Stream<Arguments> names() {
    return Stream.of(
        arguments(BOOL, "Bool"),
        arguments(STRING, "String"),
        arguments(BLOB, "Blob"),
        arguments(NOTHING, "Nothing"),
        arguments(PERSON, "Person"),
        arguments(A, "A"),

        arguments(ARRAY_BOOL, "[Bool]"),
        arguments(ARRAY_STRING, "[String]"),
        arguments(ARRAY_BLOB, "[Blob]"),
        arguments(ARRAY_NOTHING, "[Nothing]"),
        arguments(ARRAY_PERSON, "[Person]"),
        arguments(ARRAY_A, "[A]"),

        arguments(ARRAY2_BOOL, "[[Bool]]"),
        arguments(ARRAY2_STRING, "[[String]]"),
        arguments(ARRAY2_BLOB, "[[Blob]]"),
        arguments(ARRAY2_NOTHING, "[[Nothing]]"),
        arguments(ARRAY2_PERSON, "[[Person]]"),
        arguments(ARRAY2_A, "[[A]]")
    );
  }

  @ParameterizedTest
  @MethodSource("coreType_test_data")
  public void coreType(Type type, Type expected) {
    assertThat(type.coreType())
        .isEqualTo(expected);
  }

  public static List<Arguments> coreType_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, type));
      result.add(arguments(array(type), type));
      result.add(arguments(array(array(type)), type));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("replaceCoreType_test_data")
  public void replaceCoreType(Type type, Type coreType, Type expected) {
    assertThat(type.replaceCoreType(coreType))
        .isEqualTo(expected);
  }

  public static List<Arguments> replaceCoreType_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      for (Type newCore : ELEMENTARY_TYPES) {
        Type typeArray = array(type);
        ArrayType newCoreArray = array(newCore);
        result.add(arguments(type, newCore, newCore));
        result.add(arguments(typeArray, newCore, newCoreArray));
        result.add(arguments(type, newCoreArray, newCoreArray));
        result.add(arguments(typeArray, newCoreArray, array(array(newCore))));
      }
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("coreDepth_test_data")
  public void coreDepth(Type type, int expected) {
    assertThat(type.coreDepth())
        .isEqualTo(expected);
  }

  public static List<Arguments> coreDepth_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, 0));
      result.add(arguments(array(type), 1));
      result.add(arguments(array(array(type)), 2));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("isGeneric_test_data")
  public void isGeneric(Type type, boolean expected) {
    assertThat(type.isGeneric())
        .isEqualTo(expected);
  }

  public static List<Arguments> isGeneric_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_NON_GENERIC_TYPES) {
      result.add(arguments(type, false));
      result.add(arguments(array(type), false));
      result.add(arguments(array(array(type)), false));
    }
    result.add(arguments(A, true));
    result.add(arguments(array(A), true));
    result.add(arguments(array(array(A)), true));

    return result;
  }

  @ParameterizedTest
  @MethodSource("isArray_test_data")
  public void isArray(Type type, boolean expected) {
    assertThat(type.isArray())
        .isEqualTo(expected);
  }

  public static List<Arguments> isArray_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, false));
      result.add(arguments(array(type), true));
      result.add(arguments(array(array(type)), true));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("isAssignableFrom_test_data")
  public void isAssignableFrom(TestedAssignmentSpec spec) {
    assertThat(spec.target.type().isAssignableFrom(spec.source.type()))
        .isEqualTo(spec.allowed);
  }

  public static List<TestedAssignmentSpec> isAssignableFrom_test_data() {
    return TestedAssignmentSpec.assignment_test_specs();
  }

  @ParameterizedTest
  @MethodSource("isParamAssignableFrom_test_data")
  public void isParamAssignableFrom(TestedAssignmentSpec spec) {
    assertThat(spec.target.type().isParamAssignableFrom(spec.source.type()))
        .isEqualTo(spec.allowed);
  }

  public static List<TestedAssignmentSpec> isParamAssignableFrom_test_data() {
    return TestedAssignmentSpec.parameter_assignment_test_specs();
  }

  @ParameterizedTest
  @MethodSource("commonSuperType_test_data")
  public void commonSuperType(Type type1, Type type2, Optional<Type> expected) {
    assertThat(type1.commonSuperType(type2))
        .isEqualTo(expected);
    assertThat(type2.commonSuperType(type1))
        .isEqualTo(expected);
  }

  public static List<Arguments> commonSuperType_test_data() {
    return List.of(
        arguments(BLOB, BLOB, Optional.of(BLOB)),
        arguments(BLOB, BOOL, Optional.empty()),
        arguments(BLOB, NOTHING, Optional.of(BLOB)),
        arguments(BLOB, STRING, Optional.empty()),
        arguments(BLOB, DATA, Optional.empty()),
        arguments(BLOB, FLAG, Optional.empty()),
        arguments(BLOB, PERSON, Optional.empty()),
        arguments(BLOB, A, Optional.empty()),
        arguments(BLOB, ARRAY_BLOB, Optional.empty()),
        arguments(BOOL, ARRAY_BOOL, Optional.empty()),
        arguments(BOOL, ARRAY_NOTHING, Optional.empty()),
        arguments(BLOB, ARRAY_STRING, Optional.empty()),
        arguments(BOOL, ARRAY_DATA, Optional.empty()),
        arguments(BOOL, ARRAY_FLAG, Optional.empty()),
        arguments(BOOL, ARRAY_PERSON, Optional.empty()),
        arguments(BLOB, ARRAY_A, Optional.empty()),

        arguments(BOOL, BOOL, Optional.of(BOOL)),
        arguments(BOOL, NOTHING, Optional.of(BOOL)),
        arguments(BOOL, STRING, Optional.empty()),
        arguments(BOOL, DATA, Optional.empty()),
        arguments(BOOL, FLAG, Optional.empty()),
        arguments(BOOL, PERSON, Optional.empty()),
        arguments(BOOL, A, Optional.empty()),
        arguments(BOOL, ARRAY_BLOB, Optional.empty()),
        arguments(BOOL, ARRAY_BOOL, Optional.empty()),
        arguments(BOOL, ARRAY_NOTHING, Optional.empty()),
        arguments(BOOL, ARRAY_STRING, Optional.empty()),
        arguments(BOOL, ARRAY_DATA, Optional.empty()),
        arguments(BOOL, ARRAY_FLAG, Optional.empty()),
        arguments(BOOL, ARRAY_PERSON, Optional.empty()),
        arguments(BOOL, ARRAY_A, Optional.empty()),

        arguments(NOTHING, STRING, Optional.of(STRING)),
        arguments(NOTHING, DATA, Optional.of(DATA)),
        arguments(NOTHING, FLAG, Optional.of(FLAG)),
        arguments(NOTHING, PERSON, Optional.of(PERSON)),
        arguments(NOTHING, A, Optional.of(A)),
        arguments(NOTHING, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(NOTHING, ARRAY_BOOL, Optional.of(ARRAY_BOOL)),
        arguments(NOTHING, ARRAY_NOTHING, Optional.of(ARRAY_NOTHING)),
        arguments(NOTHING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(NOTHING, ARRAY_DATA, Optional.of(ARRAY_DATA)),
        arguments(NOTHING, ARRAY_FLAG, Optional.of(ARRAY_FLAG)),
        arguments(NOTHING, ARRAY_PERSON, Optional.of(ARRAY_PERSON)),
        arguments(NOTHING, ARRAY_A, Optional.of(ARRAY_A)),

        arguments(STRING, STRING, Optional.of(STRING)),
        arguments(STRING, DATA, Optional.empty()),
        arguments(STRING, FLAG, Optional.empty()),
        arguments(STRING, PERSON, Optional.empty()),
        arguments(STRING, A, Optional.empty()),
        arguments(STRING, ARRAY_BLOB, Optional.empty()),
        arguments(STRING, ARRAY_BOOL, Optional.empty()),
        arguments(STRING, ARRAY_NOTHING, Optional.empty()),
        arguments(STRING, ARRAY_STRING, Optional.empty()),
        arguments(STRING, ARRAY_DATA, Optional.empty()),
        arguments(STRING, ARRAY_FLAG, Optional.empty()),
        arguments(STRING, ARRAY_PERSON, Optional.empty()),
        arguments(STRING, ARRAY_A, Optional.empty()),

        arguments(DATA, DATA, Optional.of(DATA)),
        arguments(DATA, FLAG, Optional.empty()),
        arguments(DATA, PERSON, Optional.empty()),
        arguments(DATA, A, Optional.empty()),
        arguments(DATA, ARRAY_BLOB, Optional.empty()),
        arguments(DATA, ARRAY_BOOL, Optional.empty()),
        arguments(DATA, ARRAY_NOTHING, Optional.empty()),
        arguments(DATA, ARRAY_STRING, Optional.empty()),
        arguments(DATA, ARRAY_DATA, Optional.empty()),
        arguments(DATA, ARRAY_FLAG, Optional.empty()),
        arguments(DATA, ARRAY_PERSON, Optional.empty()),
        arguments(DATA, ARRAY_A, Optional.empty()),

        arguments(FLAG, FLAG, Optional.of(FLAG)),
        arguments(FLAG, PERSON, Optional.empty()),
        arguments(FLAG, A, Optional.empty()),
        arguments(FLAG, ARRAY_BLOB, Optional.empty()),
        arguments(FLAG, ARRAY_BOOL, Optional.empty()),
        arguments(FLAG, ARRAY_NOTHING, Optional.empty()),
        arguments(FLAG, ARRAY_STRING, Optional.empty()),
        arguments(FLAG, ARRAY_DATA, Optional.empty()),
        arguments(FLAG, ARRAY_FLAG, Optional.empty()),
        arguments(FLAG, ARRAY_PERSON, Optional.empty()),
        arguments(FLAG, ARRAY_A, Optional.empty()),

        arguments(PERSON, PERSON, Optional.of(PERSON)),
        arguments(PERSON, A, Optional.empty()),
        arguments(PERSON, ARRAY_BLOB, Optional.empty()),
        arguments(PERSON, ARRAY_BOOL, Optional.empty()),
        arguments(PERSON, ARRAY_NOTHING, Optional.empty()),
        arguments(PERSON, ARRAY_STRING, Optional.empty()),
        arguments(PERSON, ARRAY_DATA, Optional.empty()),
        arguments(PERSON, ARRAY_FLAG, Optional.empty()),
        arguments(PERSON, ARRAY_PERSON, Optional.empty()),
        arguments(PERSON, ARRAY_A, Optional.empty()),

        arguments(A, A, Optional.of(A)),
        arguments(A, B, Optional.empty()),
        arguments(A, ARRAY_BLOB, Optional.empty()),
        arguments(A, ARRAY_BOOL, Optional.empty()),
        arguments(A, ARRAY_NOTHING, Optional.empty()),
        arguments(A, ARRAY_STRING, Optional.empty()),
        arguments(A, ARRAY_DATA, Optional.empty()),
        arguments(A, ARRAY_FLAG, Optional.empty()),
        arguments(A, ARRAY_PERSON, Optional.empty()),
        arguments(A, ARRAY_A, Optional.empty()),
        arguments(A, ARRAY_B, Optional.empty()),

        arguments(ARRAY_BLOB, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, ARRAY_BOOL, Optional.empty()),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, ARRAY_STRING, Optional.empty()),
        arguments(ARRAY_BLOB, ARRAY_DATA, Optional.empty()),
        arguments(ARRAY_BLOB, ARRAY_FLAG, Optional.empty()),
        arguments(ARRAY_BLOB, ARRAY_PERSON, Optional.empty()),
        arguments(ARRAY_BLOB, ARRAY_A, Optional.empty()),

        arguments(ARRAY_BOOL, ARRAY_BOOL, Optional.of(ARRAY_BOOL)),
        arguments(ARRAY_BOOL, ARRAY_NOTHING, Optional.of(ARRAY_BOOL)),
        arguments(ARRAY_BOOL, ARRAY_STRING, Optional.empty()),
        arguments(ARRAY_BOOL, ARRAY_DATA, Optional.empty()),
        arguments(ARRAY_BOOL, ARRAY_FLAG, Optional.empty()),
        arguments(ARRAY_BOOL, ARRAY_PERSON, Optional.empty()),
        arguments(ARRAY_BOOL, ARRAY_A, Optional.empty()),

        arguments(ARRAY_NOTHING, ARRAY_NOTHING, Optional.of(ARRAY_NOTHING)),
        arguments(ARRAY_NOTHING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_NOTHING, ARRAY_A, Optional.of(ARRAY_A)),
        arguments(ARRAY_NOTHING, ARRAY_DATA, Optional.of(ARRAY_DATA)),
        arguments(ARRAY_NOTHING, ARRAY_FLAG, Optional.of(ARRAY_FLAG)),
        arguments(ARRAY_NOTHING, ARRAY_PERSON, Optional.of(ARRAY_PERSON)),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, Optional.of(ARRAY2_NOTHING)),

        arguments(ARRAY_STRING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, ARRAY_A, Optional.empty()),
        arguments(ARRAY_STRING, ARRAY_DATA, Optional.empty()),
        arguments(ARRAY_STRING, ARRAY_FLAG, Optional.empty()),
        arguments(ARRAY_STRING, ARRAY_PERSON, Optional.empty()),

        arguments(ARRAY_A, ARRAY_A, Optional.of(ARRAY_A)),
        arguments(ARRAY_A, ARRAY_B, Optional.empty()),
        arguments(ARRAY_A, ARRAY_DATA, Optional.empty()),
        arguments(ARRAY_A, ARRAY_FLAG, Optional.empty()),
        arguments(ARRAY_A, ARRAY_PERSON, Optional.empty()),

        arguments(ARRAY_DATA, ARRAY_DATA, Optional.of(ARRAY_DATA)),
        arguments(ARRAY_DATA, ARRAY_FLAG, Optional.empty()),
        arguments(ARRAY_DATA, ARRAY_PERSON, Optional.empty()),

        arguments(ARRAY_FLAG, ARRAY_FLAG, Optional.of(ARRAY_FLAG)),
        arguments(ARRAY_FLAG, ARRAY_PERSON, Optional.empty()),

        arguments(ARRAY_PERSON, ARRAY_PERSON, Optional.of(ARRAY_PERSON))
    );
  }

  @ParameterizedTest
  @MethodSource("actualCoreTypeWhenAssignedFrom_test_data")
  public void actualCoreTypeWhenAssignedFrom(Type type, Type assigned, Type expected) {
    if (expected == null) {
      assertCall(() -> type.actualCoreTypeWhenAssignedFrom(assigned))
          .throwsException(IllegalArgumentException.class);
    } else {
      assertThat(type.actualCoreTypeWhenAssignedFrom(assigned))
          .isEqualTo(expected);
    }
  }

  public static List<Arguments> actualCoreTypeWhenAssignedFrom_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : Lists.concat(ELEMENTARY_TYPES, B)) {
      if (type.isNothing()) {
        result.add(arguments(A, NOTHING, NOTHING));
        result.add(arguments(A, array(NOTHING), array(NOTHING)));
        result.add(arguments(A, array(array(NOTHING)), array(array(NOTHING))));

        result.add(arguments(array(A), NOTHING, NOTHING));
        result.add(arguments(array(A), array(NOTHING), NOTHING));
        result.add(arguments(array(A), array(array(NOTHING)), array(NOTHING)));

        result.add(arguments(array(array(A)), NOTHING, NOTHING));
        result.add(arguments(array(array(A)), array(NOTHING), NOTHING));
        result.add(arguments(array(array(A)), array(array(NOTHING)), NOTHING));
      } else {
        result.add(arguments(A, type, type));
        result.add(arguments(A, array(type), array(type)));
        result.add(arguments(A, array(array(type)), array(array(type))));

        result.add(arguments(array(A), type, null));
        result.add(arguments(array(A), array(type), type));
        result.add(arguments(array(A), array(array(type)), array(type)));

        result.add(arguments(array(array(A)), type, null));
        result.add(arguments(array(array(A)), array(type), null));
        result.add(arguments(array(array(A)), array(array(type)), type));
      }
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("elemType_test_data")
  public void elemType(ArrayType type, Type expected) {
    assertThat(type.elemType())
        .isEqualTo(expected);
  }

  public static List<Arguments> elemType_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(array(type), type));
      result.add(arguments(array(array(type)), array(type)));
      result.add(arguments(array(array(array(type))), array(array(type))));
    }
    return result;
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<Type> types = ImmutableList.<Type>builder()
        .addAll(ELEMENTARY_TYPES)
        .add(B)
        .add(struct("MyStruct", loc(), list()))
        .add(struct("MyStruct", loc(), list(field("field"))))
        .add(struct("MyStruct2", loc(), list(field("field"))))
        .add(struct("MyStruct", loc(), list(field("field2"))))
        .build();
    for (Type type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(array(type), array(type));
      equalsTester.addEqualityGroup(array(array(type)), array(array(type)));
    }
    equalsTester.testEquals();
  }

  private Item field(String name) {
    return new Item(string(), name, Optional.empty(), LOCATION);
  }
}
