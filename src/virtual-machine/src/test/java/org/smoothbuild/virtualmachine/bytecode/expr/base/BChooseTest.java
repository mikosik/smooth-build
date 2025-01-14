package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose.BSubExprs;
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
    assertCall(() -> exprDb().newChoose(choice, bTuple(bs2iLambda())))
        .throwsException(
            new IllegalArgumentException("`handlers.evaluationType().elements().size()` == 1 "
                + "must be equal `choice.evaluationType().alternatives().size()` == 2."));
  }

  @Test
  void creating_choose_with_handlers_containing_not_lambda_causes_exception()
      throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newChoose(choice, bTuple(bs2iLambda(), bInt(7))))
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
    assertCall(() -> exprDb().newChoose(choice, bTuple(bs2iLambda(), bs2iLambda())))
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
    assertCall(() -> exprDb().newChoose(choice, bTuple(bs2iLambda(), bi2sLambda())))
        .throwsException(new IllegalArgumentException(
            "`handlers.evaluationType()` have lambdas at index 0 and 1 "
                + "that have different result types: `Int` and `String`."));
  }

  @Test
  void sub_expressions_contains_choice_and_handlers() throws Exception {
    var handlers = bTuple(bs2iLambda(), bi2iLambda());
    var choose = exprDb().newChoose(bChoice(), handlers);
    assertThat(choose.subExprs()).isEqualTo(new BSubExprs(bChoice(), handlers));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BChoose> {
    @Override
    protected List<BChoose> equalExprs() throws BytecodeException {
      return list(
          bChoose(bChoice(), bTuple(bs2iLambda(), bi2iLambda())),
          bChoose(bChoice(), bTuple(bs2iLambda(), bi2iLambda())));
    }

    @Override
    protected List<BChoose> nonEqualExprs() throws BytecodeException {
      var type = bChoiceType(bStringType(), bIntType());
      var choice1 = exprDb().newChoice(type, bInt(0), bString("7"));
      var choice2 = exprDb().newChoice(type, bInt(1), bInt(8));
      return list(
          bChoose(choice1, bTuple(bs2iLambda(), bi2iLambda(11))),
          bChoose(choice1, bTuple(bs2iLambda(), bi2iLambda(12))),
          bChoose(choice2, bTuple(bs2iLambda(), bi2iLambda(11)))
      );
    }
  }

  @Test
  void choose_can_be_read_back_by_hash() throws Exception {
    var choose = bChoose(bChoice(), bTuple(bs2iLambda(), bi2iLambda()));
    assertThat(exprDbOther().get(choose.hash())).isEqualTo(choose);
  }

  @Test
  void choose_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var choice = bChoice();
    var handlers = bTuple(bs2iLambda(), bi2iLambda());
    var choose = bChoose(choice, handlers);
    assertThat(((BChoose) exprDbOther().get(choose.hash())).subExprs())
        .isEqualTo(new BSubExprs(choice, handlers));
  }

  @Test
  void to_string() throws Exception {
    var choose = bChoose(bChoice(), bTuple(bs2iLambda(), bi2iLambda()));
    assertThat(choose.toString()).isEqualTo("CHOOSE:Int(???)@" + choose.hash());
  }
}
