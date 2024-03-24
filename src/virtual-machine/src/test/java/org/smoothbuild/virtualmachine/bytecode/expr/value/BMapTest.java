package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BMapTest extends TestingVirtualMachine {
  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BMap> {
    @Override
    protected List<BMap> equalExprs() throws BytecodeException {
      return list(bMap(bIntType(), bStringType()), bMap(bIntType(), bStringType()));
    }

    @Override
    protected List<BMap> nonEqualExprs() throws BytecodeException {
      return list(
          bMap(bIntType(), bBoolType()),
          bMap(bIntType(), bStringType()),
          bMap(bStringType(), bStringType()));
    }
  }

  @Test
  public void map_can_be_read_back_by_hash() throws Exception {
    var map = bMap(bIntType(), bStringType());
    assertThat(exprDbOther().get(map.hash())).isEqualTo(map);
  }

  @Test
  public void to_string() throws Exception {
    var map = bMap(bIntType(), bStringType());
    assertThat(map.toString()).isEqualTo("MapFunc(([String],(String)->Int)->[Int])@" + map.hash());
  }
}
