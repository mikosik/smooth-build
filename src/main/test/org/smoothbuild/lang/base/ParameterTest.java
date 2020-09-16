package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.expr.Expression;

public class ParameterTest {
  private final String name = "name";
  private Parameter parameter;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new Parameter(0, null, name, Optional.empty(), internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new Parameter(0, STRING, null, Optional.empty(), internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void index_getter() {
    parameter = new Parameter(33, STRING, name, Optional.empty(), internal());
    assertThat(parameter.index())
        .isEqualTo(33);
  }

  @Test
  public void type_getter() {
    parameter = new Parameter(0, STRING, name, Optional.empty(), internal());
    assertThat(parameter.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    parameter = new Parameter(0, STRING, name, Optional.empty(), internal());
    assertThat(parameter.name())
        .isEqualTo(name);
  }

  @Test
  public void parameter_without_default() {
    parameter = new Parameter(0, STRING, name, Optional.empty(), internal());
    assertThat(parameter.hasDefaultValue())
        .isFalse();
  }

  @Test
  public void parameter_with_default_value() {
    parameter = new Parameter(0, STRING, name, Optional.of(mock(Expression.class)), internal());
    assertThat(parameter.hasDefaultValue())
        .isTrue();
  }

  @Test
  public void to_string() {
    parameter = new Parameter(0, STRING, name, Optional.of(mock(Expression.class)), internal());
    assertThat(parameter.toString())
        .isEqualTo("Parameter(`String name`)");
  }
}
