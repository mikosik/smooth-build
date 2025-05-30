package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BReferenceInlinerTest extends VmTestContext {
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
    void choice() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bChoice());
    }

    @Test
    void choose() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bChoose());
    }

    @Test
    void fold() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bFold(bArray(bInt()), bInt(), bFolderLambda()));
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

    @Test
    void switch_() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bSwitch(bChoice(), bCombine(bs2iLambda(), bi2iLambda())));
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
    void choose() throws Exception {
      assertReferenceInliningReplacesReference(r -> {
        var choiceType = bChoiceType(bStringType(), bIntType());
        return bChoose(choiceType, 1, r);
      });
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
    void fold_array() throws Exception {
      assertReferenceInliningReplacesReference(r -> bFold(bOrder(r), bInt(), bFolderLambda()));
    }

    @Test
    void fold_initial() throws Exception {
      assertReferenceInliningReplacesReference(r -> bFold(bArray(bInt()), r, bFolderLambda()));
    }

    @Test
    void fold_folder() throws Exception {
      assertReferenceInliningReplacesReference(2, bInt(1), r -> provide()
          .bytecodeFactory()
          .fold(bArray(bInt()), bInt(), bLambda(list(bIntType(), bIntType()), r)));
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

    @Test
    void switch_choice() throws Exception {
      assertReferenceInliningReplacesReference(r -> {
        var type = bChoiceType(bStringType(), bIntType());
        var choice = bChoose(type, 1, r);
        var handlers = bCombine(bs2iLambda(), bi2iLambda());
        return bSwitch(choice, handlers);
      });
    }

    @Test
    void switch_handlers() throws Exception {
      assertReferenceInliningReplacesReference(1, bInt(1), r -> {
        var choice = bChoice();
        var handlers = bCombine(bLambda(list(bStringType()), r), bLambda(list(bIntType()), r));
        return bSwitch(choice, handlers);
      });
    }
  }

  @Test
  void reference_with_index_equal_to_environment_size_causes_exception() throws Exception {
    var job = job(bReference(bStringType(), 3), bInt(), bInt(), bInt(17));
    assertCall(() -> provide().bReferenceInliner().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(3, 3));
  }

  @Test
  void reference_with_negative_index_causes_exception() throws Exception {
    var job = job(bReference(bStringType(), -1), bInt(), bInt(), bInt(17));
    assertCall(() -> provide().bReferenceInliner().inline(job))
        .throwsException(new ReferenceIndexOutOfBoundsException(-1, 3));
  }

  private BLambda bFolderLambda() throws BytecodeException {
    return bii2iLambda();
  }

  private void assertReferenceInliningReplacesReference(
      Function1<BExpr, BExpr, IOException> factory) throws IOException {
    assertReferenceInliningReplacesReference(2, bInt(3), factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, BInt expectedReplacement, Function1<BExpr, BExpr, IOException> factory)
      throws IOException {
    List<BExpr> environment = list(bInt(1), bInt(2), bInt(3));
    assertReferenceInliningReplacesReference(
        referencedIndex, expectedReplacement, environment, factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex,
      BExpr expectedReplacement,
      List<BExpr> environment,
      Function1<BExpr, BExpr, IOException> factory)
      throws IOException {
    var referenceEvaluationType = environment.get(referencedIndex).evaluationType();
    BExpr expr = factory.apply(bReference(referenceEvaluationType, referencedIndex));
    BExpr expected = factory.apply(expectedReplacement);
    var job = job(expr, environment);
    var inlined = provide().bReferenceInliner().inline(job);
    assertThat(inlined).isEqualTo(expected);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      Function1<BExpr, BExpr, IOException> factory) throws Exception {
    assertReferenceInliningDoesNotChangeExpression(1, factory);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      int referencedIndex, Function1<BExpr, BExpr, IOException> factory) throws IOException {
    var expr = factory.apply(bReference(bIntType(), referencedIndex));
    var job = job(expr, bInt(1), bInt(2), bInt(3));
    assertThat(provide().bReferenceInliner().inline(job)).isSameInstanceAs(expr);
  }
}
