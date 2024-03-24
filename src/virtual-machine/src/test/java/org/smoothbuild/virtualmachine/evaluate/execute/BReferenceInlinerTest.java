package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BReferenceInlinerTest extends TestingVirtualMachine {
  @Nested
  class _without_references {

    // operations

    @Test
    public void call() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bCall(bLambda(list(bIntType()), bInt()), bInt()));
    }

    @Test
    public void combine() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bCombine(bInt()));
    }

    @Test
    public void order() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bOrder(bInt()));
    }

    @Test
    public void pick() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bPick(bOrder(bInt(1), bInt(2)), bInt(0)));
    }

    @Test
    public void select() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bSelect(bCombine(bInt()), bInt(0)));
    }

    // values

    @Test
    public void array() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bArray(bInt()));
    }

    @Test
    public void blob() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bBlob());
    }

    @Test
    public void bool() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bBool());
    }

    @Test
    public void int_() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bInt());
    }

    @Test
    public void string() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bString());
    }

    @Test
    public void tuple() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bTuple(bInt()));
    }

    // callables

    @Test
    public void lambda_without_references() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bLambda(bInt()));
    }

    @Test
    public void if_func() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bIf(bIntType()));
    }

    @Test
    public void map_func() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bMap(bIntType(), bBlobType()));
    }

    @Test
    public void native_func() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bNativeFunc(bFuncType(bIntType(), bBlobType())));
    }
  }

  @Nested
  class _with_references_inside {
    @Test
    public void inlining_pure_reference() throws Exception {
      assertReferenceInliningReplacesReference(r -> r);
    }

    @Test
    public void lambda_body_with_var_referencing_param_of_this_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(0, r -> myLambda(r));
    }

    @Test
    public void lambda_body_with_var_referencing_param_of_enclosing_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(1, r -> myLambda(r));
    }

    @Test
    public void lambda_body_with_var_referencing_unbound_param() throws Exception {
      assertReferenceInliningReplacesReference(2, r -> myLambda(r), bInt(1));
    }

    private BLambda myLambda(BExpr expr) throws BytecodeException {
      var inner = bLambda(bFuncType(bBlobType(), bIntType()), expr);
      return bLambda(list(bIntType()), inner);
    }

    @Test
    public void call_argument() throws Exception {
      assertReferenceInliningReplacesReference(r -> bCall(bIdFunc(), r));
    }

    @Test
    public void call_func() throws Exception {
      assertReferenceInliningReplacesReference(r -> bCall(bLambda(r)));
    }

    @Test
    public void combine() throws Exception {
      assertReferenceInliningReplacesReference(BReferenceInlinerTest.this::bCombine);
    }

    @Test
    public void order() throws Exception {
      assertReferenceInliningReplacesReference(BReferenceInlinerTest.this::bOrder);
    }

    @Test
    public void pick_pickable() throws Exception {
      assertReferenceInliningReplacesReference(r -> bPick(bOrder(r), bInt()));
    }

    @Test
    public void pick_index() throws Exception {
      assertReferenceInliningReplacesReference(r -> bPick(bOrder(), r));
    }

    @Test
    public void select_selectable() throws Exception {
      assertReferenceInliningReplacesReference(r -> bSelect(bCombine(r), bInt(0)));
    }
  }

  @Test
  public void reference_with_index_equal_to_environment_size_causes_exception() throws Exception {
    var job = job(bReference(bStringType(), 3), bInt(), bInt(), bInt(17));
    assertCall(() -> bReferenceInliner().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(3, 3));
  }

  @Test
  public void reference_with_negative_index_causes_exception() throws Exception {
    var job = job(bReference(bStringType(), -1), bInt(), bInt(), bInt(17));
    assertCall(() -> bReferenceInliner().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(-1, 3));
  }

  private void assertReferenceInliningReplacesReference(
      Function1<BExpr, BExpr, BytecodeException> factory) throws BytecodeException {
    assertReferenceInliningReplacesReference(2, factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, Function1<BExpr, BExpr, BytecodeException> factory)
      throws BytecodeException {
    assertReferenceInliningReplacesReference(referencedIndex, factory, bInt(3));
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, Function1<BExpr, BExpr, BytecodeException> factory, BInt replacement)
      throws BytecodeException {
    assertReferenceInlining(
        factory.apply(bReference(bIntType(), referencedIndex)), factory.apply(replacement));
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

  private void assertReferenceInlining(BExpr expr, BExpr expected) throws BytecodeException {
    var job = job(expr, bInt(1), bInt(2), bInt(3));
    assertThat(bReferenceInliner().inline(job)).isEqualTo(expected);
  }
}
