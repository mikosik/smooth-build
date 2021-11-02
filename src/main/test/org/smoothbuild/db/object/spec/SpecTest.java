package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.spec.TestingSpecs.ANY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_ANY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_PERSON_TUPLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_VARIABLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_ANY;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_EXPR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_PERSON_TUPLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_VARIABLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.CALL;
import static org.smoothbuild.db.object.spec.TestingSpecs.CONST;
import static org.smoothbuild.db.object.spec.TestingSpecs.INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.LAMBDA;
import static org.smoothbuild.db.object.spec.TestingSpecs.NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON_TUPLE;
import static org.smoothbuild.db.object.spec.TestingSpecs.REF;
import static org.smoothbuild.db.object.spec.TestingSpecs.SELECT;
import static org.smoothbuild.db.object.spec.TestingSpecs.SPEC_DB;
import static org.smoothbuild.db.object.spec.TestingSpecs.STR;
import static org.smoothbuild.db.object.spec.TestingSpecs.TUPLE_EXPR;
import static org.smoothbuild.db.object.spec.TestingSpecs.VARIABLE;
import static org.smoothbuild.util.collect.Lists.list;

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
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.expr.TupleExpr;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.TupleExprSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.testing.TestingContextImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

/**
 * Most types are tested in TypeTest. Here we test only types which are not Types from
 * TypeFactory perspective.
 */
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
        .isEqualTo("Type(`" + name + "`)");
  }

  public static Stream<Arguments> names() {
    TestingContextImpl tc = new TestingContextImpl();
    return Stream.of(
        arguments(PERSON_TUPLE, "{String,String}"),
        arguments(tc.callSpec(tc.intSpec()), "CALL:Int"),
        arguments(tc.constSpec(tc.intSpec()), "CONST:Int"),
        arguments(tc.nativeMethodSpec(), "NATIVE_METHOD"),
        arguments(tc.arrayExprSpec(tc.strSpec()), "ARRAY:[String]"),
        arguments(tc.tupleExprSpec(list(tc.strSpec(), tc.intSpec())), "TUPLE:{String,Int}"),
        arguments(tc.selectSpec(tc.intSpec()), "SELECT:Int"),
        arguments(tc.refSpec(tc.intSpec()), "REF:Int"),

        arguments(ARRAY_PERSON_TUPLE, "[{String,String}]"),
        arguments(ARRAY2_PERSON_TUPLE, "[[{String,String}]]")
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
        arguments(BLOB, Blob.class),
        arguments(BOOL, Bool.class),
        arguments(LAMBDA, Lambda.class),
        arguments(INT, Int.class),
        arguments(NOTHING, null),
        arguments(PERSON, Struc_.class),
        arguments(STR, Str.class),
        arguments(VARIABLE, null),

        arguments(ARRAY_ANY, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_LAMBDA, Array.class),
        arguments(ARRAY_INT, Array.class),
        arguments(ARRAY_NOTHING, Array.class),
        arguments(ARRAY_PERSON_TUPLE, Array.class),
        arguments(ARRAY_STR, Array.class),
        arguments(ARRAY_VARIABLE, Array.class),

        arguments(CALL, Call.class),
        arguments(CONST, Const.class),
        arguments(ARRAY_EXPR, ArrayExpr.class),
        arguments(TUPLE_EXPR, TupleExpr.class),
        arguments(SELECT, Select.class),
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
        arguments(ARRAY_PERSON_TUPLE, PERSON_TUPLE),
        arguments(ARRAY2_PERSON_TUPLE, ARRAY_PERSON_TUPLE));
  }

  @ParameterizedTest
  @MethodSource("tuple_items_cases")
  public void tuple_item(TupleSpec spec, List<Spec> expected) {
    assertThat(spec.items())
        .isEqualTo(expected);
  }

  public static List<Arguments> tuple_items_cases() {
    return list(
        arguments(tupleSpec(), list()),
        arguments(tupleSpec(STR), list(STR)),
        arguments(tupleSpec(STR, INT), list(STR, INT)),
        arguments(tupleSpec(STR, INT, BLOB), list(STR, INT, BLOB))
    );
  }

  @Nested
  class _evaluation_spec {
    @ParameterizedTest
    @MethodSource("specs")
    public void arrayExpr(ValSpec spec) {
      assertThat(SPEC_DB.arrayExpr(spec).evaluationSpec())
          .isEqualTo(SPEC_DB.array(spec));
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void call(ValSpec spec) {
      assertThat(SPEC_DB.call(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void const_(ValSpec spec) {
      assertThat(SPEC_DB.const_(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @ParameterizedTest
    @MethodSource("tuple_expr_cases")
    public void tuple_expr(TupleExprSpec spec, TupleSpec expected) {
      assertThat(spec.evaluationSpec())
          .isEqualTo(expected);
    }

    public static List<Arguments> tuple_expr_cases() {
      return list(
          arguments(tupleExprSpec(), tupleSpec()),
          arguments(tupleExprSpec(STR), tupleSpec(STR)),
          arguments(tupleExprSpec(STR, INT), tupleSpec(STR, INT)),
          arguments(tupleExprSpec(STR, INT, BLOB), tupleSpec(STR, INT, BLOB))
      );
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void ref(ValSpec spec) {
      assertThat(SPEC_DB.ref(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    @ParameterizedTest
    @MethodSource("specs")
    public void select(ValSpec spec) {
      assertThat(SPEC_DB.select(spec).evaluationSpec())
          .isEqualTo(spec);
    }

    public static ImmutableList<Spec> specs() {
      return TestingSpecs.VAL_SPECS_TO_TEST;
    }
  }

  private static TupleSpec tupleSpec(ValSpec... items) {
    return tupleSpec(list(items));
  }

  private static TupleSpec tupleSpec(ImmutableList<ValSpec> items) {
    return SPEC_DB.tuple(items);
  }

  private static TupleExprSpec tupleExprSpec(ValSpec... items) {
    return SPEC_DB.tupleExpr(tupleSpec(list(items)));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(ANY, ANY);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(LAMBDA, LAMBDA);
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(STR, STR);
    tester.addEqualityGroup(PERSON_TUPLE, PERSON_TUPLE);
    tester.addEqualityGroup(VARIABLE, VARIABLE);

    tester.addEqualityGroup(ARRAY_ANY, ARRAY_ANY);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_LAMBDA, ARRAY_LAMBDA);
    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_NOTHING, ARRAY_NOTHING);
    tester.addEqualityGroup(ARRAY_STR, ARRAY_STR);
    tester.addEqualityGroup(ARRAY_PERSON_TUPLE, ARRAY_PERSON_TUPLE);
    tester.addEqualityGroup(ARRAY_VARIABLE, ARRAY_VARIABLE);

    tester.addEqualityGroup(ARRAY2_VARIABLE, ARRAY2_VARIABLE);
    tester.addEqualityGroup(ARRAY2_ANY, ARRAY2_ANY);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_LAMBDA, ARRAY2_LAMBDA);
    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_NOTHING, ARRAY2_NOTHING);
    tester.addEqualityGroup(ARRAY2_STR, ARRAY2_STR);
    tester.addEqualityGroup(ARRAY2_PERSON_TUPLE, ARRAY2_PERSON_TUPLE);

    tester.addEqualityGroup(CALL, CALL);
    tester.addEqualityGroup(CONST, CONST);
    tester.addEqualityGroup(ARRAY_EXPR, ARRAY_EXPR);
    tester.addEqualityGroup(TUPLE_EXPR, TUPLE_EXPR);
    tester.addEqualityGroup(SELECT, SELECT);
    tester.addEqualityGroup(REF, REF);

    tester.testEquals();
  }
}
