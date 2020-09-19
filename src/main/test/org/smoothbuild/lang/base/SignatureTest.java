package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

public class SignatureTest {
  private Parameter parameter;
  private Parameter parameter2;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> signature(null, "name", list()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> signature(STRING, null, list()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_param_is_forbidden() {
    assertCall(() -> signature(STRING, "name", null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void parameter_types() {
    parameter = new Parameter(BLOB, "blob", empty(), internal());
    parameter2 = new Parameter(STRING, "string", empty(), internal());
    assertThat(signature(STRING, "name", list(parameter, parameter2)).parameterTypes())
        .containsExactly(BLOB, STRING)
        .inOrder();
  }

  @Test
  public void to_string() {
    parameter = new Parameter(BLOB, "blob", empty(), internal());
    parameter2 = new Parameter(STRING, "string", empty(), internal());
    Signature signature = signature(STRING, "name", list(parameter, parameter2));
    assertThat(signature.toString())
        .isEqualTo(STRING.name() + " " + "name" + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
