package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BStringTest extends TestingVirtualMachine {
  private final String string = "my string";
  private final String otherString = "my string 2";

  @Test
  public void type_of_string_is_string_type() throws Exception {
    assertThat(bString(string).kind()).isEqualTo(bStringType());
  }

  @Test
  public void to_j_returns_java_string() throws Exception {
    assertThat(bString(string).toJavaString()).isEqualTo(string);
  }

  @Test
  public void to_j_returns_empty_java_string_for_empty_str() throws Exception {
    assertThat(bString("").toJavaString()).isEqualTo("");
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BString> {
    @Override
    protected List<BString> equalExprs() throws BytecodeException {
      return list(bString("abc"), bString("abc"));
    }

    @Override
    protected List<BString> nonEqualExprs() throws BytecodeException {
      return list(bString(""), bString("abc"), bString("ABC"), bString(" abc"), bString("abc "));
    }
  }

  @Test
  public void str_can_be_read_back_by_hash() throws Exception {
    var string = bString(this.string);
    assertThat(exprDbOther().get(string.hash())).isEqualTo(string);
  }

  @Test
  public void str_read_back_by_hash_has_same_to_j() throws Exception {
    var string = bString(this.string);
    assertThat(((BString) exprDbOther().get(string.hash())).toJavaString()).isEqualTo(this.string);
  }

  @Test
  public void to_string_contains_string_value() throws Exception {
    var string = bString(this.string);
    assertThat(string.toString()).isEqualTo("""
            "my string"@""" + string.hash());
  }

  @Test
  public void to_string_contains_shortened_string_value_for_long_strings() throws Exception {
    var string = bString("123456789012345678901234567890");
    assertThat(string.toString())
        .isEqualTo("""
            "12345678901234567890123456...@""" + string.hash());
  }

  @Test
  public void to_string_contains_properly_escaped_special_characters() throws Exception {
    var string = bString("\t \b \n \r \f \" \\");
    assertThat(string.toString())
        .isEqualTo("""
            "\\t \\b \\n \\r \\f \\" \\\\"@""" + string.hash());
  }
}
