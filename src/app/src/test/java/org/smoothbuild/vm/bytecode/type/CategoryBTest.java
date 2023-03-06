package org.smoothbuild.vm.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestingCatsB;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.FuncB;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.ClosureCB;
import org.smoothbuild.vm.bytecode.type.value.ExprFuncCB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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

        args(f -> f.closure(list(), f.string()), "CLOSURE:()->String"),
        args(f -> f.closure(list(f.string()), f.string()), "CLOSURE:(String)->String"),
        args(f -> f.exprFunc(list(), f.string()), "EXPR_FUNC:()->String"),
        args(f -> f.exprFunc(list(f.string()), f.string()), "EXPR_FUNC:(String)->String"),
        args(f -> f.funcT(list(), f.string()), "()->String"),
        args(f -> f.funcT(list(f.string()), f.string()), "(String)->String"),
        args(f -> f.ifFunc(f.int_()), "IF_FUNC:(Bool,Int,Int)->Int"),
        args(f -> f.mapFunc(f.int_(), f.string()), "MAP_FUNC:([String],(String)->Int)->[Int]"),
        args(f -> f.nativeFunc(list(), f.string()), "NATIVE_FUNC:()->String"),
        args(f -> f.nativeFunc(list(f.string()), f.string()), "NATIVE_FUNC:(String)->String"),

        args(f -> f.tuple(), "{}"),
        args(f -> f.tuple(f.string(), f.bool()), "{String,Bool}"),
        args(f -> f.tuple(f.tuple(f.int_())), "{{Int}}"),

        args(f -> f.call(f.int_()), "CALL:Int"),
        args(f -> f.closurize(f.funcT(list(f.string()), f.int_())), "CLOSURIZE:(String)->Int"),
        args(f -> f.combine(f.tuple(f.string(), f.int_())), "COMBINE:{String,Int}"),
        args(f -> f.order(f.array(f.string())), "ORDER:[String]"),
        args(f -> f.pick(f.int_()), "PICK:Int"),
        args(f -> f.reference(f.int_()), "REFERENCE:Int"),
        args(f -> f.select(f.int_()), "SELECT:Int")
    );
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CategoryDb, FuncTB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).result())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.funcT(list(), f.int_()), f -> f.int_()),
          args(f -> f.funcT(list(f.bool()), f.blob()), f -> f.blob()),
          args(f -> f.funcT(list(f.bool(), f.int_()), f.blob()), f -> f.blob())
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
          args(f -> f.funcT(list(), f.int_()), f -> f.tuple()),
          args(f -> f.funcT(list(f.bool()), f.blob()), f -> f.tuple(f.bool())),
          args(f -> f.funcT(list(f.bool(), f.int_()), f.blob()), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _closure {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function<CategoryDb, ClosureCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().result())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.closure(f.funcT(list(), f.int_())), f -> f.int_()),
          args(f -> f.closure(f.funcT(list(f.bool()), f.blob())), f -> f.blob()),
          args(f -> f.closure(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CategoryDb, ClosureCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.closure(f.funcT(list(), f.int_())), f -> f.tuple()),
          args(f -> f.closure(f.funcT(list(f.bool()), f.blob())), f -> f.tuple(f.bool())),
          args(f -> f.closure(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _expression_func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function<CategoryDb, ExprFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().result())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.exprFunc(f.funcT(list(), f.int_())), f -> f.int_()),
          args(f -> f.exprFunc(f.funcT(list(f.bool()), f.blob())), f -> f.blob()),
          args(f -> f.exprFunc(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(
        Function<CategoryDb, ExprFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.exprFunc(f.funcT(list(), f.int_())), f -> f.tuple()),
          args(f -> f.exprFunc(f.funcT(list(f.bool()), f.blob())), f -> f.tuple(f.bool())),
          args(f -> f.exprFunc(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _native_func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function<CategoryDb, NativeFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().result())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.nativeFunc(f.funcT(list(), f.int_())), f -> f.int_()),
          args(f -> f.nativeFunc(f.funcT(list(f.bool()), f.blob())), f -> f.blob()),
          args(f -> f.nativeFunc(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.blob())
      );
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(Function<CategoryDb, NativeFuncCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().params())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> params_cases() {
      return asList(
          args(f -> f.nativeFunc(f.funcT(list(), f.int_())), f -> f.tuple()),
          args(f -> f.nativeFunc(f.funcT(list(f.bool()), f.blob())), f -> f.tuple(f.bool())),
          args(f -> f.nativeFunc(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.tuple(f.bool(), f.int_()))
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
          args(f -> f.funcT(list(), f.string())),
          args(f -> f.int_()),
          args(f -> f.string()),
          args(f -> f.tuple(f.int_())),

          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.funcT(list(), f.string()))),
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
    @MethodSource("tuple_items_cases")
    public void tuple_items(
        Function<CategoryDb, TupleTB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).elements())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> tuple_items_cases() {
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
    TestContext CONTEXT = new TestContext();
    return list(
        arguments(CONTEXT.blobTB(), BlobB.class),
        arguments(CONTEXT.boolTB(), BoolB.class),
        arguments(CONTEXT.funcTB(CONTEXT.boolTB(), CONTEXT.blobTB()), FuncB.class),
        arguments(CONTEXT.ifFuncCB(), IfFuncB.class),
        arguments(CONTEXT.mapFuncCB(), MapFuncB.class),
        arguments(CONTEXT.intTB(), IntB.class),
        arguments(
            CONTEXT.nativeFuncCB(CONTEXT.boolTB(), CONTEXT.blobTB()), NativeFuncB.class),
        arguments(CONTEXT.personTB(), TupleB.class),
        arguments(CONTEXT.stringTB(), StringB.class),

        arguments(CONTEXT.arrayTB(CONTEXT.blobTB()), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.boolTB()), ArrayB.class),
        arguments(
            CONTEXT.arrayTB(CONTEXT.funcTB(CONTEXT.boolTB(), CONTEXT.blobTB())), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.intTB()), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.personTB()), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.stringTB()), ArrayB.class),

        arguments(CONTEXT.callCB(), CallB.class),
        arguments(CONTEXT.orderCB(), OrderB.class),
        arguments(
            CONTEXT.combineCB(CONTEXT.intTB(), CONTEXT.stringTB()), CombineB.class),
        arguments(CONTEXT.pickCB(), PickB.class),
        arguments(CONTEXT.referenceCB(CONTEXT.intTB()), ReferenceB.class),
        arguments(CONTEXT.selectCB(CONTEXT.intTB()), SelectB.class)
    );
  }

  @Nested
  class _oper {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeB type) {
      assertThat(categoryDb().call(type).evaluationT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCB type, TupleTB expected) {
      assertThat(type.evaluationT())
          .isEqualTo(expected);
    }

    public static List<Arguments> combine_cases() {
      TestContext CONTEXT = new TestContext();
      CategoryDb db = CONTEXT.categoryDb();
      return list(
          arguments(db.combine(db.tuple()), db.tuple()),
          arguments(db.combine(db.tuple(CONTEXT.stringTB())), db.tuple(
              CONTEXT.stringTB()))
      );
    }

    @ParameterizedTest
    @MethodSource("types")
    public void closurize(TypeB typeB) {
      assertThat(closurizeCB(funcTB(typeB)).evaluationT())
          .isEqualTo(funcTB(typeB));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeB type) {
      var array = arrayTB(type);
      assertThat(orderCB(type).evaluationT())
          .isEqualTo(array);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void pick(TypeB type) {
      assertThat(pickCB(type).evaluationT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void reference(TypeB type) {
      assertThat(referenceCB(type).evaluationT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeB type) {
      assertThat(selectCB(type).evaluationT())
          .isEqualTo(type);
    }

    public static ImmutableList<CategoryB> types() {
      return TestingCatsB.CATS_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() {
    var tester = new EqualsTester();
    tester.addEqualityGroup(blobTB(), blobTB());
    tester.addEqualityGroup(boolTB(), boolTB());
    tester.addEqualityGroup(
        funcTB(boolTB(), blobTB()),
        funcTB(boolTB(), blobTB()));
    tester.addEqualityGroup(intTB(), intTB());
    tester.addEqualityGroup(stringTB(), stringTB());
    tester.addEqualityGroup(personTB(), personTB());

    tester.addEqualityGroup(arrayTB(blobTB()), arrayTB(blobTB()));
    tester.addEqualityGroup(arrayTB(boolTB()), arrayTB(boolTB()));
    tester.addEqualityGroup(
        arrayTB(funcTB(boolTB(), blobTB())),
        arrayTB(funcTB(boolTB(), blobTB())));
    tester.addEqualityGroup(arrayTB(intTB()), arrayTB(intTB()));
    tester.addEqualityGroup(arrayTB(stringTB()), arrayTB(stringTB()));
    tester.addEqualityGroup(arrayTB(personTB()), arrayTB(personTB()));

    tester.addEqualityGroup(arrayTB(arrayTB(blobTB())), arrayTB(arrayTB(blobTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(boolTB())), arrayTB(arrayTB(boolTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(funcTB(intTB()))), arrayTB(arrayTB(funcTB(intTB()))));
    tester.addEqualityGroup(arrayTB(arrayTB(intTB())), arrayTB(arrayTB(intTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(stringTB())), arrayTB(arrayTB(stringTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(tupleTB(animalTB()))), arrayTB(arrayTB(tupleTB(animalTB()))));

    tester.addEqualityGroup(callCB(), callCB());
    tester.addEqualityGroup(
        combineCB(intTB(), stringTB()),
        combineCB(intTB(), stringTB()));
    tester.addEqualityGroup(ifFuncCB(), ifFuncCB());
    tester.addEqualityGroup(mapFuncCB(), mapFuncCB());
    tester.addEqualityGroup(orderCB(), orderCB());
    tester.addEqualityGroup(pickCB(), pickCB());
    tester.addEqualityGroup(referenceCB(intTB()), referenceCB(intTB()));
    tester.addEqualityGroup(selectCB(intTB()), selectCB(intTB()));

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
