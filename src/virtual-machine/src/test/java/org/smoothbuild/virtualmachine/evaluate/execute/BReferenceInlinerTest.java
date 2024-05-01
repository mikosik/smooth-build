package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class BReferenceInlinerTest extends TestingVm {
  @Nested
  class _without_references {

    // operations

    @Test
    void call() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bCall(bLambda(list(bIntType()), bInt()), bInt()));
    }

    @Test
    void combine() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bCombine(bInt()));
    }

    @Test
    void order() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bOrder(bInt()));
    }

    @Test
    void pick() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bPick(bOrder(bInt(1), bInt(2)), bInt(0)));
    }

    @Test
    void select() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bSelect(bCombine(bInt()), bInt(0)));
    }

    // values

    @Test
    void array() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bArray(bInt()));
    }

    @Test
    void blob() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bBlob());
    }

    @Test
    void bool() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bBool());
    }

    @Test
    void if_() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bIf(bBool(), bInt(), bInt()));
    }

    @Test
    void int_() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bInt());
    }

    @Test
    void invoke() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bInvoke(bIntType(), bMethodTuple(), bBool(), bTuple()));
    }

    @Test
    void lambda_without_references() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bLambda(bInt()));
    }

    @Test
    void map() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bMap(bArray(bInt()), bIntIdLambda()));
    }

    @Test
    void string() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bString());
    }

    @Test
    void tuple() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bTuple(bInt()));
    }
  }

  @Nested
  class _with_references_inside {
    @Test
    void inlining_pure_reference() throws Exception {
      assertReferenceInliningReplacesReference(r -> r);
    }

    @Test
    void lambda_body_with_var_referencing_param_of_this_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(0, r -> bLambda(list(bIntType()), r));
    }

    @Test
    void lambda_body_with_var_referencing_param_of_enclosing_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(1, r -> lambdaInsideLambda(r));
    }

    @Test
    void lambda_body_with_var_referencing_unbound_param() throws Exception {
      assertReferenceInliningReplacesReference(2, bInt(1), r -> lambdaInsideLambda(r));
    }

    private BLambda lambdaInsideLambda(BExpr r) throws BytecodeException {
      var inner = bLambda(list(bIntType()), r);
      return bLambda(list(bIntType()), inner);
    }

    @Test
    void call_argument() throws Exception {
      assertReferenceInliningReplacesReference(r -> bCall(bIntIdLambda(), r));
    }

    @Test
    void call_lambda() throws Exception {
      assertReferenceInliningReplacesReference(r -> bCall(bLambda(r)));
    }

    @Test
    void combine() throws Exception {
      assertReferenceInliningReplacesReference(BReferenceInlinerTest.this::bCombine);
    }

    @Test
    void if_condition() throws Exception {
      assertReferenceInliningReplacesReference(
          2, bBool(false), list(bInt(1), bInt(2), bBool(false)), r -> bIf(r, bInt(7), bInt(8)));
    }

    @Test
    void if_then() throws Exception {
      assertReferenceInliningReplacesReference(r -> bIf(bBool(), r, bInt(33)));
    }

    @Test
    void if_else() throws Exception {
      assertReferenceInliningReplacesReference(r -> bIf(bBool(), bInt(33), r));
    }

    @Test
    void invoke_method() throws Exception {
      assertReferenceInliningReplacesReference(
          2,
          bMethodTuple("2"),
          list(bMethodTuple("0"), bMethodTuple("1"), bMethodTuple("2")),
          r -> bInvoke(bIntType(), r, bBool(), bTuple()));
    }

    @Test
    void invoke_is_pure() throws Exception {
      assertReferenceInliningReplacesReference(
          2,
          bBool(true),
          list(bBool(false), bBool(false), bBool(true)),
          r -> bInvoke(bIntType(), bMethodTuple(), r, bTuple()));
    }

    @Test
    void invoke_arguments() throws Exception {
      assertReferenceInliningReplacesReference(
          2,
          bTuple(bInt(2)),
          list(bTuple(), bTuple(), bTuple(bInt(2))),
          r -> bInvoke(bIntType(), bMethodTuple(), bBool(), r));
    }

    @Test
    void map_array() throws Exception {
      assertReferenceInliningReplacesReference(r -> bMap(bOrder(r), bIntIdLambda()));
    }

    @Test
    void map_mapper() throws Exception {
      assertReferenceInliningReplacesReference(
          2, bInt(2), r -> bMap(bArray(bInt()), bLambda(list(bIntType()), r)));
    }

    @Test
    void order() throws Exception {
      assertReferenceInliningReplacesReference(BReferenceInlinerTest.this::bOrder);
    }

    @Test
    void pick_pickable() throws Exception {
      assertReferenceInliningReplacesReference(r -> bPick(bOrder(r), bInt()));
    }

    @Test
    void pick_index() throws Exception {
      assertReferenceInliningReplacesReference(r -> bPick(bOrder(), r));
    }

    @Test
    void select_selectable() throws Exception {
      assertReferenceInliningReplacesReference(r -> bSelect(bCombine(r), bInt(0)));
    }
  }

  @Test
  void reference_with_index_equal_to_environment_size_causes_exception() throws Exception {
    var job = job(bReference(bStringType(), 3), bInt(), bInt(), bInt(17));
    assertCall(() -> bReferenceInliner().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(3, 3));
  }

  @Test
  void reference_with_negative_index_causes_exception() throws Exception {
    var job = job(bReference(bStringType(), -1), bInt(), bInt(), bInt(17));
    assertCall(() -> bReferenceInliner().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(-1, 3));
  }

  private void assertReferenceInliningReplacesReference(
      Function1<BExpr, BExpr, BytecodeException> factory) throws BytecodeException {
    assertReferenceInliningReplacesReference(2, bInt(3), factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex,
      BInt expectedReplacement,
      Function1<BExpr, BExpr, BytecodeException> factory)
      throws BytecodeException {
    List<BExpr> environment = list(bInt(1), bInt(2), bInt(3));
    assertReferenceInliningReplacesReference(
        referencedIndex, expectedReplacement, environment, factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex,
      BExpr expectedReplacement,
      List<BExpr> environment,
      Function1<BExpr, BExpr, BytecodeException> factory)
      throws BytecodeException {
    var referenceEvaluationType = environment.get(referencedIndex).evaluationType();
    BExpr expr = factory.apply(bReference(referenceEvaluationType, referencedIndex));
    BExpr expected = factory.apply(expectedReplacement);
    var job = job(expr, environment);
    assertThat(bReferenceInliner().inline(job)).isEqualTo(expected);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      Function1<BExpr, BExpr, BytecodeException> factory) throws Exception {
    assertReferenceInliningDoesNotChangeExpression(1, factory);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      int referencedIndex, Function1<BExpr, BExpr, BytecodeException> factory)
      throws BytecodeException {
    var expr = factory.apply(bReference(bIntType(), referencedIndex));
    var job = job(expr, bInt(1), bInt(2), bInt(3));
    assertThat(bReferenceInliner().inline(job)).isSameInstanceAs(expr);
  }
}
