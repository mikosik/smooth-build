package org.smoothbuild.lang.base.type.impl;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.base.type.TestingTS.INFERABLE_BASE_TYPES;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.util.collect.NList;

import com.google.common.testing.EqualsTester;

public class TypeSTest {
  private static final TypeFactoryS f = new TypeFactoryS();

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
        arguments(f.any(), "Any"),
        arguments(f.blob(), "Blob"),
        arguments(f.bool(), "Bool"),
        arguments(f.int_(), "Int"),
        arguments(f.nothing(), "Nothing"),
        arguments(f.string(), "String"),
        arguments(f.struct("MyStruct", nList()), "MyStruct"),
        arguments(f.oVar("A"), "A"),
        arguments(f.cVar("A"), "A"),

        arguments(f.array(f.any()), "[Any]"),
        arguments(f.array(f.blob()), "[Blob]"),
        arguments(f.array(f.bool()), "[Bool]"),
        arguments(f.array(f.int_()), "[Int]"),
        arguments(f.array(f.nothing()), "[Nothing]"),
        arguments(f.array(f.string()), "[String]"),
        arguments(f.array(f.struct("MyStruct", nList())), "[MyStruct]"),
        arguments(f.array(f.oVar("A")), "[A]"),
        arguments(f.array(f.cVar("A")), "[A]"),

        arguments(f.array(f.array(f.oVar("A"))), "[[A]]"),
        arguments(f.array(f.array(f.cVar("A"))), "[[A]]"),
        arguments(f.array(f.array(f.any())), "[[Any]]"),
        arguments(f.array(f.array(f.blob())), "[[Blob]]"),
        arguments(f.array(f.array(f.bool())), "[[Bool]]"),
        arguments(f.array(f.array(f.int_())), "[[Int]]"),
        arguments(f.array(f.array(f.nothing())), "[[Nothing]]"),
        arguments(f.array(f.array(f.struct("MyStruct", nList()))), "[[MyStruct]]"),
        arguments(f.array(f.array(f.string())), "[[String]]"),

        arguments(f.func(f.oVar("A"), list(f.array(f.oVar("A")))), "A([A])"),
        arguments(f.func(f.cVar("A"), list(f.array(f.cVar("A")))), "A([A])"),
        arguments(f.func(f.string(), list(f.array(f.oVar("A")))), "String([A])"),
        arguments(f.func(f.string(), list(f.array(f.cVar("A")))), "String([A])"),
        arguments(f.func(f.oVar("A"), list(f.oVar("A"))), "A(A)"),
        arguments(f.func(f.cVar("A"), list(f.cVar("A"))), "A(A)"),
        arguments(f.func(f.string(), list()), "String()"),
        arguments(f.func(f.string(), list(f.string())), "String(String)")
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
        arguments(f.oVar("A"), true),
        arguments(f.array(f.oVar("A")), true),
        arguments(f.array(f.array(f.oVar("A"))), true),

        arguments(f.func(f.oVar("A"), list()), true),
        arguments(f.func(f.func(f.oVar("A"), list()), list()), true),
        arguments(f.func(f.func(f.func(f.oVar("A"), list()), list()), list()), true),

        arguments(f.func(f.bool(), list(f.oVar("A"))), true),
        arguments(f.func(f.bool(), list(f.func(f.oVar("A"), list()))), true),
        arguments(f.func(f.bool(), list(f.func(f.func(f.oVar("A"), list()), list()))), true),

        arguments(f.func(f.bool(), list(f.func(f.blob(), list(f.oVar("A"))))), true),

        arguments(f.cVar("A"), true),
        arguments(f.array(f.cVar("A")), true),
        arguments(f.array(f.array(f.cVar("A"))), true),

        arguments(f.func(f.cVar("A"), list()), true),
        arguments(f.func(f.func(f.cVar("A"), list()), list()), true),
        arguments(f.func(f.func(f.func(f.cVar("A"), list()), list()), list()), true),

        arguments(f.func(f.bool(), list(f.cVar("A"))), true),
        arguments(f.func(f.bool(), list(f.func(f.cVar("A"), list()))), true),
        arguments(f.func(f.bool(), list(f.func(f.func(f.cVar("A"), list()), list()))), true),

        arguments(f.func(f.bool(), list(f.func(f.blob(), list(f.cVar("A"))))), true),

        arguments(f.func(f.bool(), list(f.int_())), false),

        arguments(f.any(), false),
        arguments(f.blob(), false),
        arguments(f.bool(), false),
        arguments(f.int_(), false),
        arguments(f.nothing(), false),
        arguments(f.string(), false),
        arguments(f.struct("MyStruct", nList()), false)
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
        arguments(f.func(f.int_(), list()), f.int_()),
        arguments(f.func(f.blob(), list(f.bool())), f.blob()),
        arguments(f.func(f.blob(), list(f.bool(), f.int_())), f.blob())
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
        arguments(f.func(f.int_(), list()), list()),
        arguments(f.func(f.blob(), list(f.bool())), list(f.bool())),
        arguments(f.func(f.blob(), list(f.bool(), f.int_())), list(f.bool(), f.int_()))
    );
  }

  @Nested
  class _open_var {
    @Test
    public void illegal_name() {
      assertCall(() -> f.oVar("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _closed_var {
    @Test
    public void illegal_name() {
      assertCall(() -> f.cVar("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(TypeS type) {
      ArrayT array = f.array(type);
      assertThat(array.elem())
          .isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          arguments(f.any()),
          arguments(f.blob()),
          arguments(f.bool()),
          arguments(f.func(f.string(), list())),
          arguments(f.int_()),
          arguments(f.nothing()),
          arguments(f.string()),
          arguments(f.struct("MyStruct", nList())),
          arguments(f.oVar("A")),
          arguments(f.cVar("A")),

          arguments(f.array(f.any())),
          arguments(f.array(f.blob())),
          arguments(f.array(f.bool())),
          arguments(f.array(f.func(f.string(), list()))),
          arguments(f.array(f.int_())),
          arguments(f.array(f.nothing())),
          arguments(f.array(f.string())),
          arguments(f.array(f.oVar("A"))),
          arguments(f.array(f.cVar("A")))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      f.struct("MyStruct", nList());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      f.struct("MyStruct", nList(itemSigS("fieldName", f.nothing())));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      f.struct("MyStruct", nList(itemSigS("fieldName", f.array(f.nothing()))));
    }

    @Test
    public void struct_name() {
      var struct = f.struct("MyStruct", nList());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> f.struct(name, nList()))
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
          arguments(f.struct("Person", nList()), nList()),
          arguments(f.struct("Person", nList(itemSigS("field", f.string()))),
              nList(itemSigS("field", f.string()))),
          arguments(f.struct("Person",
              nList(itemSigS("field", f.string()), itemSigS("field2", f.int_()))),
              nList(itemSigS("field", f.string()), itemSigS("field2", f.int_())))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<TypeS> types = asList(
        f.any(),
        f.blob(),
        f.bool(),
        f.int_(),
        f.nothing(),
        f.string(),
        f.struct("MyStruct", nList()),
        f.struct("MyStruct", nList(itemSigS("field", f.int_()))),
        f.oVar("A"),
        f.oVar("B"),
        f.oVar("C"),
        f.cVar("A"),
        f.cVar("B"),
        f.cVar("C"),

        f.func(f.blob(), list()),
        f.func(f.string(), list()),
        f.func(f.blob(), list(f.string())),
        f.func(f.blob(), list(f.blob()))
    );

    for (TypeS type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(f.array(type), f.array(type));
      equalsTester.addEqualityGroup(f.array(f.array(type)), f.array(f.array(type)));
    }
    equalsTester.testEquals();
  }
}
