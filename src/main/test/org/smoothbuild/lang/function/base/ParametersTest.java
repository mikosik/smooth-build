package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.function.base.Parameter.requiredParameter;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class ParametersTest {
  List<Parameter> parameters;
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
    given(parameter1 = optionalParameter(STRING, "name"));
    given(parameters = asList(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_required_params_keeps_required_param() throws Exception {
    given(parameter1 = requiredParameter(STRING, "name"));
    given(parameters = asList(parameter1));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList(parameter1));
  }

  @Test
  public void filter_required_params_keeps_only_required_params() throws Exception {
    given(parameter1 = requiredParameter(STRING, "name"));
    given(parameter2 = optionalParameter(STRING, "name"));
    given(parameter3 = requiredParameter(STRING, "name"));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.filterRequiredParameters(parameters));
    thenReturned(asList(parameter1, parameter3));
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
    given(parameter1 = requiredParameter(STRING, "name"));
    given(parameters = asList(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_optional_params_keeps_optional_param() throws Exception {
    given(parameter1 = optionalParameter(STRING, "name"));
    given(parameters = asList(parameter1));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList(parameter1));
  }

  @Test
  public void filter_optional_params_keeps_only_optional_params() throws Exception {
    given(parameter1 = optionalParameter(STRING, "name"));
    given(parameter2 = requiredParameter(STRING, "name"));
    given(parameter3 = optionalParameter(STRING, "name"));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.filterOptionalParameters(parameters));
    thenReturned(asList(parameter1, parameter3));
  }

  // paramsToNames()

  @Test
  public void params_to_names() throws Exception {
    given(parameter1 = optionalParameter(STRING, "name1"));
    given(parameter2 = requiredParameter(STRING, "name2"));
    given(parameter3 = optionalParameter(STRING, "name3"));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.parametersToNames(parameters));
    thenReturned(asList("name1", "name2", "name3"));
  }

  // paramsToMap()

  @Test
  public void params_to_map() throws Exception {
    given(parameter1 = requiredParameter(STRING, "alpha"));
    given(parameter2 = optionalParameter(STRING, "beta"));
    given(parameter3 = optionalParameter(STRING, "gamma"));
    given(parameters = asList(parameter1, parameter2, parameter3));
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
    given(parameter1 = optionalParameter(STRING, "gamma"));
    given(parameter2 = requiredParameter(STRING, "alpha"));
    given(parameter3 = optionalParameter(STRING, "beta"));
    given(parameters = asList(parameter1, parameter2, parameter3));
    when(Parameters.sortedParameters(parameters));
    thenReturned(asList(parameter2, parameter3, parameter1));
  }

}
