package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BMapTest extends VmTestContext {
  @Test
  void name() throws IOException {
    var bMap = bMap(bArray(bInt()), bIntIdLambda());
    assertThat(bMap.name()).isEqualTo("map");
  }

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
    assertCall(() -> bMap(bArray(bInt()), bii2iLambda()))
        .throwsException(new IllegalArgumentException(
            "`mapper.parameters.type()` should be `{Int}` but is `{Int,Int}`."));
  }

  @Test
  void creating_map_with_mapper_that_has_different_type_than_array_element_type_fails() {
    assertCall(() -> bMap(bArray(bString()), bIntIdLambda()))
        .throwsException(new IllegalArgumentException(
            "`mapper.parameters.type()` should be `{String}` but is `{Int}`."));
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
    var mapRead = (BMap) exprDbOther().get(map.hash());
    assertThat(mapRead.array()).isEqualTo(array);
    assertThat(mapRead.mapper()).isEqualTo(mapper);
  }

  @Test
  void array_is_cached() throws BytecodeException {
    var map = bMap(bArray(bInt()), bIntIdLambda());
    assertThat(map.array()).isSameInstanceAs(map.array());
  }

  @Test
  void mapper_is_cached() throws BytecodeException {
    var map = bMap(bArray(bInt()), bIntIdLambda());
    assertThat(map.mapper()).isSameInstanceAs(map.mapper());
  }

  @Test
  void to_string() throws Exception {
    var map = bMap(bArray(bInt()), bIntIdLambda());
    assertThat(map.toString()).isEqualTo("""
        BMap(
          hash = 2022d8e45d15a43dabc2e89e0defe020ecc200f765e3cff9b55d947737e69c42
          evaluationType = [Int]
          array = BArray(
            hash = 0bb233bbb42989f27846b6a121ff2570f12136aeababcf0d6fe1c57195bcc2a9
            type = [Int]
            elements = [
              BInt(
                hash = d6781a8034402f1bb1369df5042c4cc9d4d726044ba4ae8eb55efce43bad6ec5
                type = Int
                value = 17
              )
            ]
          )
          mapper = BLambda(
            hash = d7d0a5f6e57fdfafa74f1354c4b252514164daf8911ef9c7f1db2d84f691f350
            type = (Int)->Int
            body = BParamRef(
              hash = c6002edfb18e99dc95cd152fb6ade7c5a765e9544aa4ad5fc8df6844dd408caa
              evaluationType = Int
              index = 1
            )
          )
        )""");
  }
}
