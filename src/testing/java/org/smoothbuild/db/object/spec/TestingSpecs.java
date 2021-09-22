package org.smoothbuild.db.object.spec;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TestingSpecs {
  private static final TestingContext CONTEXT = new TestingContext();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();
  public static final SpecDb SPEC_DB = CONTEXT.specDb();

  public static final ValSpec BLOB = SPEC_DB.blobSpec();
  public static final ValSpec BOOL = SPEC_DB.boolSpec();
  public static final ValSpec DEFINED_LAMBDA =
      SPEC_DB.definedLambdaSpec(BLOB, SPEC_DB.recSpec(list(BOOL)));
  public static final ValSpec INT = SPEC_DB.intSpec();
  public static final ValSpec NATIVE_LAMBDA =
      SPEC_DB.nativeLambdaSpec(BLOB, SPEC_DB.recSpec(list(BOOL)));
  public static final ValSpec NOTHING = SPEC_DB.nothingSpec();
  public static final ValSpec STR = SPEC_DB.strSpec();
  public static final RecSpec PERSON = CONTEXT.personSpec();
  public static final RecSpec FILE = CONTEXT.fileSpec();
  public static final RecSpec EMPTY_REC = CONTEXT.emptyRecSpec();
  public static final RecSpec REC_WITH_STRING = CONTEXT.recWithStrSpec();
  public static final Spec CALL = CONTEXT.callSpec();
  public static final Spec CONST = CONTEXT.constSpec(STR);
  public static final Spec EARRAY = CONTEXT.eArraySpec();
  public static final Spec FIELD_READ = CONTEXT.fieldReadSpec(INT);
  public static final Spec NULL = CONTEXT.nullSpec();
  public static final Spec REF = CONTEXT.refSpec(INT);

  public static final ArraySpec ARRAY_BLOB = array(BLOB);
  public static final ArraySpec ARRAY_BOOL = array(BOOL);
  public static final ArraySpec ARRAY_DEFINED_LAMBDA = array(DEFINED_LAMBDA);
  public static final ArraySpec ARRAY_INT = array(INT);
  public static final ArraySpec ARRAY_NATIVE_LAMBDA = array(NATIVE_LAMBDA);
  public static final ArraySpec ARRAY_NOTHING = array(NOTHING);
  public static final ArraySpec ARRAY_STR = array(STR);
  public static final ArraySpec ARRAY_PERSON = array(PERSON);

  public static final ArraySpec ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArraySpec ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArraySpec ARRAY2_DEFINED_LAMBDA = array(ARRAY_DEFINED_LAMBDA);
  public static final ArraySpec ARRAY2_INT = array(ARRAY_INT);
  public static final ArraySpec ARRAY2_NATIVE_LAMBDA = array(ARRAY_NATIVE_LAMBDA);
  public static final ArraySpec ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArraySpec ARRAY2_STR = array(ARRAY_STR);
  public static final ArraySpec ARRAY2_PERSON = array(ARRAY_PERSON);

  public static final ImmutableList<Spec> VAL_SPECS_TO_TEST = list(
      BLOB,
      BOOL,
      DEFINED_LAMBDA,
      INT,
      NATIVE_LAMBDA,
      NOTHING,
      STR,
      PERSON,

      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_DEFINED_LAMBDA,
      ARRAY_INT,
      ARRAY_NATIVE_LAMBDA,
      ARRAY_NOTHING,
      ARRAY_STR,
      ARRAY_PERSON,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_DEFINED_LAMBDA,
      ARRAY2_INT,
      ARRAY2_NATIVE_LAMBDA,
      ARRAY2_NOTHING,
      ARRAY2_STR,
      ARRAY2_PERSON
  );

  private static ArraySpec array(ValSpec elemSpec) {
    return SPEC_DB.arraySpec(elemSpec);
  }
}
