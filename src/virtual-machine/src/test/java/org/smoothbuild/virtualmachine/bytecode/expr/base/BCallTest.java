package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall.BSubExprs;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BCallTest extends VmTestContext {
  @Test
  void creating_call_with_lambda_type_not_being_lambda_causes_exception() {
    assertCall(() -> bCall(bInt()))
        .throwsException(new IllegalArgumentException(
            "`lambda.evaluationType()` should be `BLambdaType` but is `BIntType`."));
  }

  @Test
  void creating_call_with_too_few_args_causes_exception() {
    assertCall(() -> bCall(bLambda(list(bStringType()), bInt())))
        .throwsException(argsNotMatchingParamsException("", "String"));
  }

  @Test
  void creating_call_with_too_many_args_causes_exception() {
    assertCall(() -> bCall(bLambda(list(bStringType()), bInt()), bInt(), bInt()))
        .throwsException(argsNotMatchingParamsException("Int,Int", "String"));
  }

  @Test
  void creating_call_with_arg_not_matching_param_type_causes_exception() {
    assertCall(() -> bCall(bLambda(list(bStringType()), bInt()), bInt(3)))
        .throwsException(argsNotMatchingParamsException("Int", "String"));
  }

  private static IllegalArgumentException argsNotMatchingParamsException(
      String args, String params) {
    return new IllegalArgumentException(
        "`arguments.evaluationType()` should be `{" + params + "}` but is `{" + args + "}`.");
  }

  @Test
  void sub_exprs_returns_sub_exprs() throws Exception {
    var lambda = bLambda(list(bStringType()), bInt());
    var argument = bString();
    assertThat(bCall(lambda, argument).subExprs())
        .isEqualTo(new BSubExprs(lambda, bCombine(argument)));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCall> {
    @Override
    protected List<BCall> equalExprs() throws Exception {
      return list(
          bCall(bLambda(list(bBlobType()), bInt()), bBlob()),
          bCall(bLambda(list(bBlobType()), bInt()), bBlob()));
    }

    @Override
    protected List<BCall> nonEqualExprs() throws Exception {
      return list(
          bCall(bLambda(list(bBlobType()), bInt()), bBlob()),
          bCall(bLambda(list(bStringType()), bInt()), bString()),
          bCall(bLambda(list(bBlobType()), bString()), bBlob()));
    }
  }

  @Test
  void call_can_be_read_back_by_hash() throws Exception {
    var call = bCall(bLambda(list(bStringType()), bInt()), bString());
    assertThat(exprDbOther().get(call.hash())).isEqualTo(call);
  }

  @Test
  void call_read_back_by_hash_has_same_data() throws Exception {
    var lambda = bLambda(list(bStringType()), bInt());
    var argument = bString();
    var call = bCall(lambda, argument);
    assertThat(((BCall) exprDbOther().get(call.hash())).subExprs())
        .isEqualTo(new BSubExprs(lambda, bCombine(argument)));
  }

  @Test
  void to_string() throws Exception {
    var lambda = bLambda(list(bStringType()), bInt());
    var call = bCall(lambda, bString());
    assertThat(call.toString())
        .isEqualTo(
            """
        BCall(
          hash = 3b252948eca848beff114959af5f9fe1e39fbf0a01279dd66b52d5f008f2fcc9
          evaluationType = Int
          lambda = BLambda(
            hash = a71e9acb33d438c168f026f4df84f156b0abdb99c6006c26e382ec85378b6eff
            type = (String)->Int
            body = BInt(
              hash = d6781a8034402f1bb1369df5042c4cc9d4d726044ba4ae8eb55efce43bad6ec5
              type = Int
              value = 17
            )
          )
          arguments = BCombine(
            hash = b761d4282e51919ce2ad4a68c47d5713769c730ff59b991186d93fb838afb46a
            evaluationType = {String}
            items = [
              BString(
                hash = a8290d3ebf36fd0cda7c9e3e5e4a81199d86c6ed3585c073502313f03bdf9986
                type = String
                value = "abc"
              )
            ]
          )
        )""");
  }
}
