package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap.BSubExprs;
import org.smoothbuild.virtualmachine.testing.BytecodeTestContext;

public class BMapTest extends BytecodeTestContext {
  @Test
  void creating_map_with_non_array_fails() {
    assertCall(() -> bMap(bInt(), bIntIdLambda()))
        .throwsException(new IllegalArgumentException(
            "`array.evaluationType()` should be `BArrayType` but is `BIntType`."));
  }

  @Test
  void creating_map_with_non_lambda_fails() {
    assertCall(() -> bMap(bArray(bInt()), bInt()))
        .throwsException(new IllegalArgumentException(
            "`mapper.evaluationType()` should be `BLambdaType` but is `BIntType`."));
  }

  @Test
  void creating_map_with_mapper_which_parameter_count_is_different_than_one_fails() {
    assertCall(() -> bMap(bArray(bInt()), bLambda(list(bIntType(), bIntType()), bInt())))
        .throwsException(new IllegalArgumentException(
            "`mapper.arguments.evaluationType()` should be `{Int}` but is `{Int,Int}`."));
  }

  @Test
  void creating_map_with_mapper_that_has_different_type_than_array_element_type_fails() {
    assertCall(() -> bMap(bArray(bString()), bIntIdLambda()))
        .throwsException(new IllegalArgumentException(
            "`mapper.arguments.evaluationType()` should be `{String}` but is `{Int}`."));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BMap> {
    @Override
    protected List<BMap> equalExprs() throws BytecodeException {
      return list(bMap(bArray(bInt(0)), bIntIdLambda()), bMap(bArray(bInt(0)), bIntIdLambda()));
    }

    @Override
    protected List<BMap> nonEqualExprs() throws BytecodeException {
      return list(
          bMap(bArray(bInt(0)), bIntIdLambda()),
          bMap(bArray(bInt(1)), bIntIdLambda()),
          bMap(bArray(bString("abc")), bStringIdLambda()),
          bMap(bArray(bString("def")), bStringIdLambda()));
    }
  }

  @Test
  void map_can_be_read_back_by_hash() throws Exception {
    var map = bMap(bArray(bInt()), bIntIdLambda());
    assertThat(exprDbOther().get(map.hash())).isEqualTo(map);
  }

  @Test
  void map_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var array = bArray(bInt());
    var mapper = bIntIdLambda();
    var map = bMap(array, mapper);
    assertThat(((BMap) exprDbOther().get(map.hash())).subExprs())
        .isEqualTo(new BSubExprs(array, mapper));
  }

  @Test
  void to_string() throws Exception {
    var map = bMap(bArray(bInt()), bIntIdLambda());
    assertThat(map.toString()).isEqualTo("MAP:[Int](???)@" + map.hash());
  }
}
