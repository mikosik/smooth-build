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

public class ParamsPoolTest {
  Param param;
  ParamsPool paramsPool;

  // take(Param)

  @Test
  public void existing_param_can_be_taken_from_pool() {
    given(param = param(STRING, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool).take(param);
    thenReturned(same(param));
  }

  @Test
  public void taking_unknown_param_throws_exception() {
    given(paramsPool = new ParamsPool(Empty.paramList()));
    when(paramsPool).take(param(STRING, "unknownName", false));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_twice_from_pool() {
    given(param = param(STRING, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    given(paramsPool).take(param);
    when(paramsPool).take(param);
    thenThrown(IllegalArgumentException.class);
  }

  // take(String)

  @Test
  public void existing_param_can_be_taken_from_pool_by_name() {
    given(param = param(STRING, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool).take(param.name());
    thenReturned(same(param));
  }

  @Test
  public void taking_unknown_param_by_name_throws_exception() {
    given(paramsPool = new ParamsPool(Empty.paramList()));
    when(paramsPool).take("unknownName");
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_by_name_twice() {
    given(param = param(STRING, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    given(paramsPool).take(param.name());
    when(paramsPool).take(param.name());
    thenThrown(IllegalArgumentException.class);
  }

  // assignableFrom()

  @Test
  public void optional_string_param_is_available_in_optional_set_of_string_pool() throws Exception {
    given(param = param(STRING, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(STRING)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_string_param_is_available_in_required_set_of_string_pool() throws Exception {
    given(param = param(STRING, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(STRING)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_blob_pool() throws Exception {
    given(param = param(BLOB, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(BLOB)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_blob_pool() throws Exception {
    given(param = param(BLOB, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(BLOB)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(param = param(BLOB, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(param = param(BLOB, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_file_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(param = param(FILE, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_file_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(param = param(FILE, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_string_array_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(STRING_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_string_array_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(STRING_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_blob_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(BLOB_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_blob_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(BLOB_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_file_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_file_array_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_file_array_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE_ARRAY)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_file_array_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(FILE_ARRAY)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_nil_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(NIL)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_nil_pool() throws
      Exception {
    given(param = param(STRING_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(NIL)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_nil_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(NIL)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_nil_pool() throws
      Exception {
    given(param = param(BLOB_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(NIL)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_nil_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", false));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(NIL)).optionalParams();
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_nil_pool() throws
      Exception {
    given(param = param(FILE_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.assignableFrom(NIL)).requiredParams();
    thenReturned(ImmutableSet.of(param));
  }

  // allRequired()

  @Test
  public void available_required_params_contains_required_string_param() throws Exception {
    given(param = param(STRING, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_contains_required_blob_param() throws Exception {
    given(param = param(BLOB, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_contains_required_file_param() throws Exception {
    given(param = param(FILE, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_contains_required_string_array_param() throws Exception {
    given(param = param(STRING_ARRAY, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    when(paramsPool.allRequired());
    thenReturned(ImmutableSet.of(param));
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    given(param = param(STRING, "NAME", true));
    given(paramsPool = new ParamsPool(ImmutableList.of(param)));
    given(paramsPool.take(param));
    when(paramsPool.allRequired());
    thenReturned(ImmutableSet.<Param>of());
  }
}
