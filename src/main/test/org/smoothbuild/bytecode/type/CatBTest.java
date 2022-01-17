package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.bytecode.type.TestingCatsB.ANY;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_ANY;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_BLOB;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_BOOL;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_CLOSED_VARIABLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_FUNCTION;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_INT;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_NOTHING;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_OPEN_VARIABLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY2_STR;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_ANY;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_BLOB;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_BOOL;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_CLOSED_VARIABLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_FUNCTION;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_INT;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_METHOD;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_NOTHING;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_OPEN_VARIABLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ARRAY_STR;
import static org.smoothbuild.bytecode.type.TestingCatsB.BLOB;
import static org.smoothbuild.bytecode.type.TestingCatsB.BOOL;
import static org.smoothbuild.bytecode.type.TestingCatsB.CALL;
import static org.smoothbuild.bytecode.type.TestingCatsB.CAT_DB;
import static org.smoothbuild.bytecode.type.TestingCatsB.CLOSED_VARIABLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.COMBINE;
import static org.smoothbuild.bytecode.type.TestingCatsB.FUNC;
import static org.smoothbuild.bytecode.type.TestingCatsB.IF;
import static org.smoothbuild.bytecode.type.TestingCatsB.INT;
import static org.smoothbuild.bytecode.type.TestingCatsB.INVOKE;
import static org.smoothbuild.bytecode.type.TestingCatsB.MAP;
import static org.smoothbuild.bytecode.type.TestingCatsB.METHOD;
import static org.smoothbuild.bytecode.type.TestingCatsB.NOTHING;
import static org.smoothbuild.bytecode.type.TestingCatsB.OPEN_VARIABLE;
import static org.smoothbuild.bytecode.type.TestingCatsB.ORDER;
import static org.smoothbuild.bytecode.type.TestingCatsB.PARAM_REF;
import static org.smoothbuild.bytecode.type.TestingCatsB.PERSON;
import static org.smoothbuild.bytecode.type.TestingCatsB.SELECT;
import static org.smoothbuild.bytecode.type.TestingCatsB.STRING;
import static org.smoothbuild.bytecode.type.TestingCatsB.array;
import static org.smoothbuild.bytecode.type.TestingCatsB.cVar;
import static org.smoothbuild.bytecode.type.TestingCatsB.func;
import static org.smoothbuild.bytecode.type.TestingCatsB.oVar;
import static org.smoothbuild.bytecode.type.TestingCatsB.tuple;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
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
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class CatBTest extends TestingContext {
  @Test
  public void verify_all_base_cats_are_tested() {
    assertThat(CatKindB.values())
        .hasLength(20);
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
        args(f -> f.oVar("A"), "A"),
        args(f -> f.cVar("A"), "A"),

        args(f -> f.array(f.oVar("A")), "[A]"),
        args(f -> f.array(f.cVar("A")), "[A]"),
        args(f -> f.array(f.any()), "[Any]"),
        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.nothing()), "[Nothing]"),
        args(f -> f.array(f.string()), "[String]"),

        args(f -> f.array(f.array(f.oVar("A"))), "[[A]]"),
        args(f -> f.array(f.array(f.cVar("A"))), "[[A]]"),
        args(f -> f.array(f.array(f.any())), "[[Any]]"),
        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.nothing())), "[[Nothing]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),

        args(f -> f.func(f.oVar("A"), list(f.array(f.oVar("A")))), "A([A])"),
        args(f -> f.func(f.cVar("A"), list(f.array(f.cVar("A")))), "A([A])"),
        args(f -> f.func(f.string(), list(f.array(f.oVar("A")))), "String([A])"),
        args(f -> f.func(f.string(), list(f.array(f.cVar("A")))), "String([A])"),
        args(f -> f.func(f.oVar("A"), list(f.oVar("A"))), "A(A)"),
        args(f -> f.func(f.cVar("A"), list(f.cVar("A"))), "A(A)"),
        args(f -> f.func(f.string(), list()), "String()"),
        args(f -> f.func(f.string(), list(f.string())), "String(String)"),

        args(f -> f.method(f.oVar("A"), list(f.array(f.oVar("A")))), "_A([A])"),
        args(f -> f.method(f.cVar("A"), list(f.array(f.cVar("A")))), "_A([A])"),
        args(f -> f.method(f.string(), list(f.array(f.oVar("A")))), "_String([A])"),
        args(f -> f.method(f.string(), list(f.array(f.cVar("A")))), "_String([A])"),
        args(f -> f.method(f.oVar("A"), list(f.oVar("A"))), "_A(A)"),
        args(f -> f.method(f.cVar("A"), list(f.cVar("A"))), "_A(A)"),
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
        args(f -> f.paramRef(f.int_()), "ParamRef:Int"),
        args(f -> f.select(f.int_()), "Select:Int")
    );
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Function<CatDb, TypeB> factoryCall, boolean expected) {
    assertThat(execute(factoryCall).isPolytype())
        .isEqualTo(expected);
  }

  public static List<Arguments> isPolytype_test_data() {
    return asList(
        args(f -> f.oVar("A"), true),
        args(f -> f.array(f.oVar("A")), true),
        args(f -> f.array(f.array(f.oVar("A"))), true),

        args(f -> f.func(f.oVar("A"), list()), true),
        args(f -> f.func(f.func(f.oVar("A"), list()), list()), true),
        args(f -> f.func(f.func(f.func(f.oVar("A"), list()), list()), list()), true),

        args(f -> f.func(f.bool(), list(f.oVar("A"))), true),
        args(f -> f.func(f.bool(), list(f.func(f.oVar("A"), list()))), true),
        args(f -> f.func(f.bool(), list(f.func(f.func(f.oVar("A"), list()), list()))), true),

        args(f -> f.func(f.bool(), list(f.func(f.blob(), list(f.oVar("A"))))), true),

        args(f -> f.method(f.oVar("A"), list()), true),
        args(f -> f.method(f.func(f.oVar("A"), list()), list()), true),
        args(f -> f.method(f.func(f.func(f.oVar("A"), list()), list()), list()), true),

        args(f -> f.method(f.bool(), list(f.oVar("A"))), true),
        args(f -> f.method(f.bool(), list(f.method(f.oVar("A"), list()))), true),
        args(f -> f.method(f.bool(), list(f.method(f.func(f.oVar("A"), list()), list()))), true),

        args(f -> f.method(f.bool(), list(f.method(f.blob(), list(f.oVar("A"))))), true),


        args(f -> f.cVar("A"), true),
        args(f -> f.array(f.cVar("A")), true),
        args(f -> f.array(f.array(f.cVar("A"))), true),

        args(f -> f.func(f.cVar("A"), list()), true),
        args(f -> f.func(f.func(f.cVar("A"), list()), list()), true),
        args(f -> f.func(f.func(f.func(f.cVar("A"), list()), list()), list()), true),

        args(f -> f.func(f.bool(), list(f.cVar("A"))), true),
        args(f -> f.func(f.bool(), list(f.func(f.cVar("A"), list()))), true),
        args(f -> f.func(f.bool(), list(f.func(f.func(f.cVar("A"), list()), list()))), true),

        args(f -> f.func(f.bool(), list(f.func(f.blob(), list(f.cVar("A"))))), true),

        args(f -> f.method(f.cVar("A"), list()), true),
        args(f -> f.method(f.func(f.cVar("A"), list()), list()), true),
        args(f -> f.method(f.func(f.func(f.cVar("A"), list()), list()), list()), true),

        args(f -> f.method(f.bool(), list(f.cVar("A"))), true),
        args(f -> f.method(f.bool(), list(f.method(f.cVar("A"), list()))), true),
        args(f -> f.method(f.bool(), list(f.method(f.func(f.cVar("A"), list()), list()))), true),

        args(f -> f.method(f.bool(), list(f.method(f.blob(), list(f.cVar("A"))))), true),


        args(f -> f.any(), false),
        args(f -> f.blob(), false),
        args(f -> f.bool(), false),
        args(f -> f.int_(), false),
        args(f -> f.nothing(), false),
        args(f -> f.string(), false),
        args(f -> f.tuple(list()), false),
        args(f -> f.tuple(list(f.int_())), false),
        args(f -> f.tuple(list(f.oVar("A"))), true),
        args(f -> f.tuple(list(f.cVar("A"))), true),
        args(f -> f.tuple(list(f.tuple(list(f.oVar("A"))))), true),
        args(f -> f.tuple(list(f.tuple(list(f.cVar("A"))))), true)
        );
  }

  @ParameterizedTest
  @MethodSource("hasOpenVars_test_data")
  public void hasOpenVars(TypeB type, boolean expected) {
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

        arguments(array(INT), false),
        arguments(array(oVar("A")), true),
        arguments(array(cVar("A")), false),

        arguments(tuple(list(INT)), false),
        arguments(tuple(list(oVar("A"))), true),
        arguments(tuple(list(cVar("A"))), false),

        arguments(func(BLOB, list(BOOL)), false),
        arguments(func(oVar("A"), list(BOOL)), true),
        arguments(func(cVar("A"), list(BOOL)), false),
        arguments(func(BLOB, list(oVar("A"))), true),
        arguments(func(BLOB, list(cVar("A"))), false)
    );
  }

  @ParameterizedTest
  @MethodSource("hasClosedVars_test_data")
  public void hasOpenClosed(TypeB type, boolean expected) {
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

        arguments(array(INT), false),
        arguments(array(oVar("A")), false),
        arguments(array(cVar("A")), true),

        arguments(tuple(list(INT)), false),
        arguments(tuple(list(oVar("A"))), false),
        arguments(tuple(list(cVar("A"))), true),

        arguments(func(BLOB, list(BOOL)), false),
        arguments(func(oVar("A"), list(BOOL)), false),
        arguments(func(cVar("A"), list(BOOL)), true),
        arguments(func(BLOB, list(oVar("A"))), false),
        arguments(func(BLOB, list(cVar("A"))), true)
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
  class _open_var {
    @Test
    public void name() {
      assertThat(oVarTB("A").name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> oVarTB("a"))
          .throwsException(new IllegalArgumentException("Illegal type var name 'a'."));
    }
  }

  @Nested
  class _closed_var {
    @Test
    public void name() {
      assertThat(cVarTB("A").name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> cVarTB("a"))
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
          args(f -> f.oVar("A")),
          args(f -> f.cVar("A")),

          args(f -> f.array(f.any())),
          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.func(f.string(), list()))),
          args(f -> f.array(f.method(f.string(), list()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.nothing())),
          args(f -> f.array(f.string())),
          args(f -> f.array(f.oVar("A"))),
          args(f -> f.array(f.cVar("A")))
      );
    }
  }

  @Nested
  class _tuple {
    @Test
    public void _without_items_can_be_created() {
      tupleTB();
    }

    @Test
    public void first_item_type_can_be_nothing() {
      tupleTB(nothingTB());
    }

    @Test
    public void first_item_type_can_be_nothing_array() {
      tupleTB(arrayTB(nothingTB()));
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
          args(f -> f.tuple(list(f.oVar("A"))), f -> list(f.oVar("A"))),
          args(f -> f.tuple(list(f.cVar("A"))), f -> list(f.cVar("A"))),
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
        arguments(OPEN_VARIABLE, ValB.class),
        arguments(CLOSED_VARIABLE, ValB.class),

        arguments(ARRAY_ANY, ArrayB.class),
        arguments(ARRAY_BLOB, ArrayB.class),
        arguments(ARRAY_BOOL, ArrayB.class),
        arguments(ARRAY_FUNCTION, ArrayB.class),
        arguments(ARRAY_INT, ArrayB.class),
        arguments(ARRAY_METHOD, ArrayB.class),
        arguments(ARRAY_NOTHING, ArrayB.class),
        arguments(ARRAY_PERSON_TUPLE, ArrayB.class),
        arguments(ARRAY_STR, ArrayB.class),
        arguments(ARRAY_OPEN_VARIABLE, ArrayB.class),
        arguments(ARRAY_CLOSED_VARIABLE, ArrayB.class),

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
      assertThat(CAT_DB.paramRef(type).evalT())
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
    tester.addEqualityGroup(OPEN_VARIABLE, OPEN_VARIABLE);
    tester.addEqualityGroup(CLOSED_VARIABLE, CLOSED_VARIABLE);

    tester.addEqualityGroup(ARRAY_ANY, ARRAY_ANY);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_FUNCTION, ARRAY_FUNCTION);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_NOTHING, ARRAY_NOTHING);
    tester.addEqualityGroup(ARRAY_STR, ARRAY_STR);
    tester.addEqualityGroup(ARRAY_PERSON_TUPLE, ARRAY_PERSON_TUPLE);
    tester.addEqualityGroup(ARRAY_OPEN_VARIABLE, ARRAY_OPEN_VARIABLE);
    tester.addEqualityGroup(ARRAY_CLOSED_VARIABLE, ARRAY_CLOSED_VARIABLE);

    tester.addEqualityGroup(ARRAY2_ANY, ARRAY2_ANY);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_FUNCTION, ARRAY2_FUNCTION);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_NOTHING, ARRAY2_NOTHING);
    tester.addEqualityGroup(ARRAY2_STR, ARRAY2_STR);
    tester.addEqualityGroup(ARRAY2_PERSON_TUPLE, ARRAY2_PERSON_TUPLE);
    tester.addEqualityGroup(ARRAY2_OPEN_VARIABLE, ARRAY2_OPEN_VARIABLE);
    tester.addEqualityGroup(ARRAY2_CLOSED_VARIABLE, ARRAY2_CLOSED_VARIABLE);

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
