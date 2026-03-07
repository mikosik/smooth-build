package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BCreateVariantTest extends VmTestContext {
  @Test
  void name() throws BytecodeException {
    assertThat(bCreateVariant().name()).isEqualTo("createVariant");
  }

  @Test
  void setting_choice_with_wrong_type_throws_exception() throws BytecodeException {
    var type = bVariantType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(0);
          bCreateVariant(type, index, bBlob());
        })
        .throwsException(new IllegalArgumentException(
            "`choice.evaluationType()` should be `String` but is `Blob`."));
  }

  @Test
  void setting_index_with_index_out_of_bounds_throws_exception() throws BytecodeException {
    var type = bVariantType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(2);
          bCreateVariant(type, index, bBlob());
        })
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void kind() throws Exception {
    var type = bVariantType(bStringType(), bBlobType());
    BInt index = bInt(0);
    var createVariant = bCreateVariant(type, index, bString("7"));
    assertThat(createVariant.kind()).isEqualTo(bCreateVariantKind(type));
  }

  @Test
  void subExprs_contains_object_passed_to_builder() throws Exception {
    var type = bVariantType(bStringType(), bIntType());
    var createVariant = bCreateVariant(type, bInt(0), bString("7"));
    assertThat(createVariant.index()).isEqualTo(bInt(0));
    assertThat(createVariant.choice()).isEqualTo(bString("7"));
  }

  @Test
  void index_is_cached() throws BytecodeException {
    var createVariant = bCreateVariant();
    assertThat(createVariant.index()).isSameInstanceAs(createVariant.index());
  }

  @Test
  void choice_is_cached() throws BytecodeException {
    var createVariant = bCreateVariant();
    assertThat(createVariant.choice()).isSameInstanceAs(createVariant.choice());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BCreateVariant> {
    @Override
    protected List<BCreateVariant> equalExprs() throws BytecodeException {
      return list(bCreateVariant(), bCreateVariant());
    }

    @Override
    protected List<BCreateVariant> nonEqualExprs() throws BytecodeException {
      var type1 = bVariantType(bStringType());
      var type2 = bVariantType(bStringType(), bIntType());
      BInt index = bInt(1);
      BInt index1 = bInt(0);
      BInt index2 = bInt(0);
      BInt index3 = bInt(0);
      BInt index4 = bInt(0);
      return list(
          bCreateVariant(type1, index4, bString("7")),
          bCreateVariant(type1, index3, bString("8")),
          bCreateVariant(type2, index2, bString("7")),
          bCreateVariant(type2, index1, bString("8")),
          bCreateVariant(type2, index, bInt(11)));
    }
  }

  @Test
  void choose_can_be_read_by_hash() throws Exception {
    var choose = bCreateVariant();
    assertThat(exprDbOther().get(choose.hash())).isEqualTo(choose);
  }

  @Test
  void choose_read_by_hash_have_equal_nodes() throws Exception {
    var choose = bCreateVariant();
    var ChooseRead = (BCreateVariant) exprDbOther().get(choose.hash());
    assertThat(ChooseRead.index()).isEqualTo(choose.index());
    assertThat(ChooseRead.choice()).isEqualTo(choose.choice());
  }

  @Test
  void to_string() throws Exception {
    var choose = bCreateVariant();
    assertThat(choose.toString()).isEqualTo("""
        BCreateVariant(
          hash = caec4416a98ec12639f2b26ee748efcb87b40207d1061ad0487eee1a1398b3f5
          evaluationType = {String|Int}
          choice = BString(
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
