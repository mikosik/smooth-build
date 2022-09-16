package org.smoothbuild.vm.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.type.TestingCatsB.INT;
import static org.smoothbuild.testing.type.TestingCatsB.PERSON;
import static org.smoothbuild.testing.type.TestingCatsB.STRING;
import static org.smoothbuild.vm.compute.Computer.computationHash;
import static org.smoothbuild.vm.execute.TaskKind.COMBINE;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.InvokeTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.Output;
import org.smoothbuild.vm.task.SelectTask;
import org.smoothbuild.vm.task.Task;

public class ComputationHashTest extends TestContext {
  @Test
  public void hashes_of_computations_with_same_task_runtime_and_input_are_equal() {
    var task = task(Hash.of(1));
    var input = tupleB(stringB("input"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(computationHash(Hash.of(13), task, input));
  }

  @Test
  public void hashes_of_computations_with_different_task_but_same_runtime_and_input_are_not_equal() {
    var task1 = task(Hash.of(1));
    var task2 = task(Hash.of(2));
    var input = tupleB(stringB("input"));
    assertThat(computationHash(Hash.of(13), task1, input))
        .isNotEqualTo(computationHash(Hash.of(13), task2, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_and_input_but_different_runtime_are_not_equal() {
    var task = task(Hash.of(1));
    var input = tupleB(stringB("input"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isNotEqualTo(computationHash(Hash.of(14), task, input));
  }

  @Test
  public void hashes_of_computations_with_same_task_runtime_but_different_input_are_not_equal() {
    var task = task(Hash.of(1));
    var input1 = tupleB(stringB("input"));
    var input2 = tupleB(stringB("input2"));
    assertThat(computationHash(Hash.of(13), task, input1))
        .isNotEqualTo(computationHash(Hash.of(13), task, input2));
  }

  @Test
  public void hash_of_computation_with_order_task_and_empty_input_is_stable() {
    var task = new OrderTask(arrayTB(stringTB()), exprInfo());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("30604c02da975812b604e4ef6c64a70ebb3f2558"));
  }

  @Test
  public void hash_of_computation_with_order_task_and_non_empty_input_is_stable() {
    var task = new OrderTask(arrayTB(stringTB()), exprInfo());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("033006f518778bb169b838591f713dec1b6569b3"));
  }

  @Test
  public void hash_of_computation_with_invoke_task_and_empty_input_is_stable() {
    var method = methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(stringTB(), "name", method, null, exprInfo());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("8af4786abb7b096b17aaedf1feb5e51834f71d20"));
  }

  @Test
  public void hash_of_computation_with_invoke_task_and_non_empty_input_is_stable() {
    var method = methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(stringTB(), "name", method, null, exprInfo());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("f48d503014f4d834c91aa40c056866e992c513a0"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_empty_input_is_stable() {
    var task = new CombineTask(PERSON, exprInfo());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("2113bf2108731cfc09ecb7aaee468bfb9df2dd94"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_one_elem_input_is_stable() {
    var task = new CombineTask(PERSON, exprInfo());
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("3eed1bde59bea1a71d15bd5bd9a12fbfa8c62d36"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_two_elems_input_is_stable() {
    var task = new CombineTask(PERSON, exprInfo());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("3c2e1f674f254bd1c1df971541ada79da8fcf3ce"));
  }

  @Test
  public void hash_of_computation_with_select_task_and_one_elem_input_is_stable() {
    var task = new SelectTask(STRING, exprInfo());
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("4ae0c582dfeccb5a7ed2ce0420a25197e1b070f1"));
  }

  private static Task task(Hash hash) {
    return new Task(INT, COMBINE, exprInfo()) {
      @Override
      public Hash hash() {
        return hash;
      }

      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
