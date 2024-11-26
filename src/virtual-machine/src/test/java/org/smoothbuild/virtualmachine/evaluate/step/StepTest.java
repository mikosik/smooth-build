package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.PURE;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class StepTest extends VmTestContext {
  @Nested
  class _purity {
    @Test
    void combine_task_is_pure() throws BytecodeException {
      var combineTask = new CombineStep(bCombine(bInt()), bTrace());
      assertThat(combineTask.purity(null)).isEqualTo(PURE);
    }

    @Test
    void invoke_task_is_pure_when_is_pure_argument_is_true() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(false), bTuple(bInt()));
      var invokeTask = new InvokeStep(invoke, bTrace());
      assertThat(invokeTask.purity(bTuple(bMethodTuple(), bBool(true), bInt()))).isEqualTo(PURE);
    }

    @Test
    void invoke_task_is_impure_when_is_pure_argument_is_false() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(true), bTuple(bInt()));
      var invokeTask = new InvokeStep(invoke, bTrace());
      assertThat(invokeTask.purity(bTuple(bMethodTuple(), bBool(false), bInt())))
          .isEqualTo(IMPURE);
    }

    @Test
    void order_task_is_pure() throws BytecodeException {
      var orderTask = new OrderStep(bOrder(bInt()), bTrace());
      assertThat(orderTask.purity(null)).isEqualTo(PURE);
    }

    @Test
    void pick_task_is_pure() throws BytecodeException {
      var pickTask = new PickStep(bPick(bArray(bInt()), 0), bTrace());
      assertThat(pickTask.purity(null)).isEqualTo(PURE);
    }

    @Test
    void select_task_is_pure() throws BytecodeException {
      var selectTask = new SelectStep(bSelect(bTuple(bInt()), 0), bTrace());
      assertThat(selectTask.purity(null)).isEqualTo(PURE);
    }
  }
}
