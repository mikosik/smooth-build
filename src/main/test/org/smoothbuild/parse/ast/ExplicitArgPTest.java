package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Loc.internal;
import static org.smoothbuild.testing.TestingContext.loc;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.STRING;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.type.MonoTS;

public class ExplicitArgPTest {
  @Test
  public void named_arg_has_name() {
    var arg = new ExplicitArgP(Optional.of("name"), obj(STRING), internal());
    assertThat(arg.declaresName())
        .isTrue();
  }

  @Test
  public void nameless_arg_does_not_have_name() {
    var arg = new ExplicitArgP(Optional.empty(), obj(STRING), internal());
    assertThat(arg.declaresName())
        .isFalse();
  }

  @Test
  public void nameless_arg_throws_exception_when_asked_for_name() {
    var arg = new ExplicitArgP(Optional.empty(), obj(STRING), internal());
    assertCall(arg::name)
        .throwsException(NoSuchElementException.class);
  }

  @Test
  public void sanitized_name_of_named_arg_is_equal_its_name() {
    var arg = new ExplicitArgP(Optional.of("name"), null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("name");
  }

  @Test
  public void sanitized_name_of_nameless_arg_is_equal_to_nameless() {
    var arg = new ExplicitArgP(Optional.empty(), null, internal());
    assertThat(arg.nameSanitized())
        .isEqualTo("<nameless>");
  }

  @Test
  public void type_and_name_of_named_arg() {
    var arg = new ExplicitArgP(Optional.of("name"), obj(STRING), internal());
    arg.setTypeS(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:" + "name");
  }

  @Test
  public void nameless_arg_to_string() {
    var arg = new ExplicitArgP(Optional.empty(), obj(STRING), internal());
    arg.setTypeS(STRING);
    assertThat(arg.typeAndName())
        .isEqualTo("String:<nameless>");
  }

  private static ObjP obj(MonoTS type) {
    var ref = new RefP("name", loc());
    ref.setTypeS(type);
    return ref;
  }
}
