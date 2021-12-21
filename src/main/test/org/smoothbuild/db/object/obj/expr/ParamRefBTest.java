package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.ObjBTestCase;
import org.smoothbuild.testing.TestingContext;

public class ParamRefBTest extends TestingContext {
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
  class _equals_hash_hashcode extends ObjBTestCase<ParamRefB> {
    @Override
    protected List<ParamRefB> equalValues() {
      return list(
          paramRefB(intTB(), 1),
          paramRefB(intTB(), 1)
      );
    }

    @Override
    protected List<ParamRefB> nonEqualValues() {
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
    assertThat(byteDbOther().get(paramRef.hash()))
        .isEqualTo(paramRef);
  }

  @Test
  public void const_read_back_by_hash_has_same_value() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(((ParamRefB) byteDbOther().get(paramRef.hash())).value())
        .isEqualTo(BigInteger.valueOf(123));
  }

  @Test
  public void to_string() {
    ParamRefB paramRef = paramRefB(intTB(), 123);
    assertThat(paramRef.toString())
        .isEqualTo("ParamRef:Int(123)@" + paramRef.hash());
  }
}
