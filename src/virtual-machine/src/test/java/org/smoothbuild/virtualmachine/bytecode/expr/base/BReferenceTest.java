package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BReferenceTest extends VmTestContext {
  @Test
  void type_of_var_expr_is_var_type() throws Exception {
    assertThat(bReference(bIntType(), 123).kind()).isEqualTo(bReferenceKind(bIntType()));
  }

  @Test
  void value_returns_stored_value() throws Exception {
    assertThat(bReference(123).index()).isEqualTo(bInt(123));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BReference> {
    @Override
    protected List<BReference> equalExprs() throws BytecodeException {
      return list(bReference(bIntType(), 1), bReference(bIntType(), 1));
    }

    @Override
    protected List<BReference> nonEqualExprs() throws BytecodeException {
      return list(
          bReference(bIntType(), 1), bReference(bIntType(), 2), bReference(bStringType(), 1));
    }
  }

  @Test
  void reference_can_be_read_back_by_hash() throws Exception {
    var reference = bReference(bIntType(), 123);
    assertThat(exprDbOther().get(reference.hash())).isEqualTo(reference);
  }

  @Test
  void const_read_back_by_hash_has_same_value() throws Exception {
    var reference = bReference(bIntType(), 123);
    assertThat(((BReference) exprDbOther().get(reference.hash())).index()).isEqualTo(bInt(123));
  }

  @Test
  void to_string() throws Exception {
    var reference = bReference(bIntType(), 123);
    assertThat(reference.toString()).isEqualTo("REFERENCE:Int(123)@" + reference.hash());
  }
}
