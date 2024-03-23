package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall.SubExprsB;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BCallTest extends TestingVirtualMachine {
  @Test
  public void creating_call_with_func_type_not_being_func_causes_exception() {
    assertCall(() -> callB(intB()))
        .throwsException(
            new IllegalArgumentException("`func` component doesn't evaluate to FuncB."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> callB(lambdaB(list(stringTB()), intB())))
        .throwsException(argsNotMatchingParamsException("", "String"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> callB(lambdaB(list(stringTB()), intB()), intB(), intB()))
        .throwsException(argsNotMatchingParamsException("Int,Int", "String"));
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> callB(lambdaB(list(stringTB()), intB()), intB(3)))
        .throwsException(argsNotMatchingParamsException("Int", "String"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Argument evaluation types (" + args + ") should be"
        + " equal to function parameter types (" + params + ").");
  }

  @Test
  public void sub_exprs_returns_sub_exprs() throws Exception {
    var func = lambdaB(list(stringTB()), intB());
    var args = combineB(stringB());
    assertThat(callB(func, args).subExprs()).isEqualTo(new SubExprsB(func, args));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCall> {
    @Override
    protected java.util.List<BCall> equalExprs() throws BytecodeException {
      return list(
          callB(lambdaB(list(blobTB()), intB()), blobB()),
          callB(lambdaB(list(blobTB()), intB()), blobB()));
    }

    @Override
    protected java.util.List<BCall> nonEqualExprs() throws BytecodeException {
      return list(
          callB(lambdaB(list(blobTB()), intB()), blobB()),
          callB(lambdaB(list(stringTB()), intB()), stringB()),
          callB(lambdaB(list(blobTB()), stringB()), blobB()));
    }
  }

  @Test
  public void call_can_be_read_back_by_hash() throws Exception {
    var call = callB(lambdaB(list(stringTB()), intB()), stringB());
    assertThat(exprDbOther().get(call.hash())).isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() throws Exception {
    var func = lambdaB(list(stringTB()), intB());
    var args = combineB(stringB());
    var call = callB(func, args);
    assertThat(((BCall) exprDbOther().get(call.hash())).subExprs())
        .isEqualTo(new BCall.SubExprsB(func, args));
  }

  @Test
  public void to_string() throws Exception {
    var func = lambdaB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(call.toString()).isEqualTo("CALL:Int(???)@" + call.hash());
  }
}
