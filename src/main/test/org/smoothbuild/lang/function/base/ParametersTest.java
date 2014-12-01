package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ParametersTest {
  ImmutableList<Parameter> parameters;
  Parameter parameter1;
  Parameter parameter2;
  Parameter parameter3;

  // filterRequiredParams()

  @Test
  public void filter_required_params_converts_empty_list_to_empty_list() throws Exception {
    given(parameters = Empty.paramList());
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_required_params_omits_optional_param() throws Exception {
    given(parameter1 = parameter(STRING, "name", false));
    given(parameters = ImmutableList.of(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_required_params_keeps_required_param() throws Exception {
    given(parameter1 = parameter(STRING, "name", true));
    given(parameters = ImmutableList.of(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(ImmutableList.of(parameter1));
  }

  @Test
  public void filter_required_params_keeps_only_required_params() throws Exception {
    given(parameter1 = parameter(STRING, "name", true));
    given(parameter2 = parameter(STRING, "name", false));
    given(parameter3 = parameter(STRING, "name", true));
    given(parameters = ImmutableList.of(parameter1, parameter2, parameter3));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(ImmutableList.of(parameter1, parameter3));
  }

  // filterRequiredParams()

  @Test
  public void filter_optional_params_converts_empty_list_to_empty_list() throws Exception {
    given(parameters = Empty.paramList());
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_optional_params_omits_required_param() throws Exception {
    given(parameter1 = parameter(STRING, "name", true));
    given(parameters = ImmutableList.of(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_optional_params_keeps_optional_param() throws Exception {
    given(parameter1 = parameter(STRING, "name", false));
    given(parameters = ImmutableList.of(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(ImmutableList.of(parameter1));
  }

  @Test
  public void filter_optional_params_keeps_only_optional_params() throws Exception {
    given(parameter1 = parameter(STRING, "name", false));
    given(parameter2 = parameter(STRING, "name", true));
    given(parameter3 = parameter(STRING, "name", false));
    given(parameters = ImmutableList.of(parameter1, parameter2, parameter3));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(ImmutableList.of(parameter1, parameter3));
  }

  // paramsToNames()

  @Test
  public void params_to_names() throws Exception {
    given(parameter1 = parameter(STRING, "name1", false));
    given(parameter2 = parameter(STRING, "name2", true));
    given(parameter3 = parameter(STRING, "name3", false));
    given(parameters = ImmutableList.of(parameter1, parameter2, parameter3));
    when(Parameters.parametersToNames(parameters));
    thenReturned(ImmutableList.of("name1", "name2", "name3"));
  }

  // paramsToMap()

  @Test
  public void params_to_map() throws Exception {
    given(parameter1 = parameter(STRING, "alpha", true));
    given(parameter2 = parameter(STRING, "beta", false));
    given(parameter3 = parameter(STRING, "gamma", false));
    given(parameters = ImmutableList.of(parameter1, parameter2, parameter3));
    when(Parameters.parametersToMap(parameters));
    thenReturned(ImmutableMap.of(parameter1.name(), parameter1, parameter2.name(), parameter2,
        parameter3.name(), parameter3));
  }

  // sortedParams()

  @Test
  public void sorted_params_for_empty_list_returns_empty_list() throws Exception {
    given(parameters = Empty.paramList());
    when(Parameters.sortedParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void sorted_params() throws Exception {
    given(parameter1 = parameter(STRING, "gamma", false));
    given(parameter2 = parameter(STRING, "alpha", true));
    given(parameter3 = parameter(STRING, "beta", false));
    given(parameters = ImmutableList.of(parameter1, parameter2, parameter3));
    when(Parameters.sortedParameters(parameters));
    thenReturned(ImmutableList.of(parameter2, parameter3, parameter1));
  }

}
