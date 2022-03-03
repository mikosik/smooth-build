package org.smoothbuild.lang.type.impl;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.ANY;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.CLOSED_A;
import static org.smoothbuild.testing.type.TestingTS.INFERABLE_BASE_TYPES;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.NOTHING;
import static org.smoothbuild.testing.type.TestingTS.OPEN_A;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.a;
import static org.smoothbuild.testing.type.TestingTS.cVar;
import static org.smoothbuild.testing.type.TestingTS.f;
import static org.smoothbuild.testing.type.TestingTS.oVar;
import static org.smoothbuild.testing.type.TestingTS.struct;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.api.ArrayT;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableSet;
import com.google.common.testing.EqualsTester;

public class TypeSTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(INFERABLE_BASE_TYPES)
        .hasSize(6);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(TypeS type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(TypeS type, String name) {
    assertThat(type.q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(TypeS type, String name) {
    assertThat(type.toString())
        .isEqualTo("Type(`" + name + "`)");
  }

  public static List<Arguments> names() {
    return asList(
        arguments(ANY, "Any"),
        arguments(BLOB, "Blob"),
        arguments(BOOL, "Bool"),
        arguments(INT, "Int"),
        arguments(NOTHING, "Nothing"),
        arguments(STRING, "String"),
        arguments(struct("MyStruct", nList()), "MyStruct"),
        arguments(OPEN_A, "A"),
        arguments(CLOSED_A, "A"),

        arguments(a(ANY), "[Any]"),
        arguments(a(BLOB), "[Blob]"),
        arguments(a(BOOL), "[Bool]"),
        arguments(a(INT), "[Int]"),
        arguments(a(NOTHING), "[Nothing]"),
        arguments(a(STRING), "[String]"),
        arguments(a(struct("MyStruct", nList())), "[MyStruct]"),
        arguments(a(OPEN_A), "[A]"),
        arguments(a(CLOSED_A), "[A]"),

        arguments(a(a(OPEN_A)), "[[A]]"),
        arguments(a(a(CLOSED_A)), "[[A]]"),
        arguments(a(a(ANY)), "[[Any]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(INT)), "[[Int]]"),
        arguments(a(a(NOTHING)), "[[Nothing]]"),
        arguments(a(a(struct("MyStruct", nList()))), "[[MyStruct]]"),
        arguments(a(a(STRING)), "[[String]]"),

        arguments(f(OPEN_A, list(a(OPEN_A))), "A([A])"),
        arguments(f(CLOSED_A, list(a(CLOSED_A))), "A([A])"),
        arguments(f(STRING, list(a(OPEN_A))), "String([A])"),
        arguments(f(STRING, list(a(CLOSED_A))), "String([A])"),
        arguments(f(OPEN_A, list(OPEN_A)), "A(A)"),
        arguments(f(CLOSED_A, list(CLOSED_A)), "A(A)"),
        arguments(f(STRING, list()), "String()"),
        arguments(f(STRING, list(STRING)), "String(String)")
    );
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(TypeS type, boolean expected) {
    assertThat(type.isPolytype())
        .isEqualTo(expected);
  }

  public static List<Arguments> isPolytype_test_data() {
    return asList(
        arguments(OPEN_A, true),
        arguments(a(OPEN_A), true),
        arguments(a(a(OPEN_A)), true),

        arguments(f(OPEN_A, list()), true),
        arguments(f(f(OPEN_A, list()), list()), true),
        arguments(f(f(f(OPEN_A, list()), list()), list()), true),

        arguments(f(BOOL, list(OPEN_A)), true),
        arguments(f(BOOL, list(f(OPEN_A, list()))), true),
        arguments(f(BOOL, list(f(f(OPEN_A, list()), list()))), true),

        arguments(f(BOOL, list(f(BLOB, list(OPEN_A)))), true),

        arguments(CLOSED_A, true),
        arguments(a(CLOSED_A), true),
        arguments(a(a(CLOSED_A)), true),

        arguments(f(CLOSED_A, list()), true),
        arguments(f(f(CLOSED_A, list()), list()), true),
        arguments(f(f(f(CLOSED_A, list()), list()), list()), true),

        arguments(f(BOOL, list(CLOSED_A)), true),
        arguments(f(BOOL, list(f(CLOSED_A, list()))), true),
        arguments(f(BOOL, list(f(f(CLOSED_A, list()), list()))), true),

        arguments(f(BOOL, list(f(BLOB, list(CLOSED_A)))), true),

        arguments(f(BOOL, list(INT)), false),

        arguments(ANY, false),
        arguments(BLOB, false),
        arguments(BOOL, false),
        arguments(INT, false),
        arguments(NOTHING, false),
        arguments(STRING, false),
        arguments(struct("MyStruct", nList()), false)
    );
  }

  @ParameterizedTest
  @MethodSource("hasOpenVars_test_data")
  public void hasOpenVars(TypeS type, boolean expected) {
    assertThat(type.hasOpenVars())
        .isEqualTo(expected);
  }

  public static List<Arguments> hasOpenVars_test_data() {
    return List.of(
        arguments(ANY, false),
        arguments(BLOB, false),
        arguments(BOOL, false),
        arguments(INT, false),
        arguments(NOTHING, false),
        arguments(STRING, false),

        arguments(a(INT), false),
        arguments(a(OPEN_A), true),
        arguments(a(CLOSED_A), false),

        arguments(f(BLOB, list(BOOL)), false),
        arguments(f(OPEN_A, list(BOOL)), true),
        arguments(f(CLOSED_A, list(BOOL)), false),
        arguments(f(BLOB, list(OPEN_A)), true),
        arguments(f(BLOB, list(CLOSED_A)), false)
    );
  }

  @ParameterizedTest
  @MethodSource("hasClosedVars_test_data")
  public void hasOpenClosed(TypeS type, boolean expected) {
    assertThat(type.hasClosedVars())
        .isEqualTo(expected);
  }

  public static List<Arguments> hasClosedVars_test_data() {
    return List.of(
        arguments(ANY, false),
        arguments(BLOB, false),
        arguments(BOOL, false),
        arguments(INT, false),
        arguments(NOTHING, false),
        arguments(STRING, false),

        arguments(a(INT), false),
        arguments(a(OPEN_A), false),
        arguments(a(CLOSED_A), true),

        arguments(f(BLOB, list(BOOL)), false),
        arguments(f(OPEN_A, list(BOOL)), false),
        arguments(f(CLOSED_A, list(BOOL)), true),
        arguments(f(BLOB, list(OPEN_A)), false),
        arguments(f(BLOB, list(CLOSED_A)), true)
    );
  }

  @ParameterizedTest
  @MethodSource("openVars_test_data")
  public void openVars(TypeS type, ImmutableSet<OpenVarTS> expected) {
    assertThat(type.openVars())
        .isEqualTo(expected);
  }

  public static List<Arguments> openVars_test_data() {
    return List.of(
        arguments(ANY, ImmutableSet.of()),
        arguments(BLOB, ImmutableSet.of()),
        arguments(BOOL, ImmutableSet.of()),
        arguments(INT, ImmutableSet.of()),
        arguments(NOTHING, ImmutableSet.of()),
        arguments(STRING, ImmutableSet.of()),

        arguments(a(INT), ImmutableSet.of()),
        arguments(a(OPEN_A), ImmutableSet.of(OPEN_A)),
        arguments(a(CLOSED_A), ImmutableSet.of()),

        arguments(f(BLOB, list(BOOL)), ImmutableSet.of()),
        arguments(f(OPEN_A, list(BOOL)), ImmutableSet.of(OPEN_A)),
        arguments(f(CLOSED_A, list(BOOL)), ImmutableSet.of()),
        arguments(f(BLOB, list(OPEN_A)), ImmutableSet.of(OPEN_A)),
        arguments(f(BLOB, list(CLOSED_A)), ImmutableSet.of())
    );
  }

  @ParameterizedTest
  @MethodSource("func_result_cases")
  public void func_result(FuncTS type, TypeS expected) {
    assertThat(type.res())
        .isEqualTo(expected);
  }

  public static List<Arguments> func_result_cases() {
    return asList(
        arguments(f(INT, list()), INT),
        arguments(f(BLOB, list(BOOL)), BLOB),
        arguments(f(BLOB, list(BOOL, INT)), BLOB)
    );
  }

  @ParameterizedTest
  @MethodSource("func_params_cases")
  public void func_params(FuncTS type, Object expected) {
    assertThat(type.params())
        .isEqualTo(expected);
  }

  public static List<Arguments> func_params_cases() {
    return asList(
        arguments(f(INT, list()), list()),
        arguments(f(BLOB, list(BOOL)), list(BOOL)),
        arguments(f(BLOB, list(BOOL, INT)), list(BOOL, INT))
    );
  }

  @Nested
  class _open_var {
    @Test
    public void illegal_name() {
      assertCall(() -> oVar("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _closed_var {
    @Test
    public void illegal_name() {
      assertCall(() -> cVar("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(TypeS type) {
      ArrayT array = a(type);
      assertThat(array.elem())
          .isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          arguments(ANY),
          arguments(BLOB),
          arguments(BOOL),
          arguments(f(STRING, list())),
          arguments(INT),
          arguments(NOTHING),
          arguments(STRING),
          arguments(struct("MyStruct", nList())),
          arguments(OPEN_A),
          arguments(CLOSED_A),

          arguments(a(ANY)),
          arguments(a(BLOB)),
          arguments(a(BOOL)),
          arguments(a(f(STRING, list()))),
          arguments(a(INT)),
          arguments(a(NOTHING)),
          arguments(a(STRING)),
          arguments(a(OPEN_A)),
          arguments(a(CLOSED_A))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      struct("MyStruct", nList());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      struct("MyStruct", nList(itemSigS(NOTHING, "fieldName")));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      struct("MyStruct", nList(itemSigS(a(NOTHING), "fieldName")));
    }

    @Test
    public void struct_name() {
      var struct = struct("MyStruct", nList());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> struct(name, nList()))
          .throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(StructTS struct, NList<ItemSigS> expected) {
      assertThat(struct.fields())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_fields_cases() {
      return asList(
          arguments(struct("Person", nList()), nList()),
          arguments(struct("Person", nList(itemSigS(STRING, "field"))),
              nList(itemSigS(STRING, "field"))),
          arguments(struct("Person",
              nList(itemSigS(STRING, "field"), itemSigS(INT, "field2"))),
              nList(itemSigS(STRING, "field"), itemSigS(INT, "field2")))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = asList(
        ANY,
        BLOB,
        BOOL,
        INT,
        NOTHING,
        STRING,
        struct("MyStruct", nList()),
        struct("MyStruct", nList(itemSigS(INT, "field"))),
        OPEN_A,
        oVar("B"),
        oVar("C"),
        CLOSED_A,
        cVar("B"),
        cVar("C"),

        f(BLOB, list()),
        f(STRING, list()),
        f(BLOB, list(STRING)),
        f(BLOB, list(BLOB))
    );

    for (TypeS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.testEquals();
  }
}
