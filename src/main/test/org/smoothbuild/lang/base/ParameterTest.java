package org.smoothbuild.lang.base;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.testing.TestingContext;

public class ParameterTest extends TestingContext {
  private final ConcreteType string = stringType();
  private final String name = "name";
  private Parameter parameter;

  @Test
  public void null_type_is_forbidden() {
    when(() -> new Parameter(0, null, name, mock(Expression.class)));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new Parameter(0, string, null, mock(Expression.class)));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void index_getter() throws Exception {
    given(parameter = new Parameter(33, string, name, mock(Expression.class)));
    when(() -> parameter.index());
    thenReturned(33);
  }

  @Test
  public void type_getter() throws Exception {
    given(parameter = new Parameter(0, string, name, mock(Expression.class)));
    when(() -> parameter.type());
    thenReturned(string);
  }

  @Test
  public void name_getter() throws Exception {
    given(parameter = new Parameter(0, string, name, mock(Expression.class)));
    when(() -> parameter.name());
    thenReturned(name);
  }

  @Test
  public void parameter_without_default() throws Exception {
    given(parameter = new Parameter(0, string, name, null));
    when(() -> parameter.hasDefaultValue());
    thenReturned(false);
  }

  @Test
  public void parameter_with_default_value() throws Exception {
    given(parameter = new Parameter(0, string, name, mock(Expression.class)));
    when(() -> parameter.hasDefaultValue());
    thenReturned(true);
  }

  @Test
  public void to_string() {
    given(parameter = new Parameter(0, string, name, mock(Expression.class)));
    when(parameter.toString());
    thenReturned("Param(String: name)");
  }
}
