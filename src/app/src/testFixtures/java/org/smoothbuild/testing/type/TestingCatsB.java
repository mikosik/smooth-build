package org.smoothbuild.testing.type;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class TestingCatsB {
  public static final TestContext CONTEXT = new TestContext();

  public static final List<TypeB> BASE_CATS_TO_TEST = wrapException(() -> list(
      CONTEXT.blobTB(),
      CONTEXT.boolTB(),
      func(CONTEXT.blobTB(), CONTEXT.boolTB()),
      CONTEXT.intTB(),
      CONTEXT.stringTB(),
      CONTEXT.personTB()));

  public static final List<CategoryB> ARRAY_CATS_TO_TEST = wrapException(() -> list(
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

  public static final List<CategoryB> CATS_TO_TEST =
      ARRAY_CATS_TO_TEST.appendAll(BASE_CATS_TO_TEST);

  public static final List<CategoryB> ALL_CATS_TO_TEST = wrapException(TestingCatsB::createAllCats);

  private static <R, T extends Throwable> R wrapException(Function0<R, T> function0) {
    try {
      return function0.apply();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static List<CategoryB> createAllCats() throws BytecodeException {
    var baseCs = list(
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
    var arrayCs = baseCs.map(CONTEXT::arrayTB);
    var valueCs = baseCs.appendAll(arrayCs);
    var exprCs = list(
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

    return exprCs.appendAll(valueCs);
  }

  private static ArrayTB array(TypeB elemT) throws BytecodeException {
    return CONTEXT.arrayTB(elemT);
  }

  private static FuncTB func(TypeB res, TypeB... params) throws BytecodeException {
    return CONTEXT.funcTB(list(params), res);
  }

  private static TupleTB tuple(TypeB... params) throws BytecodeException {
    return CONTEXT.tupleTB(params);
  }
}
