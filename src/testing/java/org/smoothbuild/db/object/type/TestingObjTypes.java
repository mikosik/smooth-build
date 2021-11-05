package org.smoothbuild.db.object.type;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingObjTypes {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjDb OBJECT_DB = CONTEXT.objectDb();
  public static final ObjTypeDb OBJECT_TYPE_DB = CONTEXT.objTypeDb();

  public static final TypeV ANY = OBJECT_TYPE_DB.any();
  public static final TypeV BLOB = OBJECT_TYPE_DB.blob();
  public static final TypeV BOOL = OBJECT_TYPE_DB.bool();
  public static final TypeV LAMBDA = OBJECT_TYPE_DB.function(BLOB, list(BOOL));
  public static final TypeV VARIABLE = OBJECT_TYPE_DB.variable("A");
  public static final TypeV INT = OBJECT_TYPE_DB.int_();
  public static final TypeV NOTHING = OBJECT_TYPE_DB.nothing();
  public static final TypeV STR = OBJECT_TYPE_DB.string();
  public static final TupleTypeO PERSON = CONTEXT.personOT();
  public static final TupleTypeO FILE = CONTEXT.fileOT();
  public static final TupleTypeO TUPLE_EMPTY = CONTEXT.tupleEmptyOT();
  public static final TupleTypeO TUPLE_WITH_STRING = CONTEXT.tupleWithStrOT();
  public static final TypeO CALL = CONTEXT.callOT();
  public static final TypeO CONST = CONTEXT.constOT(STR);
  public static final TypeO ORDER = CONTEXT.orderOT();
  public static final TypeO CONSTRUCT = CONTEXT.constructOT(list(INT, STR));
  public static final TypeO SELECT = CONTEXT.selectOT(INT);
  public static final TypeO REF = CONTEXT.refOT(INT);

  public static final ArrayTypeO ARRAY_ANY = array(ANY);
  public static final ArrayTypeO ARRAY_BLOB = array(BLOB);
  public static final ArrayTypeO ARRAY_BOOL = array(BOOL);
  public static final ArrayTypeO ARRAY_LAMBDA = array(LAMBDA);
  public static final ArrayTypeO ARRAY_INT = array(INT);
  public static final ArrayTypeO ARRAY_NOTHING = array(NOTHING);
  public static final ArrayTypeO ARRAY_STR = array(STR);
  public static final ArrayTypeO ARRAY_PERSON_TUPLE = array(PERSON);
  public static final ArrayTypeO ARRAY_PERSON = array(PERSON);
  public static final ArrayTypeO ARRAY_VARIABLE = array(VARIABLE);

  public static final ArrayTypeO ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArrayTypeO ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayTypeO ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayTypeO ARRAY2_LAMBDA = array(ARRAY_LAMBDA);
  public static final ArrayTypeO ARRAY2_INT = array(ARRAY_INT);
  public static final ArrayTypeO ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayTypeO ARRAY2_STR = array(ARRAY_STR);
  public static final ArrayTypeO ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArrayTypeO ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayTypeO ARRAY2_VARIABLE = array(ARRAY_VARIABLE);

  public static final ImmutableList<TypeO> VAL_TYPES_TO_TEST = list(
      BLOB,
      BOOL,
      LAMBDA,
      INT,
      NOTHING,
      STR,
      PERSON,

      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_LAMBDA,
      ARRAY_INT,
      ARRAY_NOTHING,
      ARRAY_STR,
      ARRAY_PERSON_TUPLE,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_LAMBDA,
      ARRAY2_INT,
      ARRAY2_NOTHING,
      ARRAY2_STR,
      ARRAY2_PERSON_TUPLE
  );

  private static ArrayTypeO array(TypeV elemType) {
    return OBJECT_TYPE_DB.array(elemType);
  }
}
