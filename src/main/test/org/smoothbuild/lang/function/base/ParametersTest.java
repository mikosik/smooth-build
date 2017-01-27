package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
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
  List<Parameter> parameters;
  Parameter parameter1;
  Parameter parameter2;
  Parameter parameter3;

  // filterRequiredParams()

  @Test
  public void filter_required_params_converts_empty_list_to_empty_list() throws Exception {
    given(parameters = asList());
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_required_params_omits_optional_param() throws Exception {
    given(parameter1 = parameter(STRING, "name", mock(Expression.class)));
    given(parameters = asList(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_required_params_keeps_required_param() throws Exception {
    given(parameter1 = parameter(STRING, "name"));
    given(parameters = asList(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList(parameter1));
  }

  @Test
  public void filter_required_params_keeps_only_required_params() throws Exception {
    given(parameter1 = parameter(STRING, "name"));
    given(parameter2 = parameter(STRING, "name", mock(Expression.class)));
    given(parameter3 = parameter(STRING, "name"));
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
    given(parameter1 = parameter(STRING, "name"));
    given(parameters = asList(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void filter_optional_params_keeps_optional_param() throws Exception {
    given(parameter1 = parameter(STRING, "name", mock(Expression.class)));
    given(parameters = asList(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList(parameter1));
  }

  @Test
  public void filter_optional_params_keeps_only_optional_params() throws Exception {
    given(parameter1 = parameter(STRING, "name", mock(Expression.class)));
    given(parameter2 = parameter(STRING, "name"));
    given(parameter3 = parameter(STRING, "name", mock(Expression.class)));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList(parameter1, parameter3));
  }

  // paramsToNames()

  @Test
  public void params_to_names() throws Exception {
    given(parameter1 = parameter(STRING, "name1", mock(Expression.class)));
    given(parameter2 = parameter(STRING, "name2"));
    given(parameter3 = parameter(STRING, "name3", mock(Expression.class)));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.parametersToNames(parameters));
    thenReturned(asList("name1", "name2", "name3"));
  }

  // paramsToMap()

  @Test
  public void params_to_map() throws Exception {
    given(parameter1 = parameter(STRING, "alpha"));
    given(parameter2 = parameter(STRING, "beta", mock(Expression.class)));
    given(parameter3 = parameter(STRING, "gamma", mock(Expression.class)));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.parametersToMap(parameters));
    thenReturned(ImmutableMap.of(parameter1.name(), parameter1, parameter2.name(), parameter2,
        parameter3.name(), parameter3));
  }

  // sortedParams()

  @Test
  public void sorted_params_for_empty_list_returns_empty_list() throws Exception {
    given(parameters = asList());
    when(Parameters.sortedParameters(parameters));
    thenReturned(asList());
  }

  @Test
  public void sorted_params() throws Exception {
    given(parameter1 = parameter(STRING, "gamma", mock(Expression.class)));
    given(parameter2 = parameter(STRING, "alpha"));
    given(parameter3 = parameter(STRING, "beta", mock(Expression.class)));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.sortedParameters(parameters));
    thenReturned(asList(parameter2, parameter3, parameter1));
  }

}
