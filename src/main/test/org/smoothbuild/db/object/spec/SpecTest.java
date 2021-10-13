package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.spec.TestingSpecs.ABSENT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ANY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_ABSENT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_ANY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_VARIABLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_ABSENT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_ANY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_EXPR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_VARIABLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.CALL;
import static org.smoothbuild.db.object.spec.TestingSpecs.CONST;
import static org.smoothbuild.db.object.spec.TestingSpecs.ERECORD;
import static org.smoothbuild.db.object.spec.TestingSpecs.INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.NULL;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.REF;
import static org.smoothbuild.db.object.spec.TestingSpecs.SELECT;
import static org.smoothbuild.db.object.spec.TestingSpecs.SPEC_DB;
import static org.smoothbuild.db.object.spec.TestingSpecs.STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.VARIABLE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.obj.expr.ArrayExpr;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.RecExprSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.db.object.spec.val.VariableSpec;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;
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
        .isEqualTo(name + "@" + spec.hash());
  }

  public static Stream<Arguments> names() {
    TestingContext tc = new TestingContext();
    return Stream.of(
        arguments(ABSENT, "ABSENT"),
        arguments(ANY, "ANY"),
        arguments(BLOB, "BLOB"),
        arguments(BOOL, "BOOL"),
        arguments(LAMBDA, "BLOB(BOOL)"),
        arguments(INT, "INT"),
        arguments(NOTHING, "NOTHING"),
        arguments(STR, "STRING"),
        arguments(PERSON, "{STRING,STRING}"),
        arguments(tc.callSpec(tc.intSpec()), "CALL:INT"),
        arguments(tc.constSpec(tc.intSpec()), "CONST:INT"),
        arguments(tc.arrayExprSpec(tc.strSpec()), "ARRAY_EXPR:[STRING]"),
        arguments(tc.recExprSpec(list(tc.strSpec(), tc.intSpec())), "RECORD_EXPR:{STRING,INT}"),
        arguments(tc.selectSpec(tc.intSpec()), "SELECT:INT"),
        arguments(NULL, "NULL:NOTHING"),
        arguments(tc.refSpec(tc.intSpec()), "REF:INT"),

        arguments(ARRAY_ABSENT, "[ABSENT]"),
        arguments(ARRAY_ANY, "[ANY]"),
        arguments(ARRAY_BLOB, "[BLOB]"),
        arguments(ARRAY_BOOL, "[BOOL]"),
        arguments(ARRAY_LAMBDA, "[BLOB(BOOL)]"),
        arguments(ARRAY_INT, "[INT]"),
        arguments(ARRAY_NOTHING, "[NOTHING]"),
        arguments(ARRAY_STR, "[STRING]"),
        arguments(ARRAY_PERSON, "[{STRING,STRING}]"),

        arguments(ARRAY2_ABSENT, "[[ABSENT]]"),
        arguments(ARRAY2_ANY, "[[ANY]]"),
        arguments(ARRAY2_BLOB, "[[BLOB]]"),
        arguments(ARRAY2_BOOL, "[[BOOL]]"),
        arguments(ARRAY2_LAMBDA, "[[BLOB(BOOL)]]"),
        arguments(ARRAY2_INT, "[[INT]]"),
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
        arguments(ANY, null),
        arguments(ABSENT, null),
        arguments(BLOB, Blob.class),
        arguments(BOOL, Bool.class),
        arguments(LAMBDA, Lambda.class),
        arguments(INT, Int.class),
        arguments(NOTHING, null),
        arguments(PERSON, Rec.class),
        arguments(STR, Str.class),
        arguments(VARIABLE, null),

        arguments(ARRAY_ANY, Array.class),
        arguments(ARRAY_ABSENT, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_LAMBDA, Array.class),
        arguments(ARRAY_INT, Array.class),
        arguments(ARRAY_NOTHING, Array.class),
        arguments(ARRAY_PERSON, Array.class),
        arguments(ARRAY_STR, Array.class),
        arguments(ARRAY_VARIABLE, Array.class),

        arguments(CALL, Call.class),
        arguments(CONST, Const.class),
        arguments(ARRAY_EXPR, ArrayExpr.class),
        arguments(ERECORD, RecExpr.class),
        arguments(SELECT, Select.class),
        arguments(NULL, Null.class),
        arguments(REF, Ref.class)
    );
  }

  @ParameterizedTest
  @MethodSource("array_element_cases")
  public void array_element(ArraySpec spec, Spec expected) {
    assertThat(spec.element())
        .isEqualTo(expected);
  }

  public static List<Arguments> array_element_cases() {
    return list(
        arguments(ARRAY_ABSENT, ABSENT),
        arguments(ARRAY_ANY, ANY),
        arguments(ARRAY_BLOB, BLOB),
        arguments(ARRAY_BOOL, BOOL),
        arguments(ARRAY_LAMBDA, LAMBDA),
        arguments(ARRAY_INT, INT),
        arguments(ARRAY_NOTHING, NOTHING),
        arguments(ARRAY_STR, STR),
        arguments(ARRAY_PERSON, PERSON),

        arguments(ARRAY2_ABSENT, ARRAY_ABSENT),
        arguments(ARRAY2_ANY, ARRAY_ANY),
        arguments(ARRAY2_BLOB, ARRAY_BLOB),
        arguments(ARRAY2_BOOL, ARRAY_BOOL),
        arguments(ARRAY2_LAMBDA, ARRAY_LAMBDA),
        arguments(ARRAY2_INT, ARRAY_INT),
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
        arguments(recSpec(), list()),
        arguments(recSpec(STR), list(STR)),
        arguments(recSpec(STR, INT), list(STR, INT)),
        arguments(recSpec(STR, INT, BLOB), list(STR, INT, BLOB))
    );
  }

  @Nested
  class _evaluation_spec {
    @ParameterizedTest
    @MethodSource("specs")
    public void arrayExpr(ValSpec spec) {
      assertThat(SPEC_DB.arrayExprSpec(spec).evaluationSpec())
          .isEqualTo(SPEC_DB.arraySpec(spec));
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void call(ValSpec spec) {
      assertThat(SPEC_DB.callSpec(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void const_(ValSpec spec) {
      assertThat(SPEC_DB.constSpec(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void invoke(ValSpec spec) {
      assertThat(SPEC_DB.invokeSpec(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @Test
    public void null_() {
      assertThat(SPEC_DB.nullSpec().evaluationSpec())
          .isEqualTo(NOTHING);
    }

    @ParameterizedTest
    @MethodSource("rec_expr_cases")
    public void rec_expr(RecExprSpec spec, RecSpec expected) {
      assertThat(spec.evaluationSpec())
          .isEqualTo(expected);
    }

    public static List<Arguments> rec_expr_cases() {
      return list(
          arguments(recExprSpec(), recSpec()),
          arguments(recExprSpec(STR), recSpec(STR)),
          arguments(recExprSpec(STR, INT), recSpec(STR, INT)),
          arguments(recExprSpec(STR, INT, BLOB), recSpec(STR, INT, BLOB))
      );
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void ref(ValSpec spec) {
      assertThat(SPEC_DB.refSpec(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void select(ValSpec spec) {
      assertThat(SPEC_DB.selectSpec(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    public static ImmutableList<Spec> specs() {
      return TestingSpecs.VAL_SPECS_TO_TEST;
    }
  }

  @ParameterizedTest
  @MethodSource("lambda_result_cases")
  public void lambda_result(LambdaSpec spec, ValSpec expected) {
    assertThat(spec.result())
        .isEqualTo(expected);
  }

  public static List<Arguments> lambda_result_cases() {
    return list(
        arguments(lambdaSpec(INT, list()), INT),
        arguments(lambdaSpec(BLOB, list(BOOL)), BLOB),
        arguments(lambdaSpec(BLOB, list(BOOL, INT)), BLOB)
    );
  }

  @ParameterizedTest
  @MethodSource("lambda_parameters_cases")
  public void lambda_parameters(LambdaSpec spec, RecSpec expected) {
    assertThat(spec.parameters())
        .isEqualTo(expected);
  }

  public static List<Arguments> lambda_parameters_cases() {
    return list(
        arguments(lambdaSpec(INT, list()), recSpec()),
        arguments(lambdaSpec(BLOB, list(BOOL)), recSpec(BOOL)),
        arguments(lambdaSpec(BLOB, list(BOOL, INT)), recSpec(BOOL, INT))
    );
  }

  @ParameterizedTest
  @MethodSource("lambda_default_arguments_cases")
  public void lambda_default_arguments(LambdaSpec spec, RecSpec expected) {
    assertThat(spec.defaultArguments())
        .isEqualTo(expected);
  }

  public static List<Arguments> lambda_default_arguments_cases() {
    return list(
        arguments(lambdaSpec(INT, list(), list()), recSpec()),
        arguments(lambdaSpec(BLOB, list(BOOL), list(ABSENT)), recSpec(ABSENT)),
        arguments(lambdaSpec(
            BLOB, list(BOOL, INT), list(BOOL, ABSENT)), recSpec(BOOL, ABSENT))
    );
  }

  @Nested
  class _struct {
    @ParameterizedTest
    @MethodSource("struct_items_cases")
    public void struct_items(StructSpec spec, RecSpec expected) {
      assertThat(spec.rec())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_items_cases() {
      return list(
          arguments(structSpec(recSpec(), list()), recSpec()),
          arguments(structSpec(recSpec(STR), list("field")), recSpec(STR)),
          arguments(structSpec(recSpec(STR, INT), list("field", "field2")), recSpec(STR, INT))
      );
    }

    @ParameterizedTest
    @MethodSource("struct_names_cases")
    public void struct_names(StructSpec spec, List<String> expected) {
      assertThat(spec.names())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_names_cases() {
      return list(
          arguments(structSpec(recSpec(), list()), list()),
          arguments(structSpec(recSpec(STR), list("field")), list("field")),
          arguments(structSpec(recSpec(STR, INT), list("field", "field2")), list("field", "field2"))
      );
    }

    @Test
    public void different_size_of_items_and_names_causes_exception() {
      assertCall(() -> structSpec(recSpec(INT), list("field", "field2")))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _variable {
    @Test
    public void name() {
      VariableSpec variableSpec = SPEC_DB.variableSpec("A");
      assertThat(variableSpec.name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> SPEC_DB.variableSpec("a"))
          .throwsException(new IllegalArgumentException());
    }
  }

  private static ValSpec lambdaSpec(ValSpec result, ImmutableList<ValSpec> parameters) {
    return lambdaSpec(result, parameters, parameters);
  }

  private static LambdaSpec lambdaSpec(ValSpec result,
      ImmutableList<ValSpec> parameters, ImmutableList<ValSpec> defaultArguments) {
    return SPEC_DB.lambdaSpec(result, recSpec(parameters), recSpec(defaultArguments));
  }

  private static RecSpec recSpec(ValSpec... items) {
    return recSpec(list(items));
  }

  private static RecSpec recSpec(List<ValSpec> items) {
    return SPEC_DB.recSpec(items);
  }

  private static RecExprSpec recExprSpec(ValSpec... items) {
    return SPEC_DB.recExprSpec(list(items));
  }

  private static StructSpec structSpec(RecSpec items, ImmutableList<String> names) {
    return SPEC_DB.structSpec(items, names);
  }

  private static ConstSpec constSpec(ValSpec evaluationSpec) {
    return SPEC_DB.constSpec(evaluationSpec);
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(ANY, ANY);
    tester.addEqualityGroup(ABSENT, ABSENT);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(LAMBDA, LAMBDA);
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(STR, STR);
    tester.addEqualityGroup(PERSON, PERSON);
    tester.addEqualityGroup(VARIABLE, VARIABLE);

    tester.addEqualityGroup(ARRAY_ANY, ARRAY_ANY);
    tester.addEqualityGroup(ARRAY_ABSENT, ARRAY_ABSENT);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_LAMBDA, ARRAY_LAMBDA);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_NOTHING, ARRAY_NOTHING);
    tester.addEqualityGroup(ARRAY_STR, ARRAY_STR);
    tester.addEqualityGroup(ARRAY_PERSON, ARRAY_PERSON);
    tester.addEqualityGroup(ARRAY_VARIABLE, ARRAY_VARIABLE);

    tester.addEqualityGroup(ARRAY2_VARIABLE, ARRAY2_VARIABLE);
    tester.addEqualityGroup(ARRAY2_ANY, ARRAY2_ANY);
    tester.addEqualityGroup(ARRAY2_ABSENT, ARRAY2_ABSENT);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_LAMBDA, ARRAY2_LAMBDA);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_NOTHING, ARRAY2_NOTHING);
    tester.addEqualityGroup(ARRAY2_STR, ARRAY2_STR);
    tester.addEqualityGroup(ARRAY2_PERSON, ARRAY2_PERSON);

    tester.addEqualityGroup(CALL, CALL);
    tester.addEqualityGroup(CONST, CONST);
    tester.addEqualityGroup(ARRAY_EXPR, ARRAY_EXPR);
    tester.addEqualityGroup(ERECORD, ERECORD);
    tester.addEqualityGroup(SELECT, SELECT);
    tester.addEqualityGroup(NULL, NULL);
    tester.addEqualityGroup(REF, REF);

    tester.testEquals();
  }
}
