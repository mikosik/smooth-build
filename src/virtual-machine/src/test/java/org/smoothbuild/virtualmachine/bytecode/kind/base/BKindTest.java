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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BVariant;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.testing.TestingBKind;

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

  @ParameterizedTest(name = "{1}")
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
        args(f -> f.variant(f.blob(), f.int_()), "{Blob|Int}"),
        args(f -> f.constructVariant(f.variant(f.blob(), f.int_())), "CONSTRUCT_VARIANT"),
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
        args(f -> f.constructTuple(f.tuple(f.string(), f.int_())), "CONSTRUCT_TUPLE"),
        args(f -> f.constructArray(f.array(f.string())), "CONSTRUCT_ARRAY"),
        args(f -> f.arrayGet(f.int_()), "ARRAY_GET"),
        args(f -> f.tupleGet(f.int_()), "TUPLE_GET"),
        args(f -> f.switch_(f.int_()), "SWITCH"),
        args(f -> f.ref(f.int_()), "REF"));
  }

  @Nested
  class _lambda {
    @ParameterizedTest
    @MethodSource("result_cases")
    public void result(
        Function1<BKindDb, BLambdaType, BytecodeException> factoryCall,
        Function1<BKindDb, BType, BytecodeException> expected)
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
        Function1<BKindDb, BTupleType, BytecodeException> expected)
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
      var arrayType = bArrayType(elementType);
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
  class _variant {
    @Test
    void _without_items_can_be_created() throws Exception {
      bVariantType();
    }

    @ParameterizedTest
    @MethodSource("variant_alternatives")
    public void variant_alternatives(
        Function1<BKindDb, BVariantType, BytecodeException> factoryCall,
        Function1<BKindDb, List<BType>, BytecodeException> expected)
        throws Exception {
      assertThat(execute(factoryCall).alternatives()).isEqualTo(execute(expected));
    }

    public static java.util.List<Arguments> variant_alternatives() {
      return asList(
          args(f -> f.variant(), f -> list()),
          args(f -> f.variant(f.string()), f -> list(f.string())),
          args(f -> f.variant(f.string(), f.int_()), f -> list(f.string(), f.int_())));
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
        arguments(test.bVariantType(), BVariant.class),
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
        arguments(test.bConstructArrayKind(), BConstructArray.class),
        arguments(
            test.bConstructTupleKind(test.bIntType(), test.bStringType()), BConstructTuple.class),
        arguments(test.bArrayGetKind(), BArrayGet.class),
        arguments(test.bRefKind(test.bIntType()), BRef.class),
        arguments(test.bTupleGetKind(test.bIntType()), BTupleGet.class));
  }

  @Nested
  class _method {
    @Test
    void method_elements() throws BytecodeException {
      assertThat(provide().kindDb().method().elements())
          .isEqualTo(list(bBlobType(), bStringType(), bStringType()));
    }
  }

  @Nested
  class _operation {
    @ParameterizedTest
    @MethodSource("types")
    public void call(BType type) throws Exception {
      assertThat(bCallKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("constructTuple_cases")
    public void constructTuple(BConstructTupleKind type, BTupleType expected) {
      assertThat(type.evaluationType()).isEqualTo(expected);
    }

    public static Stream<Arguments> constructTuple_cases() throws BytecodeException {
      var c = new VmTestContext();
      return Stream.of(
          arguments(c.bConstructTupleKind(), c.bTupleType()),
          arguments(c.bConstructTupleKind(c.bStringType()), c.bTupleType(c.bStringType())));
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
    public void constructArray(BType type) throws Exception {
      var arrayType = bArrayType(type);
      assertThat(bConstructArrayKind(type).evaluationType()).isEqualTo(arrayType);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void arrayGet(BType type) throws Exception {
      assertThat(bArrayGetKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void ref(BType type) throws Exception {
      assertThat(bRefKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void tupleGet(BType type) throws Exception {
      assertThat(bTupleGetKind(type).evaluationType()).isEqualTo(type);
    }

    @ParameterizedTest
    @MethodSource("types")
    public void fold(BType type) throws Exception {
      assertThat(bFoldKind(type).evaluationType()).isEqualTo(type);
    }

    public static List<BKind> types() {
      return TestingBKind.TYPES_TO_TEST;
    }
  }

  @Test
  void equals_and_hashcode() throws Exception {
    var tester = new EqualsTester();
    tester.addEqualityGroup(bBlobType(), bBlobType());
    tester.addEqualityGroup(bBoolType(), bBoolType());
    tester.addEqualityGroup(bVariantType(), bVariantType());
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
        bConstructTupleKind(bIntType(), bStringType()),
        bConstructTupleKind(bIntType(), bStringType()));
    tester.addEqualityGroup(bFoldKind(bIntType()), bFoldKind(bIntType()));
    tester.addEqualityGroup(bIfKind(), bIfKind());
    tester.addEqualityGroup(bMapKind(), bMapKind());
    tester.addEqualityGroup(bConstructArrayKind(), bConstructArrayKind());
    tester.addEqualityGroup(bArrayGetKind(), bArrayGetKind());
    tester.addEqualityGroup(bRefKind(bIntType()), bRefKind(bIntType()));
    tester.addEqualityGroup(bTupleGetKind(bIntType()), bTupleGetKind(bIntType()));

    tester.testEquals();
  }

  private <R> R execute(Function1<BKindDb, R, BytecodeException> f) throws BytecodeException {
    return f.apply(provide().kindDb());
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
