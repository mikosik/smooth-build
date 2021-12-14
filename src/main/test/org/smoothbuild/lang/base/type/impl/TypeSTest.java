package org.smoothbuild.lang.base.type.impl;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.base.type.TestingTS.INFERABLE_BASE_TYPES;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.testing.EqualsTester;

public class TypeSTest extends TestingContext {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(INFERABLE_BASE_TYPES)
        .hasSize(6);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Function<TypeFactoryS, TypeS> factoryCall, String name) {
    assertThat(invoke(factoryCall).name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function<TypeFactoryS, TypeS> factoryCall, String name) {
    assertThat(invoke(factoryCall).q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function<TypeFactoryS, TypeS> factoryCall, String name) {
    assertThat(invoke(factoryCall).toString())
        .isEqualTo("Type(`" + name + "`)");
  }

  public static List<Arguments> names() {
    return asList(
        args(f -> f.any(), "Any"),
        args(f -> f.blob(), "Blob"),
        args(f -> f.bool(), "Bool"),
        args(f -> f.int_(), "Int"),
        args(f -> f.nothing(), "Nothing"),
        args(f -> f.string(), "String"),
        args(f -> f.struct("MyStruct", nList()), "MyStruct"),
        args(f -> f.var("A"), "A"),

        args(f -> f.array(f.any()), "[Any]"),
        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.nothing()), "[Nothing]"),
        args(f -> f.array(f.string()), "[String]"),
        args(f -> f.array(f.struct("MyStruct", nList())), "[MyStruct]"),
        args(f -> f.array(f.var("A")), "[A]"),

        args(f -> f.array(f.array(f.var("A"))), "[[A]]"),
        args(f -> f.array(f.array(f.any())), "[[Any]]"),
        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.nothing())), "[[Nothing]]"),
        args(f -> f.array(f.array(f.struct("MyStruct", nList()))), "[[MyStruct]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),

        args(f -> f.func(f.var("A"), list(f.array(f.var("A")))), "A([A])"),
        args(f -> f.func(f.string(), list(f.array(f.var("A")))), "String([A])"),
        args(f -> f.func(f.var("A"), list(f.var("A"))), "A(A)"),
        args(f -> f.func(f.string(), list()), "String()"),
        args(f -> f.func(f.string(), list(f.string())), "String(String)")
    );
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Function<TypeFactoryS, TypeS> factoryCall, boolean expected) {
    assertThat(invoke(factoryCall).isPolytype())
        .isEqualTo(expected);
  }

  public static List<Arguments> isPolytype_test_data() {
    return asList(
        args(f -> f.var("A"), true),
        args(f -> f.array(f.var("A")), true),
        args(f -> f.array(f.array(f.var("A"))), true),

        args(f -> f.func(f.var("A"), list()), true),
        args(f -> f.func(f.func(f.var("A"), list()), list()), true),
        args(f -> f.func(f.func(f.func(f.var("A"), list()), list()), list()),
            true),

        args(f -> f.func(f.bool(), list(f.var("A"))), true),
        args(f -> f.func(f.bool(), list(f.func(f.var("A"), list()))), true),
        args(f -> f
                .func(f.bool(), list(f.func(f.func(f.var("A"), list()), list()))),
            true),

        args(f -> f.func(f.bool(), list(f.func(f.blob(), list(f.var("A"))))),
            true),

        args(f -> f.func(f.bool(), list(f.int_())), false),

        args(f -> f.any(), false),
        args(f -> f.blob(), false),
        args(f -> f.bool(), false),
        args(f -> f.int_(), false),
        args(f -> f.nothing(), false),
        args(f -> f.string(), false),
        args(f -> f.struct("MyStruct", nList()), false)
    );
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(
      Function<TypeFactoryS, TypeS> factoryCall,
      Function<TypeFactoryS, Set<Var>> resultCall) {
    assertThat(invoke(factoryCall).vars())
        .containsExactlyElementsIn(invoke(resultCall))
        .inOrder();
  }

  public static List<Arguments> vars_test_data() {
    return asList(
        args(f -> f.any(), f -> set()),
        args(f -> f.blob(), f -> set()),
        args(f -> f.bool(), f -> set()),
        args(f -> f.int_(), f -> set()),
        args(f -> f.nothing(), f -> set()),
        args(f -> f.string(), f -> set()),
        args(f -> f.struct("MyStruct", nList()), f -> set()),

        args(f -> f.array(f.any()), f -> set()),
        args(f -> f.array(f.blob()), f -> set()),
        args(f -> f.array(f.bool()), f -> set()),
        args(f -> f.array(f.int_()), f -> set()),
        args(f -> f.array(f.nothing()), f -> set()),
        args(f -> f.array(f.string()), f -> set()),
        args(f -> f.array(f.var("A")), f -> set(f.var("A"))),

        args(f -> f.func(f.string(), list()), f -> set()),
        args(f -> f.func(f.string(), list(f.bool())), f -> set()),

        args(f -> f.var("A"), f -> set(f.var("A"))),
        args(f -> f.array(f.var("A")), f -> set(f.var("A"))),
        args(f -> f.array(f.array(f.var("A"))), f -> set(f.var("A"))),

        args(f -> f.func(f.var("A"), list()), f -> set(f.var("A"))),
        args(f -> f.func(f.var("A"), list(f.string())), f -> set(f.var("A"))),
        args(f -> f.func(f.string(), list(f.var("A"))), f -> set(f.var("A"))),
        args(f -> f.func(f.var("B"), list(f.var("A"))),
            f -> set(f.var("A"), f.var("B"))),

        args(f -> f.func(f.func(f.var("A"), list()), list()),
            f -> set(f.var("A"))),
        args(f -> f.func(f.var("D"), list(f.var("C"), f.var("B"))),
            f -> set(f.var("B"), f.var("C"), f.var("D")))
    );
  }

  @ParameterizedTest
  @MethodSource("func_result_cases")
  public void func_result(Function<TypeFactoryS, FuncT> factoryCall,
      Function<TypeFactoryS, List<Type>> expected) {
    assertThat(invoke(factoryCall).res())
        .isEqualTo(invoke(expected));
  }

  public static List<Arguments> func_result_cases() {
    return asList(
        args(f -> f.func(f.int_(), list()), f -> f.int_()),
        args(f -> f.func(f.blob(), list(f.bool())), f -> f.blob()),
        args(f -> f.func(f.blob(), list(f.bool(), f.int_())), f -> f.blob())
    );
  }

  @ParameterizedTest
  @MethodSource("func_params_cases")
  public void func_params(Function<TypeFactoryS, FuncT> factoryCall,
      Function<TypeFactoryS, List<Type>> expected) {
    assertThat(invoke(factoryCall).params())
        .isEqualTo(invoke(expected));
  }

  public static List<Arguments> func_params_cases() {
    return asList(
        args(f -> f.func(f.int_(), list()), f -> list()),
        args(f -> f.func(f.blob(), list(f.bool())), f -> list(f.bool())),
        args(f -> f.func(f.blob(), list(f.bool(), f.int_())), f -> list(f.bool(), f.int_()))
    );
  }

  @Nested
  class _var {
    @Test
    public void illegal_name() {
      assertCall(() -> varS("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(Function<TypeFactoryS, TypeS> factoryCall) {
      TypeS elem = invoke(factoryCall);
      ArrayT array = typeFactoryS().array(elem);
      assertThat(array.elem())
          .isEqualTo(elem);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          args(f -> f.any()),
          args(f -> f.blob()),
          args(f -> f.bool()),
          args(f -> f.func(f.string(), list())),
          args(f -> f.int_()),
          args(f -> f.nothing()),
          args(f -> f.string()),
          args(f -> f.struct("MyStruct", nList())),
          args(f -> f.var("A")),

          args(f -> f.array(f.any())),
          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.func(f.string(), list()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.nothing())),
          args(f -> f.array(f.string())),
          args(f -> f.array(f.var("A")))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      structTS("MyStruct", nList());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      structTS("MyStruct", nList(sigS(nothingTS(), "fieldName")));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      structTS("MyStruct", nList(sigS(arrayTS(nothingTS()), "fieldName")));
    }

    @Test
    public void struct_name() {
      var struct = typeFactoryS().struct("MyStruct", nList());
      assertThat(struct.name())
          .isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> typeFactoryS().struct(name, nList()))
          .throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(
        Function<TypeFactoryS, StructTS> factoryCall,
        Function<TypeFactoryS, NList<ItemSigS>> expected) {
      assertThat(invoke(factoryCall).fields())
          .isEqualTo(invoke(expected));
    }

    public static List<Arguments> struct_fields_cases() {
      return asList(
          args(f -> f.struct("Person", nList()), f -> nList()),
          args(f -> f.struct("Person", nList(itemSigS("field", f.string()))),
              f -> nList(itemSigS("field", f.string()))),
          args(f -> f.struct("Person",
              nList(itemSigS("field", f.string()), itemSigS("field2", f.int_()))),
              f -> nList(itemSigS("field", f.string()), itemSigS("field2", f.int_())))
      );
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    TypeFactoryS f = typeFactoryS();
    List<TypeS> types = asList(
        f.any(),
        f.blob(),
        f.bool(),
        f.int_(),
        f.nothing(),
        f.string(),
        f.struct("MyStruct", nList()),
        f.struct("MyStruct", nList(sigS(f.int_(), "field"))),
        f.var("A"),
        f.var("B"),
        f.var("C"),

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

  private <R> R invoke(Function<TypeFactoryS, R> f) {
    return f.apply(typeFactoryS());
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function<TypeFactoryS, R> factoryCall1,
      Function<TypeFactoryS, R> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<TypeFactoryS, R> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<TypeFactoryS, R> factoryCall) {
    return arguments(factoryCall);
  }
}
