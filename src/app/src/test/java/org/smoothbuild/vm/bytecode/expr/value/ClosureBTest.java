package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;

public class ClosureBTest extends TestContext {
  @Test
  public void setting_environment_to_null_throws_exception() {
    assertCall(() -> closureB((CombineB) null, intB()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_func_to_null_throws_exception() {
    assertCall(() -> closureB(combineB(), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_of_closure_is_func_type() {
    assertThat(closureB(idFuncB()).evaluationT())
        .isEqualTo(idFuncB().type());
  }

  @Test
  public void environment_contains_object_passed_during_construction() {
    var environment = combineB(intB());
    var closureB = closureB(environment, intB(33));
    assertThat(closureB.environment())
        .isEqualTo(environment);
  }

  @Test
  public void closure_contains_object_passed_during_construction() {
    var func = exprFuncB(intB(33));
    var closureB = closureB(combineB(intB()), func);
    assertThat(closureB.func())
        .isEqualTo(func);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ClosureB> {
    @Override
    protected List<ClosureB> equalExprs() {
      return list(
          closureB(combineB(stringB("a")), exprFuncB(list(intTB()), intB(7))),
          closureB(combineB(stringB("a")), exprFuncB(list(intTB()), intB(7)))
      );
    }

    @Override
    protected List<ClosureB> nonEqualExprs() {
      return list(
          closureB(combineB(), exprFuncB(list(), intB(7))),
          closureB(combineB(), exprFuncB(list(), intB(17))),
          closureB(combineB(), exprFuncB(list(intTB()), intB(7))),
          closureB(combineB(intB(7)), exprFuncB(list(), intB(7)))
      );
    }
  }

  @Test
  public void closure_can_be_read_by_hash() {
    var closure = closureB(intB());
    assertThat(bytecodeDbOther().get(closure.hash()))
        .isEqualTo(closure);
  }

  @Test
  public void closure_read_by_hash_have_equal_functions() {
    var closure = closureB(intB());
    var closureRead = (ClosureB) bytecodeDbOther().get(closure.hash());
    assertThat(closure.func())
        .isEqualTo(closureRead.func());
  }

  @Test
  public void to_string() {
    var closure = closureB(list(stringTB()), intB());
    assertThat(closure.toString())
        .isEqualTo("Closure((String)->Int)@" + closure.hash());
  }
}
