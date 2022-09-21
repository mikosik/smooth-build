package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class RefBTest extends TestContext {
  @Test
  public void type_of_ref_expr_is_ref_type() {
    assertThat(refB(intTB(), 123).category())
        .isEqualTo(refCB(intTB()));
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(refB(123).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<RefB> {
    @Override
    protected List<RefB> equalExprs() {
      return list(
          refB(intTB(), 1),
          refB(intTB(), 1)
      );
    }

    @Override
    protected List<RefB> nonEqualExprs() {
      return list(
          refB(intTB(), 1),
          refB(intTB(), 2),
          refB(stringTB(), 1)
      );
    }
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    var ref = refB(intTB(), 123);
    assertThat(bytecodeDbOther().get(ref.hash()))
        .isEqualTo(ref);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    var ref = refB(intTB(), 123);
    assertThat(((RefB) bytecodeDbOther().get(ref.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    var ref = refB(intTB(), 123);
    assertThat(ref.toString())
        .isEqualTo("Ref:Int(123)@" + ref.hash());
  }
}
