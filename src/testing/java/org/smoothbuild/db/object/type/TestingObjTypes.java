package org.smoothbuild.db.object.type;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingObjTypes {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();
  public static final ObjTypeDb OBJECT_TYPE_DB = CONTEXT.objTypeDb();

  public static final ValType ANY = OBJECT_TYPE_DB.any();
  public static final ValType BLOB = OBJECT_TYPE_DB.blob();
  public static final ValType BOOL = OBJECT_TYPE_DB.bool();
  public static final ValType LAMBDA = OBJECT_TYPE_DB.function(BLOB, list(BOOL));
  public static final ValType VARIABLE = OBJECT_TYPE_DB.variable("A");
  public static final ValType INT = OBJECT_TYPE_DB.int_();
  public static final ValType NOTHING = OBJECT_TYPE_DB.nothing();
  public static final ValType STR = OBJECT_TYPE_DB.string();
  public static final TupleOType PERSON_TUPLE = CONTEXT.perso_OT();
  public static final StructOType PERSON = CONTEXT.personOT();
  public static final TupleOType FILE = CONTEXT.fileOT();
  public static final TupleOType EMPTY_TUPLE = CONTEXT.tupleEmptyOT();
  public static final TupleOType TUPLE_WITH_STRING = CONTEXT.tupleWithStrOT();
  public static final ObjType CALL = CONTEXT.callOT();
  public static final ObjType CONST = CONTEXT.constOT(STR);
  public static final ObjType ORDER = CONTEXT.orderOT();
  public static final ObjType CONSTRUCT = CONTEXT.constructOT(list(INT, STR));
  public static final ObjType SELECT = CONTEXT.selectOT(INT);
  public static final ObjType REF = CONTEXT.refOT(INT);

  public static final ArrayOType ARRAY_ANY = array(ANY);
  public static final ArrayOType ARRAY_BLOB = array(BLOB);
  public static final ArrayOType ARRAY_BOOL = array(BOOL);
  public static final ArrayOType ARRAY_LAMBDA = array(LAMBDA);
  public static final ArrayOType ARRAY_INT = array(INT);
  public static final ArrayOType ARRAY_NOTHING = array(NOTHING);
  public static final ArrayOType ARRAY_STR = array(STR);
  public static final ArrayOType ARRAY_PERSON_TUPLE = array(PERSON_TUPLE);
  public static final ArrayOType ARRAY_PERSON = array(PERSON);
  public static final ArrayOType ARRAY_VARIABLE = array(VARIABLE);

  public static final ArrayOType ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArrayOType ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayOType ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayOType ARRAY2_LAMBDA = array(ARRAY_LAMBDA);
  public static final ArrayOType ARRAY2_INT = array(ARRAY_INT);
  public static final ArrayOType ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayOType ARRAY2_STR = array(ARRAY_STR);
  public static final ArrayOType ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArrayOType ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArrayOType ARRAY2_VARIABLE = array(ARRAY_VARIABLE);

  public static final ImmutableList<ObjType> VAL_TYPES_TO_TEST = list(
      BLOB,
      BOOL,
      LAMBDA,
      INT,
      NOTHING,
      STR,
      PERSON_TUPLE,

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

  private static ArrayOType array(ValType elemType) {
    return OBJECT_TYPE_DB.array(elemType);
  }
}
