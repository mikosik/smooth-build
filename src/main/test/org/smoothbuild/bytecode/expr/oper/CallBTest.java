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
    assertCall(() -> callB(blobTB(), intB()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to FuncB."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB())))
        .throwsException(argsNotMatchingParamsException("", "String"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), intB(), intB()))
        .throwsException(argsNotMatchingParamsException("Int,Int", "String"));
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), intB(3)))
        .throwsException(argsNotMatchingParamsException("Int", "String"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Argument evaluation types (" + args + ") should be"
        + " equal to callable parameter types (" + params + ").");
  }

  @Test
  public void creating_call_with_resT_not_assignable_to_evalT_causes_exc() {
    var func = funcB(list(), intB(7));
    assertCall(() -> callB(stringTB(), func))
        .throwsException(new IllegalArgumentException(
            "Call's result type `Int` cannot be assigned to evalT `String`."));
  }

  @Test
  public void func_returns_func_expr() {
    var func = funcB(list(stringTB()), intB());
    assertThat(callB(func, stringB()).data().callable())
        .isEqualTo(func);
  }

  @Test
  public void args_returns_arg_exprs() {
    var func = funcB(list(stringTB()), intB());
    assertThat(callB(func, stringB()).data().args())
        .isEqualTo(combineB(stringB()));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<CallB> {
    @Override
    protected List<CallB> equalExprs() {
      return list(
          callB(funcB(list(blobTB()), intB()), blobB()),
          callB(funcB(list(blobTB()), intB()), blobB())
      );
    }

    @Override
    protected List<CallB> nonEqualExprs() {
      return list(
          callB(funcB(list(blobTB()), intB()), blobB()),
          callB(funcB(list(stringTB()), intB()), stringB()),
          callB(funcB(list(blobTB()), stringB()), blobB())
      );
    }
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callB(funcB(list(stringTB()), intB()), stringB());
    assertThat(bytecodeDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = funcB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(((CallB) bytecodeDbOther().get(call.hash())).data())
        .isEqualTo(new CallB.Data(func, combineB(stringB())));
  }

  @Test
  public void to_string() {
    var func = funcB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(call.toString())
        .isEqualTo("Call:Int(???)@" + call.hash());
  }
}