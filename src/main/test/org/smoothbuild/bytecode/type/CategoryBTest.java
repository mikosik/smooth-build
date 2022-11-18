package org.smoothbuild.bytecode.type;

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
import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.BoolB;
import org.smoothbuild.bytecode.expr.inst.FuncB;
import org.smoothbuild.bytecode.expr.inst.IfFuncB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.MapFuncB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.bytecode.type.inst.ClosureCB;
import org.smoothbuild.bytecode.type.inst.FuncTB;
import org.smoothbuild.bytecode.type.inst.NatFuncCB;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.bytecode.type.oper.CombineCB;
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

        args(f -> f.closure(f.string(), list()), "CLOSURE:()->String"),
        args(f -> f.closure(f.string(), list(f.string())), "CLOSURE:(String)->String"),
        args(f -> f.ifFunc(f.int_()), "IF_FUNC:(Bool,Int,Int)->Int"),
        args(f -> f.mapFunc(f.int_(), f.string()), "MAP_FUNC:([String],(String)->Int)->[Int]"),
        args(f -> f.natFunc(f.string(), list()), "NAT_FUNC:()->String"),
        args(f -> f.natFunc(f.string(), list(f.string())), "NAT_FUNC:(String)->String"),
        args(f -> f.funcT(f.string(), list()), "()->String"),
        args(f -> f.funcT(f.string(), list(f.string())), "(String)->String"),

        args(f -> f.tuple(), "{}"),
        args(f -> f.tuple(f.string(), f.bool()), "{String,Bool}"),
        args(f -> f.tuple(f.tuple(f.int_())), "{{Int}}"),

        args(f -> f.call(f.int_()), "CALL:Int"),
        args(f -> f.closurize(f.funcT(f.int_(), list(f.string()))), "CLOSURIZE:(String)->Int"),
        args(f -> f.combine(f.tuple(f.string(), f.int_())), "COMBINE:{String,Int}"),
        args(f -> f.order(f.array(f.string())), "ORDER:[String]"),
        args(f -> f.pick(f.int_()), "PICK:Int"),
        args(f -> f.ref(f.int_()), "REF:Int"),
        args(f -> f.select(f.int_()), "SELECT:Int")
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
  class _closure {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(Function<CategoryDb, ClosureCB> factoryCall,
        Function<CategoryDb, List<TypeB>> expected) {
      assertThat(execute(factoryCall).type().res())
          .isEqualTo(execute(expected));
    }

    public static List<Arguments> result_cases() {
      return asList(
          args(f -> f.closure(f.funcT(f.int_(), list())), f -> f.int_()),
          args(f -> f.closure(f.funcT(f.blob(), list(f.bool()))), f -> f.blob()),
          args(f -> f.closure(f.funcT(f.blob(), list(f.bool(), f.int_()))), f -> f.blob())
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
          args(f -> f.closure(f.funcT(f.int_(), list())), f -> f.tuple()),
          args(f -> f.closure(f.funcT(f.blob(), list(f.bool()))), f -> f.tuple(f.bool())),
          args(f -> f.closure(f.funcT(f.blob(), list(f.bool(), f.int_()))), f -> f.tuple(f.bool(), f.int_()))
      );
    }
  }

  @Nested
  class _nat_func {
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
    TestContext CONTEXT = new TestContext();
    return list(
        arguments(CONTEXT.blobTB(), BlobB.class),
        arguments(CONTEXT.boolTB(), BoolB.class),
        arguments(CONTEXT.funcTB(CONTEXT.blobTB(), CONTEXT.boolTB()), FuncB.class),
        arguments(CONTEXT.ifFuncCB(), IfFuncB.class),
        arguments(CONTEXT.mapFuncCB(), MapFuncB.class),
        arguments(CONTEXT.intTB(), IntB.class),
        arguments(
            CONTEXT.natFuncCB(CONTEXT.blobTB(), CONTEXT.boolTB()), NatFuncB.class),
        arguments(CONTEXT.personTB(), TupleB.class),
        arguments(CONTEXT.stringTB(), StringB.class),

        arguments(CONTEXT.arrayTB(CONTEXT.blobTB()), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.boolTB()), ArrayB.class),
        arguments(
            CONTEXT.arrayTB(CONTEXT.funcTB(CONTEXT.blobTB(), CONTEXT.boolTB())), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.intTB()), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.personTB()), ArrayB.class),
        arguments(CONTEXT.arrayTB(CONTEXT.stringTB()), ArrayB.class),

        arguments(CONTEXT.callCB(), CallB.class),
        arguments(CONTEXT.orderCB(), OrderB.class),
        arguments(
            CONTEXT.combineCB(CONTEXT.intTB(), CONTEXT.stringTB()), CombineB.class),
        arguments(CONTEXT.pickCB(), PickB.class),
        arguments(CONTEXT.refCB(CONTEXT.intTB()), RefB.class),
        arguments(CONTEXT.selectCB(CONTEXT.intTB()), SelectB.class)
    );
  }

  @Nested
  class _oper {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeB type) {
      assertThat(categoryDb().call(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCB type, TupleTB expected) {
      assertThat(type.evalT())
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
    public void order(TypeB type) {
      var array = arrayTB(type);
      assertThat(orderCB(type).evalT())
          .isEqualTo(array);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void pick(TypeB type) {
      assertThat(pickCB(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(TypeB type) {
      assertThat(refCB(type).evalT())
          .isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeB type) {
      assertThat(selectCB(type).evalT())
          .isEqualTo(type);
    }

    public static ImmutableList<CategoryB> types() {
      return TestingCatsB.CATS_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() {
    TestContext CONTEXT = new TestContext();
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(blobTB(), blobTB());
    tester.addEqualityGroup(boolTB(), boolTB());
    tester.addEqualityGroup(
        funcTB(blobTB(), boolTB()),
        funcTB(blobTB(), boolTB()));
    tester.addEqualityGroup(intTB(), intTB());
    tester.addEqualityGroup(stringTB(), stringTB());
    tester.addEqualityGroup(personTB(), personTB());

    tester.addEqualityGroup(arrayTB(blobTB()), arrayTB(blobTB()));
    tester.addEqualityGroup(arrayTB(boolTB()), arrayTB(boolTB()));
    tester.addEqualityGroup(
        arrayTB(funcTB(blobTB(), boolTB())),
        arrayTB(funcTB(blobTB(), boolTB())));
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
    tester.addEqualityGroup(refCB(intTB()), refCB(intTB()));
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
