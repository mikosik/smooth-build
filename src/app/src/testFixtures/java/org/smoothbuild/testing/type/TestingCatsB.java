package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableList;

public class TestingCatsB {
  public static final TestContext CONTEXT = new TestContext();

  public static final ImmutableList<TypeB> BASE_CATS_TO_TEST = list(
      CONTEXT.blobTB(),
      CONTEXT.boolTB(),
      func(CONTEXT.blobTB(), CONTEXT.boolTB()),
      CONTEXT.intTB(),
      CONTEXT.stringTB(),
      CONTEXT.personTB()
  );

  public static final ImmutableList<CategoryB> ARRAY_CATS_TO_TEST = list(
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
      array(array(CONTEXT.personTB()))
  );

  public static final ImmutableList<CategoryB> CATS_TO_TEST =
      concat(BASE_CATS_TO_TEST, ARRAY_CATS_TO_TEST);

  public static final ImmutableList<CategoryB> ALL_CATS_TO_TEST = createAllCats();

  private static ImmutableList<CategoryB> createAllCats() {
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
        tuple(CONTEXT.stringTB())
    );
    var arrayCs = map(baseCs, CONTEXT::arrayTB);
    var valueCs = concat(baseCs, arrayCs);
    var exprCs = list(
        CONTEXT.callCB(CONTEXT.blobTB()),
        CONTEXT.callCB(CONTEXT.stringTB()),
        CONTEXT.combineCB(CONTEXT.tupleTB(CONTEXT.blobTB())),
        CONTEXT.combineCB(CONTEXT.tupleTB(CONTEXT.stringTB())),
        CONTEXT.closurizeCB(CONTEXT.funcTB(CONTEXT.blobTB(), CONTEXT.blobTB())),
        CONTEXT.closurizeCB(CONTEXT.funcTB(CONTEXT.blobTB(), CONTEXT.blobTB(), CONTEXT.blobTB())),
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
        CONTEXT.closureCB(CONTEXT.blobTB()),
        CONTEXT.closureCB(CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.closureCB(CONTEXT.blobTB(), CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.closureCB(CONTEXT.stringTB()),
        CONTEXT.exprFuncCB(CONTEXT.blobTB()),
        CONTEXT.exprFuncCB(CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.exprFuncCB(CONTEXT.blobTB(), CONTEXT.blobTB(), CONTEXT.blobTB()),
        CONTEXT.exprFuncCB(CONTEXT.stringTB())
    );

    return concat(valueCs, exprCs);
  }

  private static ArrayTB array(TypeB elemT) {
    return CONTEXT.arrayTB(elemT);
  }

  private static FuncTB func(TypeB res, TypeB... params) {
    return CONTEXT.funcTB(list(params), res);
  }

  private static TupleTB tuple(TypeB... params) {
    return CONTEXT.tupleTB(params);
  }
}
