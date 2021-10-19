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
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSO_;
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
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
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
        arguments(ABSENT, "!Absent"),
        arguments(ANY, "!Any"),
        arguments(BLOB, "Blob"),
        arguments(BOOL, "Bool"),
        arguments(LAMBDA, "Blob(Bool)"),
        arguments(INT, "Int"),
        arguments(NOTHING, "Nothing"),
        arguments(STR, "String"),
        arguments(PERSO_, "{String,String}"),
        arguments(PERSON, "Person"),
        arguments(tc.callSpec(tc.intSpec()), "CALL:Int"),
        arguments(tc.constSpec(tc.intSpec()), "CONST:Int"),
        arguments(tc.arrayExprSpec(tc.strSpec()), "ARRAY:[String]"),
        arguments(tc.recExprSpec(list(tc.strSpec(), tc.intSpec())), "RECORD:{String,Int}"),
        arguments(tc.selectSpec(tc.intSpec()), "SELECT:Int"),
        arguments(NULL, "NULL:Nothing"),
        arguments(tc.refSpec(tc.intSpec()), "REF:Int"),

        arguments(ARRAY_ABSENT, "[!Absent]"),
        arguments(ARRAY_ANY, "[!Any]"),
        arguments(ARRAY_BLOB, "[Blob]"),
        arguments(ARRAY_BOOL, "[Bool]"),
        arguments(ARRAY_LAMBDA, "[Blob(Bool)]"),
        arguments(ARRAY_INT, "[Int]"),
        arguments(ARRAY_NOTHING, "[Nothing]"),
        arguments(ARRAY_STR, "[String]"),
        arguments(ARRAY_PERSON, "[{String,String}]"),

        arguments(ARRAY2_ABSENT, "[[!Absent]]"),
        arguments(ARRAY2_ANY, "[[!Any]]"),
        arguments(ARRAY2_BLOB, "[[Blob]]"),
        arguments(ARRAY2_BOOL, "[[Bool]]"),
        arguments(ARRAY2_LAMBDA, "[[Blob(Bool)]]"),
        arguments(ARRAY2_INT, "[[Int]]"),
        arguments(ARRAY2_NOTHING, "[[Nothing]]"),
        arguments(ARRAY2_STR, "[[String]]"),
        arguments(ARRAY2_PERSON, "[[{String,String}]]")
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
        arguments(PERSON, Struc_.class),
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
        arguments(ARRAY_PERSON, PERSO_),

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
          .isEqualTo(SPEC_DB.array(spec));
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
    assertThat(spec.parametersRec())
        .isEqualTo(expected);
  }

  public static List<Arguments> lambda_parameters_cases() {
    return list(
        arguments(lambdaSpec(INT, list()), recSpec()),
        arguments(lambdaSpec(BLOB, list(BOOL)), recSpec(BOOL)),
        arguments(lambdaSpec(BLOB, list(BOOL, INT)), recSpec(BOOL, INT))
    );
  }

  @Nested
  class _struct {
    @ParameterizedTest
    @MethodSource("struct_name_cases")
    public void struct_name(StructSpec spec, String expected) {
      assertThat(spec.name())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_name_cases() {
      return list(
          arguments(structSpec("MyStruct", list(), list()), "MyStruct"),
          arguments(structSpec("", list(), list()), "")
      );
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(StructSpec spec, List<ValSpec> expected) {
      assertThat(spec.fields())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_fields_cases() {
      return list(
          arguments(structSpec(list(), list()), list()),
          arguments(structSpec(list(STR), list("field")), list(STR)),
          arguments(structSpec(list(STR, INT), list("field", "field2")), list(STR, INT))
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
          arguments(structSpec(list(), list()), list()),
          arguments(structSpec(list(STR), list("field")), list("field")),
          arguments(structSpec(list(STR, INT), list("field", "field2")), list("field", "field2"))
      );
    }

    @Test
    public void different_size_of_items_and_names_causes_exception() {
      assertCall(() -> structSpec(list(INT), list("field", "field2")))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Nested
  class _variable {
    @Test
    public void name() {
      VariableSpec variableSpec = SPEC_DB.variable("A");
      assertThat(variableSpec.name())
          .isEqualTo("A");
    }

    @Test
    public void illegal_name() {
      assertCall(() -> SPEC_DB.variable("a"))
          .throwsException(new IllegalArgumentException());
    }
  }

  private static LambdaSpec lambdaSpec(ValSpec result, ImmutableList<ValSpec> parameters) {
    return SPEC_DB.function(result, parameters);
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

  private static StructSpec structSpec(ImmutableList<ValSpec> fields, ImmutableList<String> names) {
    return structSpec("MyStruct", fields, names);
  }

  private static StructSpec structSpec(String name, ImmutableList<ValSpec> fields,
      ImmutableList<String> names) {
    return SPEC_DB.struct(name, fields, names);
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
    tester.addEqualityGroup(PERSO_, PERSO_);
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
