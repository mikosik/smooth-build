package org.smoothbuild.db.object.spec;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingSpecs {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();

  public static final Spec INT = OBJECT_DB.intSpec();
  public static final Spec BOOL = OBJECT_DB.boolSpec();
  public static final Spec STRING = OBJECT_DB.stringSpec();
  public static final Spec BLOB = OBJECT_DB.blobSpec();
  public static final Spec NOTHING = OBJECT_DB.nothingSpec();
  public static final TupleSpec PERSON = CONTEXT.personSpec();

  public static final ArraySpec ARRAY_INT = array(INT);
  public static final ArraySpec ARRAY_BOOL = array(BOOL);
  public static final ArraySpec ARRAY_STRING = array(STRING);
  public static final ArraySpec ARRAY_BLOB = array(BLOB);
  public static final ArraySpec ARRAY_NOTHING = array(NOTHING);
  public static final ArraySpec ARRAY_PERSON = array(PERSON);

  public static final ArraySpec ARRAY2_INT = array(ARRAY_INT);
  public static final ArraySpec ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArraySpec ARRAY2_STRING = array(ARRAY_STRING);
  public static final ArraySpec ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArraySpec ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArraySpec ARRAY2_PERSON = array(ARRAY_PERSON);

  public static ImmutableList<Spec> SPECS_TO_TEST = list(
      BLOB,
      BOOL,
      NOTHING,
      PERSON,
      STRING,
      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_NOTHING,
      ARRAY_PERSON,
      ARRAY_STRING,
      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_NOTHING,
      ARRAY2_PERSON,
      ARRAY2_STRING
  );
  private static ArraySpec array(Spec elemSpec) {
    return OBJECT_DB.arraySpec(elemSpec);
  }
}
