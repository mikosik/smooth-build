package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.testing.TestingContext;

public class LambdaTest extends TestingContext {
  @Test
  public void creating_lambda_with_body_evaluation_spec_not_equal_result_spec_causes_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    assertCall(() -> {
      list(strExpr());
      lambdaVal(lambdaSpec, boolExpr());
    })
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(boolSpec()));
    assertCall(() -> {
      list(strExpr());
      lambdaVal(lambdaSpec, null);
    })
        .throwsException(NullPointerException.class);
  }


  @Test
  public void spec_of_lambda_is_lambda_spec() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    assertThat(lambdaVal(lambdaSpec, intExpr()).spec())
        .isEqualTo(lambdaSpec);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(boolSpec()));
    Const body = intExpr(33);
    assertThat(lambdaVal(lambdaSpec, body).body())
        .isEqualTo(body);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_are_equal() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr());
    strExpr();
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr());
    assertThat(lambda1)
        .isEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_body_are_not_equal() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(1));
    strExpr();
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(2));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hashes() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    intExpr();
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr());
    intExpr();
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr());
    assertThat(lambda1.hash())
        .isEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hashes() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(1));
    strExpr();
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(2));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hash_code() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr());
    strExpr();
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr());
    assertThat(lambda1.hashCode())
        .isEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hash_code() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(intSpec()));
    intExpr();
    Lambda lambda1 = lambdaVal(lambdaSpec, intExpr(1));
    intExpr();
    Lambda lambda2 = lambdaVal(lambdaSpec, intExpr(2));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_can_be_read_by_hash() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda = lambdaVal(lambdaSpec, intExpr());
    assertThat(objectDbOther().get(lambda.hash()))
        .isEqualTo(lambda);
  }

  @Test
  public void lambdas_read_by_hash_have_equal_bodies() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda = lambdaVal(lambdaSpec, intExpr());
    Lambda lambdaRead = (Lambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.body())
        .isEqualTo(lambdaRead.body());
  }

  @Test
  public void to_string() {
    LambdaSpec lambdaSpec = lambdaSpec(intSpec(), list(strSpec()));
    strExpr();
    Lambda lambda = lambdaVal(lambdaSpec, intExpr());
    assertThat(lambda.toString())
        .isEqualTo("Lambda(INT(STRING))@" + lambda.hash());
  }
}
