package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class TestingBKind {
  public static final VmTestContext CONTEXT = new VmTestContext();

  public static final List<BType> BASE_TYPES_TO_TEST = wrapException(() -> list(
      CONTEXT.bBlobType(),
      CONTEXT.bBoolType(),
      lambda(CONTEXT.bBlobType(), CONTEXT.bBoolType()),
      CONTEXT.bIntType(),
      CONTEXT.bStringType(),
      CONTEXT.bPersonType()));

  public static final List<BKind> ARRAY_TYPES_TO_TEST = wrapException(() -> list(
      array(CONTEXT.bBlobType()),
      array(lambda(CONTEXT.bBlobType(), CONTEXT.bBoolType())),
      array(CONTEXT.bBoolType()),
      array(CONTEXT.bIntType()),
      array(CONTEXT.bStringType()),
      array(CONTEXT.bPersonType()),
      array(array(CONTEXT.bBlobType())),
      array(array(CONTEXT.bBoolType())),
      array(array(lambda(CONTEXT.bBlobType(), CONTEXT.bBoolType()))),
      array(array(CONTEXT.bIntType())),
      array(array(CONTEXT.bStringType())),
      array(array(CONTEXT.bPersonType()))));

  public static final List<BKind> TYPES_TO_TEST = ARRAY_TYPES_TO_TEST.addAll(BASE_TYPES_TO_TEST);

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
        lambda(CONTEXT.bBlobType()),
        lambda(CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        lambda(CONTEXT.bBlobType(), CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        lambda(CONTEXT.bStringType()),
        CONTEXT.bStringType(),
        choice(),
        choice(CONTEXT.bIntType()),
        choice(CONTEXT.bIntType(), CONTEXT.bStringType()),
        tuple(),
        tuple(CONTEXT.bBlobType()),
        tuple(CONTEXT.bBlobType(), CONTEXT.bBlobType()),
        tuple(CONTEXT.bStringType()));
    var arrayKinds = baseKinds.map(CONTEXT::bArrayType);
    var valueKinds = baseKinds.addAll(arrayKinds);
    List<BKind> exprKinds = list(
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
        CONTEXT.bMapKind(CONTEXT.bBlobArrayType()),
        CONTEXT.bMapKind(CONTEXT.bStringArrayType()),
        CONTEXT.bInvokeKind(CONTEXT.bBlobType()),
        CONTEXT.bInvokeKind(CONTEXT.bStringType()),
        CONTEXT.bFoldKind(CONTEXT.bBlobType()),
        CONTEXT.bFoldKind(CONTEXT.bStringType()));

    return exprKinds.addAll(valueKinds);
  }

  private static BArrayType array(BType elementType) throws BytecodeException {
    return CONTEXT.bArrayType(elementType);
  }

  private static BLambdaType lambda(BType resultType, BType... paramTypes)
      throws BytecodeException {
    return CONTEXT.bLambdaType(list(paramTypes), resultType);
  }

  private static BChoiceType choice(BType... alternativeTypes) throws BytecodeException {
    return CONTEXT.bChoiceType(alternativeTypes);
  }

  private static BTupleType tuple(BType... itemTypes) throws BytecodeException {
    return CONTEXT.bTupleType(itemTypes);
  }
}
