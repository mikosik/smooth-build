package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.type.val.LambdaOType;
import org.smoothbuild.testing.TestingContextImpl;

public class LambdaTest extends TestingContextImpl {
  @Test
  public void creating_lambda_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    assertCall(() -> {
      list(stringExpr());
      lambda(lambdaType, boolExpr());
    })
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(boolOT()));
    assertCall(() -> {
      list(stringExpr());
      lambda(lambdaType, null);
    })
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_lambda_is_lambda_type() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    assertThat(lambda(lambdaType, intExpr()).type())
        .isEqualTo(lambdaType);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(boolOT()));
    Const body = intExpr(33);
    assertThat(lambda(lambdaType, body).body())
        .isEqualTo(body);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_are_equal() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda1 = lambda(lambdaType, intExpr());
    stringExpr();
    Lambda lambda2 = lambda(lambdaType, intExpr());
    assertThat(lambda1)
        .isEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_different_body_are_not_equal() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda1 = lambda(lambdaType, intExpr(1));
    stringExpr();
    Lambda lambda2 = lambda(lambdaType, intExpr(2));
    assertThat(lambda1)
        .isNotEqualTo(lambda2);
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hashes() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(intOT()));
    intExpr();
    Lambda lambda1 = lambda(lambdaType, intExpr());
    intExpr();
    Lambda lambda2 = lambda(lambdaType, intExpr());
    assertThat(lambda1.hash())
        .isEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hashes() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda1 = lambda(lambdaType, intExpr(1));
    stringExpr();
    Lambda lambda2 = lambda(lambdaType, intExpr(2));
    assertThat(lambda1.hash())
        .isNotEqualTo(lambda2.hash());
  }

  @Test
  public void lambdas_with_equal_body_and_default_arguments_have_equal_hash_code() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda1 = lambda(lambdaType, intExpr());
    stringExpr();
    Lambda lambda2 = lambda(lambdaType, intExpr());
    assertThat(lambda1.hashCode())
        .isEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_with_different_bodies_have_not_equal_hash_code() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(intOT()));
    intExpr();
    Lambda lambda1 = lambda(lambdaType, intExpr(1));
    intExpr();
    Lambda lambda2 = lambda(lambdaType, intExpr(2));
    assertThat(lambda1.hashCode())
        .isNotEqualTo(lambda2.hashCode());
  }

  @Test
  public void lambdas_can_be_read_by_hash() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda = lambda(lambdaType, intExpr());
    assertThat(objectDbOther().get(lambda.hash()))
        .isEqualTo(lambda);
  }

  @Test
  public void lambdas_read_by_hash_have_equal_bodies() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda = lambda(lambdaType, intExpr());
    Lambda lambdaRead = (Lambda) objectDbOther().get(lambda.hash());
    assertThat(lambda.body())
        .isEqualTo(lambdaRead.body());
  }

  @Test
  public void to_string() {
    LambdaOType lambdaType = lambdaOT(intOT(), list(stringOT()));
    stringExpr();
    Lambda lambda = lambda(lambdaType, intExpr());
    assertThat(lambda.toString())
        .isEqualTo("Lambda(Int(String))@" + lambda.hash());
  }
}
