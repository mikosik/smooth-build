package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

public class TestingBKind {
  public static final TestingVirtualMachine CONTEXT = new TestingVirtualMachine();

  public static final List<BType> BASE_CATS_TO_TEST = wrapException(() -> list(
      CONTEXT.blobTB(),
      CONTEXT.boolTB(),
      func(CONTEXT.blobTB(), CONTEXT.boolTB()),
      CONTEXT.intTB(),
      CONTEXT.stringTB(),
      CONTEXT.personTB()));

  public static final List<BKind> ARRAY_CATS_TO_TEST = wrapException(() -> list(
      array(CONTEXT.blobTB()),
      array(func(CONTEXT.blobTB(), CONTEXT.boolTB())),
      array(CONTEXT.boolTB()),
      array(CONTEXT.intTB()),
      array(CONTEXT.stringTB()),
      array(CONTEXT.personTB()),
      array(array(CONTEXT.blobTB())),
      array(array(CONTEXT.boolTB())),
      array(array(func(CONTEXT.blobTB(), CONTEXT.boolTB()))),
      array(array(CONTEXT.intTB())),
      array(array(CONTEXT.stringTB())),
      array(array(CONTEXT.personTB()))));

  public static final List<BKind> CATS_TO_TEST = ARRAY_CATS_TO_TEST.appendAll(BASE_CATS_TO_TEST);

  public static final List<BKind> ALL_CATS_TO_TEST = wrapException(TestingBKind::createAllKinds);

  private static <R, T extends Throwable> R wrapException(Function0<R, T> function0) {
    try {
      return function0.apply();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static List<BKind> createAllKinds() throws BytecodeException {
    var baseKinds = list(
        CONTEXT.blobTB(),
        CONTEXT.boolTB(),
        CONTEXT.intTB(),
        func(CONTEXT.blobTB()),
        func(CONTEXT.blobTB(), CONTEXT.blobTB()),
        func(CONTEXT.blobTB(), CONTEXT.blobTB(), CONTEXT.blobTB()),
        func(CONTEXT.stringTB()),
        CONTEXT.stringTB(),
        tuple(),
        tuple(CONTEXT.blobTB()),
        tuple(CONTEXT.blobTB(), CONTEXT.blobTB()),
        tuple(CONTEXT.stringTB()));
    var arrayKinds = baseKinds.map(CONTEXT::arrayTB);
    var valueKinds = baseKinds.appendAll(arrayKinds);
    var exprKinds = list(
        CONTEXT.callCB(CONTEXT.blobTB()),
        CONTEXT.callCB(CONTEXT.stringTB()),
        CONTEXT.combineCB(CONTEXT.tupleTB(CONTEXT.blobTB())),
        CONTEXT.combineCB(CONTEXT.tupleTB(CONTEXT.stringTB())),
        CONTEXT.orderCB(array(CONTEXT.blobTB())),
        CONTEXT.orderCB(array(CONTEXT.stringTB())),
        CONTEXT.pickCB(CONTEXT.blobTB()),
        CONTEXT.pickCB(CONTEXT.stringTB()),
        CONTEXT.varCB(CONTEXT.blobTB()),
        CONTEXT.varCB(CONTEXT.stringTB()),
        CONTEXT.selectCB(CONTEXT.blobTB()),
        CONTEXT.selectCB(CONTEXT.stringTB()),
        CONTEXT.ifFuncCB(CONTEXT.blobTB()),
        CONTEXT.ifFuncCB(CONTEXT.stringTB()),
        CONTEXT.mapFuncCB(CONTEXT.stringTB(), CONTEXT.intTB()),
        CONTEXT.mapFuncCB(CONTEXT.stringTB(), CONTEXT.boolTB()),
        CONTEXT.nativeFuncCB(CONTEXT.blobTB()),
        CONTEXT.nativeFuncCB(CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.nativeFuncCB(CONTEXT.blobTB(), CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.nativeFuncCB(CONTEXT.stringTB()),
        CONTEXT.lambdaCB(CONTEXT.blobTB()),
        CONTEXT.lambdaCB(CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.lambdaCB(CONTEXT.blobTB(), CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.lambdaCB(CONTEXT.stringTB()));

    return exprKinds.appendAll(valueKinds);
  }

  private static BArrayType array(BType elementType) throws BytecodeException {
    return CONTEXT.arrayTB(elementType);
  }

  private static BFuncType func(BType resultType, BType... paramTypes) throws BytecodeException {
    return CONTEXT.funcTB(list(paramTypes), resultType);
  }

  private static BTupleType tuple(BType... paramTypes) throws BytecodeException {
    return CONTEXT.tupleTB(paramTypes);
  }
}
