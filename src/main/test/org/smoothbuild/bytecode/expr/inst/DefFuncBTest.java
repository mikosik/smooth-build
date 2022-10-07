package org.smoothbuild.bytecode.expr.inst;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class DefFuncBTest extends TestContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var funcT = funcTB(intTB(), stringTB());
    assertCall(() -> defFuncB(funcT, boolB(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var funcT = funcTB(intTB(), boolTB());
    assertCall(() -> defFuncB(funcT, null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_func_is_func_type() {
    var funcT = funcTB(intTB(), stringTB());
    assertThat(defFuncB(funcT, intB()).evalT())
        .isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_combineion() {
    var funcT = funcTB(intTB(), boolTB());
    var body = intB(33);
    assertThat(defFuncB(funcT, body).body())
        .isEqualTo(body);
  }


  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<DefFuncB> {
    @Override
    protected List<DefFuncB> equalExprs() {
      return list(
          defFuncB(funcTB(intTB(), stringTB()), intB(7)),
          defFuncB(funcTB(intTB(), stringTB()), intB(7))
      );
    }

    @Override
    protected List<DefFuncB> nonEqualExprs() {
      return list(
          defFuncB(funcTB(intTB(), stringTB()), intB(7)),
          defFuncB(funcTB(intTB(), stringTB()), intB(0)),
          defFuncB(funcTB(intTB(), blobTB()), intB(7)),
          defFuncB(funcTB(boolTB(), stringTB()), boolB(true))
      );
    }
  }

  @Test
  public void func_can_be_read_by_hash() {
    var funcT = funcTB(intTB(), stringTB());
    var func = defFuncB(funcT, intB());
    assertThat(bytecodeDbOther().get(func.hash()))
        .isEqualTo(func);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() {
    var funcT = funcTB(intTB(), stringTB());
    var func = defFuncB(funcT, intB());
    var funcRead = (DefFuncB) bytecodeDbOther().get(func.hash());
    assertThat(func.body())
        .isEqualTo(funcRead.body());
  }

  @Test
  public void to_string() {
    var funcT = funcTB(intTB(), stringTB());
    var func = defFuncB(funcT, intB());
    assertThat(func.toString())
        .isEqualTo("DefFunc(Int(String))@" + func.hash());
  }
}
