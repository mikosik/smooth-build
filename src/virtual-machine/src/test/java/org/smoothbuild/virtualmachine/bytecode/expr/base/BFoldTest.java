package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold.BSubExprs;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BFoldTest extends VmTestContext {
  @Test
  void creating_fold_with_non_array_fails() {
    assertCall(() -> bFold(bInt(), bInt(), bIntIdLambda()))
        .throwsException(new IllegalArgumentException(
            "`array.evaluationType()` should be `BArrayType` but is `BIntType`."));
  }

  @Test
  void creating_fold_with_non_lambda_fails() {
    assertCall(() -> bFold(bArray(bInt()), bInt(), bInt()))
        .throwsException(new IllegalArgumentException(
            "`folder.evaluationType()` should be `BLambdaType` but is `BIntType`."));
  }

  @Test
  void creating_fold_with_folder_which_parameter_count_is_different_than_two_fails() {
    assertCall(() -> provide()
            .bytecodeFactory()
            .fold(bArray(bInt()), bInt(), bLambda(list(bIntType()), bInt())))
        .throwsException(new IllegalArgumentException(
            "`folder.type()` should be `(Int,Int)->Int` but is `(Int)->Int`."));
  }

  @Test
  void creating_fold_with_folder_that_has_different_first_param_type_than_initial_type_fails() {
    assertCall(() -> provide()
            .bytecodeFactory()
            .fold(bArray(bInt()), bInt(), bLambda(list(bStringType(), bIntType()), bInt())))
        .throwsException(new IllegalArgumentException(
            "`folder.type()` should be `(Int,Int)->Int` but is `(String,Int)->Int`."));
  }

  @Test
  void
      creating_fold_with_folder_that_has_different_second_param_type_than_array_element_type_fails() {
    assertCall(() -> provide()
            .bytecodeFactory()
            .fold(bArray(bInt()), bInt(), bLambda(list(bIntType(), bStringType()), bInt())))
        .throwsException(new IllegalArgumentException(
            "`folder.type()` should be `(Int,Int)->Int` but is `(Int,String)->Int`."));
  }

  @Test
  void creating_fold_with_folder_that_has_different_result_type_than_initial_type_fails() {
    assertCall(() -> provide()
            .bytecodeFactory()
            .fold(bArray(bInt()), bInt(), bLambda(list(bIntType(), bIntType()), bString())))
        .throwsException(new IllegalArgumentException(
            "`folder.type()` should be `(Int,Int)->Int` but is `(Int,Int)->String`."));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BFold> {
    @Override
    protected List<BFold> equalExprs() throws BytecodeException {
      return list(
          bFold(bArray(bInt(0)), bInt(0), bii2iLambda()),
          bFold(bArray(bInt(0)), bInt(0), bii2iLambda()));
    }

    @Override
    protected List<BFold> nonEqualExprs() throws BytecodeException {
      return list(
          bFold(bArray(bInt(0)), bInt(0), bii2iLambda(0)),
          bFold(bArray(bInt(0)), bInt(0), bii2iLambda(1)),
          bFold(bArray(bInt(0)), bInt(1), bii2iLambda(0)),
          bFold(bArray(bInt(1)), bInt(0), bii2iLambda(0)));
    }
  }

  @Test
  void fold_can_be_read_back_by_hash() throws Exception {
    var fold = bFold(bArray(bInt()), bInt(), bii2iLambda());
    assertThat(exprDbOther().get(fold.hash())).isEqualTo(fold);
  }

  @Test
  void fold_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var array = bArray(bInt());
    var initial = bInt();
    var folder = bii2iLambda();
    var fold = bFold(array, initial, folder);
    assertThat(((BFold) exprDbOther().get(fold.hash())).subExprs())
        .isEqualTo(new BSubExprs(array, initial, folder));
  }

  @Test
  void to_string() throws Exception {
    var fold = bFold(bArray(bInt(17)), bInt(0), bii2iLambda());
    assertThat(fold.toString())
        .isEqualTo(
            """
        BFold(
          hash = e18ebb2da7e69fc73952e5676a8aa07176c8117607a46071eebdc21c1e27d72b
          evaluationType = Int
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
          initial = BInt(
            hash = 7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba
            type = Int
            value = 0
          )
          folder = BLambda(
            hash = 2b99b0d267c3175135b4f40519fae739993c42d3f151c56dfe41e81dc243ce99
            type = (Int,Int)->Int
            body = BInt(
              hash = b00b1c1fa3eb808c7898052142c9f222df725d8e5f3801b69326c4bc3c2d2809
              type = Int
              value = 7
            )
          )
        )""");
  }
}
