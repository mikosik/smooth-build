package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke.BSubExprs;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BInvokeTest extends VmTestContext {
  @Test
  void creating_fails_when_method_evaluation_type_is_not_tuple() {
    assertCall(() -> bInvoke(bIntType(), bInt(), bBool(), bTuple()))
        .throwsException(new IllegalArgumentException(
            "`method.evaluationType()` should be `{Blob,String,String}` but is `Int`."));
  }

  @Test
  void creating_fails_when_is_pure_evaluation_type_is_not_bool() {
    assertCall(() -> bInvoke(bIntType(), bMethodTuple(), bString(), bTuple()))
        .throwsException(new IllegalArgumentException(
            "`isPure.evaluationType()` should be `Bool` but is `String`."));
  }

  @Test
  void creating_fails_when_arguments_evaluation_type_is_not_tuple() {
    assertCall(() -> bInvoke(bIntType(), bMethodTuple(), bBool(), bString()))
        .throwsException(new IllegalArgumentException(
            "`arguments.evaluationType()` should be `BTupleType` but is `BStringType`."));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BInvoke> {
    @Override
    protected List<BInvoke> equalExprs() throws Exception {
      return list(
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(7), "a", "b"),
              bBool(true),
              bTuple()),
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(7), "a", "b"),
              bBool(true),
              bTuple()));
    }

    @Override
    protected List<BInvoke> nonEqualExprs() throws Exception {
      return list(
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(7), "a", "b"),
              bBool(true),
              bTuple()),
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(7), "a", "b"),
              bBool(true),
              bTuple(bInt(1))),
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(7), "a", "b"),
              bBool(false),
              bTuple()),
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(7), "x", "b"),
              bBool(true),
              bTuple()),
          bInvoke(
              bLambdaType(bIntType(), bStringType()),
              bMethodTuple(bBlob(9), "a", "b"),
              bBool(true),
              bTuple()),
          bInvoke(
              bLambdaType(bStringType(), bStringType()),
              bMethodTuple(bBlob(7), "a", "b"),
              bBool(true),
              bTuple()));
    }
  }

  @Test
  void invoke_can_be_read_back_by_hash() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var arguments = bTuple(bInt(1));
    var evaluationType = bIntType();
    var invoke = bInvoke(evaluationType, bMethodTuple(jar, classBinaryName), isPure, arguments);
    assertThat(exprDbOther().get(invoke.hash())).isEqualTo(invoke);
  }

  @Test
  void invoke_read_back_by_hash_has_same_data() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var method = bMethodTuple(jar, classBinaryName);
    var isPure = bBool(true);
    var arguments = bTuple(bInt(1));
    var evaluationType = bIntType();
    var invoke = bInvoke(evaluationType, method, isPure, arguments);
    assertThat(((BInvoke) exprDbOther().get(invoke.hash())).subExprs())
        .isEqualTo(new BSubExprs(method, isPure, arguments));
  }

  @Test
  void to_string() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var arguments = bTuple(bInt(1));
    var evaluationType = bIntType();
    var invoke = bInvoke(evaluationType, bMethodTuple(jar, classBinaryName), isPure, arguments);
    assertThat(invoke.toString())
        .isEqualTo(
            """
        BInvoke(
          hash = 98cf30d3344c13cbe1fcc256ec209994c4a265dce1ab035c94c3576202ad4d55
          evaluationType = Int
          method = BTuple(
            hash = 1873ed459f662fcab950a1cdac4e49b79e5ddd7105ee1afdcc372315ff1ad7d5
            type = {Blob,String,String}
            elements = [
              BBlob(
                hash = f6be1077b575e98645001b2682fe2e7bb0e87cd7486cb6607649b91d42649bde
                evaluationType = Blob
                value = 0x626c6f622064617461
              )
              BString(
                hash = a8290d3ebf36fd0cda7c9e3e5e4a81199d86c6ed3585c073502313f03bdf9986
                type = String
                value = "abc"
              )
              BString(
                hash = 55f0155613c21846e6a2c7e5c06c92c9688b0f437fe0453569258760b09a7668
                type = String
                value = "func"
              )
            ]
          )
          isPure = BBool(
            hash = e9585a54d9f08cc32a4c31683378c0fdc64e7b8fb6af4eb92ba3c9cf8911e8ba
            type = Bool
            value = true
          )
          arguments = BTuple(
            hash = 09bae0637a6711e8136a3070a1c54a5a02083b31e165692e12f1dd2e2adeb1a5
            type = {Int}
            elements = [
              BInt(
                hash = b4f5acf1123d217b7c40c9b5f694b31bf83c07bd40b24fe42cadb0e458f4ab45
                type = Int
                value = 1
              )
            ]
          )
        )""");
  }
}
