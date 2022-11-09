package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class CallBTest extends TestContext {
  @Test
  public void creating_call_with_func_type_not_being_func_causes_exception() {
    assertCall(() -> callB(intB()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to FuncB."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> callB(defFuncB(list(stringTB()), intB())))
        .throwsException(argsNotMatchingParamsException("", "String"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> callB(defFuncB(list(stringTB()), intB()), intB(), intB()))
        .throwsException(argsNotMatchingParamsException("Int,Int", "String"));
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> callB(defFuncB(list(stringTB()), intB()), intB(3)))
        .throwsException(argsNotMatchingParamsException("Int", "String"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Argument evaluation types (" + args + ") should be"
        + " equal to function parameter types (" + params + ").");
  }

  @Test
  public void dataSeq_returns_data() {
    var func = defFuncB(list(stringTB()), intB());
    var args = stringB();
    assertThat(callB(func, args).dataSeq())
        .isEqualTo(list(func, combineB(args)));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<CallB> {
    @Override
    protected List<CallB> equalExprs() {
      return list(
          callB(defFuncB(list(blobTB()), intB()), blobB()),
          callB(defFuncB(list(blobTB()), intB()), blobB())
      );
    }

    @Override
    protected List<CallB> nonEqualExprs() {
      return list(
          callB(defFuncB(list(blobTB()), intB()), blobB()),
          callB(defFuncB(list(stringTB()), intB()), stringB()),
          callB(defFuncB(list(blobTB()), stringB()), blobB())
      );
    }
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callB(defFuncB(list(stringTB()), intB()), stringB());
    assertThat(bytecodeDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = defFuncB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(((CallB) bytecodeDbOther().get(call.hash())).dataSeq())
        .isEqualTo(list(func, combineB(stringB())));
  }

  @Test
  public void to_string() {
    var func = defFuncB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(call.toString())
        .isEqualTo("CALL:Int(???)@" + call.hash());
  }
}
