package org.smoothbuild.lang.function.def.args;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class ParametersPoolTest {
  Param param;
  ParametersPool parametersPool;

  // take(Param)

  @Test
  public void existing_param_can_be_taken_from_pool() {
    given(param = param(STRING, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool).take(param);
    thenReturned(same(param));
  }

  @Test
  public void taking_unknown_param_throws_exception() {
    given(parametersPool = new ParametersPool(Empty.paramList()));
    when(parametersPool).take(param(STRING, "unknownName", false));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_twice_from_pool() {
    given(param = param(STRING, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    given(parametersPool).take(param);
    when(parametersPool).take(param);
    thenThrown(IllegalArgumentException.class);
  }

  // take(String)

  @Test
  public void existing_param_can_be_taken_from_pool_by_name() {
    given(param = param(STRING, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool).take(param.name());
    thenReturned(same(param));
  }

  @Test
  public void taking_unknown_param_by_name_throws_exception() {
    given(parametersPool = new ParametersPool(Empty.paramList()));
    when(parametersPool).take("unknownName");
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_by_name_twice() {
    given(param = param(STRING, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    given(parametersPool).take(param.name());
    when(parametersPool).take(param.name());
    thenThrown(IllegalArgumentException.class);
  }

  // assignableFrom()

  @Test
  public void optional_string_param_is_available_in_optional_set_of_string_pool() throws Exception {
    given(param = param(STRING, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(STRING)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_string_param_is_available_in_required_set_of_string_pool() throws Exception {
    given(param = param(STRING, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(STRING)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_blob_pool() throws Exception {
    given(param = param(BLOB, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(BLOB)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_blob_pool() throws Exception {
    given(param = param(BLOB, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(BLOB)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(param = param(BLOB, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(param = param(BLOB, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_file_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(param = param(FILE, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_file_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(param = param(FILE, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_string_array_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(STRING_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_string_array_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(STRING_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_blob_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(BLOB_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_blob_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(BLOB_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_file_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_file_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_file_array_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_file_array_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(FILE_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_nil_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(NIL)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_nil_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(NIL)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_nil_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(NIL)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_nil_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(NIL)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_nil_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", false));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(NIL)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_nil_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.assignableFrom(NIL)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  // allRequired()

  @Test
  public void available_required_params_contains_required_string_param() throws Exception {
    given(param = param(STRING, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_contains_required_blob_param() throws Exception {
    given(param = param(BLOB, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_contains_required_file_param() throws Exception {
    given(param = param(FILE, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_contains_required_string_array_param() throws Exception {
    given(param = param(STRING_ARRAY, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    given(param = param(STRING, "NAME", true));
    given(parametersPool = new ParametersPool(ImmutableList.of(param)));
    given(parametersPool.take(param));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.<Param>of());
  }
}
