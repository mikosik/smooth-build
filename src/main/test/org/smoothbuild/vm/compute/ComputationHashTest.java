package org.smoothbuild.vm.compute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.vm.compute.Computer.computationHash;
import static org.smoothbuild.vm.execute.TaskKind.COMBINE;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.ExecutableTask;
import org.smoothbuild.vm.task.NativeCallTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.Output;
import org.smoothbuild.vm.task.SelectTask;

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
    var task = new OrderTask(arrayTB(stringTB()), tagLoc(), traceS());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("30604c02da975812b604e4ef6c64a70ebb3f2558"));
  }

  @Test
  public void hash_of_computation_with_order_task_and_non_empty_input_is_stable() {
    var task = new OrderTask(arrayTB(stringTB()), tagLoc(), traceS());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("033006f518778bb169b838591f713dec1b6569b3"));
  }

  @Test
  public void hash_of_computation_with_nat_call_task_and_empty_input_is_stable() {
    var natFuncB = natFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new NativeCallTask(stringTB(), "name", natFuncB, null, tagLoc(), traceS());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("768a18308c96099b14ecdead9ef4fc8cd0858d5a"));
  }

  @Test
  public void hash_of_computation_with_nat_call_task_and_non_empty_input_is_stable() {
    var natFuncB = natFuncB(funcTB(intTB()), blobB(1), stringB("1"), boolB(true));
    var task = new NativeCallTask(stringTB(), "name", natFuncB, null, tagLoc(), traceS());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("49c8bb994eddedd9b642c9f03f3d32b5005c3fc4"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_empty_input_is_stable() {
    var task = new CombineTask(personTB(), tagLoc(), traceS());
    var input = tupleB();
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("0040e02acc4f1ebbe6648a6f49b78028be764180"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_one_elem_input_is_stable() {
    var task = new CombineTask(personTB(), tagLoc(), traceS());
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("dee3433c1a29da4421a06396d5b0bed371462759"));
  }

  @Test
  public void hash_of_computation_with_combine_task_and_two_elems_input_is_stable() {
    var task = new CombineTask(personTB(), tagLoc(), traceS());
    var input = tupleB(stringB("abc"), stringB("def"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("42cc3a3222c00e5f669a133f8e5989fb11ce91b0"));
  }

  @Test
  public void hash_of_computation_with_select_task_and_one_elem_input_is_stable() {
    var task = new SelectTask(stringTB(), tagLoc(), traceS());
    var input = tupleB(stringB("abc"));
    assertThat(computationHash(Hash.of(13), task, input))
        .isEqualTo(Hash.decode("4ae0c582dfeccb5a7ed2ce0420a25197e1b070f1"));
  }

  private ExecutableTask task(Hash hash) {
    return new ExecutableTask(intTB(), COMBINE, tagLoc(), traceS(), hash) {
      @Override
      public Output run(TupleB input, NativeApi nativeApi) {
        return null;
      }
    };
  }
}
