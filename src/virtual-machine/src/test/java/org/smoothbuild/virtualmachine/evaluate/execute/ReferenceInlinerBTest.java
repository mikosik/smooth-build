package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ReferenceInlinerBTest extends TestingVirtualMachine {
  @Nested
  class _without_references {

    // operations

    @Test
    public void call() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> callB(lambdaB(list(intTB()), intB()), intB()));
    }

    @Test
    public void combine() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> combineB(intB()));
    }

    @Test
    public void order() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> orderB(intB()));
    }

    @Test
    public void pick() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> pickB(orderB(intB(1), intB(2)), intB(0)));
    }

    @Test
    public void select() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> selectB(combineB(intB()), intB(0)));
    }

    // values

    @Test
    public void array() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> arrayB(intB()));
    }

    @Test
    public void blob() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> blobB());
    }

    @Test
    public void bool() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> boolB());
    }

    @Test
    public void int_() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> intB());
    }

    @Test
    public void string() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> stringB());
    }

    @Test
    public void tuple() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> tupleB(intB()));
    }

    // callables

    @Test
    public void lambda_without_references() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> lambdaB(intB()));
    }

    @Test
    public void if_func() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> ifFuncB(intTB()));
    }

    @Test
    public void map_func() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> mapFuncB(intTB(), blobTB()));
    }

    @Test
    public void native_func() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> nativeFuncB(funcTB(intTB(), blobTB())));
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
      assertReferenceInliningReplacesReference(2, r -> myLambda(r), intB(1));
    }

    private LambdaB myLambda(ExprB exprB) throws BytecodeException {
      var inner = lambdaB(funcTB(blobTB(), intTB()), exprB);
      return lambdaB(list(intTB()), inner);
    }

    @Test
    public void call_argument() throws Exception {
      assertReferenceInliningReplacesReference(r -> callB(idFuncB(), r));
    }

    @Test
    public void call_func() throws Exception {
      assertReferenceInliningReplacesReference(r -> callB(lambdaB(r)));
    }

    @Test
    public void combine() throws Exception {
      assertReferenceInliningReplacesReference(ReferenceInlinerBTest.this::combineB);
    }

    @Test
    public void order() throws Exception {
      assertReferenceInliningReplacesReference(ReferenceInlinerBTest.this::orderB);
    }

    @Test
    public void pick_pickable() throws Exception {
      assertReferenceInliningReplacesReference(r -> pickB(orderB(r), intB()));
    }

    @Test
    public void pick_index() throws Exception {
      assertReferenceInliningReplacesReference(r -> pickB(orderB(), r));
    }

    @Test
    public void select_selectable() throws Exception {
      assertReferenceInliningReplacesReference(r -> selectB(combineB(r), intB(0)));
    }
  }

  @Test
  public void reference_with_index_equal_to_environment_size_causes_exception() throws Exception {
    var job = job(referenceB(stringTB(), 3), intB(), intB(), intB(17));
    assertCall(() -> varReducerB().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(3, 3));
  }

  @Test
  public void reference_with_negative_index_causes_exception() throws Exception {
    var job = job(referenceB(stringTB(), -1), intB(), intB(), intB(17));
    assertCall(() -> varReducerB().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(-1, 3));
  }

  private void assertReferenceInliningReplacesReference(
      Function1<ExprB, ExprB, BytecodeException> factory) throws BytecodeException {
    assertReferenceInliningReplacesReference(2, factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, Function1<ExprB, ExprB, BytecodeException> factory)
      throws BytecodeException {
    assertReferenceInliningReplacesReference(referencedIndex, factory, intB(3));
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, Function1<ExprB, ExprB, BytecodeException> factory, IntB replacement)
      throws BytecodeException {
    assertReferenceInlining(
        factory.apply(referenceB(intTB(), referencedIndex)), factory.apply(replacement));
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      Function1<ExprB, ExprB, BytecodeException> factory) throws Exception {
    assertReferenceInliningDoesNotChangeExpression(1, factory);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      int referencedIndex, Function1<ExprB, ExprB, BytecodeException> factory)
      throws BytecodeException {
    var exprB = factory.apply(referenceB(intTB(), referencedIndex));
    var job = job(exprB, intB(1), intB(2), intB(3));
    assertThat(varReducerB().inline(job)).isSameInstanceAs(exprB);
  }

  private void assertReferenceInlining(ExprB exprB, ExprB expected) throws BytecodeException {
    var job = job(exprB, intB(1), intB(2), intB(3));
    assertThat(varReducerB().inline(job)).isEqualTo(expected);
  }
}
