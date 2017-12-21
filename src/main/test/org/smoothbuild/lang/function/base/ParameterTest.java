package org.smoothbuild.lang.function.base;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.util.Dag;

public class ParameterTest {
  private static final Type STRING = new TypeSystem().string();
  private final Name name = new Name("name");
  private Parameter parameter;

  @Test
  public void optional_parameter_creates_optional_parameter() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Dag.class)));
    when(parameter).isRequired();
    thenReturned(false);
  }

  @Test
  public void required_parameter_creates_required_parameter() throws Exception {
    given(parameter = new Parameter(STRING, name, null));
    when(parameter).isRequired();
    thenReturned(true);
  }

  @Test
  public void null_type_is_forbidden() {
    when(() -> new Parameter(null, name, mock(Dag.class)));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new Parameter(STRING, null, mock(Dag.class)));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_getter() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Dag.class)));
    when(() -> parameter.type());
    thenReturned(STRING);
  }

  @Test
  public void name_getter() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Dag.class)));
    when(() -> parameter.name());
    thenReturned(name);
  }

  @Test
  public void getters() {
    when(parameter = new Parameter(STRING, name, null));
    thenEqual(parameter.type(), STRING);
    thenEqual(parameter.name(), name);
  }

  @Test
  public void parameter_without_default_value_is_required() throws Exception {
    given(parameter = new Parameter(STRING, name, null));
    when(() -> parameter.isRequired());
    thenReturned(true);
  }

  @Test
  public void parameter_with_default_value_is_not_required() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Dag.class)));
    when(() -> parameter.isRequired());
    thenReturned(false);
  }

  @Test
  public void to_string() {
    given(parameter = new Parameter(STRING, name, mock(Dag.class)));
    when(parameter.toString());
    thenReturned("Param(String: name)");
  }
}
