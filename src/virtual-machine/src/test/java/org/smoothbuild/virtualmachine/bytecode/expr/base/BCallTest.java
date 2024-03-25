package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall.SubExprsB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BCallTest extends TestingVirtualMachine {
  @Test
  public void creating_call_with_func_type_not_being_func_causes_exception() {
    assertCall(() -> bCall(bInt()))
        .throwsException(
            new IllegalArgumentException("`func` component doesn't evaluate to FuncB."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> bCall(bLambda(list(bStringType()), bInt())))
        .throwsException(argsNotMatchingParamsException("", "String"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> bCall(bLambda(list(bStringType()), bInt()), bInt(), bInt()))
        .throwsException(argsNotMatchingParamsException("Int,Int", "String"));
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> bCall(bLambda(list(bStringType()), bInt()), bInt(3)))
        .throwsException(argsNotMatchingParamsException("Int", "String"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Argument evaluation types (" + args + ") should be"
        + " equal to function parameter types (" + params + ").");
  }

  @Test
  public void sub_exprs_returns_sub_exprs() throws Exception {
    var func = bLambda(list(bStringType()), bInt());
    var args = bCombine(bString());
    assertThat(bCall(func, args).subExprs()).isEqualTo(new SubExprsB(func, args));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCall> {
    @Override
    protected java.util.List<BCall> equalExprs() throws BytecodeException {
      return list(
          bCall(bLambda(list(bBlobType()), bInt()), bBlob()),
          bCall(bLambda(list(bBlobType()), bInt()), bBlob()));
    }

    @Override
    protected java.util.List<BCall> nonEqualExprs() throws BytecodeException {
      return list(
          bCall(bLambda(list(bBlobType()), bInt()), bBlob()),
          bCall(bLambda(list(bStringType()), bInt()), bString()),
          bCall(bLambda(list(bBlobType()), bString()), bBlob()));
    }
  }

  @Test
  public void call_can_be_read_back_by_hash() throws Exception {
    var call = bCall(bLambda(list(bStringType()), bInt()), bString());
    assertThat(exprDbOther().get(call.hash())).isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() throws Exception {
    var func = bLambda(list(bStringType()), bInt());
    var args = bCombine(bString());
    var call = bCall(func, args);
    assertThat(((BCall) exprDbOther().get(call.hash())).subExprs())
        .isEqualTo(new BCall.SubExprsB(func, args));
  }

  @Test
  public void to_string() throws Exception {
    var func = bLambda(list(bStringType()), bInt());
    var call = bCall(func, bString());
    assertThat(call.toString()).isEqualTo("CALL:Int(???)@" + call.hash());
  }
}
