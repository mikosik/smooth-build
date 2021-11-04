package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.lang.base.type.TestingSTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.api.Type;

public class ArgNodeTest {
  @Test
  public void named_arg_has_name() {
    ArgNode arg = new ArgNode("name", expr(STRING), internal());
    assertThat(arg.declaresName())
        .isTrue();
  }

  @Test
  public void nameless_arg_does_not_have_name() {
    ArgNode arg = new ArgNode(null, expr(STRING), internal());
    assertThat(arg.declaresName())
        .isFalse();
  }

  @Test
  public void nameless_arg_throws_exception_when_asked_for_name() {
    ArgNode arg = new ArgNode(null, expr(STRING), internal());
    assertCall(arg::name)
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void sanitized_name_of_named_argument_is_equal_its_name() {
    ArgNode arg = new ArgNode("name", null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("name");
  }

  @Test
  public void sanitized_name_of_nameless_argument_is_equal_to_nameless() {
    ArgNode arg = new ArgNode(null, null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("<nameless>");
  }

  @Test
  public void type_and_name_of_named_argument() {
    ArgNode arg = new ArgNode("name", expr(STRING), internal());
    arg.setType(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:" + "name");
  }

  @Test
  public void nameless_argument_to_string() {
    ArgNode arg = new ArgNode(null, expr(STRING), internal());
    arg.setType(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:<nameless>");
  }

  private static ExprNode expr(Type type) {
    ExprNode expr = mock(ExprNode.class);
    when(expr.type()).thenReturn(Optional.of(type));
    return expr;
  }
}
