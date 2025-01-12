package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;

import com.google.common.testing.EqualsTester;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoice;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.BKindDbException;
import org.smoothbuild.virtualmachine.testing.TestingBKind;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BKindTest extends VmTestContext {
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
        args(f -> f.choice(f.blob(), f.int_()), "{Blob|Int}"),
        args(f -> f.array(f.blob()), "[Blob]"),
        args(f -> f.array(f.bool()), "[Bool]"),
        args(f -> f.array(f.int_()), "[Int]"),
        args(f -> f.array(f.string()), "[String]"),
        args(f -> f.array(f.array(f.blob())), "[[Blob]]"),
        args(f -> f.array(f.array(f.bool())), "[[Bool]]"),
        args(f -> f.array(f.array(f.int_())), "[[Int]]"),
        args(f -> f.array(f.array(f.string())), "[[String]]"),
        args(f -> f.lambda(list(), f.string()), "()->String"),
        args(f -> f.lambda(list(f.string()), f.int_()), "(String)->Int"),
        args(f -> f.if_(f.int_()), "IF"),
        args(f -> f.map(f.array(f.int_())), "MAP"),
        args(f -> f.invoke(f.string()), "INVOKE"),
        args(f -> f.invoke(f.int_()), "INVOKE"),
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
  class _lambda {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function1<BKindDb, BLambdaType, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).result()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> result_cases() {
      return asList(
          args(f -> f.lambda(list(), f.int_()), f -> f.int_()),
          args(f -> f.lambda(list(f.bool()), f.blob()), f -> f.blob()),
          args(f -> f.lambda(list(f.bool(), f.int_()), f.blob()), f -> f.blob()));
    }

    @ParameterizedTest
    @MethodSource("params_cases")
    public void params(
        Function1<BKindDb, BLambdaType, BytecodeException> factoryCall,
        Function1<BKindDb, java.util.List<BType>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).params()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> params_cases() {
      return asList(
          args(f -> f.lambda(list(), f.int_()), f -> f.tuple()),
          args(f -> f.lambda(list(f.bool()), f.blob()), f -> f.tuple(f.bool())),
          args(
              f -> f.lambda(list(f.bool(), f.int_()), f.blob()), f -> f.tuple(f.bool(), f.int_())));
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
      assertThat(arrayType.element()).isEqualTo(elementType);
    }

    public static java.util.List<Arguments> elemType_test_data() {
      return asList(
          args(f -> f.blob()),
          args(f -> f.bool()),
          args(f -> f.lambda(list(), f.string())),
          args(f -> f.int_()),
          args(f -> f.string()),
          args(f -> f.tuple(f.int_())),
          args(f -> f.array(f.blob())),
          args(f -> f.array(f.bool())),
          args(f -> f.array(f.lambda(list(), f.string()))),
          args(f -> f.array(f.int_())),
          args(f -> f.array(f.string())));
    }
  }

  @Nested
  class _choice {
    @Test
    void _without_items_can_be_created() throws Exception {
      bChoiceType();
    }

    @ParameterizedTest
    @MethodSource("tuple_alternatives")
    public void tuple_alternatives(
        Function1<BKindDb, BChoiceType, BytecodeException> factoryCall,
        Function1<BKindDb, List<BType>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).alternatives()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> tuple_alternatives() {
      return asList(
          args(f -> f.choice(), f -> list()),
          args(f -> f.choice(f.string()), f -> list(f.string())),
          args(f -> f.choice(f.string(), f.int_()), f -> list(f.string(), f.int_())));
    }
  }

  @Nested
  class _tuple {
    @Test
    void _without_items_can_be_created() throws Exception {
      bTupleType();
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
  public void typeJ(BKind type, Class<?> expected) {
    assertThat(type.javaType()).isEqualTo(expected);
  }

  public static Stream<Arguments> typeJ_test_data() throws BytecodeException {
    var test = new VmTestContext();
    return Stream.of(
        arguments(test.bBlobType(), BBlob.class),
        arguments(test.bBoolType(), BBool.class),
        arguments(test.bChoiceType(), BChoice.class),
        arguments(test.bLambdaType(test.bBoolType(), test.bBlobType()), BLambda.class),
        arguments(test.bIfKind(), BIf.class),
        arguments(test.bMapKind(), BMap.class),
        arguments(test.bIntType(), BInt.class),
        arguments(test.bInvokeKind(test.bIntType()), BInvoke.class),
        arguments(test.bPersonType(), BTuple.class),
        arguments(test.bStringType(), BString.class),
        arguments(test.bBlobArrayType(), BArray.class),
        arguments(test.bBoolArrayType(), BArray.class),
        arguments(
            test.bArrayType(test.bLambdaType(test.bBoolType(), test.bBlobType())), BArray.class),
        arguments(test.bIntArrayType(), BArray.class),
        arguments(test.bArrayType(test.bPersonType()), BArray.class),
        arguments(test.bStringArrayType(), BArray.class),
        arguments(test.bCallKind(), BCall.class),
        arguments(test.bOrderKind(), BOrder.class),
        arguments(test.bCombineKind(test.bIntType(), test.bStringType()), BCombine.class),
        arguments(test.bPickKind(), BPick.class),
        arguments(test.bReferenceKind(test.bIntType()), BReference.class),
        arguments(test.bSelectKind(test.bIntType()), BSelect.class));
  }

  @Nested
  class _method {
    @Test
    void method_elements() throws BKindDbException {
      assertThat(kindDb().method().elements())
          .isEqualTo(list(kindDb().blob(), kindDb().string(), kindDb().string()));
    }
  }

  @Nested
  class _operation {
    @ParameterizedTest
    @MethodSource("types")
    public void call(BType type) throws Exception {
      assertThat(kindDb().call(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("combine_cases")
    public void combine(BCombineKind type, BTupleType expected) {
      assertThat(type.evaluationType()).isEqualTo(expected);
    }

    public static Stream<Arguments> combine_cases() throws BytecodeException {
      var test = new VmTestContext();
      BKindDb db = test.kindDb();
      return Stream.of(
          arguments(db.combine(db.tuple()), db.tuple()),
          arguments(db.combine(db.tuple(test.bStringType())), db.tuple(test.bStringType())));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void if_(BType type) throws Exception {
      assertThat(bIfKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void invoke(BType type) throws Exception {
      assertThat(bInvokeKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void map(BType type) throws Exception {
      assertThat(bMapKind(bArrayType(type)).evaluationType()).isEqualTo(bArrayType(type));
    }

    @ParameterizedTest
    @MethodSource("types")
    public void order(BType type) throws Exception {
      var arrayType = bArrayType(type);
      assertThat(bOrderKind(type).evaluationType()).isEqualTo(arrayType);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void pick(BType type) throws Exception {
      assertThat(bPickKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void reference(BType type) throws Exception {
      assertThat(bReferenceKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void select(BType type) throws Exception {
      assertThat(bSelectKind(type).evaluationType()).isEqualTo(type);
    }

    public static List<BKind> types() {
      return TestingBKind.KINDS_TO_TEST;
    }
  }

  @Test
  void equals_and_hashcode() throws Exception {
    var tester = new EqualsTester();
    tester.addEqualityGroup(bBlobType(), bBlobType());
    tester.addEqualityGroup(bBoolType(), bBoolType());
    tester.addEqualityGroup(bChoiceType(), bChoiceType());
    tester.addEqualityGroup(
        bLambdaType(bBoolType(), bBlobType()), bLambdaType(bBoolType(), bBlobType()));
    tester.addEqualityGroup(bIntType(), bIntType());
    tester.addEqualityGroup(bStringType(), bStringType());
    tester.addEqualityGroup(bPersonType(), bPersonType());

    tester.addEqualityGroup(bBlobArrayType(), bBlobArrayType());
    tester.addEqualityGroup(bBoolArrayType(), bBoolArrayType());
    tester.addEqualityGroup(
        bArrayType(bLambdaType(bBoolType(), bBlobType())),
        bArrayType(bLambdaType(bBoolType(), bBlobType())));
    tester.addEqualityGroup(bIntArrayType(), bIntArrayType());
    tester.addEqualityGroup(bStringArrayType(), bStringArrayType());
    tester.addEqualityGroup(bArrayType(bPersonType()), bArrayType(bPersonType()));

    tester.addEqualityGroup(bArrayType(bBlobArrayType()), bArrayType(bBlobArrayType()));
    tester.addEqualityGroup(bArrayType(bBoolArrayType()), bArrayType(bBoolArrayType()));
    tester.addEqualityGroup(
        bArrayType(bArrayType(bIntLambdaType())), bArrayType(bArrayType(bIntLambdaType())));
    tester.addEqualityGroup(bArrayType(bIntArrayType()), bArrayType(bIntArrayType()));
    tester.addEqualityGroup(bArrayType(bStringArrayType()), bArrayType(bStringArrayType()));
    tester.addEqualityGroup(
        bArrayType(bArrayType(bTupleType(bAnimalType()))),
        bArrayType(bArrayType(bTupleType(bAnimalType()))));

    tester.addEqualityGroup(bCallKind(), bCallKind());
    tester.addEqualityGroup(
        bCombineKind(bIntType(), bStringType()), bCombineKind(bIntType(), bStringType()));
    tester.addEqualityGroup(bIfKind(), bIfKind());
    tester.addEqualityGroup(bMapKind(), bMapKind());
    tester.addEqualityGroup(bOrderKind(), bOrderKind());
    tester.addEqualityGroup(bPickKind(), bPickKind());
    tester.addEqualityGroup(bReferenceKind(bIntType()), bReferenceKind(bIntType()));
    tester.addEqualityGroup(bSelectKind(bIntType()), bSelectKind(bIntType()));

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
