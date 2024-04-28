package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke.BSubExprs;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BInvokeTest extends TestingVirtualMachine {
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
    protected List<BInvoke> equalExprs() throws BytecodeException {
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
    protected List<BInvoke> nonEqualExprs() throws BytecodeException {
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
    assertThat(invoke.toString()).isEqualTo("INVOKE:Int(???)@" + invoke.hash());
  }
}
