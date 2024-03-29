package org.smoothbuild.vm.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.evaluate.task.CombineTask;
import org.smoothbuild.vm.evaluate.task.ConstTask;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.SelectTask;

public class ComputationHashTest extends TestContext {
  @Test
  public void hashes_of_computations_with_same_task_runtime_and_input_are_equal() {
    var task = new ConstTask(intB(7), traceB());
    var input = tupleB(stringB("input"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Computer.computationHash(Hash.of(13), task, input));
  }

  @Test
  public void hashes_of_computations_with_different_task_but_same_runtime_and_input_are_not_equal() {
    var task1 = new ConstTask(intB(7), traceB());
    var task2 = new ConstTask(intB(9), traceB());
    var input = tupleB(stringB("input"));
    assertThat(Computer.computationHash(Hash.of(13), task1, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), task2, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_and_input_but_different_runtime_are_not_equal() {
    var task = new ConstTask(intB(7), traceB());
    var input = tupleB(stringB("input"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(14), task, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_runtime_but_different_input_are_not_equal() {
    var task = new ConstTask(intB(7), traceB());
    var input1 = tupleB(stringB("input"));
    var input2 = tupleB(stringB("input2"));
    assertThat(Computer.computationHash(Hash.of(13), task, input1))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), task, input2));
  }

  @Test
  public void hash_of_computation_with_order_task_and_empty_input_is_stable() {
    var task = new OrderTask(orderB(stringTB()), traceB());
    var input = tupleB();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("30604c02da975812b604e4ef6c64a70ebb3f2558"));
  }

  @Test
  public void hash_of_computation_with_order_task_and_non_empty_input_is_stable() {
    var task = new OrderTask(orderB(stringTB()), traceB());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("033006f518778bb169b838591f713dec1b6569b3"));
  }

  @Test
  public void hash_of_computation_with_nat_call_task_and_empty_input_is_stable() {
    var nativeFuncB = nativeFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(callB(nativeFuncB), nativeFuncB, null, traceB());
    var input = tupleB();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("750ee778096a96419b3b8b300669f337f3e60d67"));
  }

  @Test
  public void hash_of_computation_with_nat_call_task_and_non_empty_input_is_stable() {
    var nativeFuncB = nativeFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(callB(nativeFuncB), nativeFuncB, null, traceB());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("bc6ad5c125aadf19a301dd8b6af4300eee643ce5"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_empty_input_is_stable() {
    var task = new CombineTask(combineB(), traceB());
    var input = tupleB();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("0040e02acc4f1ebbe6648a6f49b78028be764180"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_one_elem_input_is_stable() {
    var task = new CombineTask(combineB(), traceB());
    var input = tupleB(stringB("abc"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("dee3433c1a29da4421a06396d5b0bed371462759"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_two_elems_input_is_stable() {
    var task = new CombineTask(combineB(), traceB());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("42cc3a3222c00e5f669a133f8e5989fb11ce91b0"));
  }

  @Test
  public void hash_of_computation_with_select_task_and_one_elem_input_is_stable() {
    var task = new SelectTask(selectB(), traceB());
    var input = tupleB(stringB("abc"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("4ae0c582dfeccb5a7ed2ce0420a25197e1b070f1"));
  }
}
