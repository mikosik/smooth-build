package org.smoothbuild.db.object.spec;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class TestingSpecs {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();
  public static final Spec BOOL = OBJECT_DB.boolSpec();
  public static final Spec STRING = OBJECT_DB.stringSpec();
  public static final Spec BLOB = OBJECT_DB.blobSpec();
  public static final Spec NOTHING = OBJECT_DB.nothingSpec();
  public static final TupleSpec PERSON = CONTEXT.personSpec();

  public static final ArraySpec ARRAY_BOOL = array(BOOL);
  public static final ArraySpec ARRAY_STRING = array(STRING);
  public static final ArraySpec ARRAY_BLOB = array(BLOB);
  public static final ArraySpec ARRAY_NOTHING = array(NOTHING);
  public static final ArraySpec ARRAY_PERSON = array(PERSON);

  public static final ArraySpec ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArraySpec ARRAY2_STRING = array(ARRAY_STRING);
  public static final ArraySpec ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArraySpec ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArraySpec ARRAY2_PERSON = array(ARRAY_PERSON);

  private static ArraySpec array(Spec elemSpec) {
    return OBJECT_DB.arraySpec(elemSpec);
  }
}
