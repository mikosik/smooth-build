package org.smoothbuild.virtualmachine.evaluate.compute;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.evaluate.step.CombineStep;
import org.smoothbuild.virtualmachine.evaluate.step.InvokeStep;
import org.smoothbuild.virtualmachine.evaluate.step.OrderStep;
import org.smoothbuild.virtualmachine.evaluate.step.PickStep;
import org.smoothbuild.virtualmachine.evaluate.step.SelectStep;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

public class ComputationHashFactoryTest extends VmTestContext {
  @Test
  void hashes_of_computations_with_same_step_runtime_and_input_are_equal() throws Exception {
    var step = new OrderStep(bOrder(), trace());
    var input = bTuple(bString("input"));
    assertThat(create(Hash.of(13), step, input)).isEqualTo(create(Hash.of(13), step, input));
  }

  @Test
  void hashes_of_computations_with_different_step_but_same_runtime_and_input_are_not_equal()
      throws Exception {
    var step1 = new OrderStep(bOrder(bInt()), trace());
    var step2 = new OrderStep(bOrder(bString()), trace());
    var input = bTuple(bString("input"));
    assertThat(create(Hash.of(13), step1, input)).isNotEqualTo(create(Hash.of(13), step2, input));
  }

  @Test
  void hashes_of_computations_with_same_step_and_input_but_different_runtime_are_not_equal()
      throws Exception {
    var step = new OrderStep(bOrder(), trace());
    var input = bTuple(bString("input"));
    assertThat(create(Hash.of(13), step, input)).isNotEqualTo(create(Hash.of(14), step, input));
  }

  @Test
  void hashes_of_computations_with_same_step_runtime_but_different_input_are_not_equal()
      throws Exception {
    var step = new OrderStep(bOrder(), trace());
    var input1 = bTuple(bString("input"));
    var input2 = bTuple(bString("input2"));
    assertThat(create(Hash.of(13), step, input1)).isNotEqualTo(create(Hash.of(13), step, input2));
  }

  @Nested
  class _computation_hash_is_stable_for {
    @Test
    void combine_step_and_empty_input() throws Exception {
      var step = new CombineStep(bCombine(), trace());
      var input = bTuple();
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("8a8fedb17e870a96e705c91a01279e12d1c33a3ad21edd556ffe28d36fdccd24"));
    }

    @Test
    void combine_step_and_one_element_input() throws Exception {
      var step = new CombineStep(bCombine(), trace());
      var input = bTuple(bString("abc"));
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("cbb2f392778e59ec728b8d5bcdae9f07dbe690267ef74e9e6de1bd7ee901e987"));
    }

    @Test
    void combine_step_and_two_elements_input() throws Exception {
      var step = new CombineStep(bCombine(), trace());
      var input = bTuple(bString("abc"), bString("def"));
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("74398af06050d223f2ae209e8c6ec219536a05a5a1bcf1f8ea75d5adccd8db3a"));
    }

    @Test
    void invoke_step_and_empty_input() throws Exception {
      var invoke = bInvoke(bIntType(), bMethodTuple(bBlob(1), bString("1")), bBool(true), bTuple());
      var trace = trace();
      var step = new InvokeStep(invoke, trace);
      var input = bTuple();
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("22711cea8e4103d1a0a14445824a76f47803b8ef4921206eb58ce9ec91f716cb"));
    }

    @Test
    void order_step_and_empty_input() throws Exception {
      var step = new OrderStep(bOrder(bStringType()), trace());
      var input = bTuple();
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("9f0f79b4df0e66a3c10bf984f87b60e1e16165f53d6bb5921e035ed19de3ce15"));
    }

    @Test
    void pick_step() throws Exception {
      var step = new PickStep(bPick(bArray(bInt(37)), bInt(0)), trace());
      var input = bTuple();
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("e0630f6b53ef98d9774a06cf9a63a1b2099110d32e5e77f80f7b8d71bdacaa0b"));
    }

    @Test
    void order_step_and_non_empty_input() throws Exception {
      var step = new OrderStep(bOrder(bStringType()), trace());
      var input = bTuple(bString("abc"), bString("def"));
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("768225aee789a5af8d61809fe0b216451dd296f3f86b46ca2787d46dc724d662"));
    }

    @Test
    void select_step_and_one_element_input() throws Exception {
      var step = new SelectStep(bSelect(), trace());
      var input = bTuple(bString("abc"));
      assertThat(create(Hash.of(13), step, input))
          .isEqualTo(
              Hash.decode("1715db3630a84de769e55c2dd3062df4c29518c31d010bca09a00c5ff7483dac"));
    }
  }

  private static Hash create(Hash hash, Step step, BTuple input) {
    return new ComputationHashFactory(() -> hash).create(step, input);
  }
}
