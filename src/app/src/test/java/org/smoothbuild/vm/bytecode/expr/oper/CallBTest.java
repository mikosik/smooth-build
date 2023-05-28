package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class CallBTest extends TestContext {
  @Test
  public void creating_call_with_func_type_not_being_func_causes_exception() {
    assertCall(() -> callB(intB()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to FuncB."));
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
  public void sub_exprs_returns_sub_exprs() {
    var func = lambdaB(list(stringTB()), intB());
    var args = combineB(stringB());
    assertThat(callB(func, args).subExprs())
        .isEqualTo(new CallSubExprsB(func, args));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<CallB> {
    @Override
    protected List<CallB> equalExprs() {
      return list(
          callB(lambdaB(list(blobTB()), intB()), blobB()),
          callB(lambdaB(list(blobTB()), intB()), blobB())
      );
    }

    @Override
    protected List<CallB> nonEqualExprs() {
      return list(
          callB(lambdaB(list(blobTB()), intB()), blobB()),
          callB(lambdaB(list(stringTB()), intB()), stringB()),
          callB(lambdaB(list(blobTB()), stringB()), blobB())
      );
    }
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callB(lambdaB(list(stringTB()), intB()), stringB());
    assertThat(bytecodeDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = lambdaB(list(stringTB()), intB());
    var args = combineB(stringB());
    var call = callB(func, args);
    assertThat(((CallB) bytecodeDbOther().get(call.hash())).subExprs())
        .isEqualTo(new CallSubExprsB(func, args));
  }

  @Test
  public void to_string() {
    var func = lambdaB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(call.toString())
        .isEqualTo("CALL:Int(???)@" + call.hash());
  }
}
