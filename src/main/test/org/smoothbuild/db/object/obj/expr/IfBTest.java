package org.smoothbuild.db.object.obj.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.ObjBTestCase;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.type.expr.IfCB;
import org.smoothbuild.testing.TestingContext;

public class IfBTest extends TestingContext {
  @Nested
  class _infer_type_of_if {
    @Test
    public void with_both_clauses_having_same_type() {
      assertThat(ifB(boolB(true), intB(1), intB(2)).cat())
          .isEqualTo(ifCB(intTB()));
    }

    @Test
    public void with_then_clauses_being_subtype_of_else_clause() {
      assertThat(ifB(boolB(true), arrayB(nothingTB()), arrayB(stringTB())).cat())
          .isEqualTo(ifCB(arrayTB(stringTB())));
    }

    @Test
    public void with_else_clauses_being_subtype_of_then_clause() {
      assertThat(ifB(boolB(true), arrayB(stringTB()), arrayB(nothingTB())).cat())
          .isEqualTo(ifCB(arrayTB(stringTB())));
    }
  }

  @Test
  public void then_clauses_can_be_subtype_of_else_clause() {
    var ifCB = ifCB(arrayTB(stringTB()));
    var then = arrayB(stringTB());
    var else_ = arrayB(nothingTB());
    test_clauses(ifCB, then, else_);
  }

  @Test
  public void else_clauses_can_be_subtype_of_then_clause() {
    var then = arrayB(stringTB());
    var else_ = arrayB(nothingTB());
    var ifCB = ifCB(arrayTB(stringTB()));
    test_clauses(ifCB, then, else_);
  }

  private void test_clauses(IfCB ifCB, ArrayB then, ArrayB else_) {
    var ifB = ifB(boolB(true), then, else_);
    assertThat(ifB.cat())
        .isEqualTo(ifCB);
    assertThat(ifB.data().then())
        .isEqualTo(then);
    assertThat(ifB.data().else_())
        .isEqualTo(else_);
  }

  @Test
  public void creating_if_with_condition_not_being_bool_causes_exception() {
    assertCall(() -> ifB(blobB(0), intB(1), intB(2)))
        .throwsException(new IllegalArgumentException(
            "`condition` component must evaluate to BoolH but is `Blob`."));
  }

  @Test
  public void condition_getter() {
    var ifH = ifB(boolB(true), intB(1), intB(2));
    assertThat(ifH.data().condition())
        .isEqualTo(boolB(true));
  }

  @Test
  public void then_getter() {
    var ifH = ifB(boolB(true), intB(1), intB(2));
    assertThat(ifH.data().then())
        .isEqualTo(intB(1));
  }

  @Test
  public void else_getter() {
    var ifH = ifB(boolB(true), intB(1), intB(2));
    assertThat(ifH.data().else_())
        .isEqualTo(intB(2));
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<IfB> {
    @Override
    protected List<IfB> equalValues() {
      return list(
          ifB(boolB(true), intB(1), intB(2)),
          ifB(boolB(true), intB(1), intB(2))
      );
    }

    @Override
    protected List<IfB> nonEqualValues() {
      return list(
          ifB(boolB(true), intB(1), intB(2)),
          ifB(boolB(true), intB(1), intB(9)),
          ifB(boolB(true), intB(9), intB(2)),
          ifB(boolB(false), intB(1), intB(2))
      );
    }
  }

  @Test
  public void if_can_be_read_back_by_hash() {
    var condition = boolB(true);
    var then = intB(1);
    var else_ = intB(2);
    var ifH = ifB(condition, then, else_);
    assertThat(byteDbOther().get(ifH.hash()))
        .isEqualTo(ifH);
  }

  @Test
  public void invoke_read_back_by_hash_has_same_data() {
    var condition = boolB(true);
    var then = intB(1);
    var else_ = intB(2);
    var ifH = ifB(condition, then, else_);
    var readIf = (IfB) byteDbOther().get(ifH.hash());
    var readIfData = readIf.data();
    var ifData = ifH.data();

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
    var ifH = ifB(condition, then, else_);
    assertThat(ifH.toString())
        .isEqualTo("If:Int(???)@" + ifH.hash());
  }
}
