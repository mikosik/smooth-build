package org.smoothbuild.db.object.spec;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.collect.ImmutableList;

public class TestingSpecs {
  private static final TestingContextImpl CONTEXT = new TestingContextImpl();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();
  public static final SpecDb SPEC_DB = CONTEXT.specDb();

  public static final ValSpec ABSENT = SPEC_DB.absentSpec();
  public static final ValSpec ANY = SPEC_DB.any();
  public static final ValSpec BLOB = SPEC_DB.blob();
  public static final ValSpec BOOL = SPEC_DB.bool();
  public static final ValSpec LAMBDA = SPEC_DB.function(BLOB, list(BOOL));
  public static final ValSpec VARIABLE = SPEC_DB.variable("A");
  public static final ValSpec INT = SPEC_DB.int_();
  public static final ValSpec NOTHING = SPEC_DB.nothing();
  public static final ValSpec STR = SPEC_DB.string();
  public static final RecSpec PERSON_REC = CONTEXT.perso_Spec();
  public static final StructSpec PERSON = CONTEXT.personSpec();
  public static final RecSpec FILE = CONTEXT.fileSpec();
  public static final RecSpec EMPTY_REC = CONTEXT.emptyRecSpec();
  public static final RecSpec REC_WITH_STRING = CONTEXT.recWithStrSpec();
  public static final Spec CALL = CONTEXT.callSpec();
  public static final Spec CONST = CONTEXT.constSpec(STR);
  public static final Spec ARRAY_EXPR = CONTEXT.arrayExprSpec();
  public static final Spec ERECORD = CONTEXT.recExprSpec(list(INT, STR));
  public static final Spec SELECT = CONTEXT.selectSpec(INT);
  public static final Spec NULL = CONTEXT.nullSpec();
  public static final Spec REF = CONTEXT.refSpec(INT);

  public static final ArraySpec ARRAY_ANY = array(ANY);
  public static final ArraySpec ARRAY_ABSENT = array(ABSENT);
  public static final ArraySpec ARRAY_BLOB = array(BLOB);
  public static final ArraySpec ARRAY_BOOL = array(BOOL);
  public static final ArraySpec ARRAY_LAMBDA = array(LAMBDA);
  public static final ArraySpec ARRAY_INT = array(INT);
  public static final ArraySpec ARRAY_NOTHING = array(NOTHING);
  public static final ArraySpec ARRAY_STR = array(STR);
  public static final ArraySpec ARRAY_PERSON_REC = array(PERSON_REC);
  public static final ArraySpec ARRAY_PERSON = array(PERSON);
  public static final ArraySpec ARRAY_VARIABLE = array(VARIABLE);

  public static final ArraySpec ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArraySpec ARRAY2_ABSENT = array(ARRAY_ABSENT);
  public static final ArraySpec ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArraySpec ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArraySpec ARRAY2_LAMBDA = array(ARRAY_LAMBDA);
  public static final ArraySpec ARRAY2_INT = array(ARRAY_INT);
  public static final ArraySpec ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArraySpec ARRAY2_STR = array(ARRAY_STR);
  public static final ArraySpec ARRAY2_PERSON_REC = array(ARRAY_PERSON_REC);
  public static final ArraySpec ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArraySpec ARRAY2_VARIABLE = array(ARRAY_VARIABLE);

  public static final ImmutableList<Spec> VAL_SPECS_TO_TEST = list(
      BLOB,
      BOOL,
      LAMBDA,
      INT,
      NOTHING,
      STR,
      PERSON_REC,

      ARRAY_BLOB,
      ARRAY_BOOL,
      ARRAY_LAMBDA,
      ARRAY_INT,
      ARRAY_NOTHING,
      ARRAY_STR,
      ARRAY_PERSON_REC,

      ARRAY2_BLOB,
      ARRAY2_BOOL,
      ARRAY2_LAMBDA,
      ARRAY2_INT,
      ARRAY2_NOTHING,
      ARRAY2_STR,
      ARRAY2_PERSON_REC
  );

  private static ArraySpec array(ValSpec elemSpec) {
    return SPEC_DB.array(elemSpec);
  }
}
