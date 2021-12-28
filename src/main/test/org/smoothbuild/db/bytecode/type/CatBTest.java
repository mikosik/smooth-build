package org.smoothbuild.db.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ANY;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_ANY;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_BLOB;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_BOOL;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_FUNCTION;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_INT;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_NOTHING;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_STR;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY2_VARIABLE;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_ANY;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_BLOB;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_BOOL;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_FUNCTION;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_INT;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_METHOD;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_NOTHING;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_STR;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ARRAY_VARIABLE;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.BLOB;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.BOOL;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.CALL;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.CAT_DB;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.COMBINE;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.FUNC;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.IF;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.INT;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.INVOKE;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.MAP;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.METHOD;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.NOTHING;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.ORDER;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.PARAM_REF;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.PERSON;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.SELECT;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.STRING;
import static org.smoothbuild.db.bytecode.type.TestingCatsB.VARIABLE;
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
import org.smoothbuild.db.bytecode.obj.expr.CallB;
import org.smoothbuild.db.bytecode.obj.expr.CombineB;
import org.smoothbuild.db.bytecode.obj.expr.IfB;
import org.smoothbuild.db.bytecode.obj.expr.InvokeB;
import org.smoothbuild.db.bytecode.obj.expr.MapB;
import org.smoothbuild.db.bytecode.obj.expr.OrderB;
import org.smoothbuild.db.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.db.bytecode.obj.expr.SelectB;
import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.BlobB;
import org.smoothbuild.db.bytecode.obj.val.BoolB;
import org.smoothbuild.db.bytecode.obj.val.FuncB;
import org.smoothbuild.db.bytecode.obj.val.IntB;
import org.smoothbuild.db.bytecode.obj.val.MethodB;
import org.smoothbuild.db.bytecode.obj.val.StringB;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.CatB;
import org.smoothbuild.db.bytecode.type.base.CatKindB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.bytecode.type.expr.CombineCB;
import org.smoothbuild.db.bytecode.type.val.MethodTB;
import org.smoothbuild.db.bytecode.type.val.TupleTB;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class CatBTest extends TestingContext {
  @Test
  public void verify_all_base_cats_are_tested() {
    assertThat(CatKindB.values())
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
          .isEqualTo("TypeH(`" + name + "`)");
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
      assertThat(execute(factoryCall).res())
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
      assertThat(varS("A").name())
          .isEqualTo("A");
    }

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
        arguments(ANY, ValB.class),
        arguments(BLOB, BlobB.class),
        arguments(BOOL, BoolB.class),
        arguments(FUNC, FuncB.class),
        arguments(INT, IntB.class),
        arguments(METHOD, MethodB.class),
        arguments(NOTHING, ValB.class),
        arguments(PERSON, TupleB.class),
        arguments(STRING, StringB.class),
        arguments(VARIABLE, ValB.class),

        arguments(ARRAY_ANY, ArrayB.class),
        arguments(ARRAY_BLOB, ArrayB.class),
        arguments(ARRAY_BOOL, ArrayB.class),
        arguments(ARRAY_FUNCTION, ArrayB.class),
        arguments(ARRAY_INT, ArrayB.class),
        arguments(ARRAY_METHOD, ArrayB.class),
        arguments(ARRAY_NOTHING, ArrayB.class),
        arguments(ARRAY_PERSON_TUPLE, ArrayB.class),
        arguments(ARRAY_STR, ArrayB.class),
        arguments(ARRAY_VARIABLE, ArrayB.class),

        arguments(CALL, CallB.class),
        arguments(ORDER, OrderB.class),
        arguments(COMBINE, CombineB.class),
        arguments(IF, IfB.class),
        arguments(INVOKE, InvokeB.class),
        arguments(MAP, MapB.class),
        arguments(PARAM_REF, ParamRefB.class),
        arguments(SELECT, SelectB.class)
    );
  }

  @Nested
  class _eval_type {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeB type) {
      assertThat(CAT_DB.call(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCB type, TupleTB expected) {
      assertThat(type.evalT())
          .isEqualTo(expected);
    }

    public static List<Arguments> combine_cases() {
      CatDb db = CAT_DB;
      return list(
          arguments(db.combine(db.tuple(list())), db.tuple(list())),
          arguments(db.combine(db.tuple(list(STRING))), db.tuple(list(STRING)))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void invoke(TypeB type) {
      assertThat(CAT_DB.invoke(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeB type) {
      assertThat(CAT_DB.order(type).evalT())
          .isEqualTo(CAT_DB.array(type));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeB type) {
      assertThat(CAT_DB.ref(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeB type) {
      assertThat(CAT_DB.select(type).evalT())
          .isEqualTo(type);
    }

    public static ImmutableList<CatB> types() {
      return TestingCatsB.CATS_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(ANY, ANY);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(FUNC, FUNC);
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
    tester.addEqualityGroup(COMBINE, COMBINE);
    tester.addEqualityGroup(IF, IF);
    tester.addEqualityGroup(INVOKE, INVOKE);
    tester.addEqualityGroup(MAP, MAP);
    tester.addEqualityGroup(ORDER, ORDER);
    tester.addEqualityGroup(PARAM_REF, PARAM_REF);
    tester.addEqualityGroup(SELECT, SELECT);

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
