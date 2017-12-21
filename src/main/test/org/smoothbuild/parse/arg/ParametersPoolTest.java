package org.smoothbuild.parse.arg;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.util.Sets.set;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.common.Matchers.same;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.ParameterInfo;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.util.Dag;

import com.google.common.collect.ImmutableSet;

public class ParametersPoolTest {
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();
  private static final Type BLOB = TYPE_SYSTEM.blob();
  private static final Type FILE = TYPE_SYSTEM.file();
  private static final Type NOTHING = TYPE_SYSTEM.nothing();

  private final Name name = new Name("NAME");
  private TypeSystem typeSystem;
  private ParameterInfo parameter;
  private ParametersPool parametersPool;

  // take(Param)

  @Test
  public void existing_param_can_be_taken_from_pool() {
    given(typeSystem = new TypeSystem());
    given(parameter = new ParameterInfo(STRING, name, true));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool).take(parameter);
    thenReturned(same(parameter));
  }

  @Test
  public void taking_unknown_param_throws_exception() {
    given(typeSystem = new TypeSystem());
    given(parametersPool = new ParametersPool(typeSystem, set(), set()));
    when(parametersPool).take(
        new Parameter(STRING, new Name("unknownName"), mock(Dag.class)));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void param_cannot_be_taken_twice_from_pool() {
    given(typeSystem = new TypeSystem());
    given(parameter = new ParameterInfo(STRING, name, true));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    given(parametersPool).take(parameter);
    when(parametersPool).take(parameter);
    thenThrown(IllegalArgumentException.class);
  }

  // assignableFrom()

  @Test
  public void optional_string_param_is_available_in_optional_set_of_string_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new ParameterInfo(STRING, name, true));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(STRING)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_param_is_available_in_required_set_of_string_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(STRING, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(STRING)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_blob_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(BLOB, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(BLOB)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_blob_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(BLOB, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(BLOB)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(BLOB, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(FILE)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(BLOB, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(FILE)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_param_is_available_in_optional_set_of_file_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(FILE, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(FILE)).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_param_is_available_in_required_set_of_file_pool() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(FILE, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(FILE)).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_string_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(STRING), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(STRING))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_string_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(STRING), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(STRING))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_blob_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(BLOB), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(BLOB))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_blob_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(BLOB), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(BLOB))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_file_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(BLOB), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(FILE))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_file_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(BLOB), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(FILE))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_file_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(FILE), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(FILE))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_file_array_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(FILE), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(FILE))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_string_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(STRING), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(NOTHING))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_string_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(STRING), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(NOTHING))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_blob_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(BLOB), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(NOTHING))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_blob_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(BLOB), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(NOTHING))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void optional_file_array_param_is_available_in_optional_set_of_nil_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(FILE), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.assignableFrom(arrayOf(NOTHING))).optionalParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void required_file_array_param_is_available_in_required_set_of_nil_pool()
      throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(FILE), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.assignableFrom(arrayOf(NOTHING))).requiredParameters();
    thenReturned(ImmutableSet.of(parameter));
  }

  // allRequired()

  @Test
  public void available_required_params_contains_required_string_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(STRING, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_blob_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(BLOB, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_file_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(FILE, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_contains_required_string_array_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(STRING), name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void available_required_params_does_not_contain_taken_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(STRING, name, null));
    given(parametersPool = new ParametersPool(typeSystem, set(), set(parameter)));
    given(parametersPool.take(parameter));
    when(parametersPool.allRequired());
    thenReturned(ImmutableSet.<Parameter> of());
  }

  // allOptional()

  @Test
  public void all_optional_params_contains_optional_string_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new ParameterInfo(STRING, name, true));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_blob_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(BLOB, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_file_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(FILE, name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_contains_required_string_array_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new Parameter(arrayOf(STRING), name, mock(Dag.class)));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.of(parameter));
  }

  @Test
  public void all_optional_params_does_not_contain_taken_param() throws Exception {
    given(typeSystem = new TypeSystem());
    given(parameter = new ParameterInfo(STRING, name, true));
    given(parametersPool = new ParametersPool(typeSystem, set(parameter), set()));
    given(parametersPool.take(parameter));
    when(parametersPool.allOptional());
    thenReturned(ImmutableSet.<Parameter> of());
  }
}
