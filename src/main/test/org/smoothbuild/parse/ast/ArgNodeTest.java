package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.testing.TestingContext;

public class ArgNodeTest extends TestingContext {
  @Test
  public void named_arg_has_name() {
    ArgNode arg = new ArgNode(0, "name", expr(stringType()), unknownLocation());
    assertThat(arg.hasName())
        .isTrue();
  }

  @Test
  public void nameless_arg_does_not_have_name() {
    ArgNode arg = new ArgNode(0, null, expr(stringType()), unknownLocation());
    assertThat(arg.hasName())
        .isFalse();
  }

  @Test
  public void nameless_arg_throws_exception_when_asked_for_name() {
    ArgNode arg = new ArgNode(0, null, expr(stringType()), unknownLocation());
    assertCall(arg::name)
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void sanitized_name_of_named_argument_is_equal_its_name() {
    ArgNode arg = new ArgNode(1, "name", null, unknownLocation());
    assertThat(arg.nameSanitized())
        .isEqualTo("name");
  }

  @Test
  public void sanitized_name_of_nameless_argument_is_equal_to_nameless() {
    ArgNode arg = new ArgNode(1, null, null, unknownLocation());
    assertThat(arg.nameSanitized())
        .isEqualTo("<nameless>");
  }

  @Test
  public void type_and_name_of_named_argument() {
    ArgNode arg = new ArgNode(1, "name", expr(stringType()), unknownLocation());
    arg.setType(stringType());
    assertThat(arg.typeAndName())
        .isEqualTo("String:" + "name");
  }

  @Test
  public void nameless_argument_to_string() {
    ArgNode arg = new ArgNode(1, null, expr(stringType()), unknownLocation());
    arg.setType(stringType());
    assertThat(arg.typeAndName())
        .isEqualTo("String:<nameless>");
  }

  private static ExprNode expr(Type type) {
    ExprNode expr = mock(ExprNode.class);
    when(expr.type()).thenReturn(type);
    return expr;
  }
}
