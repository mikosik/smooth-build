package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Signature.signature;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.expr.Expression;

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
    parameter = new Parameter(0, BLOB, "blob", mock(Expression.class), internal());
    parameter2 = new Parameter(0, STRING, "string", mock(Expression.class), internal());
    assertThat(signature(STRING, "name", list(parameter, parameter2)).parameterTypes())
        .containsExactly(BLOB, STRING)
        .inOrder();
  }

  @Test
  public void to_string() {
    parameter = new Parameter(0, BLOB, "blob", mock(Expression.class), internal());
    parameter2 = new Parameter(0, STRING, "string", mock(Expression.class), internal());
    Signature signature = signature(STRING, "name", list(parameter, parameter2));
    assertThat(signature.toString())
        .isEqualTo(STRING.name() + " " + "name" + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
