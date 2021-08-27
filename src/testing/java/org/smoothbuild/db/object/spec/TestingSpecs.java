package org.smoothbuild.db.object.spec;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingSpecs {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();

  public static final ValSpec BLOB = OBJECT_DB.blobS();
  public static final ValSpec BOOL = OBJECT_DB.boolS();
  public static final ValSpec INT = OBJECT_DB.intS();
  public static final ValSpec NOTHING = OBJECT_DB.nothingS();
  public static final ValSpec STR = OBJECT_DB.strS();
  public static final TupleSpec PERSON = CONTEXT.personS();
  public static final Spec CALL = CONTEXT.callS();
  public static final Spec CONST = CONTEXT.constS();
  public static final Spec EARRAY = CONTEXT.eArrayS();
  public static final Spec FIELD_READ = CONTEXT.fieldReadS();

  public static final ArraySpec ARRAY_BLOB = array(BLOB);
  public static final ArraySpec ARRAY_BOOL = array(BOOL);
  public static final ArraySpec ARRAY_INT = array(INT);
  public static final ArraySpec ARRAY_NOTHING = array(NOTHING);
  public static final ArraySpec ARRAY_STR = array(STR);
  public static final ArraySpec ARRAY_PERSON = array(PERSON);

  public static final ArraySpec ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArraySpec ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArraySpec ARRAY2_INT = array(ARRAY_INT);
  public static final ArraySpec ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArraySpec ARRAY2_STR = array(ARRAY_STR);
  public static final ArraySpec ARRAY2_PERSON = array(ARRAY_PERSON);

  public static final ImmutableList<Spec> VAL_SPECS_TO_TEST = list(
      BLOB,
      BOOL,
      INT,
      NOTHING,
      STR,
      PERSON,

      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_INT,
      ARRAY_NOTHING,
      ARRAY_STR,
      ARRAY_PERSON,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_INT,
      ARRAY2_NOTHING,
      ARRAY2_STR,
      ARRAY2_PERSON
  );

  private static ArraySpec array(ValSpec elemSpec) {
    return OBJECT_DB.arrayS(elemSpec);
  }
}
