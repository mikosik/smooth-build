package org.smoothbuild.record.type;

import org.smoothbuild.record.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class TestingTypes {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();
  public static final BinaryType TYPE = OBJECT_DB.typeType();
  public static final BinaryType BOOL = OBJECT_DB.boolType();
  public static final BinaryType STRING = OBJECT_DB.stringType();
  public static final BinaryType BLOB = OBJECT_DB.blobType();
  public static final BinaryType NOTHING = OBJECT_DB.nothingType();
  public static final TupleType PERSON = CONTEXT.personType();

  public static final ArrayType ARRAY_TYPE = array(TYPE);
  public static final ArrayType ARRAY_BOOL = array(BOOL);
  public static final ArrayType ARRAY_STRING = array(STRING);
  public static final ArrayType ARRAY_BLOB = array(BLOB);
  public static final ArrayType ARRAY_NOTHING = array(NOTHING);
  public static final ArrayType ARRAY_PERSON = array(PERSON);

  public static final ArrayType ARRAY2_TYPE = array(ARRAY_TYPE);
  public static final ArrayType ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArrayType ARRAY2_STRING = array(ARRAY_STRING);
  public static final ArrayType ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArrayType ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArrayType ARRAY2_PERSON = array(ARRAY_PERSON);

  private static ArrayType array(BinaryType elemType) {
    return OBJECT_DB.arrayType(elemType);
  }
}
