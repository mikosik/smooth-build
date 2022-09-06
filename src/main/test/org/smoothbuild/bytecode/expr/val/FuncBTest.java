package org.smoothbuild.bytecode.expr.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class FuncBTest extends TestContext {
  @Test
  public void creating_func_with_body_evaluation_type_not_equal_result_type_causes_exception() {
    var funcT = funcTB(intTB(), stringTB());
    assertCall(() -> funcB(funcT, boolB(true)))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_body_to_null_throws_exception() {
    var funcT = funcTB(intTB(), boolTB());
    assertCall(() -> funcB(funcT, null))
        .throwsException(NullPointerException.class);
  }


  @Test
  public void type_of_func_is_func_type() {
    var funcT = funcTB(intTB(), stringTB());
    assertThat(funcB(funcT, intB()).cat())
        .isEqualTo(funcT);
  }

  @Test
  public void body_contains_object_passed_during_combineion() {
    var funcT = funcTB(intTB(), boolTB());
    var body = intB(33);
    assertThat(funcB(funcT, body).body())
        .isEqualTo(body);
  }


  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<FuncB> {
    @Override
    protected List<FuncB> equalExprs() {
      return list(
          funcB(funcTB(intTB(), stringTB()), intB(7)),
          funcB(funcTB(intTB(), stringTB()), intB(7))
      );
    }

    @Override
    protected List<FuncB> nonEqualExprs() {
      return list(
          funcB(funcTB(intTB(), stringTB()), intB(7)),
          funcB(funcTB(intTB(), stringTB()), intB(0)),
          funcB(funcTB(intTB(), blobTB()), intB(7)),
          funcB(funcTB(boolTB(), stringTB()), boolB(true))
      );
    }
  }

  @Test
  public void func_can_be_read_by_hash() {
    var funcT = funcTB(intTB(), stringTB());
    var func = funcB(funcT, intB());
    assertThat(bytecodeDbOther().get(func.hash()))
        .isEqualTo(func);
  }

  @Test
  public void funcs_read_by_hash_have_equal_bodies() {
    var funcT = funcTB(intTB(), stringTB());
    var func = funcB(funcT, intB());
    var funcRead = (FuncB) bytecodeDbOther().get(func.hash());
    assertThat(func.body())
        .isEqualTo(funcRead.body());
  }

  @Test
  public void to_string() {
    var funcT = funcTB(intTB(), stringTB());
    var func = funcB(funcT, intB());
    assertThat(func.toString())
        .isEqualTo("Func(Int(String))@" + func.hash());
  }
}
