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
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.testing.TestingContext;

public class LambdaTest extends TestingContext {
  @Test
  public void creating_lambda_with_less_default_arguments_than_specified_in_its_spec_causes_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, intExpr(), list()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_lambda_with_more_default_arguments_than_specified_in_its_spec_causes_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, intExpr(), list(strExpr(), strExpr())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_lambda_with_default_argument_evaluation_spec_not_equal_param_spec_causes_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, intExpr(), list(boolExpr())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_lambda_with_body_evaluation_spec_not_equal_result_spec_causes_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, boolExpr(), list(strExpr())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(boolSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, null, list(strExpr())))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_default_arguments_to_null_throws_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(boolSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, intExpr(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_default_argument_to_null_throws_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(boolSpec()));
    assertCall(() -> lambdaVal(lambdaSpec, intExpr(), singletonList(null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void spec_of_lambda_is_lambda_spec() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    assertThat(lambdaVal(lambdaSpec, intExpr(), asList(strExpr())).spec())
        .isEqualTo(lambdaSpec);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(boolSpec()));
    Const body = intExpr(33);
    assertThat(lambdaVal(lambdaSpec, body, asList(boolExpr())).data().body())
        .isEqualTo(body);
  }

  @Test
  public void default_arguments_contains_object_passed_during_construction() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    List<Expr> defaultArguments = asList(strExpr());
    assertThat(lambdaVal(lambdaSpec, intExpr(), defaultArguments).defaultArguments())
        .isEqualTo(eRecExpr(defaultArguments));
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_are_equal() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    assertThat(lambda1)
        .isEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_body_are_not_equal() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(1), asList(strExpr()));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(2), asList(strExpr()));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_default_arguments_are_not_equal() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr(1)));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr(2)));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hashes() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr()));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr()));
    assertThat(lambda1.hash())
        .isEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hashes() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(1), asList(strExpr()));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(2), asList(strExpr()));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_default_arguments_have_not_equal_hashes() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr(1)));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr(2)));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hash_code() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    assertThat(lambda1.hashCode())
        .isEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hash_code() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(1), asList(intExpr()));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(2), asList(intExpr()));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_default_arguments_have_not_equal_hash_code() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr(1)));
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(), asList(intExpr(2)));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_can_be_read_by_hash() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    assertThat(objectDbOther().get(lambda.hash()))
        .isEqualTo(lambda);
  }

  @Test
  public void lambdas_read_by_hash_have_equal_bodies() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    Lambda lambdaRead = (Lambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.data().body())
        .isEqualTo(lambdaRead.data().body());
  }

  @Test
  public void lambdas_read_by_hash_have_equal_default_arguments() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    Lambda lambdaRead = (Lambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.defaultArguments())
        .isEqualTo(lambdaRead.defaultArguments());
  }

  @Test
  public void to_string() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    Lambda lambda = lambdaVal(lambdaSpec, intExpr(), asList(strExpr()));
    assertThat(lambda.toString())
        .isEqualTo("Lambda(INT(STRING))@" + lambda.hash());
  }
}
