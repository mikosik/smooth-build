package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class NativeFuncBTest extends TestContext {
  @Test
  public void type_is_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var nativeFuncB = nativeFuncB(funcT, jar, classBinaryName, isPure);
    assertThat(nativeFuncB.evaluationT())
        .isEqualTo(funcT);
  }

  @Test
  public void components_are_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = funcTB(intTB(), stringTB());
    var nativeFuncB = nativeFuncB(type, jar, classBinaryName, isPure);

    assertThat(nativeFuncB.jar())
        .isEqualTo(jar);
    assertThat(nativeFuncB.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(nativeFuncB.isPure())
        .isEqualTo(isPure);
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<NativeFuncB> {
    @Override
    protected List<NativeFuncB> equalExprs() {
      return list(
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true))
      );
    }

    @Override
    protected List<NativeFuncB> nonEqualExprs() {
      return list(
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(false)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("b"), boolB(true)),
          nativeFuncB(funcTB(intTB(), stringTB()), blobB(9), stringB("a"), boolB(true)),
          nativeFuncB(funcTB(stringTB(), stringTB()), blobB(7), stringB("a"), boolB(true))
      );
    }
  }

  @Test
  public void native_func_can_be_read_back_by_hash() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var nativeFuncB = nativeFuncB(funcT, jar, classBinaryName, isPure);
    assertThat(bytecodeDbOther().get(nativeFuncB.hash()))
        .isEqualTo(nativeFuncB);
  }

  @Test
  public void native_func_read_back_by_hash_has_same_data() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var nativeFuncB = nativeFuncB(funcT, jar, classBinaryName, isPure);
    var readNativeFuncB = (NativeFuncB) bytecodeDbOther().get(nativeFuncB.hash());
    assertThat(readNativeFuncB.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNativeFuncB.jar())
        .isEqualTo(jar);
    assertThat(readNativeFuncB.isPure())
        .isEqualTo(isPure);
  }

  @Test
  public void to_string() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var nativeFuncB = nativeFuncB(funcT, jar, classBinaryName, isPure);
    assertThat(nativeFuncB.toString())
        .isEqualTo("NativeFunc((Int)->String)@" + nativeFuncB.hash());
  }
}
