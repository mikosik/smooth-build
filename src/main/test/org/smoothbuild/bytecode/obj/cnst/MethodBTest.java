package org.smoothbuild.bytecode.obj.cnst;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class MethodBTest extends TestingContext {
  @Test
  public void type_is_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(methodH.type())
        .isEqualTo(methodTH);
  }

  @Test
  public void components_are_read_correctly() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var type = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(type, jar, classBinaryName, isPure);

    assertThat(methodH.jar())
        .isEqualTo(jar);
    assertThat(methodH.classBinaryName())
        .isEqualTo(classBinaryName);
    assertThat(methodH.isPure())
        .isEqualTo(isPure);
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<MethodB> {
    @Override
    protected List<MethodB> equalValues() {
      return list(
          methodB(methodTB(stringTB(), list(intTB())), blobB(7), stringB("a"), boolB(true)),
          methodB(methodTB(stringTB(), list(intTB())), blobB(7), stringB("a"), boolB(true))
      );
    }

    @Override
    protected List<MethodB> nonEqualValues() {
      return list(
          methodB(methodTB(stringTB(), list(intTB())), blobB(7), stringB("a"), boolB(true)),
          methodB(methodTB(stringTB(), list(intTB())), blobB(7), stringB("a"), boolB(false)),
          methodB(methodTB(stringTB(), list(intTB())), blobB(7), stringB("b"), boolB(true)),
          methodB(methodTB(stringTB(), list(intTB())), blobB(9), stringB("a"), boolB(true)),
          methodB(methodTB(stringTB(), list(stringTB())), blobB(7), stringB("a"), boolB(true))
      );
    }
  }

  @Test
  public void method_can_be_read_back_by_hash() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(objDbOther().get(methodH.hash()))
        .isEqualTo(methodH);
  }

  @Test
  public void method_read_back_by_hash_has_same_data() {
    var jar = blobB();
    var classBinaryName = stringB();
    var isPure = boolB(true);
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    var readMethodH = (MethodB) objDbOther().get(methodH.hash());
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
    var methodTH = methodTB(stringTB(), list(intTB()));
    var methodH = methodB(methodTH, jar, classBinaryName, isPure);
    assertThat(methodH.toString())
        .isEqualTo("Method(_String(Int))@" + methodH.hash());
  }
}
