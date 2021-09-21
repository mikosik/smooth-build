package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.testing.TestingContext;

public class DefinedLambdaTest extends TestingContext {
  @Test
  public void creating_lambda_with_less_default_arguments_than_specified_in_its_spec_causes_exception() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> definedLambdaVal(lambdaSpec, constExpr(), list()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_lambda_with_more_default_arguments_than_specified_in_its_spec_causes_exception() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> definedLambdaVal(lambdaSpec, constExpr(), list(constExpr(), constExpr())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> definedLambdaVal(lambdaSpec, null, list(constExpr())))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_default_arguments_to_null_throws_exception() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> definedLambdaVal(lambdaSpec, constExpr(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_default_argument_to_null_throws_exception() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    assertCall(() -> definedLambdaVal(lambdaSpec, constExpr(), singletonList(null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void spec_of_defined_lambda_is_defined_lambda_spec() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    assertThat(definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr())).spec())
        .isEqualTo(lambdaSpec);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    Const body = constExpr(intVal(33));
    assertThat(definedLambdaVal(lambdaSpec, body, asList(constExpr())).body())
        .isEqualTo(body);
  }

  @Test
  public void default_arguments_contains_object_passed_during_construction() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    List<Expr> defaultArguments = asList(constExpr(intVal(33)));
    assertThat(definedLambdaVal(lambdaSpec, constExpr(), defaultArguments).defaultArguments())
        .isEqualTo(defaultArguments);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_are_equal() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    assertThat(lambda1)
        .isEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_body_are_not_equal() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(intVal(1)), asList(constExpr()));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(intVal(2)), asList(constExpr()));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_default_arguments_are_not_equal() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr(intVal(1))));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr(intVal(2))));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hashes() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    assertThat(lambda1.hash())
        .isEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hashes() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(intVal(1)), asList(constExpr()));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(intVal(2)), asList(constExpr()));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_default_arguments_have_not_equal_hashes() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr(intVal(1))));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr(intVal(2))));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hash_code() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    assertThat(lambda1.hashCode())
        .isEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hash_code() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(intVal(1)), asList(constExpr()));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(intVal(2)), asList(constExpr()));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_default_arguments_have_not_equal_hash_code() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda1 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr(intVal(1))));
    DefinedLambda lambda2 = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr(intVal(2))));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_can_be_read_by_hash() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    assertThat(objectDbOther().get(lambda.hash()))
        .isEqualTo(lambda);
  }

  @Test
  public void lambdas_read_by_hash_have_equal_bodies() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    DefinedLambda lambdaRead = (DefinedLambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.body())
        .isEqualTo(lambdaRead.body());
  }

  @Test
  public void lambdas_read_by_hash_have_equal_default_arguments() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    DefinedLambda lambdaRead = (DefinedLambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.defaultArguments())
        .isEqualTo(lambdaRead.defaultArguments());
  }

  @Test
  public void to_string() {
    DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), boolSpec());
    DefinedLambda lambda = definedLambdaVal(lambdaSpec, constExpr(), asList(constExpr()));
    assertThat(lambda.toString())
        .isEqualTo("DefinedLambda(INT(BOOL)):" + lambda.hash());
  }
}
