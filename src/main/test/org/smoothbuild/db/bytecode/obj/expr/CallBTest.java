package org.smoothbuild.db.bytecode.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.bytecode.obj.ObjBTestCase;
import org.smoothbuild.db.bytecode.obj.base.ObjB;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class CallBTest extends TestingContext {
  @Nested
  class _infer_type_of_call {
    @Test
    public void without_generic_params() {
      assertThat(callB(funcB(list(stringTB()), intB()), list(stringB())).cat())
          .isEqualTo(callCB(intTB()));
    }

    @Test
    public void with_generic_params() {
      assertThat(callB(funcB(list(varTB("A")), paramRefB(varTB("A"), 0)), list(intB())).cat())
          .isEqualTo(callCB(intTB()));
    }
  }

  @Test
  public void creating_call_with_func_type_not_being_func_causes_exception() {
    assertCall(() -> callB(intB(), list()))
        .throwsException(new IllegalArgumentException(
            "`func` component doesn't evaluate to FuncH."));
  }

  @Test
  public void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), list()))
        .throwsException(argsNotMatchingParamsException("{}", "{String}"));
  }

  @Test
  public void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), list(intB(), intB())))
        .throwsException(argsNotMatchingParamsException("{Int,Int}", "{String}"));
  }

  @Test
  public void creating_call_with_args_being_subtype_of_required_args_is_allowed() {
    var func = funcB(list(arrayTB(intTB())), paramRefB(arrayTB(intTB()), 0));
    var args = combineB(list(arrayB(nothingTB())));
    var call = callB(func, args);
    assertThat(call.data().args())
        .isEqualTo(args);
  }

  @Test
  public void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> callB(funcB(list(stringTB()), intB()), list(intB(3))))
        .throwsException(argsNotMatchingParamsException("{Int}", "{String}"));
  }

  @Test
  public void creating_call_with_resT_being_subtype_of_evalT() {
    var func = funcB(list(), arrayB(nothingTB()));
    var callB = callB(arrayTB(intTB()), func, list());
    assertThat(callB.data().args())
        .isEqualTo(combineB(list()));
  }

  @Test
  public void creating_call_with_resT_not_assignable_to_evalT_causes_exc() {
    var func = funcB(list(), intB(7));
    assertCall(() -> callB(stringTB(), func, list()))
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
    assertThat(callB(func, list(stringB())).data().callable())
        .isEqualTo(func);
  }

  @Test
  public void args_returns_arg_exprs() {
    var func = funcB(list(stringTB()), intB());
    ImmutableList<ObjB> args = list(stringB()) ;
    assertThat(callB(func, args).data().args())
        .isEqualTo(combineB(args));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<CallB> {
    @Override
    protected List<CallB> equalValues() {
      return list(
          callB(funcB(list(blobTB()), intB()), list(blobB())),
          callB(funcB(list(blobTB()), intB()), list(blobB()))
      );
    }

    @Override
    protected List<CallB> nonEqualValues() {
      return list(
          callB(funcB(list(blobTB()), intB()), list(blobB())),
          callB(funcB(list(stringTB()), intB()), list(stringB())),
          callB(funcB(list(blobTB()), stringB()), list(blobB()))
      );
    }
  }

  @Test
  public void call_can_be_read_back_by_hash() {
    var call = callB(funcB(list(stringTB()), intB()), list(stringB()));
    assertThat(byteDbOther().get(call.hash()))
        .isEqualTo(call);
  }

  @Test
  public void call_read_back_by_hash_has_same_data() {
    var func = funcB(list(stringTB()), intB());
    ImmutableList<ObjB> args = list(stringB());
    var call = callB(func, args);
    assertThat(((CallB) byteDbOther().get(call.hash())).data())
        .isEqualTo(new CallB.Data(func, combineB(args)));
  }

  @Test
  public void to_string() {
    var func = funcB(list(stringTB()), intB());
    var call = callB(func, list(stringB()));
    assertThat(call.toString())
        .isEqualTo("Call:Int(???)@" + call.hash());
  }
}
