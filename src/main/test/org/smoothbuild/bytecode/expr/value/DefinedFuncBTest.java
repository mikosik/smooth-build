package org.smoothbuild.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class DefinedFuncBTest extends TestContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var funcT = funcTB(stringTB(), intTB());
    assertCall(() -> defFuncB(funcT, boolB(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var funcT = funcTB(boolTB(), intTB());
    assertCall(() -> defFuncB(funcT, null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_func_is_func_type() {
    var funcT = funcTB(stringTB(), intTB());
    assertThat(defFuncB(funcT, intB()).evalT())
        .isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_construction() {
    var funcT = funcTB(boolTB(), intTB());
    var body = intB(33);
    var defFuncB = defFuncB(funcT, body);
    assertThat(defFuncB.body())
        .isEqualTo(body);
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<DefinedFuncB> {
    @Override
    protected List<DefinedFuncB> equalExprs() {
      return list(
          defFuncB(funcTB(stringTB(), intTB()), intB(7)),
          defFuncB(funcTB(stringTB(), intTB()), intB(7))
      );
    }

    @Override
    protected List<DefinedFuncB> nonEqualExprs() {
      return list(
          defFuncB(funcTB(stringTB(), intTB()), intB(7)),
          defFuncB(funcTB(stringTB(), intTB()), intB(0)),
          defFuncB(funcTB(blobTB(), intTB()), intB(7)),
          defFuncB(funcTB(stringTB(), boolTB()), boolB(true))
      );
    }
  }

  @Test
  public void func_can_be_read_by_hash() {
    var funcT = funcTB(stringTB(), intTB());
    var func = defFuncB(funcT, intB());
    assertThat(bytecodeDbOther().get(func.hash()))
        .isEqualTo(func);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() {
    var funcT = funcTB(stringTB(), intTB());
    var func = defFuncB(funcT, intB());
    var funcRead = (DefinedFuncB) bytecodeDbOther().get(func.hash());
    assertThat(func.body())
        .isEqualTo(funcRead.body());
  }

  @Test
  public void to_string() {
    var funcT = funcTB(stringTB(), intTB());
    var func = defFuncB(funcT, intB());
    assertThat(func.toString())
        .isEqualTo("DefinedFunc((String)->Int)@" + func.hash());
  }
}
