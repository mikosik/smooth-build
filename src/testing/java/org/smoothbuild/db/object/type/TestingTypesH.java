package org.smoothbuild.db.object.type;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.Arrays.stream;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.lang.reflect.Method;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingTypesH {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final TypeHDb TYPEH_DB = CONTEXT.typeHDb();

  public static final TypeHV ANY = TYPEH_DB.any();
  public static final TypeHV BLOB = TYPEH_DB.blob();
  public static final TypeHV BOOL = TYPEH_DB.bool();
  public static final TypeHV INT = TYPEH_DB.int_();
  public static final TypeHV IF_FUNCTION = TYPEH_DB.ifFunction();
  public static final TypeHV FUNCTION = TYPEH_DB.function(BLOB, list(BOOL));
  public static final TypeHV MAP_FUNCTION = TYPEH_DB.mapFunction();
  public static final TypeHV NOTHING = TYPEH_DB.nothing();
  public static final TypeHV STRING = TYPEH_DB.string();
  public static final TypeHV VARIABLE = TYPEH_DB.variable("A");

  public static final TupleTypeH PERSON = CONTEXT.personHT();
  public static final TupleTypeH FILE = CONTEXT.fileHT();
  public static final TupleTypeH TUPLE_EMPTY = CONTEXT.tupleEmptyHT();
  public static final TupleTypeH TUPLE_WITH_STRING = CONTEXT.tupleWithStrHT();

  public static final TypeH CALL = CONTEXT.callHT();
  public static final TypeH ORDER = CONTEXT.orderHT();
  public static final TypeH CONSTRUCT = CONTEXT.constructHT(list(INT, STRING));
  public static final TypeH SELECT = CONTEXT.selectHT(INT);
  public static final TypeH REF = CONTEXT.refHT(INT);

  public static final ArrayTypeH ARRAY_ANY = array(ANY);
  public static final ArrayTypeH ARRAY_BLOB = array(BLOB);
  public static final ArrayTypeH ARRAY_BOOL = array(BOOL);
  public static final ArrayTypeH ARRAY_FUNCTION = array(FUNCTION);
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

  public static final ImmutableList<TypeH> BASE_TYPESV_TO_TEST = list(
      BLOB,
      BOOL,
      FUNCTION,
      INT,
      NOTHING,
      STRING,
      PERSON
  );

  public static final ImmutableList<TypeH> ARRAY_TYPESV_TO_TEST = list(
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

  public static final ImmutableList<TypeH> TYPESV_TO_TEST =
      concat(BASE_TYPESV_TO_TEST, ARRAY_TYPESV_TO_TEST);

  private static final ImmutableList<String> TYPEH_DB_METHOD_NAMES = ImmutableList.of(
      "any", "array", "array", "blob", "bool", "call", "construct", "definedFunction",
      "equals", "function", "function", "get", "getClass", "hashCode", "ifFunction", "int_",
      "lower", "mapFunction", "nativeFunction", "nothing", "notify", "notifyAll", "oneSideBound",
      "oneSideBound", "order", "ref", "select", "string", "toString", "tuple", "unbounded", "upper",
      "variable", "wait", "wait", "wait"
      );

  private static final ImmutableList<String> TYPEH_DB_ACTUAL_METHOD_NAMES =
      stream(TypeHDb.class.getMethods())
          .map(Method::getName)
          .sorted()
          .collect(toImmutableList());

  public static final ImmutableList<TypeH> ALL_TYPES_TO_TEST = createAllTypes();

  private static ImmutableList<TypeH> createAllTypes() {
    assertTypeHDbIsNotChanged();
    var baseTypes = list(
        BLOB,
        BOOL,
        TYPEH_DB.function(BLOB, list()),
        TYPEH_DB.function(BLOB, list(BLOB)),
        TYPEH_DB.function(BLOB, list(BLOB, BLOB)),
        TYPEH_DB.function(STRING, list()),
        IF_FUNCTION,
        INT,
        MAP_FUNCTION,
        NOTHING,
        STRING,
        TYPEH_DB.tuple(list()),
        TYPEH_DB.tuple(list(BLOB)),
        TYPEH_DB.tuple(list(BLOB, BLOB)),
        TYPEH_DB.tuple(list(STRING)),
        TYPEH_DB.variable("A"),
        TYPEH_DB.variable("B")
    );
    var arrayTypes = map(baseTypes, TYPEH_DB::array);
    ImmutableList<TypeH> valueTypes = concat(baseTypes, arrayTypes);

    var exprTypes = list(
        TYPEH_DB.call(BLOB),
        TYPEH_DB.call(STRING),
        TYPEH_DB.construct(TYPEH_DB.tuple(list(BLOB))),
        TYPEH_DB.construct(TYPEH_DB.tuple(list(STRING))),
        TYPEH_DB.order(BLOB),
        TYPEH_DB.order(STRING),
        TYPEH_DB.ref(BLOB),
        TYPEH_DB.ref(STRING),
        TYPEH_DB.select(BLOB),
        TYPEH_DB.select(STRING)
    );

    return concat(valueTypes, exprTypes);
  }

  public static void assertTypeHDbIsNotChanged() {
    assertWithMessage(
        "TypeHDb API changed (method was changed). Update all tests that call this method.")
        .that(TYPEH_DB_ACTUAL_METHOD_NAMES)
        .containsExactlyElementsIn(TYPEH_DB_METHOD_NAMES);
  }

  private static ArrayTypeH array(TypeHV elemType) {
    return TYPEH_DB.array(elemType);
  }

  public static TupleTypeH tuple(ImmutableList<TypeHV> itemTypes) {
    return TYPEH_DB.tuple(itemTypes);
  }

  public static ConstructTypeH construct(TupleTypeH tupleType) {
    return TYPEH_DB.construct(tupleType);
  }
}
