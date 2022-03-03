package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingCatsB {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final CatDb CAT_DB = CONTEXT.catDb();

  public static final TypeB ANY = CONTEXT.anyTB();
  public static final TypeB BLOB = CONTEXT.blobTB();
  public static final TypeB BOOL = CONTEXT.boolTB();
  public static final TypeB INT = CONTEXT.intTB();
  public static final TypeB FUNC = func(BLOB, list(BOOL));
  public static final TypeB METHOD = method(BLOB, list(BOOL));
  public static final TypeB NOTHING = CONTEXT.nothingTB();
  public static final TypeB STRING = CONTEXT.stringTB();
  public static final TypeB VAR_A = CONTEXT.varTB("A");

  public static final TupleTB PERSON = CONTEXT.personTB();
  public static final TupleTB FILE = CONTEXT.fileTB();

  public static final CatB CALL = CONTEXT.callCB();
  public static final CatB COMBINE = CONTEXT.combineCB(INT, STRING);
  public static final CatB IF = CONTEXT.ifCB();
  public static final CatB INVOKE = CONTEXT.invokeCB();
  public static final CatB MAP = CONTEXT.mapCB();
  public static final CatB ORDER = CONTEXT.orderCB();
  public static final CatB PARAM_REF = CONTEXT.paramRefCB(INT);
  public static final CatB SELECT = CONTEXT.selectCB(INT);

  public static final ArrayTB ARRAY_ANY = array(ANY);
  public static final ArrayTB ARRAY_BLOB = array(BLOB);
  public static final ArrayTB ARRAY_BOOL = array(BOOL);
  public static final ArrayTB ARRAY_FUNCTION = array(FUNC);
  public static final ArrayTB ARRAY_INT = array(INT);
  public static final ArrayTB ARRAY_METHOD = array(METHOD);
  public static final ArrayTB ARRAY_NOTHING = array(NOTHING);
  public static final ArrayTB ARRAY_STR = array(STRING);
  public static final ArrayTB ARRAY_PERSON_TUPLE = array(PERSON);
  public static final ArrayTB ARRAY_PERSON = array(PERSON);
  public static final ArrayTB ARRAY_VAR = array(VAR_A);

  public static final ArrayTB ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArrayTB ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayTB ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayTB ARRAY2_FUNCTION = array(ARRAY_FUNCTION);
  public static final ArrayTB ARRAY2_INT = array(ARRAY_INT);
  public static final ArrayTB ARRAY2_METHOD = array(ARRAY_METHOD);
  public static final ArrayTB ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayTB ARRAY2_STR = array(ARRAY_STR);
  public static final ArrayTB ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArrayTB ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayTB ARRAY2_VAR = array(ARRAY_VAR);

  public static final ImmutableList<CatB> BASE_CATS_TO_TEST = list(
      BLOB,
      BOOL,
      FUNC,
      INT,
      METHOD,
      NOTHING,
      STRING,
      PERSON
  );

  public static final ImmutableList<CatB> ARRAY_CATS_TO_TEST = list(
      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_FUNCTION,
      ARRAY_INT,
      ARRAY_METHOD,
      ARRAY_NOTHING,
      ARRAY_STR,
      ARRAY_PERSON_TUPLE,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_FUNCTION,
      ARRAY2_INT,
      ARRAY2_METHOD,
      ARRAY2_NOTHING,
      ARRAY2_STR,
      ARRAY2_PERSON_TUPLE
  );

  public static final ImmutableList<CatB> CATS_TO_TEST =
      concat(BASE_CATS_TO_TEST, ARRAY_CATS_TO_TEST);

  public static final ImmutableList<CatB> ALL_CATS_TO_TEST = createAllCats();

  private static ImmutableList<CatB> createAllCats() {
    var baseCs = list(
        ANY,
        BLOB,
        BOOL,
        func(BLOB, list()),
        func(BLOB, list(BLOB)),
        func(BLOB, list(BLOB, BLOB)),
        func(STRING, list()),
        INT,
        method(BLOB, list()),
        method(BLOB, list(BLOB)),
        method(BLOB, list(BLOB, BLOB)),
        method(STRING, list()),
        NOTHING,
        STRING,
        tuple(list()),
        tuple(list(BLOB)),
        tuple(list(BLOB, BLOB)),
        tuple(list(STRING)),
        var("A"),
        var("B")
    );
    var arrayCs = map(baseCs, CAT_DB::array);
    var valueCs = concat(baseCs, arrayCs);
    var exprCs = list(
        CAT_DB.call(BLOB),
        CAT_DB.call(STRING),
        CAT_DB.combine(CAT_DB.tuple(list(BLOB))),
        CAT_DB.combine(CAT_DB.tuple(list(STRING))),
        CAT_DB.if_(BLOB),
        CAT_DB.if_(STRING),
        CAT_DB.invoke(BLOB),
        CAT_DB.invoke(STRING),
        CAT_DB.map(ARRAY_BLOB),
        CAT_DB.map(ARRAY_STR),
        CAT_DB.order(ARRAY_BLOB),
        CAT_DB.order(ARRAY_STR),
        CAT_DB.paramRef(BLOB),
        CAT_DB.paramRef(STRING),
        CAT_DB.select(BLOB),
        CAT_DB.select(STRING)
    );

    return concat(valueCs, exprCs);
  }

  public static ArrayTB array(TypeB elemT) {
    return CAT_DB.array(elemT);
  }

  public static FuncTB func(TypeB res, ImmutableList<TypeB> params) {
    return CONTEXT.funcTB(res, params);
  }

  public static MethodTB method(TypeB res, ImmutableList<TypeB> params) {
    return CONTEXT.methodTB(res, params);
  }

  public static TupleTB tuple(ImmutableList<TypeB> params) {
    return CAT_DB.tuple(params);
  }

  public static VarTB var(String name) {
    return CAT_DB.var(name);
  }
}
