package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.type.TestingTypesH.ANY;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_ANY;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_BLOB;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_BOOL;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_FUNCTION;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_INT;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_STR;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY2_VARIABLE;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_ANY;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_BLOB;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_BOOL;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_FUNCTION;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_INT;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_NOTHING;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_STR;
import static org.smoothbuild.db.object.type.TestingTypesH.ARRAY_VARIABLE;
import static org.smoothbuild.db.object.type.TestingTypesH.BLOB;
import static org.smoothbuild.db.object.type.TestingTypesH.BOOL;
import static org.smoothbuild.db.object.type.TestingTypesH.CALL;
import static org.smoothbuild.db.object.type.TestingTypesH.CONST;
import static org.smoothbuild.db.object.type.TestingTypesH.CONSTRUCT;
import static org.smoothbuild.db.object.type.TestingTypesH.FUNCTION;
import static org.smoothbuild.db.object.type.TestingTypesH.INT;
import static org.smoothbuild.db.object.type.TestingTypesH.INVOKE;
import static org.smoothbuild.db.object.type.TestingTypesH.NOTHING;
import static org.smoothbuild.db.object.type.TestingTypesH.ORDER;
import static org.smoothbuild.db.object.type.TestingTypesH.PERSON;
import static org.smoothbuild.db.object.type.TestingTypesH.REF;
import static org.smoothbuild.db.object.type.TestingTypesH.SELECT;
import static org.smoothbuild.db.object.type.TestingTypesH.STRING;
import static org.smoothbuild.db.object.type.TestingTypesH.TYPEH_DB;
import static org.smoothbuild.db.object.type.TestingTypesH.VARIABLE;
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
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.base.TypeKindH;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class TypeHTest extends TestingContext {
  @Test
  public void verify_all_base_TypeO_are_tested() {
    assertThat(TypeKindH.values())
        .hasLength(20);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Function<TypeFactoryH, TypeH> factoryCall, String name) {
    assertThat(invoke(factoryCall).name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function<TypeFactoryH, TypeH> factoryCall, String name) {
    assertThat(invoke(factoryCall).q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function<TypeFactoryH, TypeH> factoryCall, String name) {
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
        args(f -> f.variable("A"), "A"),

        args(f -> f.array(f.variable("A")), "[A]"),
        args(f -> f.array(f.any()), "[Any]"),
        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.nothing()), "[Nothing]"),
        args(f -> f.array(f.string()), "[String]"),

        args(f -> f.array(f.array(f.variable("A"))), "[[A]]"),
        args(f -> f.array(f.array(f.any())), "[[Any]]"),
        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.nothing())), "[[Nothing]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),

        args(f -> f.function(f.variable("A"), list(f.array(f.variable("A")))), "A([A])"),
        args(f -> f.function(f.string(), list(f.array(f.variable("A")))), "String([A])"),
        args(f -> f.function(f.variable("A"), list(f.variable("A"))), "A(A)"),
        args(f -> f.function(f.string(), list()), "String()"),
        args(f -> f.function(f.string(), list(f.string())), "String(String)"),

        args(f -> f.tuple(list()), "{}"),
        args(f -> f.tuple(list(f.string(), f.string())), "{String,String}"),
        args(f -> f.tuple(list(f.tuple(list(f.int_())))), "{{Int}}"),

        args(f -> f.call(f.int_()), "CALL:Int"),
        args(f -> f.const_(f.int_()), "CONST:Int"),
        args(f -> f.construct(f.tuple(list(f.string(), f.int_()))), "CONSTRUCT:{String,Int}"),
        args(f -> f.if_(f.int_()), "IF:Int"),
        args(f -> f.invoke(f.int_()), "INVOKE:Int"),
        args(f -> f.map(f.int_()), "MAP:Int"),
        args(f -> f.nativeMethod(), "NATIVE_METHOD"),
        args(f -> f.order(f.string()), "ORDER:[String]"),
        args(f -> f.ref(f.int_()), "REF:Int"),
        args(f -> f.select(f.int_()), "SELECT:Int")
    );
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Function<TypeFactoryH, TypeH> factoryCall, boolean expected) {
    assertThat(invoke(factoryCall).isPolytype())
        .isEqualTo(expected);
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
        args(f -> f.tuple(list()), false),
        args(f -> f.tuple(list(f.int_())), false)
        );
  }

  @ParameterizedTest
  @MethodSource("variables_test_data")
  public void variables(
      Function<TypeFactoryH, TypeH> factoryCall,
      Function<TypeFactoryH, Set<Variable>> resultCall) {
    assertThat(invoke(factoryCall).variables())
        .containsExactlyElementsIn(invoke(resultCall))
        .inOrder();
  }

  public static List<Arguments> variables_test_data() {
    return asList(
        args(f -> f.any(), f -> set()),
        args(f -> f.blob(), f -> set()),
        args(f -> f.bool(), f -> set()),
        args(f -> f.int_(), f -> set()),
        args(f -> f.nothing(), f -> set()),
        args(f -> f.string(), f -> set()),
        args(f -> f.tuple(list()), f -> set()),
        args(f -> f.tuple(list(f.int_())), f -> set()),
        args(f -> f.variable("A"), f -> set(f.variable("A"))),

        args(f -> f.array(f.any()), f -> set()),
        args(f -> f.array(f.blob()), f -> set()),
        args(f -> f.array(f.bool()), f -> set()),
        args(f -> f.array(f.int_()), f -> set()),
        args(f -> f.array(f.nothing()), f -> set()),
        args(f -> f.array(f.string()), f -> set()),
        args(f -> f.array(f.variable("A")), f -> set(f.variable("A"))),

        args(f -> f.function(f.string(), list()), f -> set()),
        args(f -> f.function(f.string(), list(f.bool())), f -> set()),

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

  @Nested
  class _function {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<TypeFactoryH, FunctionType> factoryCall,
        Function<TypeFactoryH, List<Type>> expected) {
      assertThat(invoke(factoryCall).result())
          .isEqualTo(invoke(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.function(f.int_(), list()), f -> f.int_()),
          args(f -> f.function(f.blob(), list(f.bool())), f -> f.blob()),
          args(f -> f.function(f.blob(), list(f.bool(), f.int_())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("parameters_cases")
    public void parameters(Function<TypeFactoryH, FunctionType> factoryCall,
        Function<TypeFactoryH, List<Type>> expected) {
      assertThat(invoke(factoryCall).parameters())
          .isEqualTo(invoke(expected));
    }

    public static List<Arguments> parameters_cases() {
      return asList(
          args(f -> f.function(f.int_(), list()), f -> list()),
          args(f -> f.function(f.blob(), list(f.bool())), f -> list(f.bool())),
          args(f -> f.function(f.blob(), list(f.bool(), f.int_())), f -> list(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _variable {
    @Test
    public void name() {
      assertThat(variableST("A").name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> variableST("a"))
          .throwsException(new IllegalArgumentException("Illegal type variable name 'a'."));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(Function<TypeFactoryH, TypeHV> factoryCall) {
      TypeHV element = invoke(factoryCall);
      ArrayType array = typeFactoryO().array(element);
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
          args(f -> f.tuple(list(f.int_()))),
          args(f -> f.variable("A")),

          args(f -> f.array(f.any())),
          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.function(f.string(), list()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.nothing())),
          args(f -> f.array(f.string())),
          args(f -> f.array(f.variable("A")))
      );
    }
  }

  @Nested
  class _tuple {
    @Test
    public void _without_items_can_be_created() {
      tupleOT(list());
    }

    @Test
    public void first_item_type_can_be_nothing() {
      tupleOT(list(nothingOT()));
    }

    @Test
    public void first_item_type_can_be_nothing_array() {
      tupleOT(list(arrayOT(nothingOT())));
    }

    @ParameterizedTest
    @MethodSource("tuple_item_cases")
    public void tuple_item(
        Function<TypeFactoryH, TupleTypeH> factoryCall,
        Function<TypeFactoryH, NamedList<Labeled<Type>>> expected) {
      assertThat(invoke(factoryCall).items())
          .isEqualTo(invoke(expected));
    }

    public static List<Arguments> tuple_item_cases() {
      return asList(
          args(f -> f.tuple(list()), f -> list()),
          args(f -> f.tuple(list(f.string())), f -> list(f.string())),
          args(f -> f.tuple(list(f.string(), f.int_())), f -> list(f.string(), f.int_()))
      );
    }
  }

  @ParameterizedTest
  @MethodSource("jType_test_data")
  public void jType(TypeH type, Class<?> expected) {
    assertThat(type.jType())
        .isEqualTo(expected);
  }

  public static List<Arguments> jType_test_data() {
    return list(
        arguments(ANY, null),
        arguments(BLOB, BlobH.class),
        arguments(BOOL, BoolH.class),
        arguments(FUNCTION, FunctionH.class),
        arguments(INT, IntH.class),
        arguments(NOTHING, null),
        arguments(PERSON, TupleH.class),
        arguments(STRING, StringH.class),
        arguments(VARIABLE, null),

        arguments(ARRAY_ANY, ArrayH.class),
        arguments(ARRAY_BLOB, ArrayH.class),
        arguments(ARRAY_BOOL, ArrayH.class),
        arguments(ARRAY_FUNCTION, ArrayH.class),
        arguments(ARRAY_INT, ArrayH.class),
        arguments(ARRAY_NOTHING, ArrayH.class),
        arguments(ARRAY_PERSON_TUPLE, ArrayH.class),
        arguments(ARRAY_STR, ArrayH.class),
        arguments(ARRAY_VARIABLE, ArrayH.class),

        arguments(CALL, CallH.class),
        arguments(CONST, ConstH.class),
        arguments(ORDER, OrderH.class),
        arguments(CONSTRUCT, ConstructH.class),
        arguments(SELECT, SelectH.class),
        arguments(REF, RefH.class)
    );
  }

  @Nested
  class _evaluation_type {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeHV type) {
      assertThat(TYPEH_DB.call(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void const_(TypeHV type) {
      assertThat(TYPEH_DB.const_(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("construct_cases")
    public void construct(ConstructTypeH type, TupleTypeH expected) {
      assertThat(type.evaluationType())
          .isEqualTo(expected);
    }

    public static List<Arguments> construct_cases() {
      TypeHDb db = TYPEH_DB;
      return list(
          arguments(db.construct(db.tuple(list())), db.tuple(list())),
          arguments(db.construct(db.tuple(list(STRING))), db.tuple(list(STRING)))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void if_(TypeHV type) {
      assertThat(TYPEH_DB.if_(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void invoke(TypeHV type) {
      assertThat(TYPEH_DB.invoke(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void map_(TypeHV type) {
      assertThat(TYPEH_DB.map(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeHV type) {
      assertThat(TYPEH_DB.order(type).evaluationType())
          .isEqualTo(TYPEH_DB.array(type));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeHV type) {
      assertThat(TYPEH_DB.ref(type).evaluationType())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeHV type) {
      assertThat(TYPEH_DB.select(type).evaluationType())
          .isEqualTo(type);
    }

    public static ImmutableList<TypeH> types() {
      return TestingTypesH.TYPESV_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(ANY, ANY);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(FUNCTION, FUNCTION);
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(STRING, STRING);
    tester.addEqualityGroup(PERSON, PERSON);
    tester.addEqualityGroup(VARIABLE, VARIABLE);

    tester.addEqualityGroup(ARRAY_ANY, ARRAY_ANY);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_FUNCTION, ARRAY_FUNCTION);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_NOTHING, ARRAY_NOTHING);
    tester.addEqualityGroup(ARRAY_STR, ARRAY_STR);
    tester.addEqualityGroup(ARRAY_PERSON_TUPLE, ARRAY_PERSON_TUPLE);
    tester.addEqualityGroup(ARRAY_VARIABLE, ARRAY_VARIABLE);

    tester.addEqualityGroup(ARRAY2_VARIABLE, ARRAY2_VARIABLE);
    tester.addEqualityGroup(ARRAY2_ANY, ARRAY2_ANY);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_FUNCTION, ARRAY2_FUNCTION);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_NOTHING, ARRAY2_NOTHING);
    tester.addEqualityGroup(ARRAY2_STR, ARRAY2_STR);
    tester.addEqualityGroup(ARRAY2_PERSON_TUPLE, ARRAY2_PERSON_TUPLE);

    tester.addEqualityGroup(CALL, CALL);
    tester.addEqualityGroup(CONST, CONST);
    tester.addEqualityGroup(CONSTRUCT, CONSTRUCT);
    tester.addEqualityGroup(INVOKE, INVOKE);
    tester.addEqualityGroup(ORDER, ORDER);
    tester.addEqualityGroup(REF, REF);
    tester.addEqualityGroup(SELECT, SELECT);

    tester.testEquals();
  }

  private <R> R invoke(Function<TypeFactoryH, R> f) {
    return f.apply(typeFactoryO());
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function<TypeFactoryH, R> factoryCall1,
      Function<TypeFactoryH, R> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<TypeHDb, R> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<TypeFactoryH, R> factoryCall) {
    return arguments(factoryCall);
  }
}
