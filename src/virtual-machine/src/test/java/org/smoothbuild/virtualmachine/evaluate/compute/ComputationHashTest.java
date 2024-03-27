package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.virtualmachine.evaluate.task.InvokeTask.newInvokeTask;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ComputationHashTest extends TestingVirtualMachine {
  @Test
  public void hashes_of_computations_with_same_task_runtime_and_input_are_equal() throws Exception {
    var task = new ConstTask(bInt(7), bTrace());
    var input = bTuple(bString("input"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Computer.computationHash(Hash.of(13), task, input));
  }

  @Test
  public void hashes_of_computations_with_different_task_but_same_runtime_and_input_are_not_equal()
      throws Exception {
    var task1 = new ConstTask(bInt(7), bTrace());
    var task2 = new ConstTask(bInt(9), bTrace());
    var input = bTuple(bString("input"));
    assertThat(Computer.computationHash(Hash.of(13), task1, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), task2, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_and_input_but_different_runtime_are_not_equal()
      throws Exception {
    var task = new ConstTask(bInt(7), bTrace());
    var input = bTuple(bString("input"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isNotEqualTo(Computer.computationHash(Hash.of(14), task, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_runtime_but_different_input_are_not_equal()
      throws Exception {
    var task = new ConstTask(bInt(7), bTrace());
    var input1 = bTuple(bString("input"));
    var input2 = bTuple(bString("input2"));
    assertThat(Computer.computationHash(Hash.of(13), task, input1))
        .isNotEqualTo(Computer.computationHash(Hash.of(13), task, input2));
  }

  @Test
  public void hash_of_computation_with_order_task_and_empty_input_is_stable() throws Exception {
    var task = new OrderTask(bOrder(bStringType()), bTrace());
    var input = bTuple();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("9f0f79b4df0e66a3c10bf984f87b60e1e16165f53d6bb5921e035ed19de3ce15"));
  }

  @Test
  public void hash_of_computation_with_order_task_and_non_empty_input_is_stable() throws Exception {
    var task = new OrderTask(bOrder(bStringType()), bTrace());
    var input = bTuple(bString("abc"), bString("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("768225aee789a5af8d61809fe0b216451dd296f3f86b46ca2787d46dc724d662"));
  }

  @Test
  public void hash_of_computation_with_invoke_task_and_empty_input_is_stable() throws Exception {
    var invoke = bInvoke(bIntType(), bBlob(1), bString("1"), bBool(true), bTuple());
    var task = newInvokeTask(invoke, bTrace());
    var input = bTuple();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("af39446a6505d825626406b7e483387f993ddeccc432bf45bbcf1d262deeb05d"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_empty_input_is_stable() throws Exception {
    var task = new CombineTask(bCombine(), bTrace());
    var input = bTuple();
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("8a8fedb17e870a96e705c91a01279e12d1c33a3ad21edd556ffe28d36fdccd24"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_one_element_input_is_stable()
      throws Exception {
    var task = new CombineTask(bCombine(), bTrace());
    var input = bTuple(bString("abc"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("cbb2f392778e59ec728b8d5bcdae9f07dbe690267ef74e9e6de1bd7ee901e987"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_two_elements_input_is_stable()
      throws Exception {
    var task = new CombineTask(bCombine(), bTrace());
    var input = bTuple(bString("abc"), bString("def"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("74398af06050d223f2ae209e8c6ec219536a05a5a1bcf1f8ea75d5adccd8db3a"));
  }

  @Test
  public void hash_of_computation_with_select_task_and_one_element_input_is_stable()
      throws Exception {
    var task = new SelectTask(bSelect(), bTrace());
    var input = bTuple(bString("abc"));
    assertThat(Computer.computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("1715db3630a84de769e55c2dd3062df4c29518c31d010bca09a00c5ff7483dac"));
  }
}
