package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.define.Loc.internal;
import static org.smoothbuild.testing.TestingContext.loc;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.STRING;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.type.TypeS;

public class ExplicitArgNTest {
  @Test
  public void named_arg_has_name() {
    var arg = new ExplicitArgN("name", obj(STRING), internal());
    assertThat(arg.declaresName())
        .isTrue();
  }

  @Test
  public void nameless_arg_does_not_have_name() {
    var arg = new ExplicitArgN(null, obj(STRING), internal());
    assertThat(arg.declaresName())
        .isFalse();
  }

  @Test
  public void nameless_arg_throws_exception_when_asked_for_name() {
    var arg = new ExplicitArgN(null, obj(STRING), internal());
    assertCall(arg::name)
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void sanitized_name_of_named_arg_is_equal_its_name() {
    var arg = new ExplicitArgN("name", null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("name");
  }

  @Test
  public void sanitized_name_of_nameless_arg_is_equal_to_nameless() {
    var arg = new ExplicitArgN(null, null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("<nameless>");
  }

  @Test
  public void type_and_name_of_named_arg() {
    var arg = new ExplicitArgN("name", obj(STRING), internal());
    arg.setType(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:" + "name");
  }

  @Test
  public void nameless_arg_to_string() {
    var arg = new ExplicitArgN(null, obj(STRING), internal());
    arg.setType(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:<nameless>");
  }

  private static ObjN obj(TypeS type) {
    var ref = new RefN("name", loc());
    ref.setType(type);
    return ref;
  }
}
