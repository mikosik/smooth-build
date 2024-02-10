package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.AbstractExprBTestSuite;

public class StringBTest extends TestContext {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void type_of_string_is_string_type() throws Exception {
    assertThat(stringB(string).category()).isEqualTo(stringTB());
  }

  @Test
  public void to_j_returns_java_string() throws Exception {
    assertThat(stringB(string).toJ()).isEqualTo(string);
  }

  @Test
  public void to_j_returns_empty_java_string_for_empty_str() throws Exception {
    assertThat(stringB("").toJ()).isEqualTo("");
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<StringB> {
    @Override
    protected List<StringB> equalExprs() throws BytecodeException {
      return list(stringB("abc"), stringB("abc"));
    }

    @Override
    protected List<StringB> nonEqualExprs() throws BytecodeException {
      return list(stringB(""), stringB("abc"), stringB("ABC"), stringB(" abc"), stringB("abc "));
    }
  }

  @Test
  public void str_can_be_read_back_by_hash() throws Exception {
    var stringB = stringB(string);
    assertThat(bytecodeDbOther().get(stringB.hash())).isEqualTo(stringB);
  }

  @Test
  public void str_read_back_by_hash_has_same_to_j() throws Exception {
    StringB stringB = stringB(string);
    assertThat(((StringB) bytecodeDbOther().get(stringB.hash())).toJ()).isEqualTo(string);
  }

  @Test
  public void to_string_contains_string_value() throws Exception {
    StringB stringB = stringB(string);
    assertThat(stringB.toString()).isEqualTo("""
            "my string"@""" + stringB.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() throws Exception {
    StringB stringB = stringB("123456789012345678901234567890");
    assertThat(stringB.toString())
        .isEqualTo("""
            "12345678901234567890123456...@""" + stringB.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() throws Exception {
    StringB stringB = stringB("\t \b \n \r \f \" \\");
    assertThat(stringB.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\"@""" + stringB.hash());
  }
}
