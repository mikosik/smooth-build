package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BLambdaTest extends VmTestContext {
  @Test
  void creating_lambda_with_body_evaluation_type_not_equal_result_type_causes_exception()
      throws Exception {
    var lambdaType = bLambdaType(bStringType(), bIntType());
    assertCall(() -> bLambda(lambdaType, bBool(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  void setting_body_to_null_throws_exception() throws Exception {
    var lambdaType = bLambdaType(bBoolType(), bIntType());
    assertCall(() -> bLambda(lambdaType, null)).throwsException(NullPointerException.class);
  }

  @Test
  void type_of_lambda_is_lambda_type() throws Exception {
    var lambdaType = bLambdaType(bStringType(), bIntType());
    assertThat(bLambda(lambdaType, bInt()).evaluationType()).isEqualTo(lambdaType);
  }

  @Test
  void body_contains_object_passed_during_construction() throws Exception {
    var lambdaType = bLambdaType(bBoolType(), bIntType());
    var body = bInt(33);
    var lambda = bLambda(lambdaType, body);
    assertThat(lambda.body()).isEqualTo(body);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BLambda> {
    @Override
    protected List<BLambda> equalExprs() throws BytecodeException {
      return list(
          bLambda(bLambdaType(bStringType(), bIntType()), bInt(7)),
          bLambda(bLambdaType(bStringType(), bIntType()), bInt(7)));
    }

    @Override
    protected List<BLambda> nonEqualExprs() throws BytecodeException {
      return list(
          bLambda(bLambdaType(bStringType(), bIntType()), bInt(7)),
          bLambda(bLambdaType(bStringType(), bIntType()), bInt(0)),
          bLambda(bLambdaType(bBlobType(), bIntType()), bInt(7)),
          bLambda(bLambdaType(bStringType(), bBoolType()), bBool(true)));
    }
  }

  @Test
  void lambda_can_be_read_by_hash() throws Exception {
    var lambdaType = bLambdaType(bStringType(), bIntType());
    var lambda = bLambda(lambdaType, bInt());
    assertThat(exprDbOther().get(lambda.hash())).isEqualTo(lambda);
  }

  @Test
  void lambda_read_by_hash_have_equal_bodies() throws Exception {
    var lambdaType = bLambdaType(bStringType(), bIntType());
    var lambda = bLambda(lambdaType, bInt());
    var lambdaRead = (BLambda) exprDbOther().get(lambda.hash());
    assertThat(lambda.body()).isEqualTo(lambdaRead.body());
  }

  @Test
  void to_string() throws Exception {
    var lambdaType = bLambdaType(bStringType(), bIntType());
    var lambda = bLambda(lambdaType, bInt());
    assertThat(lambda.toString())
        .isEqualTo(
            """
        BLambda(
          hash = a71e9acb33d438c168f026f4df84f156b0abdb99c6006c26e382ec85378b6eff
          type = (String)->Int
          body = BInt(
            hash = d6781a8034402f1bb1369df5042c4cc9d4d726044ba4ae8eb55efce43bad6ec5
            type = Int
            value = 17
          )
        )""");
  }
}
