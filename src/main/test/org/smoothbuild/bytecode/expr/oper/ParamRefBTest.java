package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class ParamRefBTest extends TestContext {
  @Test
  public void type_of_ref_expr_is_ref_type() {
    assertThat(paramRefB(intTB(), 123).cat())
        .isEqualTo(paramRefCB(intTB()));
  }

  @Test
  public void value_returns_stored_value() {
    assertThat(paramRefB(123).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<ParamRefB> {
    @Override
    protected List<ParamRefB> equalExprs() {
      return list(
          paramRefB(intTB(), 1),
          paramRefB(intTB(), 1)
      );
    }

    @Override
    protected List<ParamRefB> nonEqualExprs() {
      return list(
          paramRefB(intTB(), 1),
          paramRefB(intTB(), 2),
          paramRefB(stringTB(), 1)
      );
    }
  }

  @Test
  public void ref_can_be_read_back_by_hash() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(bytecodeDbOther().get(paramRef.hash()))
        .isEqualTo(paramRef);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(((ParamRefB) bytecodeDbOther().get(paramRef.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(paramRef.toString())
        .isEqualTo("ParamRef:Int(123)@" + paramRef.hash());
  }
}
