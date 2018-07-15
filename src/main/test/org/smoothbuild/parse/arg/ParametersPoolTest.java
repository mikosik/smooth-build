package org.smoothbuild.parse.arg;

import static org.smoothbuild.lang.message.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Sets.set;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.type.TestingTypesDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.util.Dag;

import com.google.common.collect.ImmutableSet;

public class ParametersPoolTest {
  private final TypesDb typesDb = new TestingTypesDb();
  private final Type string = typesDb.string();
  private final Type blob = typesDb.blob();
  private final Type file = typesDb.struct("File", list(
      new Field(typesDb.blob(), "content", unknownLocation()),
      new Field(typesDb.string(), "path", unknownLocation())));
  private final Type generic = typesDb.generic("b");

  private final String name = "NAME";
  private ParameterInfo parameter;
  private ParametersPool parametersPool;

  // take(Param)

  @Test
  public void existing_param_can_be_taken_from_pool() {
    given(parameter = new ParameterInfo(string, name, true));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool).take(parameter);
    thenReturned(same(parameter));
  }

  @Test
  public void taking_unknown_param_throws_exception() {
    given(parametersPool = new ParametersPool(set(), set()));
    when(parametersPool).take(new Parameter(string, "unknownName", mock(Dag.class)));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_twice_from_pool() {
    given(parameter = new ParameterInfo(string, name, true));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    given(parametersPool).take(parameter);
    when(parametersPool).take(parameter);
    thenThrown(IllegalArgumentException.class);
  }

  // assignableFrom()

  @Test
  public void optional_string_param_is_available_in_optional_set_of_string_pool() throws Exception {
    given(parameter = new ParameterInfo(string, name, true));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(string)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_param_is_available_in_required_set_of_string_pool() throws Exception {
    given(parameter = new Parameter(string, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(string)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_blob_pool() throws Exception {
    given(parameter = new Parameter(blob, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(blob)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_blob_pool() throws Exception {
    given(parameter = new Parameter(blob, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(blob)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(blob, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(file)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(blob, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(file)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(file, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(file)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(parameter = new Parameter(file, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(file)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_string_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(string), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(string))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_string_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(string), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(string))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_blob_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(blob), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(blob))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_blob_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(blob), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(blob))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(blob), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(file))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(blob), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(file))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(file), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(file))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_file_array_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(file), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(file))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(string), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(generic))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(string), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(generic))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(blob), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(generic))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(blob), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(generic))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(file), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.assignableFrom(typesDb.array(generic))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(parameter = new Parameter(typesDb.array(file), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.assignableFrom(typesDb.array(generic))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  // allRequired()

  @Test
  public void available_required_params_contains_required_string_param() throws Exception {
    given(parameter = new Parameter(string, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_blob_param() throws Exception {
    given(parameter = new Parameter(blob, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_file_param() throws Exception {
    given(parameter = new Parameter(file, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_string_array_param() throws Exception {
    given(parameter = new Parameter(typesDb.array(string), name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    given(parameter = new Parameter(string, name, null));
    given(parametersPool = new ParametersPool(set(), set(parameter)));
    given(parametersPool.take(parameter));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.<Parameter> of());
  }

  // allOptional()

  @Test
  public void all_optional_params_contains_optional_string_param() throws Exception {
    given(parameter = new ParameterInfo(string, name, true));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_blob_param() throws Exception {
    given(parameter = new Parameter(blob, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_file_param() throws Exception {
    given(parameter = new Parameter(file, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_string_array_param() throws Exception {
    given(parameter = new Parameter(typesDb.array(string), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_does_not_contain_taken_param() throws Exception {
    given(parameter = new ParameterInfo(string, name, true));
    given(parametersPool = new ParametersPool(set(parameter), set()));
    given(parametersPool.take(parameter));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.<Parameter> of());
  }
}
