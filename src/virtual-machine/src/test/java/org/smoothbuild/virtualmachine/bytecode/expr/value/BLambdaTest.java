package org.smoothbuild.virtualmachine.bytecode.expr.value;

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
    var funcType = funcTB(stringTB(), intTB());
    assertCall(() -> lambdaB(funcType, boolB(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() throws Exception {
    var funcType = funcTB(boolTB(), intTB());
    assertCall(() -> lambdaB(funcType, null)).throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_func_is_func_type() throws Exception {
    var funcType = funcTB(stringTB(), intTB());
    assertThat(lambdaB(funcType, intB()).evaluationType()).isEqualTo(funcType);
  }

  @Test
  public void body_contains_object_passed_during_construction() throws Exception {
    var funcType = funcTB(boolTB(), intTB());
    var body = intB(33);
    var lambda = lambdaB(funcType, body);
    assertThat(lambda.body()).isEqualTo(body);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BLambda> {
    @Override
    protected List<BLambda> equalExprs() throws BytecodeException {
      return list(
          lambdaB(funcTB(stringTB(), intTB()), intB(7)),
          lambdaB(funcTB(stringTB(), intTB()), intB(7)));
    }

    @Override
    protected List<BLambda> nonEqualExprs() throws BytecodeException {
      return list(
          lambdaB(funcTB(stringTB(), intTB()), intB(7)),
          lambdaB(funcTB(stringTB(), intTB()), intB(0)),
          lambdaB(funcTB(blobTB(), intTB()), intB(7)),
          lambdaB(funcTB(stringTB(), boolTB()), boolB(true)));
    }
  }

  @Test
  public void func_can_be_read_by_hash() throws Exception {
    var funcType = funcTB(stringTB(), intTB());
    var lambda = lambdaB(funcType, intB());
    assertThat(exprDbOther().get(lambda.hash())).isEqualTo(lambda);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() throws Exception {
    var funcType = funcTB(stringTB(), intTB());
    var lambda = lambdaB(funcType, intB());
    var lambdaRead = (BLambda) exprDbOther().get(lambda.hash());
    assertThat(lambda.body()).isEqualTo(lambdaRead.body());
  }

  @Test
  public void to_string() throws Exception {
    var funcType = funcTB(stringTB(), intTB());
    var func = lambdaB(funcType, intB());
    assertThat(func.toString()).isEqualTo("Lambda((String)->Int)@" + func.hash());
  }
}
