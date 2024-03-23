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
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcType = funcTB(intTB(), stringTB());
    var nativeFunc = nativeFuncB(funcType, jar, classBinaryName, isPure);
    assertThat(nativeFunc.evaluationType()).isEqualTo(funcType);
  }

  @Test
  public void components_are_read_correctly() throws Exception {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = funcTB(intTB(), stringTB());
    var nativeFunc = nativeFuncB(type, jar, classBinaryName, isPure);

    assertThat(nativeFunc.jar()).isEqualTo(jar);
    assertThat(nativeFunc.classBinaryName()).isEqualTo(classBinaryName);
    assertThat(nativeFunc.isPure()).isEqualTo(isPure);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BNativeFunc> {
    @Override
    protected List<BNativeFunc> equalExprs() throws BytecodeException {
      return list(
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)));
    }

    @Override
    protected List<BNativeFunc> nonEqualExprs() throws BytecodeException {
      return list(
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(false)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("b"), boolB(true)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(9), stringB("a"), boolB(true)),
          nativeFuncB(funcTB(stringTB(), stringTB()), blobB(7), stringB("a"), boolB(true)));
    }
  }

  @Test
  public void native_func_can_be_read_back_by_hash() throws Exception {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcType = funcTB(intTB(), stringTB());
    var nativeFunc = nativeFuncB(funcType, jar, classBinaryName, isPure);
    assertThat(exprDbOther().get(nativeFunc.hash())).isEqualTo(nativeFunc);
  }

  @Test
  public void native_func_read_back_by_hash_has_same_data() throws Exception {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcType = funcTB(intTB(), stringTB());
    var nativeFunc = nativeFuncB(funcType, jar, classBinaryName, isPure);
    var readNativeFunc = (BNativeFunc) exprDbOther().get(nativeFunc.hash());
    assertThat(readNativeFunc.classBinaryName()).isEqualTo(classBinaryName);
    assertThat(readNativeFunc.jar()).isEqualTo(jar);
    assertThat(readNativeFunc.isPure()).isEqualTo(isPure);
  }

  @Test
  public void to_string() throws Exception {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcType = funcTB(intTB(), stringTB());
    var nativeFunc = nativeFuncB(funcType, jar, classBinaryName, isPure);
    assertThat(nativeFunc.toString()).isEqualTo("NativeFunc((Int)->String)@" + nativeFunc.hash());
  }
}
