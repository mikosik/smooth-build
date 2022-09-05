package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprBTestCase;
import org.smoothbuild.testing.TestContext;

public class IfBTest extends TestContext {
  @Test
  public void creating_if_with_condition_not_being_bool_causes_exception() {
    assertCall(() -> ifB(intTB(), blobB(0), intB(1), intB(2)))
        .throwsException(new IllegalArgumentException(
            "`condition` component must evaluate to Bool but is `Blob`."));
  }

  @Test
  public void creating_if_with_then_clause_of_wrong_type_causes_exception() {
    assertCall(() -> ifB(intTB(), boolB(), blobB(), intB()))
        .throwsException(new IllegalArgumentException(
            "`then` component must evaluate to `Int` but is `Blob`."));
  }

  @Test
  public void creating_if_with_else_clause_of_wrong_type_causes_exception() {
    assertCall(() -> ifB(intTB(), boolB(), intB(), blobB()))
        .throwsException(new IllegalArgumentException(
            "`else` component must evaluate to `Int` but is `Blob`."));
  }

  @Test
  public void condition_getter() {
    var ifB = ifB(intTB(), boolB(true), intB(1), intB(2));
    assertThat(ifB.data().condition())
        .isEqualTo(boolB(true));
  }

  @Test
  public void then_getter() {
    var ifB = ifB(intTB(), boolB(true), intB(1), intB(2));
    assertThat(ifB.data().then())
        .isEqualTo(intB(1));
  }

  @Test
  public void else_getter() {
    var ifB = ifB(intTB(), boolB(true), intB(1), intB(2));
    assertThat(ifB.data().else_())
        .isEqualTo(intB(2));
  }

  @Nested
  class _equals_hash_hashcode extends ExprBTestCase<IfB> {
    @Override
    protected List<IfB> equalExprs() {
      return list(
          ifB(intTB(), boolB(true), intB(1), intB(2)),
          ifB(intTB(), boolB(true), intB(1), intB(2))
      );
    }

    @Override
    protected List<IfB> nonEqualExprs() {
      return list(
          ifB(intTB(), boolB(true), intB(1), intB(2)),
          ifB(intTB(), boolB(true), intB(1), intB(9)),
          ifB(intTB(), boolB(true), intB(9), intB(2)),
          ifB(intTB(), boolB(false), intB(1), intB(2))
      );
    }
  }

  @Test
  public void if_can_be_read_back_by_hash() {
    var condition = boolB(true);
    var then = intB(1);
    var else_ = intB(2);
    var ifB = ifB(intTB(), condition, then, else_);
    assertThat(bytecodeDbOther().get(ifB.hash()))
        .isEqualTo(ifB);
  }

  @Test
  public void map_read_back_by_hash_has_same_data() {
    var condition = boolB(true);
    var then = intB(1);
    var else_ = intB(2);
    var ifB = ifB(intTB(), condition, then, else_);
    var readIf = (IfB) bytecodeDbOther().get(ifB.hash());
    var readIfData = readIf.data();
    var ifData = ifB.data();

    assertThat(readIfData.condition())
        .isEqualTo(ifData.condition());
    assertThat(readIfData.then())
        .isEqualTo(ifData.then());
    assertThat(readIfData.else_())
        .isEqualTo(ifData.else_());
  }

  @Test
  public void to_string() {
    var condition = boolB(true);
    var then = intB(1);
    var else_ = intB(2);
    var ifB = ifB(intTB(), condition, then, else_);
    assertThat(ifB.toString())
        .isEqualTo("If:Int(???)@" + ifB.hash());
  }
}
