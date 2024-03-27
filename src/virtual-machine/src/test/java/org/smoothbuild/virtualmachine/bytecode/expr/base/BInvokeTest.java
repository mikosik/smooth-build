package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BInvokeTest extends TestingVirtualMachine {
  @Test
  void creating_fails_when_jar_evaluation_type_is_not_blob() {
    assertCall(() -> bInvoke(bIntType(), bString(), bString(), bBool(), bTuple()))
        .throwsException(new IllegalArgumentException(
            "`jar.evaluationType()` should be `Blob` but is `String`."));
  }

  @Test
  void creating_fails_when_class_binary_name_evaluation_type_is_not_string() {
    assertCall(() -> bInvoke(bIntType(), bBlob(), bInt(), bBool(), bTuple()))
        .throwsException(new IllegalArgumentException(
            "`classBinaryName.evaluationType()` should be `String` but is `Int`."));
  }

  @Test
  void creating_fails_when_is_pure_evaluation_type_is_not_bool() {
    assertCall(() -> bInvoke(bIntType(), bBlob(), bString(), bString(), bTuple()))
        .throwsException(new IllegalArgumentException(
            "`isPure.evaluationType()` should be `Bool` but is `String`."));
  }

  @Test
  void creating_fails_when_arguments_evaluation_type_is_not_tuple() {
    assertCall(() -> bInvoke(bIntType(), bBlob(), bString(), bBool(), bString()))
        .throwsException(new IllegalArgumentException(
            "`arguments.evaluationType()` should be `BTupleType` but is `BStringType`."));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BInvoke> {
    @Override
    protected List<BInvoke> equalExprs() throws BytecodeException {
      return list(
          bInvoke(
              bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(true), bTuple()),
          bInvoke(
              bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(true), bTuple()));
    }

    @Override
    protected List<BInvoke> nonEqualExprs() throws BytecodeException {
      return list(
          bInvoke(
              bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(true), bTuple()),
          bInvoke(
              bFuncType(bIntType(), bStringType()),
              bBlob(7),
              bString("a"),
              bBool(true),
              bTuple(bInt(1))),
          bInvoke(
              bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(false), bTuple()),
          bInvoke(
              bFuncType(bIntType(), bStringType()), bBlob(7), bString("b"), bBool(true), bTuple()),
          bInvoke(
              bFuncType(bIntType(), bStringType()), bBlob(9), bString("a"), bBool(true), bTuple()),
          bInvoke(
              bFuncType(bStringType(), bStringType()),
              bBlob(7),
              bString("a"),
              bBool(true),
              bTuple()));
    }
  }

  @Test
  public void invoke_can_be_read_back_by_hash() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var arguments = bTuple(bInt(1));
    var evaluationType = bIntType();
    var invoke = bInvoke(evaluationType, jar, classBinaryName, isPure, arguments);
    assertThat(exprDbOther().get(invoke.hash())).isEqualTo(invoke);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var arguments = bTuple(bInt(1));
    var evaluationType = bIntType();
    var invoke = bInvoke(evaluationType, jar, classBinaryName, isPure, arguments);
    assertThat(((BInvoke) exprDbOther().get(invoke.hash())).subExprs())
        .isEqualTo(new BInvoke.SubExprsB(jar, classBinaryName, isPure, arguments));
  }

  @Test
  public void to_string() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var arguments = bTuple(bInt(1));
    var evaluationType = bIntType();
    var invoke = bInvoke(evaluationType, jar, classBinaryName, isPure, arguments);
    assertThat(invoke.toString()).isEqualTo("INVOKE:Int(???)@" + invoke.hash());
  }
}
