package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class CallBTest extends TestingContext {
  @Nested
  class _infer_type_of_call {
    @Test
    public void without_generic_params() {
      assertThat(callB(funcB(list(stringTB()), intB()), stringB()).cat())
          .isEqualTo(callCB(intTB()));
    }

    @Test
    public void with_generic_params() {
      var oa = oVarTB("A");
      assertThat(callB(funcB(oa, list(oa), paramRefB(cVarTB("A"), 0)), intB()).cat())
          .isEqualTo(callCB(intTB()));
    }
  }

  @Test
  public void creating_call_with_func_type_not_being_func_causes_exception() {
    assertCall(() -> callB(blobTB(), intB()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to FuncH."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB())))
        .throwsException(argsNotMatchingParamsException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), intB(), intB()))
        .throwsException(argsNotMatchingParamsException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_args_being_subtype_of_required_args_is_allowed() {
    var func = funcB(list(arrayTB(intTB())), paramRefB(arrayTB(intTB()), 0));
    var call = callB(func, arrayB(nothingTB()));
    assertThat(call.data().args())
        .isEqualTo(combineB(arrayB(nothingTB())));
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), intB(3)))
        .throwsException(argsNotMatchingParamsException("{Int}", "{String}"));
  }

  @Test
  public void creating_call_with_resT_being_subtype_of_evalT() {
    var func = funcB(list(), arrayB(nothingTB()));
    var callB = callB(arrayTB(intTB()), func);
    assertThat(callB.data().args())
        .isEqualTo(combineB());
  }

  @Test
  public void creating_call_with_resT_not_assignable_to_evalT_causes_exc() {
    var func = funcB(list(), intB(7));
    assertCall(() -> callB(stringTB(), func))
        .throwsException(new IllegalArgumentException(
            "Call's result type `Int` cannot be assigned to evalT `String`."));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException("Arguments evaluation type `" + args + "` should be"
        + " equal to callable type parameters `" + params + "`.");
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
  class _equals_hash_hashcode extends ObjBTestCase<CallB> {
    @Override
    protected List<CallB> equalValues() {
      return list(
          callB(funcB(list(blobTB()), intB()), blobB()),
          callB(funcB(list(blobTB()), intB()), blobB())
      );
    }

    @Override
    protected List<CallB> nonEqualValues() {
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
    assertThat(objDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = funcB(list(stringTB()), intB());
    var call = callB(func, stringB());
    assertThat(((CallB) objDbOther().get(call.hash())).data())
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
