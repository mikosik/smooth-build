package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BNativeFuncTest extends TestingVirtualMachine {
  @Test
  public void type_is_read_correctly() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var funcType = bFuncType(bIntType(), bStringType());
    var nativeFunc = bNativeFunc(funcType, jar, classBinaryName, isPure);
    assertThat(nativeFunc.evaluationType()).isEqualTo(funcType);
  }

  @Test
  public void components_are_read_correctly() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var type = bFuncType(bIntType(), bStringType());
    var nativeFunc = bNativeFunc(type, jar, classBinaryName, isPure);

    assertThat(nativeFunc.jar()).isEqualTo(jar);
    assertThat(nativeFunc.classBinaryName()).isEqualTo(classBinaryName);
    assertThat(nativeFunc.isPure()).isEqualTo(isPure);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BNativeFunc> {
    @Override
    protected List<BNativeFunc> equalExprs() throws BytecodeException {
      return list(
          bNativeFunc(bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(true)),
          bNativeFunc(bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(true)));
    }

    @Override
    protected List<BNativeFunc> nonEqualExprs() throws BytecodeException {
      return list(
          bNativeFunc(bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(true)),
          bNativeFunc(bFuncType(bIntType(), bStringType()), bBlob(7), bString("a"), bBool(false)),
          bNativeFunc(bFuncType(bIntType(), bStringType()), bBlob(7), bString("b"), bBool(true)),
          bNativeFunc(bFuncType(bIntType(), bStringType()), bBlob(9), bString("a"), bBool(true)),
          bNativeFunc(
              bFuncType(bStringType(), bStringType()), bBlob(7), bString("a"), bBool(true)));
    }
  }

  @Test
  public void native_func_can_be_read_back_by_hash() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var funcType = bFuncType(bIntType(), bStringType());
    var nativeFunc = bNativeFunc(funcType, jar, classBinaryName, isPure);
    assertThat(exprDbOther().get(nativeFunc.hash())).isEqualTo(nativeFunc);
  }

  @Test
  public void native_func_read_back_by_hash_has_same_data() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var funcType = bFuncType(bIntType(), bStringType());
    var nativeFunc = bNativeFunc(funcType, jar, classBinaryName, isPure);
    var readNativeFunc = (BNativeFunc) exprDbOther().get(nativeFunc.hash());
    assertThat(readNativeFunc.classBinaryName()).isEqualTo(classBinaryName);
    assertThat(readNativeFunc.jar()).isEqualTo(jar);
    assertThat(readNativeFunc.isPure()).isEqualTo(isPure);
  }

  @Test
  public void to_string() throws Exception {
    var jar = bBlob();
    var classBinaryName = bString();
    var isPure = bBool(true);
    var funcType = bFuncType(bIntType(), bStringType());
    var nativeFunc = bNativeFunc(funcType, jar, classBinaryName, isPure);
    assertThat(nativeFunc.toString()).isEqualTo("NativeFunc((Int)->String)@" + nativeFunc.hash());
  }
}
