package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.define.Loc.internal;
import static org.smoothbuild.testing.TestingContext.loc;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.STRING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.type.TypeS;

public class ArgNTest {
  @Test
  public void named_arg_has_name() {
    ArgN arg = new ArgN("name", expr(STRING), internal());
    assertThat(arg.declaresName())
        .isTrue();
  }

  @Test
  public void nameless_arg_does_not_have_name() {
    ArgN arg = new ArgN(null, expr(STRING), internal());
    assertThat(arg.declaresName())
        .isFalse();
  }

  @Test
  public void nameless_arg_throws_exception_when_asked_for_name() {
    ArgN arg = new ArgN(null, expr(STRING), internal());
    assertCall(arg::name)
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void sanitized_name_of_named_arg_is_equal_its_name() {
    ArgN arg = new ArgN("name", null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("name");
  }

  @Test
  public void sanitized_name_of_nameless_arg_is_equal_to_nameless() {
    ArgN arg = new ArgN(null, null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("<nameless>");
  }

  @Test
  public void type_and_name_of_named_arg() {
    ArgN arg = new ArgN("name", expr(STRING), internal());
    arg.setType(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:" + "name");
  }

  @Test
  public void nameless_arg_to_string() {
    ArgN arg = new ArgN(null, expr(STRING), internal());
    arg.setType(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:<nameless>");
  }

  private static ExprN expr(TypeS type) {
    RefN ref = new RefN("name", loc());
    ref.setType(type);
    return ref;
  }
}
