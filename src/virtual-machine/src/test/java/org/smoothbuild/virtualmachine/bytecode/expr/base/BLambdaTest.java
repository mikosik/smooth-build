package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BLambdaTest extends TestingVirtualMachine {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception()
      throws Exception {
    var funcType = bFuncType(bStringType(), bIntType());
    assertCall(() -> bLambda(funcType, bBool(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() throws Exception {
    var funcType = bFuncType(bBoolType(), bIntType());
    assertCall(() -> bLambda(funcType, null)).throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_func_is_func_type() throws Exception {
    var funcType = bFuncType(bStringType(), bIntType());
    assertThat(bLambda(funcType, bInt()).evaluationType()).isEqualTo(funcType);
  }

  @Test
  public void body_contains_object_passed_during_construction() throws Exception {
    var funcType = bFuncType(bBoolType(), bIntType());
    var body = bInt(33);
    var lambda = bLambda(funcType, body);
    assertThat(lambda.body()).isEqualTo(body);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BLambda> {
    @Override
    protected List<BLambda> equalExprs() throws BytecodeException {
      return list(
          bLambda(bFuncType(bStringType(), bIntType()), bInt(7)),
          bLambda(bFuncType(bStringType(), bIntType()), bInt(7)));
    }

    @Override
    protected List<BLambda> nonEqualExprs() throws BytecodeException {
      return list(
          bLambda(bFuncType(bStringType(), bIntType()), bInt(7)),
          bLambda(bFuncType(bStringType(), bIntType()), bInt(0)),
          bLambda(bFuncType(bBlobType(), bIntType()), bInt(7)),
          bLambda(bFuncType(bStringType(), bBoolType()), bBool(true)));
    }
  }

  @Test
  public void func_can_be_read_by_hash() throws Exception {
    var funcType = bFuncType(bStringType(), bIntType());
    var lambda = bLambda(funcType, bInt());
    assertThat(exprDbOther().get(lambda.hash())).isEqualTo(lambda);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() throws Exception {
    var funcType = bFuncType(bStringType(), bIntType());
    var lambda = bLambda(funcType, bInt());
    var lambdaRead = (BLambda) exprDbOther().get(lambda.hash());
    assertThat(lambda.body()).isEqualTo(lambdaRead.body());
  }

  @Test
  public void to_string() throws Exception {
    var funcType = bFuncType(bStringType(), bIntType());
    var func = bLambda(funcType, bInt());
    assertThat(func.toString()).isEqualTo("Lambda((String)->Int)@" + func.hash());
  }
}
