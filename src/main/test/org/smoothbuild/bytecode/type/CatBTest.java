package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
import org.smoothbuild.bytecode.obj.expr.CallB;
import org.smoothbuild.bytecode.obj.expr.CombineB;
import org.smoothbuild.bytecode.obj.expr.IfB;
import org.smoothbuild.bytecode.obj.expr.InvokeB;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.obj.expr.OrderB;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BoolB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.IntB;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.StringB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.expr.CombineCB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;

public class CatBTest extends TestingContext {
  @Test
  public void verify_all_base_cats_are_tested() {
    Truth.assertThat(CatKindB.values())
        .hasLength(19);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Function<CatDb, CatB> factoryCall, String name) {
    assertThat(execute(factoryCall).name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function<CatDb, CatB> factoryCall, String name) {
    assertThat(execute(factoryCall).q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function<CatDb, CatB> factoryCall, String name) {
    var catH = execute(factoryCall);
    if (catH instanceof TypeB) {
      assertThat(catH.toString())
          .isEqualTo("TypeB(`" + name + "`)");
    } else {
      assertThat(catH.toString())
          .isEqualTo("Category(`" + name + "`)");
    }
  }

  public static List<Arguments> names() {
    return asList(
        args(f -> f.any(), "Any"),
        args(f -> f.blob(), "Blob"),
        args(f -> f.bool(), "Bool"),
        args(f -> f.int_(), "Int"),
        args(f -> f.nothing(), "Nothing"),
        args(f -> f.string(), "String"),
        args(f -> f.var("A"), "A"),

        args(f -> f.array(f.var("A")), "[A]"),
        args(f -> f.array(f.any()), "[Any]"),
        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.nothing()), "[Nothing]"),
        args(f -> f.array(f.string()), "[String]"),

        args(f -> f.array(f.array(f.var("A"))), "[[A]]"),
        args(f -> f.array(f.array(f.any())), "[[Any]]"),
        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.nothing())), "[[Nothing]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),

        args(f -> f.func(f.var("A"), list(f.array(f.var("A")))), "A([A])"),
        args(f -> f.func(f.string(), list(f.array(f.var("A")))), "String([A])"),
        args(f -> f.func(f.var("A"), list(f.var("A"))), "A(A)"),
        args(f -> f.func(f.string(), list()), "String()"),
        args(f -> f.func(f.string(), list(f.string())), "String(String)"),

        args(f -> f.method(f.var("A"), list(f.array(f.var("A")))), "_A([A])"),
        args(f -> f.method(f.string(), list(f.array(f.var("A")))), "_String([A])"),
        args(f -> f.method(f.var("A"), list(f.var("A"))), "_A(A)"),
        args(f -> f.method(f.string(), list()), "_String()"),
        args(f -> f.method(f.string(), list(f.string())), "_String(String)"),

        args(f -> f.tuple(list()), "{}"),
        args(f -> f.tuple(list(f.string(), f.bool())), "{String,Bool}"),
        args(f -> f.tuple(list(f.tuple(list(f.int_())))), "{{Int}}"),

        args(f -> f.call(f.int_()), "Call:Int"),
        args(f -> f.combine(f.tuple(list(f.string(), f.int_()))), "Combine:{String,Int}"),
        args(f -> f.if_(f.int_()), "If:Int"),
        args(f -> f.map(f.array(f.int_())), "Map:[Int]"),
        args(f -> f.invoke(f.int_()), "Invoke:Int"),
        args(f -> f.func(f.blob(), list(f.bool())), "Blob(Bool)"),
        args(f -> f.func(f.blob(), list(f.bool())), "Blob(Bool)"),
        args(f -> f.order(f.string()), "Order:[String]"),
        args(f -> f.ref(f.int_()), "ParamRef:Int"),
        args(f -> f.select(f.int_()), "Select:Int")
    );
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Function<CatDb, CatB> factoryCall, boolean expected) {
    assertThat(execute(factoryCall).isPolytype())
        .isEqualTo(expected);
  }

  public static List<Arguments> isPolytype_test_data() {
    return asList(
        args(f -> f.var("A"), true),
        args(f -> f.array(f.var("A")), true),
        args(f -> f.array(f.array(f.var("A"))), true),

        args(f -> f.func(f.var("A"), list()), true),
        args(f -> f.func(f.func(f.var("A"), list()), list()), true),
        args(f -> f.func(f.func(f.func(f.var("A"), list()), list()), list()), true),

        args(f -> f.func(f.bool(), list(f.var("A"))), true),
        args(f -> f.func(f.bool(), list(f.func(f.var("A"), list()))), true),
        args(f -> f.func(f.bool(), list(f.func(f.func(f.var("A"), list()), list()))), true),

        args(f -> f.func(f.bool(), list(f.func(f.blob(), list(f.var("A"))))), true),

        args(f -> f.method(f.var("A"), list()), true),
        args(f -> f.method(f.func(f.var("A"), list()), list()), true),
        args(f -> f.method(f.func(f.func(f.var("A"), list()), list()), list()), true),

        args(f -> f.method(f.bool(), list(f.var("A"))), true),
        args(f -> f.method(f.bool(), list(f.method(f.var("A"), list()))), true),
        args(f -> f.method(f.bool(), list(f.method(f.func(f.var("A"), list()), list()))), true),

        args(f -> f.method(f.bool(), list(f.method(f.blob(), list(f.var("A"))))), true),

        args(f -> f.any(), false),
        args(f -> f.blob(), false),
        args(f -> f.bool(), false),
        args(f -> f.int_(), false),
        args(f -> f.nothing(), false),
        args(f -> f.string(), false),
        args(f -> f.tuple(list()), false),
        args(f -> f.tuple(list(f.int_())), false),
        args(f -> f.tuple(list(f.var("A"))), true),
        args(f -> f.tuple(list(f.tuple(list(f.var("A"))))), true)
        );
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(
      Function<CatDb, CatB> factoryCall,
      Function<CatDb, Set<Var>> resultCall) {
    assertThat(execute(factoryCall).vars())
        .containsExactlyElementsIn(execute(resultCall))
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
        args(f -> f.tuple(list()), f -> set()),
        args(f -> f.tuple(list(f.int_())), f -> set()),
        args(f -> f.tuple(list(f.var("A"))), f -> set(f.var("A"))),
        args(f -> f.tuple(list(f.tuple(list(f.var("A"))))), f -> set(f.var("A"))),
        args(f -> f.var("A"), f -> set(f.var("A"))),

        args(f -> f.array(f.any()), f -> set()),
        args(f -> f.array(f.blob()), f -> set()),
        args(f -> f.array(f.bool()), f -> set()),
        args(f -> f.array(f.int_()), f -> set()),
        args(f -> f.array(f.nothing()), f -> set()),
        args(f -> f.array(f.string()), f -> set()),
        args(f -> f.array(f.var("A")), f -> set(f.var("A"))),

        args(f -> f.func(f.string(), list()), f -> set()),
        args(f -> f.func(f.string(), list(f.bool())), f -> set()),

        args(f -> f.method(f.string(), list()), f -> set()),
        args(f -> f.method(f.string(), list(f.bool())), f -> set()),

        args(f -> f.array(f.var("A")), f -> set(f.var("A"))),
        args(f -> f.array(f.array(f.var("A"))), f -> set(f.var("A"))),

        args(f -> f.func(f.var("A"), list()), f -> set(f.var("A"))),
        args(f -> f.func(f.var("A"), list(f.string())), f -> set(f.var("A"))),
        args(f -> f.func(f.string(), list(f.var("A"))), f -> set(f.var("A"))),
        args(f -> f.func(f.var("B"), list(f.var("A"))), f -> set(f.var("A"), f.var("B"))),

        args(f -> f.func(f.func(f.var("A"), list()), list()), f -> set(f.var("A"))),
        args(f -> f.func(f.var("D"), list(f.var("C"), f.var("B"))), f -> set(f.var("B"), f.var("C"), f.var("D"))),

        args(f -> f.method(f.var("A"), list()), f -> set(f.var("A"))),
        args(f -> f.method(f.var("A"), list(f.string())), f -> set(f.var("A"))),
        args(f -> f.method(f.string(), list(f.var("A"))), f -> set(f.var("A"))),
        args(f -> f.method(f.var("B"), list(f.var("A"))), f -> set(f.var("A"), f.var("B"))),

        args(f -> f.method(f.method(f.var("A"), list()), list()), f -> set(f.var("A"))),
        args(f -> f.method(f.var("D"), list(f.var("C"), f.var("B"))),
            f -> set(f.var("B"), f.var("C"), f.var("D")))
    );
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CatDb, FuncT> factoryCall,
        Function<CatDb, List<Type>> expected) {
      assertThat(execute(factoryCall).res())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.func(f.int_(), list()), f -> f.int_()),
          args(f -> f.func(f.blob(), list(f.bool())), f -> f.blob()),
          args(f -> f.func(f.blob(), list(f.bool(), f.int_())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CatDb, FuncT> factoryCall,
        Function<CatDb, List<Type>> expected) {
      assertThat(execute(factoryCall).params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.func(f.int_(), list()), f -> list()),
          args(f -> f.func(f.blob(), list(f.bool())), f -> list(f.bool())),
          args(f -> f.func(f.blob(), list(f.bool(), f.int_())), f -> list(f.bool(), f.int_()))
      );
    }
  }
  @Nested
  class _method {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CatDb, MethodTB> factoryCall,
        Function<CatDb, List<Type>> expected) {
      Truth.assertThat(execute(factoryCall).res())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.method(f.int_(), list()), f -> f.int_()),
          args(f -> f.method(f.blob(), list(f.bool())), f -> f.blob()),
          args(f -> f.method(f.blob(), list(f.bool(), f.int_())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CatDb, MethodTB> factoryCall,
        Function<CatDb, List<Type>> expected) {
      assertThat(execute(factoryCall).params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.method(f.int_(), list()), f -> list()),
          args(f -> f.method(f.blob(), list(f.bool())), f -> list(f.bool())),
          args(f -> f.method(f.blob(), list(f.bool(), f.int_())), f -> list(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _var {
    @Test
    public void name() {
      assertThat(varTS("A").name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> varTS("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(Function<CatDb, TypeB> factoryCall) {
      TypeB elem = execute(factoryCall);
      ArrayT array = typeFactoryB().array(elem);
      assertThat(array.elem())
          .isEqualTo(elem);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          args(f -> f.any()),
          args(f -> f.blob()),
          args(f -> f.bool()),
          args(f -> f.func(f.string(), list())),
          args(f -> f.method(f.string(), list())),
          args(f -> f.int_()),
          args(f -> f.nothing()),
          args(f -> f.string()),
          args(f -> f.tuple(list(f.int_()))),
          args(f -> f.var("A")),

          args(f -> f.array(f.any())),
          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.func(f.string(), list()))),
          args(f -> f.array(f.method(f.string(), list()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.nothing())),
          args(f -> f.array(f.string())),
          args(f -> f.array(f.var("A")))
      );
    }
  }

  @Nested
  class _tuple {
    @Test
    public void _without_items_can_be_created() {
      tupleTB(list());
    }

    @Test
    public void first_item_type_can_be_nothing() {
      tupleTB(list(nothingTB()));
    }

    @Test
    public void first_item_type_can_be_nothing_array() {
      tupleTB(list(arrayTB(nothingTB())));
    }

    @ParameterizedTest
    @MethodSource("tuple_item_cases")
    public void tuple_item(
        Function<CatDb, TupleTB> factoryCall,
        Function<CatDb, NList<Labeled<Type>>> expected) {
      assertThat(execute(factoryCall).items())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> tuple_item_cases() {
      return asList(
          args(f -> f.tuple(list()), f -> list()),
          args(f -> f.tuple(list(f.string())), f -> list(f.string())),
          args(f -> f.tuple(list(f.var("A"))), f -> list(f.var("A"))),
          args(f -> f.tuple(list(f.string(), f.int_())), f -> list(f.string(), f.int_()))
      );
    }
  }

  @ParameterizedTest
  @MethodSource("typeJ_test_data")
  public void typeJ(CatB type, Class<?> expected) {
    assertThat(type.typeJ())
        .isEqualTo(expected);
  }

  public static List<Arguments> typeJ_test_data() {
    return list(
        arguments(TestingCatsB.ANY, ValB.class),
        arguments(TestingCatsB.BLOB, BlobB.class),
        arguments(TestingCatsB.BOOL, BoolB.class),
        arguments(TestingCatsB.FUNC, FuncB.class),
        arguments(TestingCatsB.INT, IntB.class),
        arguments(TestingCatsB.METHOD, MethodB.class),
        arguments(TestingCatsB.NOTHING, ValB.class),
        arguments(TestingCatsB.PERSON, TupleB.class),
        arguments(TestingCatsB.STRING, StringB.class),
        arguments(TestingCatsB.VARIABLE, ValB.class),

        arguments(TestingCatsB.ARRAY_ANY, ArrayB.class),
        arguments(TestingCatsB.ARRAY_BLOB, ArrayB.class),
        arguments(TestingCatsB.ARRAY_BOOL, ArrayB.class),
        arguments(TestingCatsB.ARRAY_FUNCTION, ArrayB.class),
        arguments(TestingCatsB.ARRAY_INT, ArrayB.class),
        arguments(TestingCatsB.ARRAY_METHOD, ArrayB.class),
        arguments(TestingCatsB.ARRAY_NOTHING, ArrayB.class),
        arguments(TestingCatsB.ARRAY_PERSON_TUPLE, ArrayB.class),
        arguments(TestingCatsB.ARRAY_STR, ArrayB.class),
        arguments(TestingCatsB.ARRAY_VARIABLE, ArrayB.class),

        arguments(TestingCatsB.CALL, CallB.class),
        arguments(TestingCatsB.ORDER, OrderB.class),
        arguments(TestingCatsB.COMBINE, CombineB.class),
        arguments(TestingCatsB.IF, IfB.class),
        arguments(TestingCatsB.INVOKE, InvokeB.class),
        arguments(TestingCatsB.MAP, MapB.class),
        arguments(TestingCatsB.PARAM_REF, ParamRefB.class),
        arguments(TestingCatsB.SELECT, SelectB.class)
    );
  }

  @Nested
  class _eval_type {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeB type) {
      Truth.assertThat(TestingCatsB.CAT_DB.call(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCB type, TupleTB expected) {
      Truth.assertThat(type.evalT())
          .isEqualTo(expected);
    }

    public static List<Arguments> combine_cases() {
      CatDb db = TestingCatsB.CAT_DB;
      return list(
          arguments(db.combine(db.tuple(list())), db.tuple(list())),
          arguments(db.combine(db.tuple(list(TestingCatsB.STRING))), db.tuple(list(
              TestingCatsB.STRING)))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void invoke(TypeB type) {
      Truth.assertThat(TestingCatsB.CAT_DB.invoke(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeB type) {
      Truth.assertThat(TestingCatsB.CAT_DB.order(type).evalT())
          .isEqualTo(TestingCatsB.CAT_DB.array(type));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeB type) {
      Truth.assertThat(TestingCatsB.CAT_DB.ref(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeB type) {
      Truth.assertThat(TestingCatsB.CAT_DB.select(type).evalT())
          .isEqualTo(type);
    }

    public static ImmutableList<CatB> types() {
      return TestingCatsB.CATS_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(TestingCatsB.ANY, TestingCatsB.ANY);
    tester.addEqualityGroup(TestingCatsB.BLOB, TestingCatsB.BLOB);
    tester.addEqualityGroup(TestingCatsB.BOOL, TestingCatsB.BOOL);
    tester.addEqualityGroup(TestingCatsB.FUNC, TestingCatsB.FUNC);
    tester.addEqualityGroup(TestingCatsB.INT, TestingCatsB.INT);
    tester.addEqualityGroup(TestingCatsB.NOTHING, TestingCatsB.NOTHING);
    tester.addEqualityGroup(TestingCatsB.STRING, TestingCatsB.STRING);
    tester.addEqualityGroup(TestingCatsB.PERSON, TestingCatsB.PERSON);
    tester.addEqualityGroup(TestingCatsB.VARIABLE, TestingCatsB.VARIABLE);

    tester.addEqualityGroup(TestingCatsB.ARRAY_ANY, TestingCatsB.ARRAY_ANY);
    tester.addEqualityGroup(TestingCatsB.ARRAY_BLOB, TestingCatsB.ARRAY_BLOB);
    tester.addEqualityGroup(TestingCatsB.ARRAY_BOOL, TestingCatsB.ARRAY_BOOL);
    tester.addEqualityGroup(TestingCatsB.ARRAY_FUNCTION, TestingCatsB.ARRAY_FUNCTION);
    tester.addEqualityGroup(TestingCatsB.ARRAY_INT, TestingCatsB.ARRAY_INT);
    tester.addEqualityGroup(TestingCatsB.ARRAY_NOTHING, TestingCatsB.ARRAY_NOTHING);
    tester.addEqualityGroup(TestingCatsB.ARRAY_STR, TestingCatsB.ARRAY_STR);
    tester.addEqualityGroup(TestingCatsB.ARRAY_PERSON_TUPLE, TestingCatsB.ARRAY_PERSON_TUPLE);
    tester.addEqualityGroup(TestingCatsB.ARRAY_VARIABLE, TestingCatsB.ARRAY_VARIABLE);

    tester.addEqualityGroup(TestingCatsB.ARRAY2_VARIABLE, TestingCatsB.ARRAY2_VARIABLE);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_ANY, TestingCatsB.ARRAY2_ANY);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_BLOB, TestingCatsB.ARRAY2_BLOB);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_BOOL, TestingCatsB.ARRAY2_BOOL);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_FUNCTION, TestingCatsB.ARRAY2_FUNCTION);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_INT, TestingCatsB.ARRAY2_INT);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_NOTHING, TestingCatsB.ARRAY2_NOTHING);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_STR, TestingCatsB.ARRAY2_STR);
    tester.addEqualityGroup(TestingCatsB.ARRAY2_PERSON_TUPLE, TestingCatsB.ARRAY2_PERSON_TUPLE);

    tester.addEqualityGroup(TestingCatsB.CALL, TestingCatsB.CALL);
    tester.addEqualityGroup(TestingCatsB.COMBINE, TestingCatsB.COMBINE);
    tester.addEqualityGroup(TestingCatsB.IF, TestingCatsB.IF);
    tester.addEqualityGroup(TestingCatsB.INVOKE, TestingCatsB.INVOKE);
    tester.addEqualityGroup(TestingCatsB.MAP, TestingCatsB.MAP);
    tester.addEqualityGroup(TestingCatsB.ORDER, TestingCatsB.ORDER);
    tester.addEqualityGroup(TestingCatsB.PARAM_REF, TestingCatsB.PARAM_REF);
    tester.addEqualityGroup(TestingCatsB.SELECT, TestingCatsB.SELECT);

    tester.testEquals();
  }

  private <R> R execute(Function<CatDb, R> f) {
    return f.apply(catDb());
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function<CatDb, R> factoryCall1,
      Function<CatDb, R> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<CatDb, R> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<CatDb, R> factoryCall) {
    return arguments(factoryCall);
  }
}
