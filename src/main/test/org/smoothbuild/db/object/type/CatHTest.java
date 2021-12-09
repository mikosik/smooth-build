package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.type.TestingCatsH.ANY;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_ANY;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_BLOB;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_BOOL;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_FUNCTION;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_INT;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_STR;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY2_VARIABLE;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_ANY;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_BLOB;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_BOOL;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_FUNCTION;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_INT;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_METHOD;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_NOTHING;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_STR;
import static org.smoothbuild.db.object.type.TestingCatsH.ARRAY_VARIABLE;
import static org.smoothbuild.db.object.type.TestingCatsH.BLOB;
import static org.smoothbuild.db.object.type.TestingCatsH.BOOL;
import static org.smoothbuild.db.object.type.TestingCatsH.CALL;
import static org.smoothbuild.db.object.type.TestingCatsH.COMBINE;
import static org.smoothbuild.db.object.type.TestingCatsH.FUNC;
import static org.smoothbuild.db.object.type.TestingCatsH.IF;
import static org.smoothbuild.db.object.type.TestingCatsH.INT;
import static org.smoothbuild.db.object.type.TestingCatsH.INVOKE;
import static org.smoothbuild.db.object.type.TestingCatsH.MAP;
import static org.smoothbuild.db.object.type.TestingCatsH.METHOD;
import static org.smoothbuild.db.object.type.TestingCatsH.NOTHING;
import static org.smoothbuild.db.object.type.TestingCatsH.ORDER;
import static org.smoothbuild.db.object.type.TestingCatsH.PARAM_REF;
import static org.smoothbuild.db.object.type.TestingCatsH.PERSON;
import static org.smoothbuild.db.object.type.TestingCatsH.SELECT;
import static org.smoothbuild.db.object.type.TestingCatsH.STRING;
import static org.smoothbuild.db.object.type.TestingCatsH.TYPEH_DB;
import static org.smoothbuild.db.object.type.TestingCatsH.VARIABLE;
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
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.CombineCH;
import org.smoothbuild.db.object.type.val.MethodTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class CatHTest extends TestingContext {
  @Test
  public void verify_all_base_cats_are_tested() {
    assertThat(CatKindH.values())
        .hasLength(19);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Function<CatDb, CatH> factoryCall, String name) {
    assertThat(execute(factoryCall).name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function<CatDb, CatH> factoryCall, String name) {
    assertThat(execute(factoryCall).q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function<CatDb, CatH> factoryCall, String name) {
    assertThat(execute(factoryCall).toString())
        .isEqualTo("Category(`" + name + "`)");
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
  public void isPolytype(Function<CatDb, CatH> factoryCall, boolean expected) {
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
        args(f -> f.tuple(list(f.int_())), false)
        );
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(
      Function<CatDb, CatH> factoryCall,
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
    public void result(Function<CatDb, MethodTH> factoryCall,
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
    public void params(Function<CatDb, MethodTH> factoryCall,
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
    public void elemType(Function<CatDb, TypeH> factoryCall) {
      TypeH elem = execute(factoryCall);
      ArrayT array = typeFactoryH().array(elem);
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
      tupleTH(list());
    }

    @Test
    public void first_item_type_can_be_nothing() {
      tupleTH(list(nothingTH()));
    }

    @Test
    public void first_item_type_can_be_nothing_array() {
      tupleTH(list(arrayTH(nothingTH())));
    }

    @ParameterizedTest
    @MethodSource("tuple_item_cases")
    public void tuple_item(
        Function<CatDb, TupleTH> factoryCall,
        Function<CatDb, NList<Labeled<Type>>> expected) {
      assertThat(execute(factoryCall).items())
          .isEqualTo(execute(expected));
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
  @MethodSource("typeJ_test_data")
  public void typeJ(CatH type, Class<?> expected) {
    assertThat(type.typeJ())
        .isEqualTo(expected);
  }

  public static List<Arguments> typeJ_test_data() {
    return list(
        arguments(ANY, ValH.class),
        arguments(BLOB, BlobH.class),
        arguments(BOOL, BoolH.class),
        arguments(FUNC, FuncH.class),
        arguments(INT, IntH.class),
        arguments(METHOD, MethodH.class),
        arguments(NOTHING, ValH.class),
        arguments(PERSON, TupleH.class),
        arguments(STRING, StringH.class),
        arguments(VARIABLE, ValH.class),

        arguments(ARRAY_ANY, ArrayH.class),
        arguments(ARRAY_BLOB, ArrayH.class),
        arguments(ARRAY_BOOL, ArrayH.class),
        arguments(ARRAY_FUNCTION, ArrayH.class),
        arguments(ARRAY_INT, ArrayH.class),
        arguments(ARRAY_METHOD, ArrayH.class),
        arguments(ARRAY_NOTHING, ArrayH.class),
        arguments(ARRAY_PERSON_TUPLE, ArrayH.class),
        arguments(ARRAY_STR, ArrayH.class),
        arguments(ARRAY_VARIABLE, ArrayH.class),

        arguments(CALL, CallH.class),
        arguments(ORDER, OrderH.class),
        arguments(COMBINE, CombineH.class),
        arguments(IF, IfH.class),
        arguments(INVOKE, InvokeH.class),
        arguments(MAP, MapH.class),
        arguments(PARAM_REF, ParamRefH.class),
        arguments(SELECT, SelectH.class)
    );
  }

  @Nested
  class _eval_type {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeH type) {
      assertThat(TYPEH_DB.call(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCH type, TupleTH expected) {
      assertThat(type.evalT())
          .isEqualTo(expected);
    }

    public static List<Arguments> combine_cases() {
      CatDb db = TYPEH_DB;
      return list(
          arguments(db.combine(db.tuple(list())), db.tuple(list())),
          arguments(db.combine(db.tuple(list(STRING))), db.tuple(list(STRING)))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void invoke(TypeH type) {
      assertThat(TYPEH_DB.invoke(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeH type) {
      assertThat(TYPEH_DB.order(type).evalT())
          .isEqualTo(TYPEH_DB.array(type));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeH type) {
      assertThat(TYPEH_DB.ref(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeH type) {
      assertThat(TYPEH_DB.select(type).evalT())
          .isEqualTo(type);
    }

    public static ImmutableList<CatH> types() {
      return TestingCatsH.CATS_TO_TEST;
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
