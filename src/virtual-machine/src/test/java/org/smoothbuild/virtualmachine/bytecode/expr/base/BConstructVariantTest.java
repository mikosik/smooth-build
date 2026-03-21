package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BConstructVariantTest extends VmTestContext {
  @Test
  void name() throws BytecodeException {
    assertThat(bConstructVariant().name()).isEqualTo("constructVariant");
  }

  @Test
  void setting_choice_with_wrong_type_throws_exception() throws BytecodeException {
    var type = bVariantType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(0);
          bConstructVariant(type, index, bBlob());
        })
        .throwsException(new IllegalArgumentException(
            "`choice.evaluationType()` should be `String` but is `Blob`."));
  }

  @Test
  void setting_index_with_index_out_of_bounds_throws_exception() throws BytecodeException {
    var type = bVariantType(bStringType(), bBlobType());
    assertCall(() -> {
          BInt index = bInt(2);
          bConstructVariant(type, index, bBlob());
        })
        .throwsException(new IndexOutOfBoundsException("index (2) must be less than size (2)"));
  }

  @Test
  void kind() throws Exception {
    var type = bVariantType(bStringType(), bBlobType());
    BInt index = bInt(0);
    var constructVariant = bConstructVariant(type, index, bString("7"));
    assertThat(constructVariant.kind()).isEqualTo(bConstructVariantKind(type));
  }

  @Test
  void subExprs_contains_object_passed_to_builder() throws Exception {
    var type = bVariantType(bStringType(), bIntType());
    var constructVariant = bConstructVariant(type, bInt(0), bString("7"));
    assertThat(constructVariant.index()).isEqualTo(bInt(0));
    assertThat(constructVariant.choice()).isEqualTo(bString("7"));
  }

  @Test
  void index_is_cached() throws BytecodeException {
    var constructVariant = bConstructVariant();
    assertThat(constructVariant.index()).isSameInstanceAs(constructVariant.index());
  }

  @Test
  void choice_is_cached() throws BytecodeException {
    var constructVariant = bConstructVariant();
    assertThat(constructVariant.choice()).isSameInstanceAs(constructVariant.choice());
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BConstructVariant> {
    @Override
    protected List<BConstructVariant> equalExprs() throws BytecodeException {
      return list(bConstructVariant(), bConstructVariant());
    }

    @Override
    protected List<BConstructVariant> nonEqualExprs() throws BytecodeException {
      var type1 = bVariantType(bStringType());
      var type2 = bVariantType(bStringType(), bIntType());
      BInt index = bInt(1);
      BInt index1 = bInt(0);
      BInt index2 = bInt(0);
      BInt index3 = bInt(0);
      BInt index4 = bInt(0);
      return list(
          bConstructVariant(type1, index4, bString("7")),
          bConstructVariant(type1, index3, bString("8")),
          bConstructVariant(type2, index2, bString("7")),
          bConstructVariant(type2, index1, bString("8")),
          bConstructVariant(type2, index, bInt(11)));
    }
  }

  @Test
  void choose_can_be_read_by_hash() throws Exception {
    var choose = bConstructVariant();
    assertThat(exprDbOther().get(choose.hash())).isEqualTo(choose);
  }

  @Test
  void choose_read_by_hash_have_equal_nodes() throws Exception {
    var choose = bConstructVariant();
    var ChooseRead = (BConstructVariant) exprDbOther().get(choose.hash());
    assertThat(ChooseRead.index()).isEqualTo(choose.index());
    assertThat(ChooseRead.choice()).isEqualTo(choose.choice());
  }

  @Test
  void to_string() throws Exception {
    var choose = bConstructVariant();
    assertThat(choose.toString()).isEqualTo("""
        BConstructVariant(
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
