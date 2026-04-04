package org.smoothbuild.virtualmachine.evaluate.base;

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
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.evaluate.job.RefIndexOutOfBoundsException;

public class BRefInlinerTest extends VmTestContext {
  @Nested
  class _without_references {

    // operations

    @Test
    void call() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bCall(bLambda(list(bIntType()), bInt()), bInt()));
    }

    @Test
    void constructTuple() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bConstructTuple(bInt()));
    }

    @Test
    void variant() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bVariant());
    }

    @Test
    void constructVariant() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bConstructVariant());
    }

    @Test
    void fold() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bFold(bArray(bInt()), bInt(), bFolderLambda()));
    }

    @Test
    void constructArray() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(r -> bConstructArray(bInt()));
    }

    @Test
    void arrayGet() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bArrayGet(bConstructArray(bInt(1), bInt(2)), bInt(0)));
    }

    @Test
    void tupleGet() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bTupleGet(bConstructTuple(bInt()), bInt(0)));
    }

    @Test
    void switch_() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(
          r -> bSwitch(bVariant(), bConstructTuple(bs2iLambda(), bi2iLambda())));
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
    void lambda_body_with_ref_to_param_of_this_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(1, r -> bLambda(list(bIntType()), r));
    }

    @Test
    void lambda_body_with_recursive_ref_to_this_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(0, r -> bLambda(list(bIntType()), r));
    }

    @Test
    void lambda_body_with_ref_to_param_of_enclosing_lambda() throws Exception {
      assertReferenceInliningDoesNotChangeExpression(3, r -> lambdaInsideLambda(r));
    }

    @Test
    void lambda_body_with_ref_to_unbound_param() throws Exception {
      assertReferenceInliningReplacesReference(5, bInt(1), r -> lambdaInsideLambda(r));
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
      assertReferenceInliningReplacesReference(3, bInt(2), r -> bCall(bLambda(r)));
    }

    @Test
    void constructVariant() throws Exception {
      assertReferenceInliningReplacesReference(r -> {
        var variantType = bVariantType(bStringType(), bIntType());
        return bConstructVariant(variantType, 1, r);
      });
    }

    @Test
    void constructTuple() throws Exception {
      assertReferenceInliningReplacesReference(BRefInlinerTest.this::bConstructTuple);
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
      assertReferenceInliningReplacesReference(r -> bMap(bConstructArray(r), bIntIdLambda()));
    }

    @Test
    void map_mapper() throws Exception {
      assertReferenceInliningReplacesReference(
          2, bInt(0), r -> bMap(bArray(bInt()), bLambda(list(bIntType()), r)));
    }

    @Test
    void fold_array() throws Exception {
      assertReferenceInliningReplacesReference(
          r -> bFold(bConstructArray(r), bInt(), bFolderLambda()));
    }

    @Test
    void fold_initial() throws Exception {
      assertReferenceInliningReplacesReference(r -> bFold(bArray(bInt()), r, bFolderLambda()));
    }

    @Test
    void fold_folder() throws Exception {
      assertReferenceInliningReplacesReference(3, bInt(0), r -> provide()
          .bytecodeFactory()
          .fold(bArray(bInt()), bInt(), bLambda(list(bIntType(), bIntType()), r)));
    }

    @Test
    void constructArray() throws Exception {
      assertReferenceInliningReplacesReference(BRefInlinerTest.this::bConstructArray);
    }

    @Test
    void arrayGet_array() throws Exception {
      assertReferenceInliningReplacesReference(r -> bArrayGet(bConstructArray(r), bInt()));
    }

    @Test
    void arrayGet_index() throws Exception {
      assertReferenceInliningReplacesReference(r -> bArrayGet(bConstructArray(), r));
    }

    @Test
    void tupleGet_tuple() throws Exception {
      assertReferenceInliningReplacesReference(r -> bTupleGet(bConstructTuple(r), bInt(0)));
    }

    @Test
    void tuple_element() throws Exception {
      assertReferenceInliningReplacesReference(2, bInt(1), r -> bTuple(bLambda(list(), r)));
    }

    @Test
    void array_element() throws Exception {
      assertReferenceInliningReplacesReference(2, bInt(1), r -> bArray(bLambda(list(), r)));
    }

    @Test
    void variant_choice() throws Exception {
      BVariantType variantType = bVariantType(bLambdaType(bIntType()));
      assertReferenceInliningReplacesReference(
          2, bInt(1), r -> bVariant(variantType, 0, bLambda(list(), r)));
    }

    @Test
    void switch_variant() throws Exception {
      assertReferenceInliningReplacesReference(r -> {
        var type = bVariantType(bStringType(), bIntType());
        var choice = bConstructVariant(type, 1, r);
        var handlers = bConstructTuple(bs2iLambda(), bi2iLambda());
        return bSwitch(choice, handlers);
      });
    }

    @Test
    void switch_handlers() throws Exception {
      assertReferenceInliningReplacesReference(2, bInt(0), r -> {
        var choice = bVariant();
        var handlers =
            bConstructTuple(bLambda(list(bStringType()), r), bLambda(list(bIntType()), r));
        return bSwitch(choice, handlers);
      });
    }
  }

  @Test
  void reference_with_index_equal_to_environment_size_causes_exception() throws Exception {
    var job = job(bRef(bStringType(), 3), bInt(), bInt(), bInt(17));
    assertCall(() -> provide().bRefInliner().inline(job))
        .throwsException(new RefIndexOutOfBoundsException(3, 3));
  }

  @Test
  void reference_with_negative_index_causes_exception() throws Exception {
    var job = job(bRef(bStringType(), -1), bInt(), bInt(), bInt(17));
    assertCall(() -> provide().bRefInliner().inline(job))
        .throwsException(new RefIndexOutOfBoundsException(-1, 3));
  }

  private BLambda bFolderLambda() throws BytecodeException {
    return bii2iLambda();
  }

  private void assertReferenceInliningReplacesReference(
      Function1<BExpr, BExpr, IOException> factory) throws Exception {
    assertReferenceInliningReplacesReference(3, bInt(3), factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex, BInt expectedReplacement, Function1<BExpr, BExpr, IOException> factory)
      throws Exception {
    List<BExpr> environment = list(bInt(0), bInt(1), bInt(2), bInt(3), bInt(4), bInt(5));
    assertReferenceInliningReplacesReference(
        referencedIndex, expectedReplacement, environment, factory);
  }

  private void assertReferenceInliningReplacesReference(
      int referencedIndex,
      BExpr expectedReplacement,
      List<BExpr> environment,
      Function1<BExpr, BExpr, IOException> factory)
      throws Exception {
    var referenceEvaluationType = environment.get(referencedIndex).evaluationType();
    BExpr expr = factory.apply(bRef(referenceEvaluationType, referencedIndex));
    BExpr expected = factory.apply(expectedReplacement);
    var job = job(expr, environment);
    var inlined = provide().bRefInliner().inline(job);
    assertThat(inlined).isEqualTo(expected);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      Function1<BExpr, BExpr, IOException> factory) throws Exception {
    assertReferenceInliningDoesNotChangeExpression(1, factory);
  }

  private void assertReferenceInliningDoesNotChangeExpression(
      int referencedIndex, Function1<BExpr, BExpr, IOException> factory) throws Exception {
    var expr = factory.apply(bRef(bIntType(), referencedIndex));
    var job = job(expr, bInt(1), bInt(2), bInt(3));
    assertThat(provide().bRefInliner().inline(job)).isSameInstanceAs(expr);
  }
}
