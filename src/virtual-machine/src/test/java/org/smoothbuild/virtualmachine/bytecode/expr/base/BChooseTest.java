package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect.BSubExprs;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BChooseTest extends VmTestContext {
  @Test
  void creating_choose_with_handlers_not_evaluating_to_tuple_causes_exception()
      throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newChoose(choice, bInt(7)))
        .throwsException(new IllegalArgumentException(
            "`handlers.evaluationType()` should be `BTupleType` but is `BIntType`."));
  }

  @Test
  void creating_choose_with_handlers_size_different_than_choice_alternative_size_causes_exception()
      throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newChoose(choice, bTuple(stringToIntLambda())))
        .throwsException(
            new IllegalArgumentException("`handlers.evaluationType().elements().size()` == 1 "
                + "must be equal `choice.evaluationType().alternatives().size()` == 2."));
  }

  @Test
  void creating_choose_with_handlers_containing_not_lambda_causes_exception()
      throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newChoose(choice, bTuple(stringToIntLambda(), bInt(7))))
        .throwsException(
            new IllegalArgumentException(
                "`alternatives.evaluationType()` is tuple with element at index 1 not equal to lambda type"));
  }

  @Test
  void
      creating_choose_with_handlers_containing_lambda_with_parameter_type_not_equal_to_expected_causes_exception()
          throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newChoose(choice, bTuple(stringToIntLambda(), stringToIntLambda())))
        .throwsException(
            new IllegalArgumentException(
                "`handlers.evaluationType()` is tuple with element at index 1 being lambda with parameters `(String)` but expected `(Int)`."));
  }

  @Test
  void
      creating_choose_with_handlers_containing_lambda_with_different_result_types_causes_exception()
          throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newChoose(choice, bTuple(stringToIntLambda(), intToStringLambda())))
        .throwsException(new IllegalArgumentException(
            "`handlers.evaluationType()` have lambdas at index 0 and 1 "
                + "that have different result types: `Int` and `String`."));
  }

  /// TODO continue here
  ///
  @Test
  void creating_select_with_too_great_index_causes_exception() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    assertCall(() -> bSelect(tuple, bInt(2)).kind())
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void creating_select_with_index_lower_than_zero_causes_exception() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    assertCall(() -> bSelect(tuple, bInt(-1)).kind())
        .throwsException(new IndexOutOfBoundsException("index (-1) must not be negative"));
  }

  @Test
  void sub_expressions_contains_tuple_and_index() throws Exception {
    var selectable = bTuple(bInt(7));
    var index = bInt(0);
    assertThat(bSelect(selectable, index).subExprs()).isEqualTo(new BSubExprs(selectable, index));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BSelect> {
    @Override
    protected List<BSelect> equalExprs() throws BytecodeException {
      return list(
          bSelect(bTuple(bInt(7), bString("abc")), bInt(0)),
          bSelect(bTuple(bInt(7), bString("abc")), bInt(0)));
    }

    @Override
    protected List<BSelect> nonEqualExprs() throws BytecodeException {
      return list(
          bSelect(bTuple(bInt(1)), bInt(0)),
          bSelect(bTuple(bInt(2)), bInt(0)),
          bSelect(bTuple(bInt(2), bInt(2)), bInt(0)),
          bSelect(bTuple(bInt(2), bInt(2)), bInt(1)),
          bSelect(bTuple(bInt(2), bInt(7)), bInt(0)),
          bSelect(bTuple(bInt(7), bInt(2)), bInt(0)));
    }
  }

  @Test
  void select_can_be_read_back_by_hash() throws Exception {
    var tuple = bAnimal("rabbit", 7);
    var select = bSelect(tuple, bInt(0));
    assertThat(exprDbOther().get(select.hash())).isEqualTo(select);
  }

  @Test
  void select_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var selectable = bAnimal();
    var index = bInt(0);
    var select = bSelect(selectable, index);
    assertThat(((BSelect) exprDbOther().get(select.hash())).subExprs())
        .isEqualTo(new BSubExprs(selectable, index));
  }

  @Test
  void to_string() throws Exception {
    var select = bSelect(bAnimal(), bInt(0));
    assertThat(select.toString()).isEqualTo("SELECT:String(???)@" + select.hash());
  }

  private BLambda stringToIntLambda() throws BytecodeException {
    return bLambda(list(bStringType()), bInt(7));
  }

  private BLambda intToStringLambda() throws BytecodeException {
    return bLambda(list(bIntType()), bString("a"));
  }
}
