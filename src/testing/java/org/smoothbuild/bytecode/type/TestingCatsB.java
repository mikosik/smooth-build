package org.smoothbuild.bytecode.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingCatsB {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final CatDb CAT_DB = CONTEXT.catDb();

  public static final TypeB ANY = CAT_DB.any();
  public static final TypeB BLOB = CAT_DB.blob();
  public static final TypeB BOOL = CAT_DB.bool();
  public static final TypeB INT = CAT_DB.int_();
  public static final TypeB FUNC = CAT_DB.func(BLOB, list(BOOL));
  public static final TypeB METHOD = CAT_DB.method(BLOB, list(BOOL));
  public static final TypeB NOTHING = CAT_DB.nothing();
  public static final TypeB STRING = CAT_DB.string();
  public static final TypeB OPEN_VARIABLE = CAT_DB.oVar("A");
  public static final TypeB CLOSED_VARIABLE = CAT_DB.cVar("A");

  public static final TupleTB PERSON = CONTEXT.personTB();
  public static final TupleTB FILE = CONTEXT.fileTB();
  public static final TupleTB TUPLE_EMPTY = CONTEXT.tupleEmptyTB();
  public static final TupleTB TUPLE_WITH_STRING = CONTEXT.tupleWithStrTB();

  public static final CatB CALL = CONTEXT.callCB();
  public static final CatB COMBINE = CONTEXT.combineCB(list(INT, STRING));
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
  public static final ArrayTB ARRAY_OPEN_VARIABLE = array(OPEN_VARIABLE);
  public static final ArrayTB ARRAY_CLOSED_VARIABLE = array(CLOSED_VARIABLE);

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
  public static final ArrayTB ARRAY2_OPEN_VARIABLE = array(ARRAY_OPEN_VARIABLE);
  public static final ArrayTB ARRAY2_CLOSED_VARIABLE = array(ARRAY_CLOSED_VARIABLE);

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
        CAT_DB.func(BLOB, list()),
        CAT_DB.func(BLOB, list(BLOB)),
        CAT_DB.func(BLOB, list(BLOB, BLOB)),
        CAT_DB.func(STRING, list()),
        INT,
        CAT_DB.method(BLOB, list()),
        CAT_DB.method(BLOB, list(BLOB)),
        CAT_DB.method(BLOB, list(BLOB, BLOB)),
        CAT_DB.method(STRING, list()),
        NOTHING,
        STRING,
        CAT_DB.tuple(list()),
        CAT_DB.tuple(list(BLOB)),
        CAT_DB.tuple(list(BLOB, BLOB)),
        CAT_DB.tuple(list(STRING)),
        CAT_DB.oVar("A"),
        CAT_DB.oVar("B"),
        CAT_DB.cVar("A"),
        CAT_DB.cVar("B")
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
        CAT_DB.order(BLOB),
        CAT_DB.order(STRING),
        CAT_DB.paramRef(BLOB),
        CAT_DB.paramRef(STRING),
        CAT_DB.select(BLOB),
        CAT_DB.select(STRING)
    );

    return concat(valueCs, exprCs);
  }

  private static ArrayTB array(TypeB elemT) {
    return CAT_DB.array(elemT);
  }
}
