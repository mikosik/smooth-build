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
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_NON_GENERIC_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_NON_STRUCT_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_TYPES;
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
  @MethodSource("changeCoreDepthBy_test_data_with_illegal_values")
  public void changeCoreDepthBy_fails_for(Type type, int change) {
    assertCall(() -> type.changeCoreDepthBy(change))
        .throwsException(IllegalArgumentException.class);
  }

  public static List<Arguments> changeCoreDepthBy_test_data_with_illegal_values() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, -1));
      result.add(arguments(type, -2));
      result.add(arguments(array(type), -2));
      result.add(arguments(array(type), -3));
      result.add(arguments(array(array(type)), -3));
      result.add(arguments(array(array(type)), -4));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("changeCoreDepth_test_data")
  public void changeCoreDepthBy(Type type, int change, Type expected) {
    assertThat(type.changeCoreDepthBy(change))
        .isEqualTo(expected);
  }

  public static List<Arguments> changeCoreDepth_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, 0, type));
      result.add(arguments(type, 1, array(type)));
      result.add(arguments(type, 2, array(array(type))));

      result.add(arguments(array(type), -1, type));
      result.add(arguments(array(type), 0, array(type)));
      result.add(arguments(array(type), 1, array(array(type))));
      result.add(arguments(array(type), 2, array(array(array(type)))));

      result.add(arguments(array(array(type)), -2, type));
      result.add(arguments(array(array(type)), -1, array(type)));
      result.add(arguments(array(array(type)), 0, array(array(type))));
      result.add(arguments(array(array(type)), 1, array(array(array(type)))));
      result.add(arguments(array(array(type)), 2, array(array(array(array(type))))));
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
  @MethodSource("superType_test_data")
  public void superType(Type type, Type expected) {
    assertThat(type.superType())
        .isEqualTo(expected);
  }

  public static List<Arguments> superType_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_NON_STRUCT_TYPES) {
      result.add(arguments(type, null));
      result.add(arguments(array(type), null));
      result.add(arguments(array(array(type)), null));
    }
    result.add(arguments(PERSON, STRING));
    result.add(arguments(array(PERSON), array(STRING)));
    result.add(arguments(array(array(PERSON)), array(array(STRING))));
    return result;
  }

  @ParameterizedTest
  @MethodSource("hierarchy_test_data")
  public void hierarchy(List<Type> hierarchy) {
    Type root = hierarchy.get(hierarchy.size() - 1);
    assertThat(root.hierarchy())
        .isEqualTo(hierarchy);
  }

  public static List<Arguments> hierarchy_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_NON_STRUCT_TYPES) {
      result.add(arguments(list(type)));
      result.add(arguments(list(array(type))));
      result.add(arguments(list(array(array(type)))));
    }
    result.add(arguments(list(STRING, PERSON)));
    result.add(arguments(list(array(STRING), array(PERSON))));
    result.add(arguments(list(array(array(STRING)), array(array(PERSON)))));
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
        arguments(BOOL, STRING, Optional.empty()),
        arguments(BOOL, BOOL, Optional.of(BOOL)),
        arguments(BOOL, BLOB, Optional.empty()),
        arguments(BOOL, NOTHING, Optional.of(BOOL)),
        arguments(BOOL, A, Optional.empty()),
        arguments(BOOL, ARRAY_STRING, Optional.empty()),
        arguments(BOOL, ARRAY_BOOL, Optional.empty()),
        arguments(BOOL, ARRAY_BLOB, Optional.empty()),
        arguments(BOOL, ARRAY_NOTHING, Optional.empty()),
        arguments(BOOL, ARRAY_A, Optional.empty()),

        arguments(STRING, STRING, Optional.of(STRING)),
        arguments(STRING, BLOB, Optional.empty()),
        arguments(STRING, NOTHING, Optional.of(STRING)),
        arguments(STRING, A, Optional.empty()),
        arguments(STRING, ARRAY_STRING, Optional.empty()),
        arguments(STRING, ARRAY_BOOL, Optional.empty()),
        arguments(STRING, ARRAY_BLOB, Optional.empty()),
        arguments(STRING, ARRAY_NOTHING, Optional.empty()),
        arguments(STRING, ARRAY_A, Optional.empty()),

        arguments(BLOB, BLOB, Optional.of(BLOB)),
        arguments(BLOB, NOTHING, Optional.of(BLOB)),
        arguments(BLOB, A, Optional.empty()),
        arguments(BLOB, ARRAY_STRING, Optional.empty()),
        arguments(BLOB, ARRAY_BLOB, Optional.empty()),
        arguments(BLOB, ARRAY_NOTHING, Optional.empty()),
        arguments(BLOB, ARRAY_A, Optional.empty()),

        arguments(NOTHING, NOTHING, Optional.of(NOTHING)),
        arguments(NOTHING, A, Optional.of(A)),
        arguments(NOTHING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(NOTHING, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(NOTHING, ARRAY_NOTHING, Optional.of(ARRAY_NOTHING)),
        arguments(NOTHING, ARRAY_A, Optional.of(ARRAY_A)),

        arguments(A, A, Optional.of(A)),
        arguments(A, B, Optional.empty()),
        arguments(A, ARRAY_STRING, Optional.empty()),
        arguments(A, ARRAY_BLOB, Optional.empty()),
        arguments(A, ARRAY_NOTHING, Optional.empty()),
        arguments(A, ARRAY_A, Optional.empty()),
        arguments(A, ARRAY_B, Optional.empty()),

        arguments(ARRAY_STRING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, ARRAY_BLOB, Optional.empty()),
        arguments(ARRAY_STRING, ARRAY_NOTHING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, NOTHING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, ARRAY_A, Optional.empty()),

        arguments(ARRAY_BLOB, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, NOTHING, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, ARRAY_A, Optional.empty()),

        arguments(ARRAY_NOTHING, ARRAY_NOTHING, Optional.of(ARRAY_NOTHING)),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, Optional.of(ARRAY2_NOTHING)),
        arguments(ARRAY_NOTHING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_NOTHING, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_NOTHING, ARRAY_A, Optional.of(ARRAY_A)),

        arguments(ARRAY_A, ARRAY_A, Optional.of(ARRAY_A)),
        arguments(ARRAY_A, ARRAY_B, Optional.empty()));
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
