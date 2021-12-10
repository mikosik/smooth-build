package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class InvokeHTest extends TestingContext {
  @Nested
  class _infer_type_of_invoke {
    @Test
    public void without_generic_params() {
      assertThat(invokeH(methodH(methodTH(intTH(), list(boolTH()))), list(boolH())).cat())
          .isEqualTo(invokeCH(intTH()));
    }

    @Test
    public void with_generic_params() {
      assertThat(invokeH(methodH(methodTH(varTH("A"), list(varTH("A")))), list(boolH())).cat())
          .isEqualTo(invokeCH(boolTH()));
    }
  }

  @Test
  public void creating_invoke_with_expr_not_being_method_causes_exception() {
    assertCall(() -> invokeH(intH(), combineH(list())))
        .throwsException(new IllegalArgumentException(
            "`method` component doesn't evaluate to MethodH."));
  }

  @Test
  public void creating_invoke_with_too_few_args_causes_exception() {
    var methodT = methodTH(intTH(), list(stringTH()));
    assertCall(() -> invokeH(methodH(methodT), list()))
        .throwsException(argsNotMatchingParamsException("{}", "{String}"));
  }

  @Test
  public void creating_invoke_with_too_many_args_causes_exception() {
    var methodT = methodTH(intTH(), list(stringTH()));
    assertCall(() -> invokeH(methodH(methodT), list(intH(), intH())))
        .throwsException(argsNotMatchingParamsException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_invoke_with_arg_not_matching_param_type_causes_exception() {
    var methodT = methodTH(intTH(), list(stringTH()));
    assertCall(() -> invokeH(methodH(methodT), list(intH(3))))
        .throwsException(argsNotMatchingParamsException("{Int}", "{String}"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Arguments evaluation type " + args + " should be"
        + " equal to callable type parameters " + params + ".");
  }

  @Test
  public void method_returns_method_expr() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    assertThat(invokeH(method, list(stringH())).data().method())
        .isEqualTo(method);
  }

  @Test
  public void args_returns_arg_exprs() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method, args).data().args())
        .isEqualTo(args);
  }

  @Test
  public void invoke_with_equal_values_are_equal() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method, args))
        .isEqualTo(invokeH(method, args));
  }

  @Test
  public void invoke_with_different_method_are_not_equal() {
    var method1 = methodH(methodTH(intTH(), list(stringTH())));
    var method2 = methodH(methodTH(boolTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method1, args))
        .isNotEqualTo(invokeH(method2, args));
  }

  @Test
  public void invoke_with_different_args_are_not_equal() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args1 = combineH(list(stringH("a")));
    var args2 = combineH(list(stringH("b")));
    assertThat(invokeH(method, args1))
        .isNotEqualTo(invokeH(method, args2));
  }

  @Test
  public void hash_of_invoke_with_equal_values_are_equal() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method, args).hash())
        .isEqualTo(invokeH(method, args).hash());
  }

  @Test
  public void hash_of_invoke_with_different_method_are_not_equal() {
    var method1 = methodH(methodTH(intTH(), list(stringTH())));
    var method2 = methodH(methodTH(boolTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method1, args).hash())
        .isNotEqualTo(invokeH(method2, args).hash());
  }

  @Test
  public void hash_of_invoke_with_different_args_are_not_equal() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args1 = combineH(list(stringH("a")));
    var args2 = combineH(list(stringH("b")));
    assertThat(invokeH(method, args1).hash())
        .isNotEqualTo(invokeH(method, args2).hash());
  }

  @Test
  public void hashCode_of_invoke_with_equal_values_are_equal() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method, args).hashCode())
        .isEqualTo(invokeH(method, args).hashCode());
  }

  @Test
  public void hashCode_of_invoke_with_different_method_are_not_equal() {
    var method1 = methodH(methodTH(intTH(), list(stringTH())));
    var method2 = methodH(methodTH(boolTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    assertThat(invokeH(method1, args).hashCode())
        .isNotEqualTo(invokeH(method2, args).hashCode());
  }

  @Test
  public void hashCode_of_invoke_with_different_args_are_not_equal() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args1 = combineH(list(stringH("a")));
    var args2 = combineH(list(stringH("b")));
    assertThat(invokeH(method, args1).hashCode())
        .isNotEqualTo(invokeH(method, args2).hashCode());
  }

  @Test
  public void invoke_can_be_read_back_by_hash() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    var invoke = invokeH(method, args);
    assertThat(objDbOther().get(invoke.hash()))
        .isEqualTo(invoke);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    var invoke = invokeH(method, args);
    var readInvoke = (InvokeH) objDbOther().get(invoke.hash());
    var readInvokeData = readInvoke.data();
    var invokeData = invoke.data();
    assertThat(readInvokeData.method())
        .isEqualTo(invokeData.method());
    assertThat(readInvokeData.args())
        .isEqualTo(invokeData.args());
  }

  @Test
  public void to_string() {
    var method = methodH(methodTH(intTH(), list(stringTH())));
    var args = combineH(list(stringH()));
    var invoke = invokeH(method, args);
    assertThat(invoke.toString())
        .isEqualTo("Invoke:Int(???)@" + invoke.hash());
  }
}
