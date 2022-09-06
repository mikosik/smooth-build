package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class InvokeBTest extends TestContext {
  @Test
  public void creating_invoke_with_expr_not_being_method_causes_exception() {
    assertCall(() -> invokeB(blobTB(), intB()))
        .throwsException(new IllegalArgumentException(
            "`method` component doesn't evaluate to MethodB."));
  }

  @Test
  public void creating_invoke_with_too_few_args_causes_exception() {
    var methodT = methodTB(intTB(), stringTB());
    assertCall(() -> invokeB(methodB(methodT)))
        .throwsException(argsNotMatchingParamsException("", "String"));
  }

  @Test
  public void creating_invoke_with_too_many_args_causes_exception() {
    var methodT = methodTB(intTB(), stringTB());
    assertCall(() -> invokeB(methodB(methodT), intB(), intB()))
        .throwsException(argsNotMatchingParamsException("Int,Int", "String"));
  }

  @Test
  public void creating_invoke_with_arg_not_matching_param_type_causes_exception() {
    var method = methodB(methodTB(intTB(), stringTB()));
    assertCall(() -> invokeB(method, intB(3)))
        .throwsException(argsNotMatchingParamsException("Int", "String"));
  }

  @Test
  public void creating_invoke_with_resT_not_assignable_to_evalT_causes_exc() {
    var method = methodB(methodTB(intTB()));
    assertCall(() -> invokeB(stringTB(), method))
        .throwsException(new IllegalArgumentException(
            "Method's result type `Int` cannot be assigned to evalT `String`."));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Argument evaluation types (" + args + ") should be"
        + " equal to callable parameter types (" + params + ").");
  }

  @Test
  public void method_returns_method_expr() {
    var method = methodB(methodTB(intTB(), stringTB()));
    assertThat(invokeB(method, stringB()).data().method())
        .isEqualTo(method);
  }

  @Test
  public void args_returns_arg_exprs() {
    var method = methodB(methodTB(intTB(), stringTB()));
    assertThat(invokeB(method, stringB()).data().args())
        .isEqualTo(combineB(stringB()));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<InvokeB> {
    @Override
    protected List<InvokeB> equalExprs() {
      return list(
          invokeB(methodB(methodTB(intTB(), stringTB())), stringB()),
          invokeB(methodB(methodTB(intTB(), stringTB())), stringB())
      );
    }

    @Override
    protected List<InvokeB> nonEqualExprs() {
      var m1 = methodB(methodTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true));
      var m2 = methodB(methodTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(false));
      var m3 = methodB(methodTB(intTB(), stringTB()), blobB(7), stringB("b"), boolB(true));
      var m4 = methodB(methodTB(intTB(), stringTB()), blobB(0), stringB("a"), boolB(true));
      var m5 = methodB(methodTB(intTB(), blobTB()), blobB(7), stringB("a"), boolB(true));
      var m6 = methodB(methodTB(blobTB(), stringTB()), blobB(7), stringB("a"), boolB(true));

      var stringArg = stringB();
      var blobArg = blobB();

      return list(
          invokeB(m1, stringArg),
          invokeB(m2, stringArg),
          invokeB(m3, stringArg),
          invokeB(m4, stringArg),
          invokeB(m5, blobArg),
          invokeB(m6, stringArg)
      );
    }
  }

  @Test
  public void invoke_can_be_read_back_by_hash() {
    var method = methodB(methodTB(intTB(), stringTB()));
    var invoke = invokeB(method, stringB());
    assertThat(bytecodeDbOther().get(invoke.hash()))
        .isEqualTo(invoke);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var method = methodB(methodTB(intTB(), stringTB()));
    var invoke = invokeB(method, stringB());
    var readInvoke = (InvokeB) bytecodeDbOther().get(invoke.hash());
    var readInvokeData = readInvoke.data();
    var invokeData = invoke.data();
    assertThat(readInvokeData.method())
        .isEqualTo(invokeData.method());
    assertThat(readInvokeData.args())
        .isEqualTo(invokeData.args());
  }

  @Test
  public void to_string() {
    var method = methodB(methodTB(intTB(), stringTB()));
    var invoke = invokeB(method, stringB());
    assertThat(invoke.toString())
        .isEqualTo("Invoke:Int(???)@" + invoke.hash());
  }
}
