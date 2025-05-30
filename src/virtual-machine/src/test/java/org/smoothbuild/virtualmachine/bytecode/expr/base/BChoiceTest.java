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

public class BChoiceTest extends VmTestContext {
  @Test
  void setting_chosen_with_wrong_type_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(0);
          bChoice(type, index, bBlob());
        })
        .throwsException(
            new IllegalArgumentException("`chosen.type()` should be `String` but is `Blob`."));
  }

  @Test
  void setting_index_with_index_out_of_bounds_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(2);
          bChoice(type, index, bBlob());
        })
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void setting_chosen_to_null_throws_exception() {
    assertCall(() -> {
          BChoiceType type = bChoiceType(bStringType(), bBlobType());
          BInt index = bInt(0);
          bChoice(type, index, null);
        })
        .throwsException(NullPointerException.class);
  }

  @Test
  void setting_index_to_null_throws_exception() {
    assertCall(() -> {
          BChoiceType type = bChoiceType(bStringType(), bBlobType());
          bChoice(type, null, bString());
        })
        .throwsException(NullPointerException.class);
  }

  @Test
  void type() throws Exception {
    var type = bChoiceType(bStringType(), bBlobType());
    BInt index = bInt(0);
    var choice = bChoice(type, index, bString("7"));
    assertThat(choice.kind()).isEqualTo(type);
  }

  @Test
  void members_contains_object_passed_to_builder() throws Exception {
    var choice = bChoice();
    assertThat(choice.members()).isEqualTo(new BChoice.BSubExprs(bInt(0), bString("7")));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BChoice> {
    @Override
    protected List<BChoice> equalExprs() throws BytecodeException {
      return list(bChoice(), bChoice());
    }

    @Override
    protected List<BChoice> nonEqualExprs() throws BytecodeException {
      var type1 = bChoiceType(bStringType());
      var type2 = bChoiceType(bStringType(), bIntType());
      BInt index = bInt(1);
      BInt index1 = bInt(0);
      BInt index2 = bInt(0);
      BInt index3 = bInt(0);
      BInt index4 = bInt(0);
      return list(
          BChoiceTest.this.bChoice(type1, index4, bString("7")),
          BChoiceTest.this.bChoice(type1, index3, bString("8")),
          BChoiceTest.this.bChoice(type2, index2, bString("7")),
          BChoiceTest.this.bChoice(type2, index1, bString("8")),
          BChoiceTest.this.bChoice(type2, index, bInt(11)));
    }
  }

  @Test
  void choice_can_be_read_by_hash() throws Exception {
    var choice = bChoice();
    assertThat(exprDbOther().get(choice.hash())).isEqualTo(choice);
  }

  @Test
  void choice_read_by_hash_have_equal_members() throws Exception {
    var choice = bChoice();
    var choiceRead = (BChoice) exprDbOther().get(choice.hash());
    assertThat(choiceRead.members()).isEqualTo(choice.members());
  }

  @Test
  void to_string() throws Exception {
    assertThat(bChoice().toString())
        .isEqualTo(
            """
        BChoice(
          hash = 656815d265879fc6dbd78bdc187affd260d2b6af8bcfad10dc9b32f5c5ae9d4b
          type = {String|Int}
          members = [
            BInt(
              hash = 7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba
              type = Int
              value = 0
            )
            BString(
              hash = 1e1c0b706a66964d2af072b61122f728afb591ebfeacaec9ef1b846e00a16676
              type = String
              value = "7"
            )
          ]
        )""");
  }
}
