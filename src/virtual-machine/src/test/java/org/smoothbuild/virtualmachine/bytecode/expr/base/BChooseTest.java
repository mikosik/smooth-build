package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BChooseTest extends VmTestContext {
  @Test
  void setting_chosen_with_wrong_type_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> exprDb().newChoose(type, bInt(0), bBlob()))
        .throwsException(new IllegalArgumentException(
            "`chosen.evaluationType()` should be `String` but is `Blob`."));
  }

  @Test
  void setting_index_with_index_out_of_bounds_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> exprDb().newChoose(type, bInt(2), bBlob()))
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void setting_chosen_to_null_throws_exception() {
    assertCall(() -> exprDb().newChoose(bChoiceType(bStringType(), bBlobType()), bInt(0), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  void setting_index_to_null_throws_exception() {
    assertCall(() -> exprDb().newChoose(bChoiceType(bStringType(), bBlobType()), null, bString()))
        .throwsException(NullPointerException.class);
  }

  @Test
  void kind() throws Exception {
    var type = bChoiceType(bStringType(), bBlobType());
    var choice = exprDb().newChoose(type, bInt(0), bString("7"));
    assertThat(choice.kind()).isEqualTo(bChooseKind(type));
  }

  @Test
  void subExprs_contains_object_passed_to_builder() throws Exception {
    var choice = bChoose();
    assertThat(choice.subExprs()).isEqualTo(new BChoose.BSubExprs(bInt(0), bString("7")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BChoose> {
    @Override
    protected List<BChoose> equalExprs() throws BytecodeException {
      return list(bChoose(), bChoose());
    }

    @Override
    protected List<BChoose> nonEqualExprs() throws BytecodeException {
      var type1 = bChoiceType(bStringType());
      var type2 = bChoiceType(bStringType(), bIntType());
      return list(
          exprDb().newChoose(type1, bInt(0), bString("7")),
          exprDb().newChoose(type1, bInt(0), bString("8")),
          exprDb().newChoose(type2, bInt(0), bString("7")),
          exprDb().newChoose(type2, bInt(0), bString("8")),
          exprDb().newChoose(type2, bInt(1), bInt(11)));
    }
  }

  @Test
  void choose_can_be_read_by_hash() throws Exception {
    var choose = bChoose();
    assertThat(exprDbOther().get(choose.hash())).isEqualTo(choose);
  }

  @Test
  void choose_read_by_hash_have_equal_nodes() throws Exception {
    var choose = bChoose();
    var ChooseRead = (BChoose) exprDbOther().get(choose.hash());
    assertThat(ChooseRead.subExprs()).isEqualTo(choose.subExprs());
  }

  @Test
  void to_string() throws Exception {
    var choose = bChoose();
    assertThat(choose.toString()).isEqualTo("CHOOSE:{String|Int}(???)@" + choose.hash());
  }
}
