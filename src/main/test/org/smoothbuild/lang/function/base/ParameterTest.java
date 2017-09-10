package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;

public class ParameterTest {
  private final Name name = new Name("name");
  private Parameter parameter;

  @Test
  public void optional_parameter_creates_optional_parameter() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Expression.class)));
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
    when(() -> new Parameter(null, name, mock(Expression.class)));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new Parameter(STRING, null, mock(Expression.class)));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_getter() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Expression.class)));
    when(() -> parameter.type());
    thenReturned(STRING);
  }

  @Test
  public void name_getter() throws Exception {
    given(parameter = new Parameter(STRING, name, mock(Expression.class)));
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
    given(parameter = new Parameter(STRING, name, mock(Expression.class)));
    when(() -> parameter.isRequired());
    thenReturned(false);
  }

  @Test
  public void to_padded_string() {
    given(parameter = new Parameter(STRING, new Name("myName"), mock(Expression.class)));
    when(parameter.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(parameter = new Parameter(STRING, new Name("myName"), mock(Expression.class)));
    when(parameter.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() {
    given(parameter = new Parameter(STRING, name, mock(Expression.class)));
    when(parameter.toString());
    thenReturned("Param(String: name)");
  }

  @Test
  public void params_to_string() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter(STRING, new Name("param1"), mock(Expression.class)));
    parameters.add(new Parameter(
        STRING, new Name("param2-with-very-long"), mock(Expression.class)));
    parameters.add(new Parameter(FILE_ARRAY, new Name("param3"), null));

    when(parametersToString(parameters));
    thenReturned(""
        + "  String: param1               \n"
        + "  String: param2-with-very-long\n"
        + "  [File]: param3               \n");
  }
}
