package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class LambdaBTest extends TestContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception()
      throws Exception {
    var funcT = funcTB(stringTB(), intTB());
    assertCall(() -> lambdaB(funcT, boolB(true))).throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() throws Exception {
    var funcT = funcTB(boolTB(), intTB());
    assertCall(() -> lambdaB(funcT, null)).throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_func_is_func_type() throws Exception {
    var funcT = funcTB(stringTB(), intTB());
    assertThat(lambdaB(funcT, intB()).evaluationT()).isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_construction() throws Exception {
    var funcT = funcTB(boolTB(), intTB());
    var body = intB(33);
    var lambdaB = lambdaB(funcT, body);
    assertThat(lambdaB.body()).isEqualTo(body);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<LambdaB> {
    @Override
    protected List<LambdaB> equalExprs() throws BytecodeException {
      return list(
          lambdaB(funcTB(stringTB(), intTB()), intB(7)),
          lambdaB(funcTB(stringTB(), intTB()), intB(7)));
    }

    @Override
    protected List<LambdaB> nonEqualExprs() throws BytecodeException {
      return list(
          lambdaB(funcTB(stringTB(), intTB()), intB(7)),
          lambdaB(funcTB(stringTB(), intTB()), intB(0)),
          lambdaB(funcTB(blobTB(), intTB()), intB(7)),
          lambdaB(funcTB(stringTB(), boolTB()), boolB(true)));
    }
  }

  @Test
  public void func_can_be_read_by_hash() throws Exception {
    var funcT = funcTB(stringTB(), intTB());
    var lambdaB = lambdaB(funcT, intB());
    assertThat(bytecodeDbOther().get(lambdaB.hash())).isEqualTo(lambdaB);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() throws Exception {
    var funcT = funcTB(stringTB(), intTB());
    var lambdaB = lambdaB(funcT, intB());
    var lambdaRead = (LambdaB) bytecodeDbOther().get(lambdaB.hash());
    assertThat(lambdaB.body()).isEqualTo(lambdaRead.body());
  }

  @Test
  public void to_string() throws Exception {
    var funcT = funcTB(stringTB(), intTB());
    var func = lambdaB(funcT, intB());
    assertThat(func.toString()).isEqualTo("Lambda((String)->Int)@" + func.hash());
  }
}
