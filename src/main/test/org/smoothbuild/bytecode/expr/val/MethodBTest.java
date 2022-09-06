package org.smoothbuild.bytecode.expr.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class MethodBTest extends TestContext {
  @Test
  public void type_is_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), intTB());
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(methodH.type())
        .isEqualTo(methodTH);
  }

  @Test
  public void components_are_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), intTB());
    var methodH = methodB(type, jar, classBinaryName, isPure);

    assertThat(methodH.jar())
        .isEqualTo(jar);
    assertThat(methodH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(methodH.isPure())
        .isEqualTo(isPure);
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<MethodB> {
    @Override
    protected List<MethodB> equalExprs() {
      return list(
          methodB(methodTB(stringTB(), intTB()), blobB(7), stringB("a"), boolB(true)),
          methodB(methodTB(stringTB(), intTB()), blobB(7), stringB("a"), boolB(true))
      );
    }

    @Override
    protected List<MethodB> nonEqualExprs() {
      return list(
          methodB(methodTB(stringTB(), intTB()), blobB(7), stringB("a"), boolB(true)),
          methodB(methodTB(stringTB(), intTB()), blobB(7), stringB("a"), boolB(false)),
          methodB(methodTB(stringTB(), intTB()), blobB(7), stringB("b"), boolB(true)),
          methodB(methodTB(stringTB(), intTB()), blobB(9), stringB("a"), boolB(true)),
          methodB(methodTB(stringTB(), stringTB()), blobB(7), stringB("a"), boolB(true))
      );
    }
  }

  @Test
  public void method_can_be_read_back_by_hash() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), intTB());
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(bytecodeDbOther().get(methodH.hash()))
        .isEqualTo(methodH);
  }

  @Test
  public void method_read_back_by_hash_has_same_data() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), intTB());
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    var readMethodH = (MethodB) bytecodeDbOther().get(methodH.hash());
    assertThat(readMethodH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(readMethodH.jar())
        .isEqualTo(jar);
    assertThat(readMethodH.isPure())
        .isEqualTo(isPure);
  }

  @Test
  public void to_string() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), intTB());
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(methodH.toString())
        .isEqualTo("Method(_String(Int))@" + methodH.hash());
  }
}
