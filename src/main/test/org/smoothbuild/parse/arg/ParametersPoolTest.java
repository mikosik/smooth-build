package org.smoothbuild.parse.arg;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.TypedName;
import org.smoothbuild.util.Dag;

import com.google.common.collect.ImmutableSet;

public class ParametersPoolTest {
  private final Name name = new Name("NAME");
  private TypedName parameter;
  private ParametersPool parametersPool;

  // take(Param)

  @Test
  public void existing_param_can_be_taken_from_pool() {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool).take(parameter);
    thenReturned(same(parameter));
  }

  @Test
  public void taking_unknown_param_throws_exception() {
    given(parametersPool = new ParametersPool(asList(), asList()));
    when(parametersPool).take(
        new Parameter(STRING, new Name("unknownName"), mock(Dag.class)));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_twice_from_pool() {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    given(parametersPool).take(parameter);
    when(parametersPool).take(parameter);
    thenThrown(IllegalArgumentException.class);
  }

  // take(String)

  @Test
  public void existing_param_can_be_taken_from_pool_by_name() {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool).take(parameter.name());
    thenReturned(same(parameter));
  }

  @Test
  public void taking_unknown_param_by_name_throws_exception() {
    given(parametersPool = new ParametersPool(asList(), asList()));
    when(parametersPool).take(new Name("unknownName"));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_by_name_twice() {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    given(parametersPool).take(parameter.name());
    when(parametersPool).take(parameter.name());
    thenThrown(IllegalArgumentException.class);
  }

  // assignableFrom()

  @Test
  public void optional_string_param_is_available_in_optional_set_of_string_pool() throws Exception {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(STRING)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_param_is_available_in_required_set_of_string_pool() throws Exception {
    given(parameter = new Parameter(STRING, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(STRING)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_blob_pool() throws Exception {
    given(parameter = new Parameter(BLOB, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(BLOB)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_blob_pool() throws Exception {
    given(parameter = new Parameter(BLOB, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(BLOB)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(BLOB, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(FILE)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(BLOB, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(FILE)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(FILE, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(FILE)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(FILE, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(FILE)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_string_array_pool()
      throws Exception {
    given(parameter = new Parameter(STRING_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(STRING_ARRAY)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_string_array_pool()
      throws Exception {
    given(parameter = new Parameter(STRING_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(STRING_ARRAY)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_blob_array_pool()
      throws Exception {
    given(parameter = new Parameter(BLOB_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(BLOB_ARRAY)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_blob_array_pool()
      throws Exception {
    given(parameter = new Parameter(BLOB_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(BLOB_ARRAY)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(BLOB_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(FILE_ARRAY)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(BLOB_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(FILE_ARRAY)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(FILE_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(FILE_ARRAY)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(FILE_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(FILE_ARRAY)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(STRING_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(NIL)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(STRING_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(NIL)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(BLOB_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(NIL)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(BLOB_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(NIL)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(FILE_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.assignableFrom(NIL)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(FILE_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.assignableFrom(NIL)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  // allRequired()

  @Test
  public void available_required_params_contains_required_string_param() throws Exception {
    given(parameter = new Parameter(STRING, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_blob_param() throws Exception {
    given(parameter = new Parameter(BLOB, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_file_param() throws Exception {
    given(parameter = new Parameter(FILE, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_string_array_param() throws Exception {
    given(parameter = new Parameter(STRING_ARRAY, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    given(parameter = new Parameter(STRING, name, null));
    given(parametersPool = new ParametersPool(asList(), asList(parameter)));
    given(parametersPool.take(parameter));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.<Parameter> of());
  }

  // allOptional()

  @Test
  public void all_optional_params_contains_optional_string_param() throws Exception {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_blob_param() throws Exception {
    given(parameter = new Parameter(BLOB, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_file_param() throws Exception {
    given(parameter = new Parameter(FILE, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_string_array_param() throws Exception {
    given(parameter = new Parameter(STRING_ARRAY, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_does_not_contain_taken_param() throws Exception {
    given(parameter = new TypedName(STRING, name));
    given(parametersPool = new ParametersPool(asList(parameter), asList()));
    given(parametersPool.take(parameter));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.<Parameter> of());
  }
}
