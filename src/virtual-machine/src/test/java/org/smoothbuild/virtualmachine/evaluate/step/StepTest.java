package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.PURE;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class StepTest extends VmTestContext {
  @Nested
  class _purity {
    @Test
    void combine_step_is_pure() throws BytecodeException {
      var combineStep = new CombineStep(bCombine(bInt()), trace());
      assertThat(combineStep.purity(bTuple())).isEqualTo(PURE);
    }

    @Test
    void invoke_step_is_pure_when_is_pure_argument_is_true() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(false), bTuple(bInt()));
      var invokeStep = new InvokeStep(invoke, trace());
      assertThat(invokeStep.purity(bTuple(bMethodTuple(), bBool(true), bInt()))).isEqualTo(PURE);
    }

    @Test
    void invoke_step_is_impure_when_is_pure_argument_is_false() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(true), bTuple(bInt()));
      var invokeStep = new InvokeStep(invoke, trace());
      assertThat(invokeStep.purity(bTuple(bMethodTuple(), bBool(false), bInt())))
          .isEqualTo(IMPURE);
    }

    @Test
    void order_step_is_pure() throws BytecodeException {
      var orderStep = new OrderStep(bOrder(bInt()), trace());
      assertThat(orderStep.purity(bTuple())).isEqualTo(PURE);
    }

    @Test
    void pick_step_is_pure() throws BytecodeException {
      var pickStep = new PickStep(bPick(bArray(bInt()), 0), trace());
      assertThat(pickStep.purity(bTuple())).isEqualTo(PURE);
    }

    @Test
    void select_step_is_pure() throws BytecodeException {
      var selectStep = new SelectStep(bSelect(bTuple(bInt()), 0), trace());
      assertThat(selectStep.purity(bTuple())).isEqualTo(PURE);
    }
  }
}
