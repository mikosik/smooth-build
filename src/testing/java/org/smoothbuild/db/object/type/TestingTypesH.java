package org.smoothbuild.db.object.type;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.Arrays.stream;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.lang.reflect.Method;

import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.CombineTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingTypesH {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final TypeDb TYPEH_DB = CONTEXT.typeDb();

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

  public static final TupleTypeH PERSON = CONTEXT.personHT();
  public static final TupleTypeH FILE = CONTEXT.fileHT();
  public static final TupleTypeH TUPLE_EMPTY = CONTEXT.tupleEmptyHT();
  public static final TupleTypeH TUPLE_WITH_STRING = CONTEXT.tupleWithStrHT();

  public static final SpecH CALL = CONTEXT.callHT();
  public static final SpecH ORDER = CONTEXT.orderHT();
  public static final SpecH CONSTRUCT = CONTEXT.combineHT(list(INT, STRING));
  public static final SpecH SELECT = CONTEXT.selectHT(INT);
  public static final SpecH PARAM_REF = CONTEXT.refHT(INT);

  public static final ArrayTypeH ARRAY_ANY = array(ANY);
  public static final ArrayTypeH ARRAY_BLOB = array(BLOB);
  public static final ArrayTypeH ARRAY_BOOL = array(BOOL);
  public static final ArrayTypeH ARRAY_FUNCTION = array(ABST_FUNC);
  public static final ArrayTypeH ARRAY_INT = array(INT);
  public static final ArrayTypeH ARRAY_NOTHING = array(NOTHING);
  public static final ArrayTypeH ARRAY_STR = array(STRING);
  public static final ArrayTypeH ARRAY_PERSON_TUPLE = array(PERSON);
  public static final ArrayTypeH ARRAY_PERSON = array(PERSON);
  public static final ArrayTypeH ARRAY_VARIABLE = array(VARIABLE);

  public static final ArrayTypeH ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArrayTypeH ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayTypeH ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayTypeH ARRAY2_FUNCTION = array(ARRAY_FUNCTION);
  public static final ArrayTypeH ARRAY2_INT = array(ARRAY_INT);
  public static final ArrayTypeH ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayTypeH ARRAY2_STR = array(ARRAY_STR);
  public static final ArrayTypeH ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArrayTypeH ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayTypeH ARRAY2_VARIABLE = array(ARRAY_VARIABLE);

  public static final ImmutableList<SpecH> BASE_TYPESV_TO_TEST = list(
      BLOB,
      BOOL, ABST_FUNC,
      INT,
      NOTHING,
      STRING,
      PERSON
  );

  public static final ImmutableList<SpecH> ARRAY_TYPESV_TO_TEST = list(
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

  public static final ImmutableList<SpecH> TYPESV_TO_TEST =
      concat(BASE_TYPESV_TO_TEST, ARRAY_TYPESV_TO_TEST);

  private static final ImmutableList<String> TYPEH_DB_METHOD_NAMES = ImmutableList.of(
      "any", "array", "array", "blob", "bool", "call", "combine", "defFunc",
      "equals", "func", "func", "get", "getClass", "hashCode", "ifFunc", "int_",
      "lower", "mapFunc", "natFunc", "nothing", "notify", "notifyAll", "oneSideBound",
      "oneSideBound", "order", "ref", "select", "string", "toString", "tuple", "unbounded", "upper",
      "var", "wait", "wait", "wait"
      );

  private static final ImmutableList<String> TYPEH_DB_ACTUAL_METHOD_NAMES =
      stream(TypeDb.class.getMethods())
          .map(Method::getName)
          .sorted()
          .collect(toImmutableList());

  public static final ImmutableList<SpecH> ALL_TYPES_TO_TEST = createAllTypes();

  private static ImmutableList<SpecH> createAllTypes() {
    assertTypeDbIsNotChanged();
    var baseTypes = list(
        BLOB,
        BOOL,
        TYPEH_DB.func(BLOB, list()),
        TYPEH_DB.func(BLOB, list(BLOB)),
        TYPEH_DB.func(BLOB, list(BLOB, BLOB)),
        TYPEH_DB.func(STRING, list()), IF_FUNC,
        INT, MAP_FUNC,
        NOTHING,
        STRING,
        TYPEH_DB.tuple(list()),
        TYPEH_DB.tuple(list(BLOB)),
        TYPEH_DB.tuple(list(BLOB, BLOB)),
        TYPEH_DB.tuple(list(STRING)),
        TYPEH_DB.var("A"),
        TYPEH_DB.var("B")
    );
    var arrayTypes = map(baseTypes, TYPEH_DB::array);
    ImmutableList<SpecH> valueTypes = concat(baseTypes, arrayTypes);

    var exprTypes = list(
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

    return concat(valueTypes, exprTypes);
  }

  public static void assertTypeDbIsNotChanged() {
    assertWithMessage(
        "TypeHDb API changed (method was changed). Update all tests that call this method.")
        .that(TYPEH_DB_ACTUAL_METHOD_NAMES)
        .containsExactlyElementsIn(TYPEH_DB_METHOD_NAMES);
  }

  private static ArrayTypeH array(TypeH elemType) {
    return TYPEH_DB.array(elemType);
  }

  public static TupleTypeH tuple(ImmutableList<TypeH> itemTypes) {
    return TYPEH_DB.tuple(itemTypes);
  }

  public static CombineTypeH combine(TupleTypeH tupleType) {
    return TYPEH_DB.combine(tupleType);
  }
}
