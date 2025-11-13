package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.evaluate.evaluator.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.evaluator.Purity.PURE;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class OperationEvaluatorTest extends VmTestContext {
  @Nested
  class _purity {
    @Test
    void combine_evaluation_is_pure() throws BytecodeException {
      var evaluator = new CombineEvaluator(bCombine(bInt()), trace());
      assertThat(evaluator.purity(bTuple())).isEqualTo(PURE);
    }

    @Test
    void invoke_evaluation_is_pure_when_is_pure_argument_is_true() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(false), bTuple(bInt()));
      var evaluator = new InvokeEvaluator(invoke, trace());
      assertThat(evaluator.purity(bTuple(bMethodTuple(), bBool(true), bInt()))).isEqualTo(PURE);
    }

    @Test
    void invoke_evaluation_is_impure_when_is_pure_argument_is_false() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(true), bTuple(bInt()));
      var evaluator = new InvokeEvaluator(invoke, trace());
      assertThat(evaluator.purity(bTuple(bMethodTuple(), bBool(false), bInt()))).isEqualTo(IMPURE);
    }

    @Test
    void order_evaluation_is_pure() throws BytecodeException {
      var evaluator = new OrderEvaluator(bOrder(bInt()), trace());
      assertThat(evaluator.purity(bTuple())).isEqualTo(PURE);
    }

    @Test
    void pick_evaluation_is_pure() throws BytecodeException {
      var evaluator = new PickEvaluator(bPick(bArray(bInt()), 0), trace());
      assertThat(evaluator.purity(bTuple())).isEqualTo(PURE);
    }

    @Test
    void select_evaluation_is_pure() throws BytecodeException {
      var evaluator = new SelectEvaluator(bSelect(bTuple(bInt()), 0), trace());
      assertThat(evaluator.purity(bTuple())).isEqualTo(PURE);
    }
  }
}
