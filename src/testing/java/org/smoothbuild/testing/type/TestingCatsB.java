package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.testing.TestContext;

import com.google.common.collect.ImmutableList;

public class TestingCatsB {
  private static final TestContext CONTEXT = new TestContext();
  public static final CatDb CAT_DB = CONTEXT.catDb();
  public static final TypeB BLOB = CONTEXT.blobTB();
  public static final TypeB BOOL = CONTEXT.boolTB();
  public static final TypeB INT = CONTEXT.intTB();
  public static final TypeB FUNC = func(BLOB, BOOL);
  public static final CatB METHOD = CONTEXT.natFuncCB(BLOB, BOOL);
  public static final TypeB STRING = CONTEXT.stringTB();

  public static final TupleTB PERSON = CONTEXT.personTB();
  public static final TupleTB FILE = CONTEXT.fileTB();

  public static final CatB CALL = CONTEXT.callCB();
  public static final CatB COMBINE = CONTEXT.combineCB(INT, STRING);
  public static final CatB IF_FUNC = CONTEXT.ifFuncCB();
  public static final CatB MAP_FUNC = CONTEXT.mapFuncCB();
  public static final CatB ORDER = CONTEXT.orderCB();
  public static final CatB PARAM_REF = CONTEXT.paramRefCB(INT);
  public static final CatB SELECT = CONTEXT.selectCB(INT);

  public static final ArrayTB ARRAY_BLOB = array(BLOB);
  public static final ArrayTB ARRAY_BOOL = array(BOOL);
  public static final ArrayTB ARRAY_FUNC = array(FUNC);
  public static final ArrayTB ARRAY_INT = array(INT);
  public static final ArrayTB ARRAY_STRING = array(STRING);
  public static final ArrayTB ARRAY_PERSON_TUPLE = array(PERSON);
  public static final ArrayTB ARRAY_PERSON = array(PERSON);

  public static final ArrayTB ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayTB ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayTB ARRAY2_FUNCTION = array(ARRAY_FUNC);
  public static final ArrayTB ARRAY2_INT = array(ARRAY_INT);
  public static final ArrayTB ARRAY2_STRING = array(ARRAY_STRING);
  public static final ArrayTB ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArrayTB ARRAY2_PERSON = array(ARRAY_PERSON);

  public static final ImmutableList<TypeB> BASE_CATS_TO_TEST = list(
      BLOB,
      BOOL,
      FUNC,
      INT,
      STRING,
      PERSON
  );

  public static final ImmutableList<CatB> ARRAY_CATS_TO_TEST = list(
      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_FUNC,
      ARRAY_INT,
      ARRAY_STRING,
      ARRAY_PERSON_TUPLE,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_FUNCTION,
      ARRAY2_INT,
      ARRAY2_STRING,
      ARRAY2_PERSON_TUPLE
  );

  public static final ImmutableList<CatB> CATS_TO_TEST =
      concat(BASE_CATS_TO_TEST, ARRAY_CATS_TO_TEST);

  public static final ImmutableList<CatB> ALL_CATS_TO_TEST = createAllCats();

  private static ImmutableList<CatB> createAllCats() {
    var baseCs = list(
        BLOB,
        BOOL,
        INT,
        func(BLOB),
        func(BLOB, BLOB),
        func(BLOB, BLOB, BLOB),
        func(STRING),
        STRING,
        tuple(),
        tuple(BLOB),
        tuple(BLOB, BLOB),
        tuple(STRING)
    );
    var arrayCs = map(baseCs, CONTEXT::arrayTB);
    var valueCs = concat(baseCs, arrayCs);
    var exprCs = list(
        CONTEXT.callCB(BLOB),
        CONTEXT.callCB(STRING),
        CONTEXT.combineCB(CONTEXT.tupleTB(BLOB)),
        CONTEXT.combineCB(CONTEXT.tupleTB(STRING)),
        CONTEXT.orderCB(ARRAY_BLOB),
        CONTEXT.orderCB(ARRAY_STRING),
        CONTEXT.paramRefCB(BLOB),
        CONTEXT.paramRefCB(STRING),
        CONTEXT.selectCB(BLOB),
        CONTEXT.selectCB(STRING),
        CONTEXT.ifFuncCB(BLOB),
        CONTEXT.ifFuncCB(STRING),
        CONTEXT.mapFuncCB(STRING, INT),
        CONTEXT.mapFuncCB(STRING, BOOL),
        CONTEXT.natFuncCB(BLOB),
        CONTEXT.natFuncCB(BLOB, BLOB),
        CONTEXT.natFuncCB(BLOB, BLOB, BLOB),
        CONTEXT.natFuncCB(STRING),
        CONTEXT.defFuncCB(BLOB),
        CONTEXT.defFuncCB(BLOB, BLOB),
        CONTEXT.defFuncCB(BLOB, BLOB, BLOB),
        CONTEXT.defFuncCB(STRING)
        );

    return concat(valueCs, exprCs);
  }

  public static ArrayTB array(TypeB elemT) {
    return CONTEXT.arrayTB(elemT);
  }

  public static FuncTB func(TypeB res, TypeB... params) {
    return CONTEXT.funcTB(res, params);
  }

  public static TupleTB tuple(TypeB... params) {
    return CONTEXT.tupleTB(params);
  }
}
