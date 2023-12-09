package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class MapFuncBTest extends TestContext {
  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<MapFuncB> {
    @Override
    protected List<MapFuncB> equalExprs() {
      return list(mapFuncB(intTB(), stringTB()), mapFuncB(intTB(), stringTB()));
    }

    @Override
    protected List<MapFuncB> nonEqualExprs() {
      return list(
          mapFuncB(intTB(), boolTB()),
          mapFuncB(intTB(), stringTB()),
          mapFuncB(stringTB(), stringTB()));
    }
  }

  @Test
  public void map_can_be_read_back_by_hash() {
    var mapB = mapFuncB(intTB(), stringTB());
    assertThat(bytecodeDbOther().get(mapB.hash())).isEqualTo(mapB);
  }

  @Test
  public void to_string() {
    var mapB = mapFuncB(intTB(), stringTB());
    assertThat(mapB.toString())
        .isEqualTo("MapFunc(([String],(String)->Int)->[Int])@" + mapB.hash());
  }
}
