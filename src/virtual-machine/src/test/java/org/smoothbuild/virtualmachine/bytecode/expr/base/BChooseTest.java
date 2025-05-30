package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BChooseTest extends VmTestContext {
  @Test
  void setting_chosen_with_wrong_type_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(0);
          bChoose(type, index, bBlob());
        })
        .throwsException(new IllegalArgumentException(
            "`chosen.evaluationType()` should be `String` but is `Blob`."));
  }

  @Test
  void setting_index_with_index_out_of_bounds_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(2);
          bChoose(type, index, bBlob());
        })
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void setting_chosen_to_null_throws_exception() {
    assertCall(() -> {
          BChoiceType choiceType = bChoiceType(bStringType(), bBlobType());
          BInt index = bInt(0);
          bChoose(choiceType, index, null);
        })
        .throwsException(NullPointerException.class);
  }

  @Test
  void setting_index_to_null_throws_exception() {
    assertCall(() -> {
          BChoiceType choiceType = bChoiceType(bStringType(), bBlobType());
          bChoose(choiceType, null, bString());
        })
        .throwsException(NullPointerException.class);
  }

  @Test
  void kind() throws Exception {
    var type = bChoiceType(bStringType(), bBlobType());
    BInt index = bInt(0);
    var choice = bChoose(type, index, bString("7"));
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
      BInt index = bInt(1);
      BInt index1 = bInt(0);
      BInt index2 = bInt(0);
      BInt index3 = bInt(0);
      BInt index4 = bInt(0);
      return list(
          BChooseTest.this.bChoose(type1, index4, bString("7")),
          BChooseTest.this.bChoose(type1, index3, bString("8")),
          BChooseTest.this.bChoose(type2, index2, bString("7")),
          BChooseTest.this.bChoose(type2, index1, bString("8")),
          BChooseTest.this.bChoose(type2, index, bInt(11)));
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
    assertThat(choose.toString())
        .isEqualTo(
            """
        BChoose(
          hash = caec4416a98ec12639f2b26ee748efcb87b40207d1061ad0487eee1a1398b3f5
          evaluationType = {String|Int}
          chosen = BString(
            hash = 1e1c0b706a66964d2af072b61122f728afb591ebfeacaec9ef1b846e00a16676
            type = String
            value = "7"
          )
          index = BInt(
            hash = 7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba
            type = Int
            value = 0
          )
        )""");
  }
}
