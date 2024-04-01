package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.FAST;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.PURE;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TaskTest extends TestingVirtualMachine {
  @Nested
  class _purity {
    @Test
    void combine_task_is_pure() throws BytecodeException {
      var combineTask = new CombineTask(bCombine(bInt()), bTrace());
      assertThat(combineTask.purity(null)).isEqualTo(PURE);
    }

    @Test
    void const_task_is_fast() throws BytecodeException {
      var constTask = new ConstTask(bInt(), bTrace());
      assertThat(constTask.purity(null)).isEqualTo(FAST);
    }

    @Test
    void invoke_task_is_pure_when_is_pure_argument_is_true() throws BytecodeException {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(false), bTuple(bInt()));
      var invokeTask = new InvokeTask(invoke, bTrace());
      assertThat(invokeTask.purity(bTuple(bMethodTuple(), bBool(true), bInt()))).isEqualTo(PURE);
    }

    @Test
    void invoke_task_is_impure_when_is_pure_argument_is_false() throws BytecodeException {
      var invoke = bInvoke(bIntType(), bMethodTuple(), bBool(true), bTuple(bInt()));
      var invokeTask = new InvokeTask(invoke, bTrace());
      assertThat(invokeTask.purity(bTuple(bMethodTuple(), bBool(false), bInt())))
          .isEqualTo(IMPURE);
    }

    @Test
    void order_task_is_pure() throws BytecodeException {
      var orderTask = new OrderTask(bOrder(bInt()), bTrace());
      assertThat(orderTask.purity(null)).isEqualTo(PURE);
    }

    @Test
    void pick_task_is_pure() throws BytecodeException {
      var pickTask = new PickTask(bPick(bArray(bInt()), 0), bTrace());
      assertThat(pickTask.purity(null)).isEqualTo(PURE);
    }

    @Test
    void select_task_is_pure() throws BytecodeException {
      var selectTask = new SelectTask(bSelect(bTuple(bInt()), 0), bTrace());
      assertThat(selectTask.purity(null)).isEqualTo(PURE);
    }
  }
}
