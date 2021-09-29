package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_DEFINED_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_NATIVE_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_DEFINED_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_NATIVE_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.CALL;
import static org.smoothbuild.db.object.spec.TestingSpecs.CONST;
import static org.smoothbuild.db.object.spec.TestingSpecs.DEFINED_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.EARRAY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ERECORD;
import static org.smoothbuild.db.object.spec.TestingSpecs.INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.NATIVE_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.NULL;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.REF;
import static org.smoothbuild.db.object.spec.TestingSpecs.SELECT;
import static org.smoothbuild.db.object.spec.TestingSpecs.SPEC_DB;
import static org.smoothbuild.db.object.spec.TestingSpecs.STR;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.ERec;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ERecSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.testing.TestingContext;

import com.google.common.testing.EqualsTester;

public class SpecTest {
  @ParameterizedTest
  @MethodSource("names")
  public void name(Spec spec, String name) {
    assertThat(spec.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Spec spec, String name) {
    assertThat(spec.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Spec spec, String name) {
    assertThat(spec.toString())
        .isEqualTo(name + ":" + spec.hash());
  }

  public static Stream<Arguments> names() {
    TestingContext tc = new TestingContext();
    return Stream.of(
        arguments(BLOB, "BLOB"),
        arguments(BOOL, "BOOL"),
        arguments(DEFINED_LAMBDA, "BLOB(BOOL)"),
        arguments(INT, "INT"),
        arguments(NATIVE_LAMBDA, "BLOB(BOOL)"),
        arguments(NOTHING, "NOTHING"),
        arguments(STR, "STRING"),
        arguments(PERSON, "{STRING,STRING}"),
        arguments(tc.callSpec(tc.intSpec()), "CALL:INT"),
        arguments(tc.constSpec(tc.intSpec()), "CONST:INT"),
        arguments(tc.eArraySpec(tc.strSpec()), "EARRAY:[STRING]"),
        arguments(tc.eRecSpec(list(tc.strSpec(), tc.intSpec())), "ERECORD:{STRING,INT}"),
        arguments(tc.selectSpec(tc.intSpec()), "SELECT:INT"),
        arguments(NULL, "NULL:NOTHING"),
        arguments(tc.refSpec(tc.intSpec()), "REF:INT"),

        arguments(ARRAY_BLOB, "[BLOB]"),
        arguments(ARRAY_BOOL, "[BOOL]"),
        arguments(ARRAY_DEFINED_LAMBDA, "[BLOB(BOOL)]"),
        arguments(ARRAY_INT, "[INT]"),
        arguments(ARRAY_NATIVE_LAMBDA, "[BLOB(BOOL)]"),
        arguments(ARRAY_NOTHING, "[NOTHING]"),
        arguments(ARRAY_STR, "[STRING]"),
        arguments(ARRAY_PERSON, "[{STRING,STRING}]"),

        arguments(ARRAY2_BLOB, "[[BLOB]]"),
        arguments(ARRAY2_BOOL, "[[BOOL]]"),
        arguments(ARRAY2_DEFINED_LAMBDA, "[[BLOB(BOOL)]]"),
        arguments(ARRAY2_INT, "[[INT]]"),
        arguments(ARRAY2_NATIVE_LAMBDA, "[[BLOB(BOOL)]]"),
        arguments(ARRAY2_NOTHING, "[[NOTHING]]"),
        arguments(ARRAY2_STR, "[[STRING]]"),
        arguments(ARRAY2_PERSON, "[[{STRING,STRING}]]")
    );
  }

  @ParameterizedTest
  @MethodSource("jType_test_data")
  public void jType(Spec spec, Class<?> expected) {
    assertThat(spec.jType())
        .isEqualTo(expected);
  }

  public static List<Arguments> jType_test_data() {
    return list(
        arguments(BLOB, Blob.class),
        arguments(BOOL, Bool.class),
        arguments(DEFINED_LAMBDA, DefinedLambda.class),
        arguments(INT, Int.class),
        arguments(NATIVE_LAMBDA, NativeLambda.class),
        arguments(NOTHING, null),
        arguments(PERSON, Rec.class),
        arguments(STR, Str.class),

        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_DEFINED_LAMBDA, Array.class),
        arguments(ARRAY_INT, Array.class),
        arguments(ARRAY_NATIVE_LAMBDA, Array.class),
        arguments(ARRAY_NOTHING, Array.class),
        arguments(ARRAY_PERSON, Array.class),
        arguments(ARRAY_STR, Array.class),

        arguments(CALL, Call.class),
        arguments(CONST, Const.class),
        arguments(EARRAY, EArray.class),
        arguments(ERECORD, ERec.class),
        arguments(SELECT, Select.class),
        arguments(NULL, Null.class),
        arguments(REF, Ref.class)
    );
  }

  @ParameterizedTest
  @MethodSource("elem_spec_cases")
  public void array_element(ArraySpec spec, Spec expected) {
    assertThat(spec.element())
        .isEqualTo(expected);
  }

  public static List<Arguments> elem_spec_cases() {
    return list(
        arguments(ARRAY_BLOB, BLOB),
        arguments(ARRAY_BOOL, BOOL),
        arguments(ARRAY_DEFINED_LAMBDA, DEFINED_LAMBDA),
        arguments(ARRAY_INT, INT),
        arguments(ARRAY_NATIVE_LAMBDA, NATIVE_LAMBDA),
        arguments(ARRAY_NOTHING, NOTHING),
        arguments(ARRAY_STR, STR),
        arguments(ARRAY_PERSON, PERSON),

        arguments(ARRAY2_BLOB, ARRAY_BLOB),
        arguments(ARRAY2_BOOL, ARRAY_BOOL),
        arguments(ARRAY2_DEFINED_LAMBDA, ARRAY_DEFINED_LAMBDA),
        arguments(ARRAY2_INT, ARRAY_INT),
        arguments(ARRAY2_NATIVE_LAMBDA, ARRAY_NATIVE_LAMBDA),
        arguments(ARRAY2_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY2_STR, ARRAY_STR),
        arguments(ARRAY2_PERSON, ARRAY_PERSON));
  }

  @ParameterizedTest
  @MethodSource("record_items_cases")
  public void record_item(RecSpec spec, List<Spec> expected) {
    assertThat(spec.items())
        .isEqualTo(expected);
  }

  public static List<Arguments> record_items_cases() {
    return list(
        arguments(rec(), list()),
        arguments(rec(STR), list(STR)),
        arguments(rec(STR, INT), list(STR, INT)),
        arguments(rec(STR, INT, BLOB), list(STR, INT, BLOB))
    );
  }

  @ParameterizedTest
  @MethodSource("erecord_items_cases")
  public void erecord_item(ERecSpec spec, RecSpec expected) {
    assertThat(spec.evaluationSpec())
        .isEqualTo(expected);
  }

  public static List<Arguments> erecord_items_cases() {
    return list(
        arguments(erec(), rec()),
        arguments(erec(STR), rec(STR)),
        arguments(erec(STR, INT), rec(STR, INT)),
        arguments(erec(STR, INT, BLOB), rec(STR, INT, BLOB))
    );
  }

  @ParameterizedTest
  @MethodSource("defined_lambda_result_cases")
  public void defined_lambda_result(DefinedLambdaSpec spec, ValSpec expected) {
    assertThat(spec.result())
        .isEqualTo(expected);
  }

  public static List<Arguments> defined_lambda_result_cases() {
    return list(
        arguments(definedLambda(INT), INT),
        arguments(definedLambda(BLOB, BOOL), BLOB),
        arguments(definedLambda(BLOB, BOOL, INT), BLOB)
    );
  }

  @ParameterizedTest
  @MethodSource("defined_lambda_parameters_cases")
  public void defined_lambda_parameters(DefinedLambdaSpec spec, RecSpec expected) {
    assertThat(spec.parameters())
        .isEqualTo(expected);
  }

  public static List<Arguments> defined_lambda_parameters_cases() {
    return list(
        arguments(definedLambda(INT), rec()),
        arguments(definedLambda(BLOB, BOOL), rec(BOOL)),
        arguments(definedLambda(BLOB, BOOL, INT), rec(BOOL, INT))
    );
  }

  @ParameterizedTest
  @MethodSource("native_lambda_result_cases")
  public void native_lambda_result(NativeLambdaSpec spec, ValSpec expected) {
    assertThat(spec.result())
        .isEqualTo(expected);
  }

  public static List<Arguments> native_lambda_result_cases() {
    return list(
        arguments(nativeLambda(INT), INT),
        arguments(nativeLambda(BLOB, BOOL), BLOB),
        arguments(nativeLambda(BLOB, BOOL, INT), BLOB)
    );
  }

  @ParameterizedTest
  @MethodSource("native_lambda_parameters_cases")
  public void native_lambda_parameters(NativeLambdaSpec spec, RecSpec expected) {
    assertThat(spec.parameters())
        .isEqualTo(expected);
  }

  public static List<Arguments> native_lambda_parameters_cases() {
    return list(
        arguments(nativeLambda(INT), rec()),
        arguments(nativeLambda(BLOB, BOOL), rec(BOOL)),
        arguments(nativeLambda(BLOB, BOOL, INT), rec(BOOL, INT))
    );
  }

  private static ValSpec definedLambda(ValSpec result, ValSpec... parameters) {
    return SPEC_DB.definedLambdaSpec(result, rec(parameters));
  }

  private static ValSpec nativeLambda(ValSpec result, ValSpec... parameters) {
    return SPEC_DB.nativeLambdaSpec(result, rec(parameters));
  }

  private static RecSpec rec(ValSpec... items) {
    return SPEC_DB.recSpec(list(items));
  }

  private static ERecSpec erec(ValSpec... items) {
    return SPEC_DB.eRecSpec(list(items));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(DEFINED_LAMBDA, DEFINED_LAMBDA);
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(NATIVE_LAMBDA, NATIVE_LAMBDA);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(STR, STR);
    tester.addEqualityGroup(PERSON, PERSON);

    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_DEFINED_LAMBDA, ARRAY_DEFINED_LAMBDA);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_NATIVE_LAMBDA, ARRAY_NATIVE_LAMBDA);
    tester.addEqualityGroup(ARRAY_NOTHING, ARRAY_NOTHING);
    tester.addEqualityGroup(ARRAY_STR, ARRAY_STR);
    tester.addEqualityGroup(ARRAY_PERSON, ARRAY_PERSON);

    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_DEFINED_LAMBDA, ARRAY2_DEFINED_LAMBDA);
    tester.addEqualityGroup(ARRAY2_NATIVE_LAMBDA, ARRAY2_NATIVE_LAMBDA);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_NOTHING, ARRAY2_NOTHING);
    tester.addEqualityGroup(ARRAY2_STR, ARRAY2_STR);
    tester.addEqualityGroup(ARRAY2_PERSON, ARRAY2_PERSON);

    tester.addEqualityGroup(CALL, CALL);
    tester.addEqualityGroup(CONST, CONST);
    tester.addEqualityGroup(EARRAY, EARRAY);
    tester.addEqualityGroup(ERECORD, ERECORD);
    tester.addEqualityGroup(SELECT, SELECT);
    tester.addEqualityGroup(NULL, NULL);
    tester.addEqualityGroup(REF, REF);

    tester.testEquals();
  }
}
