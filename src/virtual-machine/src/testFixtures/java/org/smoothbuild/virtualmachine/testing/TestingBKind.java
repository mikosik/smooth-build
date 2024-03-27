package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class TestingBKind {
  public static final TestingVirtualMachine CONTEXT = new TestingVirtualMachine();

  public static final List<BType> BASE_KINDS_TO_TEST = wrapException(() -> list(
      CONTEXT.bBlobType(),
      CONTEXT.bBoolType(),
      func(CONTEXT.bBlobType(), CONTEXT.bBoolType()),
      CONTEXT.bIntType(),
      CONTEXT.bStringType(),
      CONTEXT.bPersonType()));

  public static final List<BKind> ARRAY_CATS_TO_TEST = wrapException(() -> list(
      array(CONTEXT.bBlobType()),
      array(func(CONTEXT.bBlobType(), CONTEXT.bBoolType())),
      array(CONTEXT.bBoolType()),
      array(CONTEXT.bIntType()),
      array(CONTEXT.bStringType()),
      array(CONTEXT.bPersonType()),
      array(array(CONTEXT.bBlobType())),
      array(array(CONTEXT.bBoolType())),
      array(array(func(CONTEXT.bBlobType(), CONTEXT.bBoolType()))),
      array(array(CONTEXT.bIntType())),
      array(array(CONTEXT.bStringType())),
      array(array(CONTEXT.bPersonType()))));

  public static final List<BKind> KINDS_TO_TEST = ARRAY_CATS_TO_TEST.appendAll(BASE_KINDS_TO_TEST);

  public static final List<BKind> ALL_KINDS_TO_TEST = wrapException(TestingBKind::createAllKinds);

  private static <R, T extends Throwable> R wrapException(Function0<R, T> function0) {
    try {
      return function0.apply();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static List<BKind> createAllKinds() throws BytecodeException {
    var baseKinds = list(
        CONTEXT.bBlobType(),
        CONTEXT.bBoolType(),
        CONTEXT.bIntType(),
        func(CONTEXT.bBlobType()),
        func(CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        func(CONTEXT.bBlobType(), CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        func(CONTEXT.bStringType()),
        CONTEXT.bStringType(),
        tuple(),
        tuple(CONTEXT.bBlobType()),
        tuple(CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        tuple(CONTEXT.bStringType()));
    var arrayKinds = baseKinds.map(CONTEXT::bArrayType);
    var valueKinds = baseKinds.appendAll(arrayKinds);
    var exprKinds = list(
        CONTEXT.bCallKind(CONTEXT.bBlobType()),
        CONTEXT.bCallKind(CONTEXT.bStringType()),
        CONTEXT.bCombineKind(CONTEXT.bTupleType(CONTEXT.bBlobType())),
        CONTEXT.bCombineKind(CONTEXT.bTupleType(CONTEXT.bStringType())),
        CONTEXT.bOrderKind(array(CONTEXT.bBlobType())),
        CONTEXT.bOrderKind(array(CONTEXT.bStringType())),
        CONTEXT.bPickKind(CONTEXT.bBlobType()),
        CONTEXT.bPickKind(CONTEXT.bStringType()),
        CONTEXT.bReferenceKind(CONTEXT.bBlobType()),
        CONTEXT.bReferenceKind(CONTEXT.bStringType()),
        CONTEXT.bSelectKind(CONTEXT.bBlobType()),
        CONTEXT.bSelectKind(CONTEXT.bStringType()),
        CONTEXT.bIfKind(CONTEXT.bBlobType()),
        CONTEXT.bIfKind(CONTEXT.bStringType()),
        CONTEXT.bMapKind(CONTEXT.bArrayType(CONTEXT.bBlobType())),
        CONTEXT.bMapKind(CONTEXT.bArrayType(CONTEXT.bStringType())),
        CONTEXT.bInvokeKind(CONTEXT.bBlobType()),
        CONTEXT.bInvokeKind(CONTEXT.bStringType()),
        CONTEXT.bLambdaKind(CONTEXT.bBlobType()),
        CONTEXT.bLambdaKind(CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        CONTEXT.bLambdaKind(CONTEXT.bBlobType(), CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        CONTEXT.bLambdaKind(CONTEXT.bStringType()));

    return exprKinds.appendAll(valueKinds);
  }

  private static BArrayType array(BType elementType) throws BytecodeException {
    return CONTEXT.bArrayType(elementType);
  }

  private static BFuncType func(BType resultType, BType... paramTypes) throws BytecodeException {
    return CONTEXT.bFuncType(list(paramTypes), resultType);
  }

  private static BTupleType tuple(BType... paramTypes) throws BytecodeException {
    return CONTEXT.bTupleType(paramTypes);
  }
}
