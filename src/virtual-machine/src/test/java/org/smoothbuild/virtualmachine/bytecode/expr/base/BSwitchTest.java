package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch.BSubExprs;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BSwitchTest extends VmTestContext {
  @Test
  void creating_switch_with_handlers_size_different_than_choice_alternative_size_causes_exception()
      throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newSwitch(choice, bCombine(bs2iLambda())))
        .throwsException(
            new IllegalArgumentException("`handlers.evaluationType().elements().size()` == 1 "
                + "must be equal `choice.evaluationType().alternatives().size()` == 2."));
  }

  @Test
  void creating_switch_with_handlers_containing_not_lambda_causes_exception()
      throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newSwitch(choice, bCombine(bs2iLambda(), bInt(7))))
        .throwsException(
            new IllegalArgumentException(
                "`alternatives.evaluationType()` is tuple with element at index 1 not equal to lambda type"));
  }

  @Test
  void
      creating_switch_with_handlers_containing_lambda_with_parameter_type_not_equal_to_expected_causes_exception()
          throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newSwitch(choice, bCombine(bs2iLambda(), bs2iLambda())))
        .throwsException(new IllegalArgumentException("`handlers.evaluationType()` is tuple "
            + "with element at index 1 being lambda with parameters `(String)` "
            + "but expected `(Int)`."));
  }

  @Test
  void
      creating_switch_with_handlers_containing_lambda_with_different_result_types_causes_exception()
          throws BytecodeException {
    var type = bChoiceType(bStringType(), bIntType());
    var choice = bChoice(type, 0, bString("a"));
    assertCall(() -> exprDb().newSwitch(choice, bCombine(bs2iLambda(), bi2sLambda())))
        .throwsException(new IllegalArgumentException(
            "`handlers.evaluationType()` have lambdas at index 0 and 1 "
                + "that have different result types: `Int` and `String`."));
  }

  @Test
  void sub_expressions_contains_choice_and_handlers() throws Exception {
    var handlers = bCombine(bs2iLambda(), bi2iLambda());
    var switch_ = exprDb().newSwitch(bChoice(), handlers);
    assertThat(switch_.subExprs()).isEqualTo(new BSubExprs(bChoice(), handlers));
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BSwitch> {
    @Override
    protected List<BSwitch> equalExprs() throws BytecodeException {
      return list(
          bSwitch(bChoice(), bCombine(bs2iLambda(), bi2iLambda())),
          bSwitch(bChoice(), bCombine(bs2iLambda(), bi2iLambda())));
    }

    @Override
    protected List<BSwitch> nonEqualExprs() throws BytecodeException {
      var type = bChoiceType(bStringType(), bIntType());
      var choice1 = exprDb().newChoice(type, bInt(0), bString("7"));
      var choice2 = exprDb().newChoice(type, bInt(1), bInt(8));
      return list(
          bSwitch(choice1, bCombine(bs2iLambda(), bi2iLambda(11))),
          bSwitch(choice1, bCombine(bs2iLambda(), bi2iLambda(12))),
          bSwitch(choice2, bCombine(bs2iLambda(), bi2iLambda(11))));
    }
  }

  @Test
  void switch_can_be_read_back_by_hash() throws Exception {
    var switch_ = bSwitch(bChoice(), bCombine(bs2iLambda(), bi2iLambda()));
    assertThat(exprDbOther().get(switch_.hash())).isEqualTo(switch_);
  }

  @Test
  void switch_read_back_by_hash_has_same_sub_expressions() throws Exception {
    var choice = bChoice();
    var handlers = bCombine(bs2iLambda(), bi2iLambda());
    var switch_ = bSwitch(choice, handlers);
    assertThat(((BSwitch) exprDbOther().get(switch_.hash())).subExprs())
        .isEqualTo(new BSubExprs(choice, handlers));
  }

  @Test
  void to_string() throws Exception {
    var switch_ = bSwitch(bChoice(), bCombine(bs2iLambda(), bi2iLambda()));
    assertThat(switch_.toString())
        .isEqualTo(
            """
        BSwitch(
          hash = f614d35874aeaf5899d6fe657b35e5845e530ee0cda1d35a6c33ba0416e2bf64
          evaluationType = Int
          choice = BChoice(
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
          )
          handlers = BCombine(
            hash = 2a737302c93c91e983667dfe73b2f73f278d17ea8a8c954ff18b8001a68fc595
            evaluationType = {(String)->Int,(Int)->Int}
            items = [
              BLambda(
                hash = 97b03178cca9427f6e72d6647f13066901f24bb909d7a0d4c049a5430a65ac76
                type = (String)->Int
                body = BInt(
                  hash = b00b1c1fa3eb808c7898052142c9f222df725d8e5f3801b69326c4bc3c2d2809
                  type = Int
                  value = 7
                )
              )
              BLambda(
                hash = 76d31558eba1d3a2d1abc8d5bd329d5518a0430faa8763578c0fc8c0018ce231
                type = (Int)->Int
                body = BInt(
                  hash = b00b1c1fa3eb808c7898052142c9f222df725d8e5f3801b69326c4bc3c2d2809
                  type = Int
                  value = 7
                )
              )
            ]
          )
        )""");
  }
}
