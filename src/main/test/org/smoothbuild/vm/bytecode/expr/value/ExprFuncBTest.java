package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class ExprFuncBTest extends TestContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var funcT = funcTB(stringTB(), intTB());
    assertCall(() -> exprFuncB(funcT, boolB(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var funcT = funcTB(boolTB(), intTB());
    assertCall(() -> exprFuncB(funcT, null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_func_is_func_type() {
    var funcT = funcTB(stringTB(), intTB());
    assertThat(exprFuncB(funcT, intB()).evalT())
        .isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    var funcT = funcTB(boolTB(), intTB());
    var body = intB(33);
    var exprFuncB = exprFuncB(funcT, body);
    assertThat(exprFuncB.body())
        .isEqualTo(body);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ExprFuncB> {
    @Override
    protected List<ExprFuncB> equalExprs() {
      return list(
          exprFuncB(funcTB(stringTB(), intTB()), intB(7)),
          exprFuncB(funcTB(stringTB(), intTB()), intB(7))
      );
    }

    @Override
    protected List<ExprFuncB> nonEqualExprs() {
      return list(
          exprFuncB(funcTB(stringTB(), intTB()), intB(7)),
          exprFuncB(funcTB(stringTB(), intTB()), intB(0)),
          exprFuncB(funcTB(blobTB(), intTB()), intB(7)),
          exprFuncB(funcTB(stringTB(), boolTB()), boolB(true))
      );
    }
  }

  @Test
  public void func_can_be_read_by_hash() {
    var funcT = funcTB(stringTB(), intTB());
    var func = exprFuncB(funcT, intB());
    assertThat(bytecodeDbOther().get(func.hash()))
        .isEqualTo(func);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() {
    var funcT = funcTB(stringTB(), intTB());
    var func = exprFuncB(funcT, intB());
    var funcRead = (ExprFuncB) bytecodeDbOther().get(func.hash());
    assertThat(func.body())
        .isEqualTo(funcRead.body());
  }

  @Test
  public void to_string() {
    var funcT = funcTB(stringTB(), intTB());
    var func = exprFuncB(funcT, intB());
    assertThat(func.toString())
        .isEqualTo("ExprFunc((String)->Int)@" + func.hash());
  }
}
