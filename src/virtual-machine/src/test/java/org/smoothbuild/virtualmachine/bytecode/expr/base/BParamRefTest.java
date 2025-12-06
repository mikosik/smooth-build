package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BParamRefTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var bParamRef = bParamRef(123);
    assertThat(bParamRef.name()).isEqualTo("paramRef");
  }

  @Test
  void type_of_var_expr_is_var_type() throws Exception {
    assertThat(bParamRef(bIntType(), 123).kind()).isEqualTo(bParamRefKind(bIntType()));
  }

  @Test
  void value_returns_stored_value() throws Exception {
    assertThat(bParamRef(123).index()).isEqualTo(bInt(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BParamRef> {
    @Override
    protected List<BParamRef> equalExprs() throws BytecodeException {
      return list(bParamRef(bIntType(), 1), bParamRef(bIntType(), 1));
    }

    @Override
    protected List<BParamRef> nonEqualExprs() throws BytecodeException {
      return list(bParamRef(bIntType(), 1), bParamRef(bIntType(), 2), bParamRef(bStringType(), 1));
    }
  }

  @Test
  void paramRef_can_be_read_back_by_hash() throws Exception {
    var paramRef = bParamRef(bIntType(), 123);
    assertThat(exprDbOther().get(paramRef.hash())).isEqualTo(paramRef);
  }

  @Test
  void const_read_back_by_hash_has_same_value() throws Exception {
    var paramRef = bParamRef(bIntType(), 123);
    assertThat(((BParamRef) exprDbOther().get(paramRef.hash())).index()).isEqualTo(bInt(123));
  }

  @Test
  void to_string() throws Exception {
    var paramRef = bParamRef(bIntType(), 123);
    assertThat(paramRef.toString())
        .isEqualTo(
            """
        BParamRef(
          hash = 835fd9277c4aa2efb281e2e777cc65e74be8d939758e7454a4a6041c47aa4887
          evaluationType = Int
          index = 123
        )""");
  }
}
