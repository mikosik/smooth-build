package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BMapTest extends TestingVirtualMachine {
  @Test
  void creating_map_with_non_array_fails() {
    assertCall(() -> bMap(bInt(), bIntIdFunc()))
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
    assertCall(() -> bMap(bArray(bString()), bIntIdFunc()))
        .throwsException(new IllegalArgumentException(
            "`mapper.arguments.evaluationType()` should be `{String}` but is `{Int}`."));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BMap> {
    @Override
    protected List<BMap> equalExprs() throws BytecodeException {
      return list(bMap(bArray(bInt(0)), bIntIdFunc()), bMap(bArray(bInt(0)), bIntIdFunc()));
    }

    @Override
    protected List<BMap> nonEqualExprs() throws BytecodeException {
      return list(
          bMap(bArray(bInt(0)), bIntIdFunc()),
          bMap(bArray(bInt(1)), bIntIdFunc()),
          bMap(bArray(bString("abc")), bStringIdFunc()),
          bMap(bArray(bString("def")), bStringIdFunc()));
    }
  }

  @Test
  public void map_can_be_read_back_by_hash() throws Exception {
    var map = bMap(bArray(bInt()), bIntIdFunc());
    assertThat(exprDbOther().get(map.hash())).isEqualTo(map);
  }

  @Test
  public void map_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var array = bArray(bInt());
    var mapper = bIntIdFunc();
    var map = bMap(array, mapper);
    assertThat(((BMap) exprDbOther().get(map.hash())).subExprs())
        .isEqualTo(new BMap.SubExprsB(array, mapper));
  }

  @Test
  public void to_string() throws Exception {
    var map = bMap(bArray(bInt()), bIntIdFunc());
    assertThat(map.toString()).isEqualTo("MAP:[Int](???)@" + map.hash());
  }
}
