package org.smoothbuild.virtualmachine.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.FuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.MapFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.testing.TestingCategoryB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class CategoryBTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("names")
  public void name(Function1<CategoryDb, CategoryB, BytecodeException> factoryCall, String name)
      throws Exception {
    assertThat(execute(factoryCall).name()).isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(
      Function1<CategoryDb, CategoryB, BytecodeException> factoryCall, String name)
      throws Exception {
    assertThat(execute(factoryCall).q()).isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(
      Function1<CategoryDb, CategoryB, BytecodeException> factoryCall, String name)
      throws Exception {
    var categoryB = execute(factoryCall);
    assertThat(categoryB.toString()).isEqualTo(name);
  }

  public static java.util.List<Arguments> names() {
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
        args(f -> f.lambda(list(), f.string()), "LAMBDA"),
        args(f -> f.lambda(list(f.string()), f.string()), "LAMBDA"),
        args(f -> f.funcT(list(), f.string()), "()->String"),
        args(f -> f.funcT(list(f.string()), f.string()), "(String)->String"),
        args(f -> f.ifFunc(f.int_()), "IF"),
        args(f -> f.mapFunc(f.int_(), f.string()), "MAP"),
        args(f -> f.nativeFunc(list(), f.string()), "NATIVE_FUNC"),
        args(f -> f.nativeFunc(list(f.string()), f.string()), "NATIVE_FUNC"),
        args(f -> f.tuple(), "{}"),
        args(f -> f.tuple(f.string(), f.bool()), "{String,Bool}"),
        args(f -> f.tuple(f.tuple(f.int_())), "{{Int}}"),
        args(f -> f.call(f.int_()), "CALL"),
        args(f -> f.combine(f.tuple(f.string(), f.int_())), "COMBINE"),
        args(f -> f.order(f.array(f.string())), "ORDER"),
        args(f -> f.pick(f.int_()), "PICK"),
        args(f -> f.select(f.int_()), "SELECT"),
        args(f -> f.reference(f.int_()), "REFERENCE"));
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function1<CategoryDb, FuncTB, BytecodeException> factoryCall,
        Function1<CategoryDb, java.util.List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).result()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> result_cases() {
      return asList(
          args(f -> f.funcT(list(), f.int_()), f -> f.int_()),
          args(f -> f.funcT(list(f.bool()), f.blob()), f -> f.blob()),
          args(f -> f.funcT(list(f.bool(), f.int_()), f.blob()), f -> f.blob()));
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(
        Function1<CategoryDb, FuncTB, BytecodeException> factoryCall,
        Function1<CategoryDb, java.util.List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).params()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> params_cases() {
      return asList(
          args(f -> f.funcT(list(), f.int_()), f -> f.tuple()),
          args(f -> f.funcT(list(f.bool()), f.blob()), f -> f.tuple(f.bool())),
          args(f -> f.funcT(list(f.bool(), f.int_()), f.blob()), f -> f.tuple(f.bool(), f.int_())));
    }
  }

  @Nested
  class _lambda {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function1<CategoryDb, LambdaCB, BytecodeException> factoryCall,
        Function1<CategoryDb, java.util.List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).type().result()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> result_cases() {
      return asList(
          args(f -> f.lambda(f.funcT(list(), f.int_())), f -> f.int_()),
          args(f -> f.lambda(f.funcT(list(f.bool()), f.blob())), f -> f.blob()),
          args(f -> f.lambda(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.blob()));
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(
        Function1<CategoryDb, LambdaCB, BytecodeException> factoryCall,
        Function1<CategoryDb, java.util.List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).type().params()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> params_cases() {
      return asList(
          args(f -> f.lambda(f.funcT(list(), f.int_())), f -> f.tuple()),
          args(f -> f.lambda(f.funcT(list(f.bool()), f.blob())), f -> f.tuple(f.bool())),
          args(
              f -> f.lambda(f.funcT(list(f.bool(), f.int_()), f.blob())),
              f -> f.tuple(f.bool(), f.int_())));
    }
  }

  @Nested
  class _native_func {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function1<CategoryDb, NativeFuncCB, BytecodeException> factoryCall,
        Function1<CategoryDb, java.util.List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).type().result()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> result_cases() {
      return asList(
          args(f -> f.nativeFunc(f.funcT(list(), f.int_())), f -> f.int_()),
          args(f -> f.nativeFunc(f.funcT(list(f.bool()), f.blob())), f -> f.blob()),
          args(f -> f.nativeFunc(f.funcT(list(f.bool(), f.int_()), f.blob())), f -> f.blob()));
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(
        Function1<CategoryDb, NativeFuncCB, BytecodeException> factoryCall,
        Function1<CategoryDb, java.util.List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).type().params()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> params_cases() {
      return asList(
          args(f -> f.nativeFunc(f.funcT(list(), f.int_())), f -> f.tuple()),
          args(f -> f.nativeFunc(f.funcT(list(f.bool()), f.blob())), f -> f.tuple(f.bool())),
          args(
              f -> f.nativeFunc(f.funcT(list(f.bool(), f.int_()), f.blob())),
              f -> f.tuple(f.bool(), f.int_())));
    }
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(Function1<CategoryDb, TypeB, BytecodeException> factoryCall)
        throws Exception {
      TypeB elem = execute(factoryCall);
      ArrayTB array = categoryDb().array(elem);
      assertThat(array.elem()).isEqualTo(elem);
    }

    public static java.util.List<Arguments> elemType_test_data() {
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
          args(f -> f.array(f.string())));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void _without_items_can_be_created() throws Exception {
      tupleTB();
    }

    @ParameterizedTest
    @MethodSource("tuple_items_cases")
    public void tuple_items(
        Function1<CategoryDb, TupleTB, BytecodeException> factoryCall,
        Function1<CategoryDb, List<TypeB>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).elements()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> tuple_items_cases() {
      return asList(
          args(f -> f.tuple(), f -> list()),
          args(f -> f.tuple(f.string()), f -> list(f.string())),
          args(f -> f.tuple(f.string(), f.int_()), f -> list(f.string(), f.int_())));
    }
  }

  @ParameterizedTest
  @MethodSource("typeJ_test_data")
  public void typeJ(CategoryB type, Class<?> expected) throws Exception {
    assertThat(type.javaType()).isEqualTo(expected);
  }

  public static java.util.List<Arguments> typeJ_test_data() throws BytecodeException {
    TestingVirtualMachine test = new TestingVirtualMachine();
    return list(
        arguments(test.blobTB(), BlobB.class),
        arguments(test.boolTB(), BoolB.class),
        arguments(test.funcTB(test.boolTB(), test.blobTB()), FuncB.class),
        arguments(test.ifFuncCB(), IfFuncB.class),
        arguments(test.mapFuncCB(), MapFuncB.class),
        arguments(test.intTB(), IntB.class),
        arguments(test.nativeFuncCB(test.boolTB(), test.blobTB()), NativeFuncB.class),
        arguments(test.personTB(), TupleB.class),
        arguments(test.stringTB(), StringB.class),
        arguments(test.arrayTB(test.blobTB()), ArrayB.class),
        arguments(test.arrayTB(test.boolTB()), ArrayB.class),
        arguments(test.arrayTB(test.funcTB(test.boolTB(), test.blobTB())), ArrayB.class),
        arguments(test.arrayTB(test.intTB()), ArrayB.class),
        arguments(test.arrayTB(test.personTB()), ArrayB.class),
        arguments(test.arrayTB(test.stringTB()), ArrayB.class),
        arguments(test.callCB(), CallB.class),
        arguments(test.orderCB(), OrderB.class),
        arguments(test.combineCB(test.intTB(), test.stringTB()), CombineB.class),
        arguments(test.pickCB(), PickB.class),
        arguments(test.varCB(test.intTB()), ReferenceB.class),
        arguments(test.selectCB(test.intTB()), SelectB.class));
  }

  @Nested
  class _oper {
    @ParameterizedTest
    @MethodSource("types")
    public void call(TypeB type) throws Exception {
      assertThat(categoryDb().call(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(CombineCB type, TupleTB expected) throws Exception {
      assertThat(type.evaluationType()).isEqualTo(expected);
    }

    public static java.util.List<Arguments> combine_cases() throws BytecodeException {
      TestingVirtualMachine test = new TestingVirtualMachine();
      CategoryDb db = test.categoryDb();
      return list(
          arguments(db.combine(db.tuple()), db.tuple()),
          arguments(db.combine(db.tuple(test.stringTB())), db.tuple(test.stringTB())));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(TypeB type) throws Exception {
      var array = arrayTB(type);
      assertThat(orderCB(type).evaluationType()).isEqualTo(array);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void pick(TypeB type) throws Exception {
      assertThat(pickCB(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void reference(TypeB type) throws Exception {
      assertThat(varCB(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(TypeB type) throws Exception {
      assertThat(selectCB(type).evaluationType()).isEqualTo(type);
    }

    public static List<CategoryB> types() {
      return TestingCategoryB.CATS_TO_TEST;
    }
  }

  @Test
  public void equals_and_hashcode() throws Exception {
    var tester = new EqualsTester();
    tester.addEqualityGroup(blobTB(), blobTB());
    tester.addEqualityGroup(boolTB(), boolTB());
    tester.addEqualityGroup(funcTB(boolTB(), blobTB()), funcTB(boolTB(), blobTB()));
    tester.addEqualityGroup(intTB(), intTB());
    tester.addEqualityGroup(stringTB(), stringTB());
    tester.addEqualityGroup(personTB(), personTB());

    tester.addEqualityGroup(arrayTB(blobTB()), arrayTB(blobTB()));
    tester.addEqualityGroup(arrayTB(boolTB()), arrayTB(boolTB()));
    tester.addEqualityGroup(
        arrayTB(funcTB(boolTB(), blobTB())), arrayTB(funcTB(boolTB(), blobTB())));
    tester.addEqualityGroup(arrayTB(intTB()), arrayTB(intTB()));
    tester.addEqualityGroup(arrayTB(stringTB()), arrayTB(stringTB()));
    tester.addEqualityGroup(arrayTB(personTB()), arrayTB(personTB()));

    tester.addEqualityGroup(arrayTB(arrayTB(blobTB())), arrayTB(arrayTB(blobTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(boolTB())), arrayTB(arrayTB(boolTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(funcTB(intTB()))), arrayTB(arrayTB(funcTB(intTB()))));
    tester.addEqualityGroup(arrayTB(arrayTB(intTB())), arrayTB(arrayTB(intTB())));
    tester.addEqualityGroup(arrayTB(arrayTB(stringTB())), arrayTB(arrayTB(stringTB())));
    tester.addEqualityGroup(
        arrayTB(arrayTB(tupleTB(animalTB()))), arrayTB(arrayTB(tupleTB(animalTB()))));

    tester.addEqualityGroup(callCB(), callCB());
    tester.addEqualityGroup(combineCB(intTB(), stringTB()), combineCB(intTB(), stringTB()));
    tester.addEqualityGroup(ifFuncCB(), ifFuncCB());
    tester.addEqualityGroup(mapFuncCB(), mapFuncCB());
    tester.addEqualityGroup(orderCB(), orderCB());
    tester.addEqualityGroup(pickCB(), pickCB());
    tester.addEqualityGroup(varCB(intTB()), varCB(intTB()));
    tester.addEqualityGroup(selectCB(intTB()), selectCB(intTB()));

    tester.testEquals();
  }

  private <R> R execute(Function1<CategoryDb, R, BytecodeException> f) throws BytecodeException {
    return f.apply(categoryDb());
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function1<CategoryDb, R, BytecodeException> factoryCall1,
      Function1<CategoryDb, R, BytecodeException> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function1<CategoryDb, R, BytecodeException> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function1<CategoryDb, R, BytecodeException> factoryCall) {
    return arguments(factoryCall);
  }
}
