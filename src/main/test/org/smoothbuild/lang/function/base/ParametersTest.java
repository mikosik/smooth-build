package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableMap;

public class ParametersTest {
  private final Name name = new Name("name");
  private List<Parameter> parameters;
  private Parameter parameter1;
  private Parameter parameter2;
  private Parameter parameter3;

  // filterRequiredParams()

  @Test
  public void filter_required_params_converts_empty_list_to_empty_list() throws Exception {
    given(parameters = asList());
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_required_params_omits_optional_param() throws Exception {
    given(parameter1 = new Parameter(STRING, name, mock(Expression.class)));
    given(parameters = asList(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_required_params_keeps_required_param() throws Exception {
    given(parameter1 = new Parameter(STRING, name, null));
    given(parameters = asList(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList(parameter1));
  }

  @Test
  public void filter_required_params_keeps_only_required_params() throws Exception {
    given(parameter1 = new Parameter(STRING, name, null));
    given(parameter2 = new Parameter(STRING, name, mock(Expression.class)));
    given(parameter3 = new Parameter(STRING, name, null));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList(parameter1, parameter3));
  }

  // filterRequiredParams()

  @Test
  public void filter_optional_params_converts_empty_list_to_empty_list() throws Exception {
    given(parameters = asList());
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_optional_params_omits_required_param() throws Exception {
    given(parameter1 = new Parameter(STRING, name, null));
    given(parameters = asList(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_optional_params_keeps_optional_param() throws Exception {
    given(parameter1 = new Parameter(STRING, name, mock(Expression.class)));
    given(parameters = asList(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList(parameter1));
  }

  @Test
  public void filter_optional_params_keeps_only_optional_params() throws Exception {
    given(parameter1 = new Parameter(STRING, name, mock(Expression.class)));
    given(parameter2 = new Parameter(STRING, name, null));
    given(parameter3 = new Parameter(STRING, name, mock(Expression.class)));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList(parameter1, parameter3));
  }

  // paramsToMap()

  @Test
  public void params_to_map() throws Exception {
    given(parameter1 = new Parameter(STRING, new Name("alpha"), null));
    given(parameter2 = new Parameter(STRING, new Name("beta"), mock(Expression.class)));
    given(parameter3 = new Parameter(STRING, new Name("gamma"), mock(Expression.class)));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.parametersToMap(parameters));
    thenReturned(ImmutableMap.of(parameter1.name(), parameter1, parameter2.name(), parameter2,
        parameter3.name(), parameter3));
  }
}
