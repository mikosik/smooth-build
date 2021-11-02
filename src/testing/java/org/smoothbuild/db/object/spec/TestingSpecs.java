package org.smoothbuild.db.object.spec;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.collect.ImmutableList;

public class TestingSpecs {
  private static final TestingContextImpl CONTEXT = new TestingContextImpl();
  public static final ObjectDb OBJECT_DB = CONTEXT.objectDb();
  public static final SpecDb SPEC_DB = CONTEXT.specDb();

  public static final ValSpec ANY = SPEC_DB.any();
  public static final ValSpec BLOB = SPEC_DB.blob();
  public static final ValSpec BOOL = SPEC_DB.bool();
  public static final ValSpec LAMBDA = SPEC_DB.function(BLOB, list(BOOL));
  public static final ValSpec VARIABLE = SPEC_DB.variable("A");
  public static final ValSpec INT = SPEC_DB.int_();
  public static final ValSpec NOTHING = SPEC_DB.nothing();
  public static final ValSpec STR = SPEC_DB.string();
  public static final TupleSpec PERSON_TUPLE = CONTEXT.perso_Spec();
  public static final StructSpec PERSON = CONTEXT.personSpec();
  public static final TupleSpec FILE = CONTEXT.fileSpec();
  public static final TupleSpec EMPTY_TUPLE = CONTEXT.emptyTupleSpec();
  public static final TupleSpec TUPLE_WITH_STRING = CONTEXT.tupleWithStrSpec();
  public static final Spec CALL = CONTEXT.callSpec();
  public static final Spec CONST = CONTEXT.constSpec(STR);
  public static final Spec ORDER = CONTEXT.orderSpec();
  public static final Spec CONSTRUCT = CONTEXT.constructSpec(list(INT, STR));
  public static final Spec SELECT = CONTEXT.selectSpec(INT);
  public static final Spec REF = CONTEXT.refSpec(INT);

  public static final ArraySpec ARRAY_ANY = array(ANY);
  public static final ArraySpec ARRAY_BLOB = array(BLOB);
  public static final ArraySpec ARRAY_BOOL = array(BOOL);
  public static final ArraySpec ARRAY_LAMBDA = array(LAMBDA);
  public static final ArraySpec ARRAY_INT = array(INT);
  public static final ArraySpec ARRAY_NOTHING = array(NOTHING);
  public static final ArraySpec ARRAY_STR = array(STR);
  public static final ArraySpec ARRAY_PERSON_TUPLE = array(PERSON_TUPLE);
  public static final ArraySpec ARRAY_PERSON = array(PERSON);
  public static final ArraySpec ARRAY_VARIABLE = array(VARIABLE);

  public static final ArraySpec ARRAY2_ANY = array(ARRAY_ANY);
  public static final ArraySpec ARRAY2_BLOB = array(ARRAY_BLOB);
  public static final ArraySpec ARRAY2_BOOL = array(ARRAY_BOOL);
  public static final ArraySpec ARRAY2_LAMBDA = array(ARRAY_LAMBDA);
  public static final ArraySpec ARRAY2_INT = array(ARRAY_INT);
  public static final ArraySpec ARRAY2_NOTHING = array(ARRAY_NOTHING);
  public static final ArraySpec ARRAY2_STR = array(ARRAY_STR);
  public static final ArraySpec ARRAY2_PERSON_TUPLE = array(ARRAY_PERSON_TUPLE);
  public static final ArraySpec ARRAY2_PERSON = array(ARRAY_PERSON);
  public static final ArraySpec ARRAY2_VARIABLE = array(ARRAY_VARIABLE);

  public static final ImmutableList<Spec> VAL_SPECS_TO_TEST = list(
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

  private static ArraySpec array(ValSpec elemSpec) {
    return SPEC_DB.array(elemSpec);
  }
}
