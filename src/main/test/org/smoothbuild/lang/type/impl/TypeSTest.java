package org.smoothbuild.lang.type.impl;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.INFERABLE_BASE_TYPES;
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
  private static final TypeFS F = new TypeFS();

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
        arguments(F.any(), "Any"),
        arguments(F.blob(), "Blob"),
        arguments(F.bool(), "Bool"),
        arguments(F.int_(), "Int"),
        arguments(F.nothing(), "Nothing"),
        arguments(F.string(), "String"),
        arguments(F.struct("MyStruct", nList()), "MyStruct"),
        arguments(F.oVar("A"), "A"),
        arguments(F.cVar("A"), "A"),

        arguments(F.array(F.any()), "[Any]"),
        arguments(F.array(F.blob()), "[Blob]"),
        arguments(F.array(F.bool()), "[Bool]"),
        arguments(F.array(F.int_()), "[Int]"),
        arguments(F.array(F.nothing()), "[Nothing]"),
        arguments(F.array(F.string()), "[String]"),
        arguments(F.array(F.struct("MyStruct", nList())), "[MyStruct]"),
        arguments(F.array(F.oVar("A")), "[A]"),
        arguments(F.array(F.cVar("A")), "[A]"),

        arguments(F.array(F.array(F.oVar("A"))), "[[A]]"),
        arguments(F.array(F.array(F.cVar("A"))), "[[A]]"),
        arguments(F.array(F.array(F.any())), "[[Any]]"),
        arguments(F.array(F.array(F.blob())), "[[Blob]]"),
        arguments(F.array(F.array(F.bool())), "[[Bool]]"),
        arguments(F.array(F.array(F.int_())), "[[Int]]"),
        arguments(F.array(F.array(F.nothing())), "[[Nothing]]"),
        arguments(F.array(F.array(F.struct("MyStruct", nList()))), "[[MyStruct]]"),
        arguments(F.array(F.array(F.string())), "[[String]]"),

        arguments(F.func(F.oVar("A"), list(F.array(F.oVar("A")))), "A([A])"),
        arguments(F.func(F.cVar("A"), list(F.array(F.cVar("A")))), "A([A])"),
        arguments(F.func(F.string(), list(F.array(F.oVar("A")))), "String([A])"),
        arguments(F.func(F.string(), list(F.array(F.cVar("A")))), "String([A])"),
        arguments(F.func(F.oVar("A"), list(F.oVar("A"))), "A(A)"),
        arguments(F.func(F.cVar("A"), list(F.cVar("A"))), "A(A)"),
        arguments(F.func(F.string(), list()), "String()"),
        arguments(F.func(F.string(), list(F.string())), "String(String)")
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
        arguments(F.oVar("A"), true),
        arguments(F.array(F.oVar("A")), true),
        arguments(F.array(F.array(F.oVar("A"))), true),

        arguments(F.func(F.oVar("A"), list()), true),
        arguments(F.func(F.func(F.oVar("A"), list()), list()), true),
        arguments(F.func(F.func(F.func(F.oVar("A"), list()), list()), list()), true),

        arguments(F.func(F.bool(), list(F.oVar("A"))), true),
        arguments(F.func(F.bool(), list(F.func(F.oVar("A"), list()))), true),
        arguments(F.func(F.bool(), list(F.func(F.func(F.oVar("A"), list()), list()))), true),

        arguments(F.func(F.bool(), list(F.func(F.blob(), list(F.oVar("A"))))), true),

        arguments(F.cVar("A"), true),
        arguments(F.array(F.cVar("A")), true),
        arguments(F.array(F.array(F.cVar("A"))), true),

        arguments(F.func(F.cVar("A"), list()), true),
        arguments(F.func(F.func(F.cVar("A"), list()), list()), true),
        arguments(F.func(F.func(F.func(F.cVar("A"), list()), list()), list()), true),

        arguments(F.func(F.bool(), list(F.cVar("A"))), true),
        arguments(F.func(F.bool(), list(F.func(F.cVar("A"), list()))), true),
        arguments(F.func(F.bool(), list(F.func(F.func(F.cVar("A"), list()), list()))), true),

        arguments(F.func(F.bool(), list(F.func(F.blob(), list(F.cVar("A"))))), true),

        arguments(F.func(F.bool(), list(F.int_())), false),

        arguments(F.any(), false),
        arguments(F.blob(), false),
        arguments(F.bool(), false),
        arguments(F.int_(), false),
        arguments(F.nothing(), false),
        arguments(F.string(), false),
        arguments(F.struct("MyStruct", nList()), false)
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
        arguments(F.any(), false),
        arguments(F.blob(), false),
        arguments(F.bool(), false),
        arguments(F.int_(), false),
        arguments(F.nothing(), false),
        arguments(F.string(), false),

        arguments(F.array(F.int_()), false),
        arguments(F.array(F.oVar("A")), true),
        arguments(F.array(F.cVar("A")), false),

        arguments(F.func(F.blob(), list(F.bool())), false),
        arguments(F.func(F.oVar("A"), list(F.bool())), true),
        arguments(F.func(F.cVar("A"), list(F.bool())), false),
        arguments(F.func(F.blob(), list(F.oVar("A"))), true),
        arguments(F.func(F.blob(), list(F.cVar("A"))), false)
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
        arguments(F.any(), false),
        arguments(F.blob(), false),
        arguments(F.bool(), false),
        arguments(F.int_(), false),
        arguments(F.nothing(), false),
        arguments(F.string(), false),

        arguments(F.array(F.int_()), false),
        arguments(F.array(F.oVar("A")), false),
        arguments(F.array(F.cVar("A")), true),

        arguments(F.func(F.blob(), list(F.bool())), false),
        arguments(F.func(F.oVar("A"), list(F.bool())), false),
        arguments(F.func(F.cVar("A"), list(F.bool())), true),
        arguments(F.func(F.blob(), list(F.oVar("A"))), false),
        arguments(F.func(F.blob(), list(F.cVar("A"))), true)
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
        arguments(F.any(), ImmutableSet.of()),
        arguments(F.blob(), ImmutableSet.of()),
        arguments(F.bool(), ImmutableSet.of()),
        arguments(F.int_(), ImmutableSet.of()),
        arguments(F.nothing(), ImmutableSet.of()),
        arguments(F.string(), ImmutableSet.of()),

        arguments(F.array(F.int_()), ImmutableSet.of()),
        arguments(F.array(F.oVar("A")), ImmutableSet.of(F.oVar("A"))),
        arguments(F.array(F.cVar("A")), ImmutableSet.of()),

        arguments(F.func(F.blob(), list(F.bool())), ImmutableSet.of()),
        arguments(F.func(F.oVar("A"), list(F.bool())), ImmutableSet.of(F.oVar("A"))),
        arguments(F.func(F.cVar("A"), list(F.bool())), ImmutableSet.of()),
        arguments(F.func(F.blob(), list(F.oVar("A"))), ImmutableSet.of(F.oVar("A"))),
        arguments(F.func(F.blob(), list(F.cVar("A"))), ImmutableSet.of())
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
        arguments(F.func(F.int_(), list()), F.int_()),
        arguments(F.func(F.blob(), list(F.bool())), F.blob()),
        arguments(F.func(F.blob(), list(F.bool(), F.int_())), F.blob())
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
        arguments(F.func(F.int_(), list()), list()),
        arguments(F.func(F.blob(), list(F.bool())), list(F.bool())),
        arguments(F.func(F.blob(), list(F.bool(), F.int_())), list(F.bool(), F.int_()))
    );
  }

  @Nested
  class _open_var {
    @Test
    public void illegal_name() {
      assertCall(() -> F.oVar("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _closed_var {
    @Test
    public void illegal_name() {
      assertCall(() -> F.cVar("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(TypeS type) {
      ArrayT array = F.array(type);
      assertThat(array.elem())
          .isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          arguments(F.any()),
          arguments(F.blob()),
          arguments(F.bool()),
          arguments(F.func(F.string(), list())),
          arguments(F.int_()),
          arguments(F.nothing()),
          arguments(F.string()),
          arguments(F.struct("MyStruct", nList())),
          arguments(F.oVar("A")),
          arguments(F.cVar("A")),

          arguments(F.array(F.any())),
          arguments(F.array(F.blob())),
          arguments(F.array(F.bool())),
          arguments(F.array(F.func(F.string(), list()))),
          arguments(F.array(F.int_())),
          arguments(F.array(F.nothing())),
          arguments(F.array(F.string())),
          arguments(F.array(F.oVar("A"))),
          arguments(F.array(F.cVar("A")))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      F.struct("MyStruct", nList());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      F.struct("MyStruct", nList(itemSigS(F.nothing(), "fieldName")));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      F.struct("MyStruct", nList(itemSigS(F.array(F.nothing()), "fieldName")));
    }

    @Test
    public void struct_name() {
      var struct = F.struct("MyStruct", nList());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> F.struct(name, nList()))
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
          arguments(F.struct("Person", nList()), nList()),
          arguments(F.struct("Person", nList(itemSigS(F.string(), "field"))),
              nList(itemSigS(F.string(), "field"))),
          arguments(F.struct("Person",
              nList(itemSigS(F.string(), "field"), itemSigS(F.int_(), "field2"))),
              nList(itemSigS(F.string(), "field"), itemSigS(F.int_(), "field2")))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = asList(
        F.any(),
        F.blob(),
        F.bool(),
        F.int_(),
        F.nothing(),
        F.string(),
        F.struct("MyStruct", nList()),
        F.struct("MyStruct", nList(itemSigS(F.int_(), "field"))),
        F.oVar("A"),
        F.oVar("B"),
        F.oVar("C"),
        F.cVar("A"),
        F.cVar("B"),
        F.cVar("C"),

        F.func(F.blob(), list()),
        F.func(F.string(), list()),
        F.func(F.blob(), list(F.string())),
        F.func(F.blob(), list(F.blob()))
    );

    for (TypeS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(F.array(type), F.array(type));
      equalsTester.addEqualityGroup(F.array(F.array(type)), F.array(F.array(type)));
    }
    equalsTester.testEquals();
  }
}
