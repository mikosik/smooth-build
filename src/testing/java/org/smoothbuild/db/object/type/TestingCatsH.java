package org.smoothbuild.db.object.type;

import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.CombineCH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingCatsH {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final CatDb TYPEH_DB = CONTEXT.catDb();

  public static final TypeH ANY = TYPEH_DB.any();
  public static final TypeH BLOB = TYPEH_DB.blob();
  public static final TypeH BOOL = TYPEH_DB.bool();
  public static final TypeH INT = TYPEH_DB.int_();
  public static final TypeH IF_FUNC = TYPEH_DB.ifFunc();
  public static final TypeH ABST_FUNC = TYPEH_DB.func(BLOB, list(BOOL));
  public static final TypeH MAP_FUNC = TYPEH_DB.mapFunc();
  public static final TypeH NOTHING = TYPEH_DB.nothing();
  public static final TypeH STRING = TYPEH_DB.string();
  public static final TypeH VARIABLE = TYPEH_DB.var("A");

  public static final TupleTH PERSON = CONTEXT.personTH();
  public static final TupleTH FILE = CONTEXT.fileTH();
  public static final TupleTH TUPLE_EMPTY = CONTEXT.tupleEmptyTH();
  public static final TupleTH TUPLE_WITH_STRING = CONTEXT.tupleWithStrTH();

  public static final CatH CALL = CONTEXT.callCH();
  public static final CatH ORDER = CONTEXT.orderCH();
  public static final CatH COMBINE = CONTEXT.combineCH(list(INT, STRING));
  public static final CatH SELECT = CONTEXT.selectCH(INT);
  public static final CatH PARAM_REF = CONTEXT.paramRefCH(INT);

  public static final ArrayTH ARRAY_ANY = array(ANY);
  public static final ArrayTH ARRAY_BLOB = array(BLOB);
  public static final ArrayTH ARRAY_BOOL = array(BOOL);
  public static final ArrayTH ARRAY_FUNCTION = array(ABST_FUNC);
  public static final ArrayTH ARRAY_INT = array(INT);
  public static final ArrayTH ARRAY_NOTHING = array(NOTHING);
  public static final ArrayTH ARRAY_STR = array(STRING);
  public static final ArrayTH ARRAY_PERSON_TUPLE = array(PERSON);
  public static final ArrayTH ARRAY_PERSON = array(PERSON);
  public static final ArrayTH ARRAY_VARIABLE = array(VARIABLE);

  public static final ArrayTH ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArrayTH ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayTH ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayTH ARRAY2_FUNCTION = array(ARRAY_FUNCTION);
  public static final ArrayTH ARRAY2_INT = array(ARRAY_INT);
  public static final ArrayTH ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayTH ARRAY2_STR = array(ARRAY_STR);
  public static final ArrayTH ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArrayTH ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayTH ARRAY2_VARIABLE = array(ARRAY_VARIABLE);

  public static final ImmutableList<CatH> BASE_CATS_TO_TEST = list(
      BLOB,
      BOOL, ABST_FUNC,
      INT,
      NOTHING,
      STRING,
      PERSON
  );

  public static final ImmutableList<CatH> ARRAY_CATS_TO_TEST = list(
      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_FUNCTION,
      ARRAY_INT,
      ARRAY_NOTHING,
      ARRAY_STR,
      ARRAY_PERSON_TUPLE,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_FUNCTION,
      ARRAY2_INT,
      ARRAY2_NOTHING,
      ARRAY2_STR,
      ARRAY2_PERSON_TUPLE
  );

  public static final ImmutableList<CatH> CATS_TO_TEST =
      concat(BASE_CATS_TO_TEST, ARRAY_CATS_TO_TEST);

  public static final ImmutableList<CatH> ALL_CATS_TO_TEST = createAllCats();

  private static ImmutableList<CatH> createAllCats() {
    var baseCs = list(
        ANY,
        BLOB,
        BOOL,
        TYPEH_DB.func(BLOB, list()),
        TYPEH_DB.func(BLOB, list(BLOB)),
        TYPEH_DB.func(BLOB, list(BLOB, BLOB)),
        TYPEH_DB.func(STRING, list()),
        TYPEH_DB.defFunc(BLOB, list()),
        TYPEH_DB.natFunc(BLOB, list()),
        IF_FUNC,
        INT,
        MAP_FUNC,
        NOTHING,
        STRING,
        TYPEH_DB.tuple(list()),
        TYPEH_DB.tuple(list(BLOB)),
        TYPEH_DB.tuple(list(BLOB, BLOB)),
        TYPEH_DB.tuple(list(STRING)),
        TYPEH_DB.var("A"),
        TYPEH_DB.var("B")
    );
    var arrayCs = map(baseCs, TYPEH_DB::array);
    ImmutableList<CatH> valueCs = concat(baseCs, arrayCs);

    var exprCs = list(
        TYPEH_DB.call(BLOB),
        TYPEH_DB.call(STRING),
        TYPEH_DB.combine(TYPEH_DB.tuple(list(BLOB))),
        TYPEH_DB.combine(TYPEH_DB.tuple(list(STRING))),
        TYPEH_DB.order(BLOB),
        TYPEH_DB.order(STRING),
        TYPEH_DB.ref(BLOB),
        TYPEH_DB.ref(STRING),
        TYPEH_DB.select(BLOB),
        TYPEH_DB.select(STRING)
    );

    return concat(valueCs, exprCs);
  }

  private static ArrayTH array(TypeH elemT) {
    return TYPEH_DB.array(elemT);
  }

  public static TupleTH tuple(ImmutableList<TypeH> itemTs) {
    return TYPEH_DB.tuple(itemTs);
  }

  public static CombineCH combine(TupleTH tupleType) {
    return TYPEH_DB.combine(tupleType);
  }
}
