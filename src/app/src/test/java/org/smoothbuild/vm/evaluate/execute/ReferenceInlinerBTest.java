package org.smoothbuild.vm.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ClosureB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;

public class ReferenceInlinerBTest extends TestContext {
  @Nested
  class _without_references {

    // operations

    @Test
    public void call() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> callB(exprFuncB(list(intTB()), intB()), intB()));
    }

    @Test
    public void closurize() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> closurizeB(intB()));
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
    public void closure_without_references() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> closureB(intB()));
    }

    @Test
    public void expr_func_without_references() {
      assertReferenceInliningDoesNotChangeExpression(
          r -> exprFuncB(intB()));
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
    public void expr_func_body_when_referencing_index_is_outside_arguments_size() {
      var twoParamFuncTB = funcTB(blobTB(), blobTB(), intTB());
      assertReferenceInliningReplacesReference(2, r -> exprFuncB(twoParamFuncTB, r), intB(1));
    }

    @Test
    public void expr_func_body_when_referencing_index_is_inside_arguments_size() {
      var twoParamFuncTB = funcTB(blobTB(), blobTB(), intTB());
      assertReferenceInliningDoesNotChangeExpression(1, r -> exprFuncB(twoParamFuncTB, r));
    }

    @Test
    public void closure_body_when_referencing_index_is_outside_arguments_size_plus_closure_env_size() {
      assertReferenceInliningReplacesReference(2, r -> myClosure(r), intB(1));
    }

    @Test
    public void closure_body_when_referencing_index_references_closure_environment() {
      assertReferenceInliningDoesNotChangeExpression(1, r -> myClosure(r));
    }

    @Test
    public void closure_body_when_referencing_index_references_closure_func_argument() {
      assertReferenceInliningDoesNotChangeExpression(0, r -> myClosure(r));
    }

    private ClosureB myClosure(ExprB exprB) {
      var closureEnvironment = intB(33);
      var exprFuncB = exprFuncB(funcTB(blobTB(), intTB()), exprB);
      return closureB(combineB(closureEnvironment), exprFuncB);
    }

    @Test
    public void call_argument() {
      assertReferenceInliningReplacesReference(r -> callB(idFuncB(), r));
    }

    @Test
    public void call_func() {
      assertReferenceInliningReplacesReference(r -> callB(exprFuncB(r)));
    }

    @Test
    public void combine() {
      assertReferenceInliningReplacesReference(ReferenceInlinerBTest.this::combineB);
    }

    @Test
    public void order() {
      assertReferenceInliningReplacesReference(ReferenceInlinerBTest.this::orderB);
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
    assertCall(() -> referenceInlinerB().inline(job))
        .throwsException(new VarOutOfBoundsExc(3, 3));
  }

  @Test
  public void reference_with_negative_index_causes_exception() {
    var job = job(varB(stringTB(), -1), intB(), intB(), intB(17));
    assertCall(() -> referenceInlinerB().inline(job))
        .throwsException(new VarOutOfBoundsExc(-1, 3));
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
    assertThat(referenceInlinerB().inline(job))
        .isSameInstanceAs(exprB);
  }

  private void assertReferenceInlining(ExprB exprB, ExprB expected) {
    var job = job(exprB, intB(1), intB(2), intB(3));
    assertThat(referenceInlinerB().inline(job))
        .isEqualTo(expected);
  }
}
