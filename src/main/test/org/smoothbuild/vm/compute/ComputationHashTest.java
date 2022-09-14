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
        .isEqualTo(Hash.decode("bf12b71e9cfb738ee7ffb6e57667aea0e5478c6c"));
  }

  @Test
  public void hash_of_computation_with_order_task_and_non_empty_input_is_stable() {
    var task = new OrderTask(arrayTB(stringTB()), exprInfo());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("a9116fd5e113887efe896d6e15263cc3f31dbf23"));
  }

  @Test
  public void hash_of_computation_with_invoke_task_and_empty_input_is_stable() {
    var method = methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(stringTB(), "name", method, null, exprInfo());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("b52d91d785a4b35449b0f667423c82527eca83bb"));
  }

  @Test
  public void hash_of_computation_with_invoke_task_and_non_empty_input_is_stable() {
    var method = methodB(methodTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new InvokeTask(stringTB(), "name", method, null, exprInfo());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("640b3acc88c7aec5ff496c5976d09eaeaeffdfa5"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_empty_input_is_stable() {
    var task = new CombineTask(PERSON, exprInfo());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("1b06a8340051d9c875d0d92e3552e2c91336b016"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_one_elem_input_is_stable() {
    var task = new CombineTask(PERSON, exprInfo());
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("5d29a481f10d703e72ad4bd32de1458808ab1e70"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_two_elems_input_is_stable() {
    var task = new CombineTask(PERSON, exprInfo());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("0c4dd2848c3cb653c2a4874a32fe0b6a488cbaf4"));
  }

  @Test
  public void hash_of_computation_with_select_task_and_one_elem_input_is_stable() {
    var task = new SelectTask(STRING, exprInfo());
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("11e4a89011aa0972ea9b785529d0320a7cdfed14"));
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
