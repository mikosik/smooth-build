package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ParamsTest {
  ImmutableList<Param> params;
  Param param1;
  Param param2;
  Param param3;

  // filterRequiredParams()

  @Test
  public void filter_required_params_converts_empty_list_to_empty_list() throws Exception {
    given(params = Empty.paramList());
    when(Params.filterRequiredParams(params));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_required_params_omits_optional_param() throws Exception {
    given(param1 = param(STRING, "name", false));
    given(params = ImmutableList.of(param1));
    when(Params.filterRequiredParams(params));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_required_params_keeps_required_param() throws Exception {
    given(param1 = param(STRING, "name", true));
    given(params = ImmutableList.of(param1));
    when(Params.filterRequiredParams(params));
    thenReturned(ImmutableList.of(param1));
  }

  @Test
  public void filter_required_params_keeps_only_required_params() throws Exception {
    given(param1 = param(STRING, "name", true));
    given(param2 = param(STRING, "name", false));
    given(param3 = param(STRING, "name", true));
    given(params = ImmutableList.of(param1, param2, param3));
    when(Params.filterRequiredParams(params));
    thenReturned(ImmutableList.of(param1, param3));
  }

  // filterRequiredParams()

  @Test
  public void filter_optional_params_converts_empty_list_to_empty_list() throws Exception {
    given(params = Empty.paramList());
    when(Params.filterOptionalParams(params));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_optional_params_omits_required_param() throws Exception {
    given(param1 = param(STRING, "name", true));
    given(params = ImmutableList.of(param1));
    when(Params.filterOptionalParams(params));
    thenReturned(Empty.paramList());
  }

  @Test
  public void filter_optional_params_keeps_optional_param() throws Exception {
    given(param1 = param(STRING, "name", false));
    given(params = ImmutableList.of(param1));
    when(Params.filterOptionalParams(params));
    thenReturned(ImmutableList.of(param1));
  }

  @Test
  public void filter_optional_params_keeps_only_optional_params() throws Exception {
    given(param1 = param(STRING, "name", false));
    given(param2 = param(STRING, "name", true));
    given(param3 = param(STRING, "name", false));
    given(params = ImmutableList.of(param1, param2, param3));
    when(Params.filterOptionalParams(params));
    thenReturned(ImmutableList.of(param1, param3));
  }

  // paramsToNames()

  @Test
  public void params_to_names() throws Exception {
    given(param1 = param(STRING, "name1", false));
    given(param2 = param(STRING, "name2", true));
    given(param3 = param(STRING, "name3", false));
    given(params = ImmutableList.of(param1, param2, param3));
    when(Params.paramsToNames(params));
    thenReturned(ImmutableList.of("name1", "name2", "name3"));
  }

  // paramsToMap()

  @Test
  public void params_to_map() throws Exception {
    given(param1 = param(STRING, "alpha", true));
    given(param2 = param(STRING, "beta", false));
    given(param3 = param(STRING, "gamma", false));
    given(params = ImmutableList.of(param1, param2, param3));
    when(Params.paramsToMap(params));
    thenReturned(ImmutableMap.of(param1.name(), param1, param2.name(), param2, param3.name(),
        param3));
  }

  // sortedParams()

  @Test
  public void sorted_params_for_empty_list_returns_empty_list() throws Exception {
    given(params = Empty.paramList());
    when(Params.sortedParams(params));
    thenReturned(Empty.paramList());
  }

  @Test
  public void sorted_params() throws Exception {
    given(param1 = param(STRING, "gamma", false));
    given(param2 = param(STRING, "alpha", true));
    given(param3 = param(STRING, "beta", false));
    given(params = ImmutableList.of(param1, param2, param3));
    when(Params.sortedParams(params));
    thenReturned(ImmutableList.of(param2, param3, param1));
  }

}
