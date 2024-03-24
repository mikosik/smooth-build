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
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BLambdaKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BNativeFuncKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.testing.TestingBKind;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BKindTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("names")
  public void name(Function1<BKindDb, BKind, BytecodeException> factoryCall, String name)
      throws Exception {
    assertThat(execute(factoryCall).name()).isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Function1<BKindDb, BKind, BytecodeException> factoryCall, String name)
      throws Exception {
    assertThat(execute(factoryCall).q()).isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Function1<BKindDb, BKind, BytecodeException> factoryCall, String name)
      throws Exception {
    var kindB = execute(factoryCall);
    assertThat(kindB.toString()).isEqualTo(name);
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
        Function1<BKindDb, BFuncType, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
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
        Function1<BKindDb, BFuncType, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
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
        Function1<BKindDb, BLambdaKind, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
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
        Function1<BKindDb, BLambdaKind, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
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
        Function1<BKindDb, BNativeFuncKind, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
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
        Function1<BKindDb, BNativeFuncKind, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
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
    public void elemType(Function1<BKindDb, BType, BytecodeException> factoryCall)
        throws Exception {
      var elementType = execute(factoryCall);
      var arrayType = kindDb().array(elementType);
      assertThat(arrayType.elem()).isEqualTo(elementType);
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
        Function1<BKindDb, BTupleType, BytecodeException> factoryCall,
        Function1<BKindDb, List<BType>, BytecodeException> expected)
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
  public void typeJ(BKind type, Class<?> expected) throws Exception {
    assertThat(type.javaType()).isEqualTo(expected);
  }

  public static java.util.List<Arguments> typeJ_test_data() throws BytecodeException {
    TestingVirtualMachine test = new TestingVirtualMachine();
    return list(
        arguments(test.blobTB(), BBlob.class),
        arguments(test.boolTB(), BBool.class),
        arguments(test.funcTB(test.boolTB(), test.blobTB()), BFunc.class),
        arguments(test.ifFuncCB(), BIf.class),
        arguments(test.mapFuncCB(), BMap.class),
        arguments(test.intTB(), BInt.class),
        arguments(test.nativeFuncCB(test.boolTB(), test.blobTB()), BNativeFunc.class),
        arguments(test.personTB(), BTuple.class),
        arguments(test.stringTB(), BString.class),
        arguments(test.arrayTB(test.blobTB()), BArray.class),
        arguments(test.arrayTB(test.boolTB()), BArray.class),
        arguments(test.arrayTB(test.funcTB(test.boolTB(), test.blobTB())), BArray.class),
        arguments(test.arrayTB(test.intTB()), BArray.class),
        arguments(test.arrayTB(test.personTB()), BArray.class),
        arguments(test.arrayTB(test.stringTB()), BArray.class),
        arguments(test.callCB(), BCall.class),
        arguments(test.orderCB(), BOrder.class),
        arguments(test.combineCB(test.intTB(), test.stringTB()), BCombine.class),
        arguments(test.pickCB(), BPick.class),
        arguments(test.varCB(test.intTB()), BReference.class),
        arguments(test.selectCB(test.intTB()), BSelect.class));
  }

  @Nested
  class _oper {
    @ParameterizedTest
    @MethodSource("types")
    public void call(BType type) throws Exception {
      assertThat(kindDb().call(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(BCombineKind type, BTupleType expected) throws Exception {
      assertThat(type.evaluationType()).isEqualTo(expected);
    }

    public static java.util.List<Arguments> combine_cases() throws BytecodeException {
      TestingVirtualMachine test = new TestingVirtualMachine();
      BKindDb db = test.kindDb();
      return list(
          arguments(db.combine(db.tuple()), db.tuple()),
          arguments(db.combine(db.tuple(test.stringTB())), db.tuple(test.stringTB())));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(BType type) throws Exception {
      var arrayType = arrayTB(type);
      assertThat(orderCB(type).evaluationType()).isEqualTo(arrayType);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void pick(BType type) throws Exception {
      assertThat(pickCB(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void reference(BType type) throws Exception {
      assertThat(varCB(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(BType type) throws Exception {
      assertThat(selectCB(type).evaluationType()).isEqualTo(type);
    }

    public static List<BKind> types() {
      return TestingBKind.CATS_TO_TEST;
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

  private <R> R execute(Function1<BKindDb, R, BytecodeException> f) throws BytecodeException {
    return f.apply(kindDb());
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function1<BKindDb, R, BytecodeException> factoryCall1,
      Function1<BKindDb, R, BytecodeException> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function1<BKindDb, R, BytecodeException> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function1<BKindDb, R, BytecodeException> factoryCall) {
    return arguments(factoryCall);
  }
}
