package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypes.INFERABLE_BASE_TYPES;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Sets.set;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.testing.AbstractTestingContext;

import com.google.common.testing.EqualsTester;

public abstract class AbstractTypeTest extends AbstractTestingContext {
  public static List<Arguments> names() {
    return asList(
        args(f -> f.variable("A"), "A"),
        args(f -> f.any(), "Any"),
        args(f -> f.blob(), "Blob"),
        args(f -> f.bool(), "Bool"),
        args(f -> f.int_(), "Int"),
        args(f -> f.nothing(), "Nothing"),
        args(f -> f.string(), "String"),
        args(f -> f.struct("Person", list(), list()), "Person"),
        args(f -> f.struct("Person", list(f.string()), list("name")), "Person"),

        args(f -> f.array(f.variable("A")), "[A]"),
        args(f -> f.array(f.any()), "[Any]"),
        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.nothing()), "[Nothing]"),
        args(f -> f.array(f.string()), "[String]"),
        args(f -> f.array(f.struct("Person", list(), list())), "[Person]"),
        args(f -> f.array(f.struct("Person", list(f.string()), list("n"))), "[Person]"),

        args(f -> f.array(f.array(f.variable("A"))), "[[A]]"),
        args(f -> f.array(f.array(f.any())), "[[Any]]"),
        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.nothing())), "[[Nothing]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),
        args(f -> f.array(f.array(f.struct("Person", list(), list()))), "[[Person]]"),
        args(f -> f.array(f.array(f.struct("Person", list(f.string()), list("n")))),
                "[[Person]]"),

        args(f -> f.function(f.variable("A"), list(f.array(f.variable("A")))), "A([A])"),
        args(f -> f.function(f.string(), list(f.array(f.variable("A")))), "String([A])"),
        args(f -> f.function(f.variable("A"), list(f.variable("A"))), "A(A)"),
        args(f -> f.function(f.struct("Person", list(), list()), list()), "Person()"),
        args(f -> f.function(f.string(), list()), "String()"),
        args(f -> f.function(f.string(), list(f.string())), "String(String)")
    );
  }

  public static List<Arguments> isPolytype_test_data() {
    return asList(
        args(f -> f.variable("A"), true),
        args(f -> f.array(f.variable("A")), true),
        args(f -> f.array(f.array(f.variable("A"))), true),

        args(f -> f.function(f.variable("A"), list()), true),
        args(f -> f.function(f.function(f.variable("A"), list()), list()), true),
        args(f -> f.function(f.function(f.function(f.variable("A"), list()), list()), list()),
                true),

        args(f -> f.function(f.bool(), list(f.variable("A"))), true),
        args(f -> f.function(f.bool(), list(f.function(f.variable("A"), list()))), true),
        args(f -> f
                    .function(f.bool(), list(f.function(f.function(f.variable("A"), list()), list()))),
                true),

        args(f -> f.function(f.bool(), list(f.function(f.blob(), list(f.variable("A"))))),
                true),

        args(f -> f.any(), false),
        args(f -> f.blob(), false),
        args(f -> f.bool(), false),
        args(f -> f.int_(), false),
        args(f -> f.nothing(), false),
        args(f -> f.string(), false),
        args(f -> f.struct("Person", list(f.string()), list("name")), false)
    );
  }

  public static List<Arguments> variables_test_data() {
    return asList(
        args(f -> f.any(), f -> set()),
        args(f -> f.blob(), f -> set()),
        args(f -> f.bool(), f -> set()),
        args(f -> f.int_(), f -> set()),
        args(f -> f.nothing(), f -> set()),
        args(f -> f.string(), f -> set()),
        args(f -> f.struct("Person", list(), list()), f -> set()),
        args(f -> f.struct("Person", list(f.string()), list("name")), f -> set()),

        args(f -> f.array(f.any()), f -> set()),
        args(f -> f.array(f.blob()), f -> set()),
        args(f -> f.array(f.bool()), f -> set()),
        args(f -> f.array(f.int_()), f -> set()),
        args(f -> f.array(f.nothing()), f -> set()),
        args(f -> f.array(f.string()), f -> set()),
        args(f -> f.array(f.struct("Person", list(), list())), f -> set()),
        args(f -> f.array(f.struct("Person", list(f.string()), list("name"))), f -> set()),
        args(f -> f.array(f.variable("A")), f -> set(f.variable("A"))),

        args(f -> f.function(f.string(), list()), f -> set()),
        args(f -> f.function(f.string(), list(f.bool())), f -> set()),

        args(f -> f.struct("Data", list(f.variable("A")), list("payload")),
            f -> set(f.variable("A"))),

        args(f -> f.variable("A"), f -> set(f.variable("A"))),
        args(f -> f.array(f.variable("A")), f -> set(f.variable("A"))),
        args(f -> f.array(f.array(f.variable("A"))), f -> set(f.variable("A"))),

        args(f -> f.function(f.variable("A"), list()), f -> set(f.variable("A"))),
        args(f -> f.function(f.variable("A"), list(f.string())), f -> set(f.variable("A"))),
        args(f -> f.function(f.string(), list(f.variable("A"))), f -> set(f.variable("A"))),
        args(f -> f.function(f.variable("B"), list(f.variable("A"))),
            f -> set(f.variable("A"), f.variable("B"))),

        args(f -> f.function(f.function(f.variable("A"), list()), list()),
            f -> set(f.variable("A"))),
        args(f -> f.function(f.variable("D"), list(f.variable("C"), f.variable("B"))),
                f -> set(f.variable("B"), f.variable("C"), f.variable("D")))
    );
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <T> Arguments args(Function<TypeFactory, T> factoryCall) {
    return arguments(factoryCall);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <T> Arguments args(Function<TypeFactory, T> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <T> Arguments args(Function<TypeFactory, T> factoryCall,
      Function<TypeFactory, T> arg2) {
    return arguments(factoryCall, arg2);
  }

  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(INFERABLE_BASE_TYPES)
        .hasSize(6);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Function<TypeFactory, Type> factoryCall, String name) {
    assertThat(invoke(factoryCall).name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function<TypeFactory, Type> factoryCall, String name) {
    assertThat(invoke(factoryCall).q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function<TypeFactory, Type> factoryCall, String name) {
    assertThat(invoke(factoryCall).toString())
        .isEqualTo("Type(`" + name + "`)");
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Function<TypeFactory, Type> factoryCall, boolean expected) {
    assertThat(invoke(factoryCall).isPolytype())
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("variables_test_data")
  public void variables(
      Function<TypeFactory, Type> factoryCall,
      Function<TypeFactory, Set<Variable>> resultCall) {
    assertThat(invoke(factoryCall).variables())
        .containsExactlyElementsIn(invoke(resultCall))
        .inOrder();
  }

  @ParameterizedTest
  @MethodSource("function_result_cases")
  public void function_result(Function<TypeFactory, FunctionType> factoryCall,
      Function<TypeFactory, List<Type>> expected) {
    assertThat(invoke(factoryCall).result())
        .isEqualTo(invoke(expected));
  }

  public static List<Arguments> function_result_cases() {
    return asList(
        args(f -> f.function(f.int_(), list()), f -> f.int_()),
        args(f -> f.function(f.blob(), list(f.bool())), f -> f.blob()),
        args(f -> f.function(f.blob(), list(f.bool(), f.int_())), f -> f.blob())
    );
  }

  @ParameterizedTest
  @MethodSource("function_parameters_cases")
  public void function_parameters(Function<TypeFactory, FunctionType> factoryCall,
      Function<TypeFactory, List<Type>> expected) {
    assertThat(invoke(factoryCall).parameters())
        .isEqualTo(invoke(expected));
  }

  public static List<Arguments> function_parameters_cases() {
    return asList(
        args(f -> f.function(f.int_(), list()), f -> list()),
        args(f -> f.function(f.blob(), list(f.bool())), f -> list(f.bool())),
        args(f -> f.function(f.blob(), list(f.bool(), f.int_())), f -> list(f.bool(), f.int_()))
    );
  }

  @Nested
  class _variable {
    @Test
    public void name() {
      assertThat(variable("A").name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> variable("a"))
          .throwsException(new IllegalArgumentException("Illegal type variable name 'a'."));
    }
  }


  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    TypeFactory f = typeFactory();
    List<Type> types = asList(
        f.any(),
        f.blob(),
        f.bool(),
        f.int_(),
        f.nothing(),
        f.variable("A"),
        f.variable("B"),
        f.variable("C"),

        f.function(f.blob(), list()),
        f.function(f.string(), list()),
        f.function(f.blob(), list(f.string())),
        f.function(f.blob(), list(f.blob())),

        f.struct("Person", list(), list()),
        f.struct("Person2", list(), list()),
        f.struct("Person", list(f.blob()), list("name")),
        f.struct("Person", list(f.string()), list("name")),
        f.struct("Person", list(f.blob()), list("name2"))
    );

    for (Type type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(f.array(type), f.array(type));
      equalsTester.addEqualityGroup(f.array(f.array(type)), f.array(f.array(type)));
    }
    equalsTester.testEquals();
  }

  private <T> T invoke(Function<TypeFactory, T> f) {
    return f.apply(typeFactory());
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(Function<TypeFactory, Type> factoryCall) {
      Type element = invoke(factoryCall);
      ArrayType array = typeFactory().array(element);
      assertThat(array.element())
          .isEqualTo(element);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          args(f -> f.any()),
          args(f -> f.blob()),
          args(f -> f.bool()),
          args(f -> f.function(f.string(), list())),
          args(f -> f.int_()),
          args(f -> f.nothing()),
          args(f -> f.string()),
          args(f -> f.struct("Person", list(f.string()), list("name"))),
          args(f -> f.variable("A")),

          args(f -> f.array(f.any())),
          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.function(f.string(), list()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.nothing())),
          args(f -> f.array(f.string())),
          args(f -> f.array(f.struct("Person", list(f.string()), list("name")))),
          args(f -> f.array(f.variable("A")))
      );
    }
  }

  @Nested
  class _struct {
    @Test
    public void _without_fields_can_be_created() {
      structT("Struct", list());
    }

    @Test
    public void first_field_type_can_be_nothing() {
      structT("Struct", list(nothingT()));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      structT("Struct", list(arrayT(nothingT())));
    }

    @Test
    public void different_size_of_fields_and_names_causes_exception() {
      assertCall(() -> structT("Struct", list(stringT()), list("name1", "name2")))
          .throwsException(IllegalArgumentException.class);
    }
    @ParameterizedTest
    @MethodSource("struct_name_cases")
    public void struct_name(Function<TypeFactory, StructType> factoryCall, String expected) {
      assertThat(invoke(factoryCall).name())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_name_cases() {
      return asList(
          args(f -> f.struct("MyStruct", list(), list()), "MyStruct"),
          args(f -> f.struct("", list(), list()), "")
      );
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(Function<TypeFactory, StructType> factoryCall,
        Function<TypeFactory, List<Type>> expected) {
      assertThat(invoke(factoryCall).fields())
          .isEqualTo(invoke(expected));
    }

    public static List<Arguments> struct_fields_cases() {
      return asList(
          args(f -> f.struct("Person", list(), list()), f -> list()),
          args(f -> f.struct("Person", list(f.string()), list("field")), f -> list(f.string())),
          args(f -> f.struct("Person", list(f.string(), f.int_()), list("field", "field2")),
              f -> list(f.string(), f.int_()))
      );
    }

    @ParameterizedTest
    @MethodSource("struct_names_cases")
    public void struct_names(Function<TypeFactory, StructType> factoryCall,
        List<String> expected) {
      assertThat(invoke(factoryCall).names())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_names_cases() {
      return list(
          args(f -> f.struct("Person", list(), list()), list()),
          args(f -> f.struct("Person", list(f.string()), list("field")), list("field")),
          args(f -> f.struct("Person", list(f.string(), f.int_()), list("field", "field2")),
              list("field", "field2"))
      );
    }
  }
}
