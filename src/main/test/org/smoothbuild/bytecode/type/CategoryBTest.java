package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY2_BLOB;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY2_BOOL;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY2_FUNCTION;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY2_INT;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY2_STRING;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY_BLOB;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY_BOOL;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY_FUNC;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY_INT;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.testing.type.TestingCatsB.ARRAY_STRING;
import static org.smoothbuild.testing.type.TestingCatsB.BLOB;
import static org.smoothbuild.testing.type.TestingCatsB.BOOL;
import static org.smoothbuild.testing.type.TestingCatsB.CALL;
import static org.smoothbuild.testing.type.TestingCatsB.CATEGORY_DB;
import static org.smoothbuild.testing.type.TestingCatsB.COMBINE;
import static org.smoothbuild.testing.type.TestingCatsB.FUNC;
import static org.smoothbuild.testing.type.TestingCatsB.IF_FUNC;
import static org.smoothbuild.testing.type.TestingCatsB.INT;
import static org.smoothbuild.testing.type.TestingCatsB.MAP_FUNC;
import static org.smoothbuild.testing.type.TestingCatsB.METHOD;
import static org.smoothbuild.testing.type.TestingCatsB.ORDER;
import static org.smoothbuild.testing.type.TestingCatsB.PERSON;
import static org.smoothbuild.testing.type.TestingCatsB.REF;
import static org.smoothbuild.testing.type.TestingCatsB.SELECT;
import static org.smoothbuild.testing.type.TestingCatsB.STRING;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.IfFuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.MapFuncB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.type.oper.CombineCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.DefFuncCB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.NatFuncCB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestingCatsB;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class CategoryBTest extends TestContext {
  @ParameterizedTest
  @MethodSource("names")
  public void name(Function<CategoryDb, CategoryB> factoryCall, String name) {
    assertThat(execute(factoryCall).name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function<CategoryDb, CategoryB> factoryCall, String name) {
    assertThat(execute(factoryCall).q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function<CategoryDb, CategoryB> factoryCall, String name) {
    var catH = execute(factoryCall);
    assertThat(catH.toString())
        .isEqualTo(name);
  }

  public static List<Arguments> names() {
    return asList(
        args(f -> f.blob(), "Blob"),
        args(f -> f.bool(), "Bool"),
        args(f -> f.int_(), "Int"),
        args(f -> f.string(), "String"),

        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.string()), "[String]"),

        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),

        args(f -> f.defFunc(f.string(), list()), "DEF_FUNC:String()"),
        args(f -> f.defFunc(f.string(), list(f.string())), "DEF_FUNC:String(String)"),
        args(f -> f.ifFunc(f.int_()), "IF_FUNC:Int(Bool,Int,Int)"),
        args(f -> f.mapFunc(f.int_(), f.string()), "MAP_FUNC:[Int]([String],Int(String))"),
        args(f -> f.natFunc(f.string(), list()), "NAT_FUNC:String()"),
        args(f -> f.natFunc(f.string(), list(f.string())), "NAT_FUNC:String(String)"),
        args(f -> f.funcT(f.string(), list()), "String()"),
        args(f -> f.funcT(f.string(), list(f.string())), "String(String)"),

        args(f -> f.tuple(), "{}"),
        args(f -> f.tuple(f.string(), f.bool()), "{String,Bool}"),
        args(f -> f.tuple(f.tuple(f.int_())), "{{Int}}"),

        args(f -> f.call(f.int_()), "Call:Int"),
        args(f -> f.combine(f.tuple(f.string(), f.int_())), "Combine:{String,Int}"),
        args(f -> f.order(f.array(f.string())), "Order:[String]"),
        args(f -> f.ref(f.int_()), "Ref:Int"),
        args(f -> f.select(f.int_()), "Select:Int")
    );
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CategoryDb, FuncTB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).res())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.funcT(f.int_(), list()), f -> f.int_()),
          args(f -> f.funcT(f.blob(), list(f.bool())), f -> f.blob()),
          args(f -> f.funcT(f.blob(), list(f.bool(), f.int_())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CategoryDb, FuncTB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.funcT(f.int_(), list()), f -> f.tuple()),
          args(f -> f.funcT(f.blob(), list(f.bool())), f -> f.tuple(f.bool())),
          args(f -> f.funcT(f.blob(), list(f.bool(), f.int_())), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _def_func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CategoryDb, DefFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().res())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.defFunc(f.funcT(f.int_(), list())), f -> f.int_()),
          args(f -> f.defFunc(f.funcT(f.blob(), list(f.bool()))), f -> f.blob()),
          args(f -> f.defFunc(f.funcT(f.blob(), list(f.bool(), f.int_()))), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CategoryDb, DefFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.defFunc(f.funcT(f.int_(), list())), f -> f.tuple()),
          args(f -> f.defFunc(f.funcT(f.blob(), list(f.bool()))), f -> f.tuple(f.bool())),
          args(f -> f.defFunc(f.funcT(f.blob(), list(f.bool(), f.int_()))), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _method {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CategoryDb, NatFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().res())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.natFunc(f.funcT(f.int_(), list())), f -> f.int_()),
          args(f -> f.natFunc(f.funcT(f.blob(), list(f.bool()))), f -> f.blob()),
          args(f -> f.natFunc(f.funcT(f.blob(), list(f.bool(), f.int_()))), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CategoryDb, NatFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.natFunc(f.funcT(f.int_(), list())), f -> f.tuple()),
          args(f -> f.natFunc(f.funcT(f.blob(), list(f.bool()))), f -> f.tuple(f.bool())),
          args(f -> f.natFunc(f.funcT(f.blob(), list(f.bool(), f.int_()))), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(Function<CategoryDb, TypeB> factoryCall) {
      TypeB elem = execute(factoryCall);
      ArrayTB array = categoryDb().array(elem);
      assertThat(array.elem())
          .isEqualTo(elem);
    }

    public static List<Arguments> elemType_test_data() {
      return asList(
          args(f -> f.blob()),
          args(f -> f.bool()),
          args(f -> f.funcT(f.string(), list())),
          args(f -> f.int_()),
          args(f -> f.string()),
          args(f -> f.tuple(f.int_())),

          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.funcT(f.string(), list()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.string()))
      );
    }
  }

  @Nested
  class _tuple {
    @Test
    public void _without_items_can_be_created() {
      tupleTB();
    }

    @ParameterizedTest
    @MethodSource("tuple_item_cases")
    public void tuple_item(
        Function<CategoryDb, TupleTB> factoryCall,
        Function<CategoryDb, NList<Labeled<TypeB>>> expected) {
      assertThat(execute(factoryCall).items())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> tuple_item_cases() {
      return asList(
          args(f -> f.tuple(), f -> list()),
          args(f -> f.tuple(f.string()), f -> list(f.string())),
          args(f -> f.tuple(f.string(), f.int_()), f -> list(f.string(), f.int_()))
      );
    }
  }

  @ParameterizedTest
  @MethodSource("typeJ_test_data")
  public void typeJ(CategoryB type, Class<?> expected) {
    assertThat(type.typeJ())
        .isEqualTo(expected);
  }

  public static List<Arguments> typeJ_test_data() {
    return list(
        arguments(BLOB, BlobB.class),
        arguments(BOOL, BoolB.class),
        arguments(FUNC, FuncB.class),
        arguments(IF_FUNC, IfFuncB.class),
        arguments(MAP_FUNC, MapFuncB.class),
        arguments(INT, IntB.class),
        arguments(METHOD, NatFuncB.class),
        arguments(PERSON, TupleB.class),
        arguments(STRING, StringB.class),

        arguments(ARRAY_BLOB, ArrayB.class),
        arguments(ARRAY_BOOL, ArrayB.class),
        arguments(ARRAY_FUNC, ArrayB.class),
        arguments(ARRAY_INT, ArrayB.class),
        arguments(ARRAY_PERSON_TUPLE, ArrayB.class),
        arguments(ARRAY_STRING, ArrayB.class),

        arguments(CALL, CallB.class),
        arguments(ORDER, OrderB.class),
        arguments(COMBINE, CombineB.class),
        arguments(REF, RefB.class),
        arguments(SELECT, SelectB.class)
    );
  }

  @Nested
  class _eval_type {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeB type) {
      assertThat(CATEGORY_DB.call(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCB type, TupleTB expected) {
      assertThat(type.evalT())
          .isEqualTo(expected);
    }

    public static List<Arguments> combine_cases() {
      CategoryDb db = CATEGORY_DB;
      return list(
          arguments(db.combine(db.tuple()), db.tuple()),
          arguments(db.combine(db.tuple(STRING)), db.tuple(STRING))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeB type) {
      var array = CATEGORY_DB.array(type);
      assertThat(CATEGORY_DB.order(array).evalT())
          .isEqualTo(array);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeB type) {
      assertThat(CATEGORY_DB.ref(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeB type) {
      assertThat(CATEGORY_DB.select(type).evalT())
          .isEqualTo(type);
    }

    public static ImmutableList<CategoryB> types() {
      return TestingCatsB.CATS_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(FUNC, FUNC);
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(STRING, STRING);
    tester.addEqualityGroup(PERSON, PERSON);

    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_FUNC, ARRAY_FUNC);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_STRING, ARRAY_STRING);
    tester.addEqualityGroup(ARRAY_PERSON_TUPLE, ARRAY_PERSON_TUPLE);

    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_FUNCTION, ARRAY2_FUNCTION);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_STRING, ARRAY2_STRING);
    tester.addEqualityGroup(ARRAY2_PERSON_TUPLE, ARRAY2_PERSON_TUPLE);

    tester.addEqualityGroup(CALL, CALL);
    tester.addEqualityGroup(COMBINE, COMBINE);
    tester.addEqualityGroup(IF_FUNC, IF_FUNC);
    tester.addEqualityGroup(MAP_FUNC, MAP_FUNC);
    tester.addEqualityGroup(ORDER, ORDER);
    tester.addEqualityGroup(REF, REF);
    tester.addEqualityGroup(SELECT, SELECT);

    tester.testEquals();
  }

  private <R> R execute(Function<CategoryDb, R> f) {
    return f.apply(categoryDb());
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function<CategoryDb, R> factoryCall1,
      Function<CategoryDb, R> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<CategoryDb, R> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<CategoryDb, R> factoryCall) {
    return arguments(factoryCall);
  }
}
