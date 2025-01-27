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

public class BChoiceTest extends VmTestContext {
  @Test
  void setting_chosen_with_wrong_type_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> exprDb().newChoice(type, bInt(0), bBlob()))
        .throwsException(
            new IllegalArgumentException("`chosen.type()` should be `String` but is `Blob`."));
  }

  @Test
  void setting_index_with_index_out_of_bounds_throws_exception() throws BytecodeException {
    var type = bChoiceType(bStringType(), bBlobType());
    assertCall(() -> exprDb().newChoice(type, bInt(2), bBlob()))
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void setting_chosen_to_null_throws_exception() {
    assertCall(() -> exprDb().newChoice(bChoiceType(bStringType(), bBlobType()), bInt(0), null))
        .throwsException(NullPointerException.class);
  }

  @Test
  void setting_index_to_null_throws_exception() {
    assertCall(() -> exprDb().newChoice(bChoiceType(bStringType(), bBlobType()), null, bString()))
        .throwsException(NullPointerException.class);
  }

  @Test
  void type() throws Exception {
    var type = bChoiceType(bStringType(), bBlobType());
    var choice = exprDb().newChoice(type, bInt(0), bString("7"));
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
      return list(
          exprDb().newChoice(type1, bInt(0), bString("7")),
          exprDb().newChoice(type1, bInt(0), bString("8")),
          exprDb().newChoice(type2, bInt(0), bString("7")),
          exprDb().newChoice(type2, bInt(0), bString("8")),
          exprDb().newChoice(type2, bInt(1), bInt(11)));
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
    assertThat(bChoice().toString()).isEqualTo("{|0=>\"7\"|}@" + bChoice().hash());
  }
}
