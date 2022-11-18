package org.smoothbuild.bytecode.expr.inst;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class NatFuncBTest extends TestContext {
  @Test
  public void type_is_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var natFunc = natFuncB(funcT, jar, classBinaryName, isPure);
    assertThat(natFunc.evalT())
        .isEqualTo(funcT);
  }

  @Test
  public void components_are_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = funcTB(intTB(), stringTB());
    var natFunc = natFuncB(type, jar, classBinaryName, isPure);

    assertThat(natFunc.jar())
        .isEqualTo(jar);
    assertThat(natFunc.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(natFunc.isPure())
        .isEqualTo(isPure);
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<NatFuncB> {
    @Override
    protected List<NatFuncB> equalExprs() {
      return list(
          natFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)),
          natFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true))
      );
    }

    @Override
    protected List<NatFuncB> nonEqualExprs() {
      return list(
          natFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(true)),
          natFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("a"), boolB(false)),
          natFuncB(funcTB(intTB(), stringTB()), blobB(7), stringB("b"), boolB(true)),
          natFuncB(funcTB(intTB(), stringTB()), blobB(9), stringB("a"), boolB(true)),
          natFuncB(funcTB(stringTB(), stringTB()), blobB(7), stringB("a"), boolB(true))
      );
    }
  }

  @Test
  public void nat_func_can_be_read_back_by_hash() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var natFunc = natFuncB(funcT, jar, classBinaryName, isPure);
    assertThat(bytecodeDbOther().get(natFunc.hash()))
        .isEqualTo(natFunc);
  }

  @Test
  public void nat_func_read_back_by_hash_has_same_data() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var natFuncB = natFuncB(funcT, jar, classBinaryName, isPure);
    var readNatFuncB = (NatFuncB) bytecodeDbOther().get(natFuncB.hash());
    assertThat(readNatFuncB.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readNatFuncB.jar())
        .isEqualTo(jar);
    assertThat(readNatFuncB.isPure())
        .isEqualTo(isPure);
  }

  @Test
  public void to_string() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var funcT = funcTB(intTB(), stringTB());
    var natFunc = natFuncB(funcT, jar, classBinaryName, isPure);
    assertThat(natFunc.toString())
        .isEqualTo("NatFunc((Int)->String)@" + natFunc.hash());
  }
}
