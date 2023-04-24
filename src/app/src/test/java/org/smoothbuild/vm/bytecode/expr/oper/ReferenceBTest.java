package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class ReferenceBTest extends TestContext {
  @Test
  public void type_of_ref_expr_is_ref_type() {
    assertThat(referenceB(intTB(), 123).category())
        .isEqualTo(referenceCB(intTB()));
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(referenceB(123).index())
        .isEqualTo(intB(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<ReferenceB> {
    @Override
    protected List<ReferenceB> equalExprs() {
      return list(
          referenceB(intTB(), 1),
          referenceB(intTB(), 1)
      );
    }

    @Override
    protected List<ReferenceB> nonEqualExprs() {
      return list(
          referenceB(intTB(), 1),
          referenceB(intTB(), 2),
          referenceB(stringTB(), 1)
      );
    }
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    var ref = referenceB(intTB(), 123);
    assertThat(bytecodeDbOther().get(ref.hash()))
        .isEqualTo(ref);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    var ref = referenceB(intTB(), 123);
    assertThat(((ReferenceB) bytecodeDbOther().get(ref.hash())).index())
        .isEqualTo(intB(123));
  }

  @Test
  public void to_string() {
    var ref = referenceB(intTB(), 123);
    assertThat(ref.toString())
        .isEqualTo("REFERENCE:Int(123)@" + ref.hash());
  }
}
