package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class InvokeBTest extends TestingContext {
  @Nested
  class _infer_type_of_invoke {
    @Test
    public void without_generic_params() {
      assertThat(invokeB(methodB(methodTB(intTB(), list(boolTB()))), list(boolB())).cat())
          .isEqualTo(invokeCB(intTB()));
    }

    @Test
    public void with_generic_params() {
      assertThat(invokeB(methodB(methodTB(varTB("A"), list(varTB("A")))), list(boolB())).cat())
          .isEqualTo(invokeCB(boolTB()));
    }
  }

  @Test
  public void creating_invoke_with_expr_not_being_method_causes_exception() {
    assertCall(() -> invokeB(intB(), combineB(list())))
        .throwsException(new IllegalArgumentException(
            "`method` component doesn't evaluate to MethodH."));
  }

  @Test
  public void creating_invoke_with_too_few_args_causes_exception() {
    var methodT = methodTB(intTB(), list(stringTB()));
    assertCall(() -> invokeB(methodB(methodT), list()))
        .throwsException(argsNotMatchingParamsException("{}", "{String}"));
  }

  @Test
  public void creating_invoke_with_too_many_args_causes_exception() {
    var methodT = methodTB(intTB(), list(stringTB()));
    assertCall(() -> invokeB(methodB(methodT), list(intB(), intB())))
        .throwsException(argsNotMatchingParamsException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_invoke_with_args_being_subtype_of_required_args_is_allowed() {
    var methodT = methodTB(intTB(), list(arrayTB(intTB())));
    var method = methodB(methodT);
    var args = combineB(list(arrayB(nothingTB())));
    var invoke = invokeB(method, args);
    assertThat(invoke.data().args())
        .isEqualTo(args);
  }

  @Test
  public void creating_invoke_with_arg_not_matching_param_type_causes_exception() {
    var method = methodB(methodTB(intTB(), list(stringTB())));
    assertCall(() -> invokeB(method, list(intB(3))))
        .throwsException(argsNotMatchingParamsException("{Int}", "{String}"));
  }

  @Test
  public void creating_invoke_with_resT_being_subtype_of_evalT() {
    var method = methodB(methodTB(arrayTB(nothingTB()), list()));
    var invokeB = invokeB(arrayTB(intTB()), method, list());
    assertThat(invokeB.data().args())
        .isEqualTo(combineB(list()));
  }

  @Test
  public void creating_invoke_with_resT_not_assignable_to_evalT_causes_exc() {
    var method = methodB(methodTB(intTB(), list()));
    assertCall(() -> invokeB(stringTB(), method, list()))
        .throwsException(new IllegalArgumentException(
            "Method's result type `Int` cannot be assigned to evalT `String`."));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Arguments evaluation type `" + args + "` should be"
        + " equal to callable type parameters `" + params + "`.");
  }

  @Test
  public void method_returns_method_expr() {
    var method = methodB(methodTB(intTB(), list(stringTB())));
    assertThat(invokeB(method, list(stringB())).data().method())
        .isEqualTo(method);
  }

  @Test
  public void args_returns_arg_exprs() {
    var method = methodB(methodTB(intTB(), list(stringTB())));
    var args = combineB(list(stringB()));
    assertThat(invokeB(method, args).data().args())
        .isEqualTo(args);
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<InvokeB> {
    @Override
    protected List<InvokeB> equalValues() {
      return list(
          invokeB(methodB(methodTB(intTB(), list(stringTB()))), combineB(list(stringB()))),
          invokeB(methodB(methodTB(intTB(), list(stringTB()))), combineB(list(stringB())))
      );
    }

    @Override
    protected List<InvokeB> nonEqualValues() {
      var m1 = methodB(methodTB(intTB(), list(stringTB())), blobB(7), stringB("a"), boolB(true));
      var m2 = methodB(methodTB(intTB(), list(stringTB())), blobB(7), stringB("a"), boolB(false));
      var m3 = methodB(methodTB(intTB(), list(stringTB())), blobB(7), stringB("b"), boolB(true));
      var m4 = methodB(methodTB(intTB(), list(stringTB())), blobB(0), stringB("a"), boolB(true));
      var m5 = methodB(methodTB(intTB(), list(blobTB())), blobB(7), stringB("a"), boolB(true));
      var m6 = methodB(methodTB(blobTB(), list(stringTB())), blobB(7), stringB("a"), boolB(true));

      var stringArgs = combineB(list(stringB()));
      var blobArgs = combineB(list(blobB()));

      return list(
          invokeB(m1, stringArgs),
          invokeB(m2, stringArgs),
          invokeB(m3, stringArgs),
          invokeB(m4, stringArgs),
          invokeB(m5, blobArgs),
          invokeB(m6, stringArgs)
      );
    }
  }

  @Test
  public void invoke_can_be_read_back_by_hash() {
    var method = methodB(methodTB(intTB(), list(stringTB())));
    var args = combineB(list(stringB()));
    var invoke = invokeB(method, args);
    assertThat(byteDbOther().get(invoke.hash()))
        .isEqualTo(invoke);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var method = methodB(methodTB(intTB(), list(stringTB())));
    var args = combineB(list(stringB()));
    var invoke = invokeB(method, args);
    var readInvoke = (InvokeB) byteDbOther().get(invoke.hash());
    var readInvokeData = readInvoke.data();
    var invokeData = invoke.data();
    assertThat(readInvokeData.method())
        .isEqualTo(invokeData.method());
    assertThat(readInvokeData.args())
        .isEqualTo(invokeData.args());
  }

  @Test
  public void to_string() {
    var method = methodB(methodTB(intTB(), list(stringTB())));
    var args = combineB(list(stringB()));
    var invoke = invokeB(method, args);
    assertThat(invoke.toString())
        .isEqualTo("Invoke:Int(???)@" + invoke.hash());
  }
}
