package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class ComputationHashTest extends TestVirtualMachine {
  @Test
  public void hashes_of_computations_with_same_task_runtime_and_input_are_equal() throws Exception {
    var task = new ConstTask(intB(7), traceB());
    var input = tupleB(stringB("input"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Computer.computationHash(Hash.of(13), task, input));
  }

  @Test
  public void hashes_of_computations_with_different_task_but_same_runtime_and_input_are_not_equal()
      throws Exception {
    var task1 = new ConstTask(intB(7), traceB());
    var task2 = new ConstTask(intB(9), traceB());
    var input = tupleB(stringB("input"));
    assertThat(Computer.computationHash(Hash.of(13), task1, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), task2, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_and_input_but_different_runtime_are_not_equal()
      throws Exception {
    var task = new ConstTask(intB(7), traceB());
    var input = tupleB(stringB("input"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(14), task, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_runtime_but_different_input_are_not_equal()
      throws Exception {
    var task = new ConstTask(intB(7), traceB());
    var input1 = tupleB(stringB("input"));
    var input2 = tupleB(stringB("input2"));
    assertThat(Computer.computationHash(Hash.of(13), task, input1))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), task, input2));
  }

  @Test
  public void hash_of_computation_with_order_task_and_empty_input_is_stable() throws Exception {
    var task = new OrderTask(orderB(stringTB()), traceB());
    var input = tupleB();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("9f0f79b4df0e66a3c10bf984f87b60e1e16165f53d6bb5921e035ed19de3ce15"));
  }

  @Test
  public void hash_of_computation_with_order_task_and_non_empty_input_is_stable() throws Exception {
    var task = new OrderTask(orderB(stringTB()), traceB());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("768225aee789a5af8d61809fe0b216451dd296f3f86b46ca2787d46dc724d662"));
  }

  @Test
  public void hash_of_computation_with_nat_call_task_and_empty_input_is_stable() throws Exception {
    var nativeFuncB = nativeFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(callB(nativeFuncB), nativeFuncB, traceB());
    var input = tupleB();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("38c1af6ca8f69718173ff5db0d2f1fe51cbfcb0ccc9185ec3c3daef0199cdf1e"));
  }

  @Test
  public void hash_of_computation_with_nat_call_task_and_non_empty_input_is_stable()
      throws Exception {
    var nativeFuncB = nativeFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(callB(nativeFuncB), nativeFuncB, traceB());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("8afeb389e75e86790d622a92d4454b484a62f04a624db873f2fc4c9d2d4dd772"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_empty_input_is_stable() throws Exception {
    var task = new CombineTask(combineB(), traceB());
    var input = tupleB();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("8a8fedb17e870a96e705c91a01279e12d1c33a3ad21edd556ffe28d36fdccd24"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_one_elem_input_is_stable()
      throws Exception {
    var task = new CombineTask(combineB(), traceB());
    var input = tupleB(stringB("abc"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("cbb2f392778e59ec728b8d5bcdae9f07dbe690267ef74e9e6de1bd7ee901e987"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_two_elems_input_is_stable()
      throws Exception {
    var task = new CombineTask(combineB(), traceB());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("74398af06050d223f2ae209e8c6ec219536a05a5a1bcf1f8ea75d5adccd8db3a"));
  }

  @Test
  public void hash_of_computation_with_select_task_and_one_elem_input_is_stable() throws Exception {
    var task = new SelectTask(selectB(), traceB());
    var input = tupleB(stringB("abc"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("1715db3630a84de769e55c2dd3062df4c29518c31d010bca09a00c5ff7483dac"));
  }
}
