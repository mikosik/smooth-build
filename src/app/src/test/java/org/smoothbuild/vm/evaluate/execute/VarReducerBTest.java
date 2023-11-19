package org.smoothbuild.vm.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;

import io.vavr.collection.Array;

public class VarReducerBTest extends TestContext {
  @Nested
  class _without_references {

    // operations

    @Test
    public void call() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> callB(lambdaB(Array.of(intTB()), intB()), intB()));
    }

    @Test
    public void combine() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> combineB(intB()));
    }

    @Test
    public void order() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> orderB(intB()));
    }

    @Test
    public void pick() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> pickB(orderB(intB(1), intB(2)), intB(0)));
    }

    @Test
    public void select() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> selectB(combineB(intB()), intB(0)));
    }

    // values

    @Test
    public void array() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> arrayB(intB()));
    }

    @Test
    public void blob() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> blobB());
    }

    @Test
    public void bool() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> boolB());
    }

    @Test
    public void int_() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> intB());
    }

    @Test
    public void string() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> stringB());
    }

    @Test
    public void tuple() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> tupleB(intB()));
    }

    // callables

     @Test
    public void lambda_without_references() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> lambdaB(intB()));
    }

    @Test
    public void if_func() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> ifFuncB(intTB()));
    }

    @Test
    public void map_func() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> mapFuncB(intTB(), blobTB()));
    }

    @Test
    public void native_func() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> nativeFuncB(funcTB(intTB(), blobTB())));
    }
  }

  @Nested
  class _with_references_inside {
    @Test
    public void inlining_pure_reference() {
      assertReferenceInliningReplacesReference(r -> r);
    }

    @Test
    public void lambda_body_with_var_referencing_param_of_this_lambda() {
      assertReferenceInliningDoesNotChangeExpression(0, r -> myLambda(r));
    }

    @Test
    public void lambda_body_with_var_referencing_param_of_enclosing_lambda() {
      assertReferenceInliningDoesNotChangeExpression(1, r -> myLambda(r));
    }

    @Test
    public void lambda_body_with_var_referencing_unbound_param() {
      assertReferenceInliningReplacesReference(2, r -> myLambda(r), intB(1));
    }

    private LambdaB myLambda(ExprB exprB) {
      var inner = lambdaB(funcTB(blobTB(), intTB()), exprB);
      return lambdaB(Array.of(intTB()), inner);
    }

    @Test
    public void call_argument() {
      assertReferenceInliningReplacesReference(r -> callB(idFuncB(), r));
    }

    @Test
    public void call_func() {
      assertReferenceInliningReplacesReference(r -> callB(lambdaB(r)));
    }

    @Test
    public void combine() {
      assertReferenceInliningReplacesReference(VarReducerBTest.this::combineB);
    }

    @Test
    public void order() {
      assertReferenceInliningReplacesReference(VarReducerBTest.this::orderB);
    }

    @Test
    public void pick_pickable() {
      assertReferenceInliningReplacesReference(r -> pickB(orderB(r), intB()));
    }

    @Test
    public void pick_index() {
      assertReferenceInliningReplacesReference(r -> pickB(orderB(), r));
    }

    @Test
    public void select_selectable() {
      assertReferenceInliningReplacesReference(r -> selectB(combineB(r), intB(0)));
    }
  }

  @Test
  public void reference_with_index_equal_to_environment_size_causes_exception() {
    var job = job(varB(stringTB(), 3), intB(), intB(), intB(17));
    assertCall(() -> varReducerB().inline(job))
        .throwsException(new VarOutOfBoundsException(3, 3));
  }

  @Test
  public void reference_with_negative_index_causes_exception() {
    var job = job(varB(stringTB(), -1), intB(), intB(), intB(17));
    assertCall(() -> varReducerB().inline(job))
        .throwsException(new VarOutOfBoundsException(-1, 3));
  }

  private void assertReferenceInliningReplacesReference(Function<ExprB, ExprB> factory) {
    assertReferenceInliningReplacesReference(2, factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, Function<ExprB, ExprB> factory) {
    assertReferenceInliningReplacesReference(referencedIndex, factory, intB(3));
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, Function<ExprB, ExprB> factory, IntB replacement) {
    assertReferenceInlining(
        factory.apply(varB(intTB(), referencedIndex)),
        factory.apply(replacement));
  }

  private void assertReferenceInliningDoesNotChangeExpression(Function<ExprB, ExprB> factory) {
    assertReferenceInliningDoesNotChangeExpression(1, factory);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      int referencedIndex, Function<ExprB, ExprB> factory) {
    var exprB = factory.apply(varB(intTB(), referencedIndex));
    var job = job(exprB, intB(1), intB(2), intB(3));
    assertThat(varReducerB().inline(job))
        .isSameInstanceAs(exprB);
  }

  private void assertReferenceInlining(ExprB exprB, ExprB expected) {
    var job = job(exprB, intB(1), intB(2), intB(3));
    assertThat(varReducerB().inline(job))
        .isEqualTo(expected);
  }
}
