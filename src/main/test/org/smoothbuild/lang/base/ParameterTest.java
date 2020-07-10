package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.parse.expr.Expression;
import org.smoothbuild.testing.TestingContext;

public class ParameterTest extends TestingContext {
  private final ConcreteType string = stringType();
  private final String name = "name";
  private Parameter parameter;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new Parameter(0, null, name, mock(Expression.class), internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new Parameter(0, string, null, mock(Expression.class), internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void index_getter() {
    parameter = new Parameter(33, string, name, mock(Expression.class), internal());
    assertThat(parameter.index())
        .isEqualTo(33);
  }

  @Test
  public void type_getter() {
    parameter = new Parameter(0, string, name, mock(Expression.class), internal());
    assertThat(parameter.type())
        .isEqualTo(string);
  }

  @Test
  public void name_getter() {
    parameter = new Parameter(0, string, name, mock(Expression.class), internal());
    assertThat(parameter.name())
        .isEqualTo(name);
  }

  @Test
  public void parameter_without_default() {
    parameter = new Parameter(0, string, name, null, internal());
    assertThat(parameter.hasDefaultValue())
        .isFalse();
  }

  @Test
  public void parameter_with_default_value() {
    parameter = new Parameter(0, string, name, mock(Expression.class), internal());
    assertThat(parameter.hasDefaultValue())
        .isTrue();
  }

  @Test
  public void to_string() {
    parameter = new Parameter(0, string, name, mock(Expression.class), internal());
    assertThat(parameter.toString())
        .isEqualTo("Param(String: name)");
  }
}
