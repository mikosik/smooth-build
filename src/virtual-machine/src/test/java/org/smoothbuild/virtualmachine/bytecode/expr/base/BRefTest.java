package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BRefTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var bRef = bRef(123);
    assertThat(bRef.name()).isEqualTo("ref");
  }

  @Test
  void type_of_ref_expr_is_ref_type() throws Exception {
    assertThat(bRef(bIntType(), 123).kind()).isEqualTo(bRefKind(bIntType()));
  }

  @Test
  void value_returns_stored_value() throws Exception {
    assertThat(bRef(123).index()).isEqualTo(bInt(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BRef> {
    @Override
    protected List<BRef> equalExprs() throws BytecodeException {
      return list(bRef(bIntType(), 1), bRef(bIntType(), 1));
    }

    @Override
    protected List<BRef> nonEqualExprs() throws BytecodeException {
      return list(bRef(bIntType(), 1), bRef(bIntType(), 2), bRef(bStringType(), 1));
    }
  }

  @Test
  void ref_can_be_read_back_by_hash() throws Exception {
    var ref = bRef(bIntType(), 123);
    assertThat(exprDbOther().get(ref.hash())).isEqualTo(ref);
  }

  @Test
  void const_read_back_by_hash_has_same_value() throws Exception {
    var ref = bRef(bIntType(), 123);
    assertThat(((BRef) exprDbOther().get(ref.hash())).index()).isEqualTo(bInt(123));
  }

  @Test
  void to_string() throws Exception {
    var ref = bRef(bIntType(), 123);
    assertThat(ref.toString()).isEqualTo("""
        BRef(
          hash = 835fd9277c4aa2efb281e2e777cc65e74be8d939758e7454a4a6041c47aa4887
          evaluationType = Int
          index = 123
        )""");
  }
}
