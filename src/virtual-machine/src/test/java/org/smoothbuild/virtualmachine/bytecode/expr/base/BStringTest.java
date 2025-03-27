package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BStringTest extends VmTestContext {
  private final String string = "my string";

  @Test
  void type_of_string_is_string_type() throws Exception {
    assertThat(bString(string).kind()).isEqualTo(bStringType());
  }

  @Test
  void to_j_returns_java_string() throws Exception {
    assertThat(bString(string).toJavaString()).isEqualTo(string);
  }

  @Test
  void to_j_returns_empty_java_string_for_empty_str() throws Exception {
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
  void str_can_be_read_back_by_hash() throws Exception {
    var string = bString(this.string);
    assertThat(exprDbOther().get(string.hash())).isEqualTo(string);
  }

  @Test
  void str_read_back_by_hash_has_same_to_j() throws Exception {
    var string = bString(this.string);
    assertThat(((BString) exprDbOther().get(string.hash())).toJavaString()).isEqualTo(this.string);
  }

  @Test
  void to_string_contains_string_value() throws Exception {
    var string = bString(this.string);
    assertThat(string.toString())
        .isEqualTo(
            """
        BString(
          hash = 379067128c589bc435ecef2c681b1304f2b1dc1178aaf14b0cad64f106a530d8
          type = String
          value = "my string"
        )""");
  }

  @Test
  void to_string_contains_shortened_string_value_for_long_strings() throws Exception {
    var string = bString("123456789012345678901234567890");
    assertThat(string.toString())
        .isEqualTo(
            """
            BString(
              hash = 5f251f596780439f80762ea535ec451732453d353dba40585b96179dd72a35c5
              type = String
              value = "123456789012345678901234567890"
            )""");
  }

  @Test
  void to_string_contains_properly_escaped_special_characters() throws Exception {
    var string = bString("\t \b \n \r \f \" \\");
    assertThat(string.toString())
        .isEqualTo(
            """
            BString(
              hash = c1e55a8bf36d2593479401421e0f26ee2ad378886f8c256c091cafb96c0ae83d
              type = String
              value = "\\t \\b \\n \\r \\f \\" \\\\"
            )""");
  }
}
