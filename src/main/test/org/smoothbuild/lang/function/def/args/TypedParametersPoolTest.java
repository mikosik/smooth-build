package org.smoothbuild.lang.function.def.args;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Parameter;

public class TypedParametersPoolTest {
  private final Parameter string = parameter(STRING, "string1", false);
  private final Parameter blob = parameter(BLOB, "blob", false);
  private final Parameter stringRequired = parameter(STRING, "stringRequired", true);
  private final Parameter stringRequired2 = parameter(STRING, "stringRequired2", true);

  private Set<Parameter> optional;
  private Set<Parameter> required;
  private Set<Parameter> noParameters;
  private TypedParametersPool pool;

  @Before
  public void before() {
    givenTest(this);
    given(noParameters = unmodifiableSet(new HashSet<Parameter>()));
  }

  @Test
  public void parameters_getter() {
    given(optional = newHashSet(string));
    given(required = newHashSet(stringRequired));
    when(pool = new TypedParametersPool(optional, required));
    thenEqual(pool.optionalParameters(), newHashSet(string));
    thenEqual(pool.requiredParameters(), newHashSet(stringRequired));
  }

  @Test
  public void creates_pool_with_no_parameters() {
    when(pool = new TypedParametersPool(noParameters, noParameters));
    then(pool.requiredParameters(), empty());
    then(pool.optionalParameters(), empty());
  }

  @Test
  public void has_candidate_when_pool_has_one_required_parameter() {
    given(optional = noParameters);
    given(required = newHashSet(stringRequired));
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(stringRequired));
  }

  @Test
  public void has_candidate_when_pool_has_one_optional_parameter() {
    given(optional = newHashSet(string));
    given(required = noParameters);
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(string));
  }

  @Test
  public void has_candidate_when_pool_has_one_optional_and_one_required_parameter() {
    given(optional = newHashSet(string));
    given(required = newHashSet(stringRequired));
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(stringRequired));
  }

  @Test
  public void has_no_candidate_when_pool_is_empty() {
    given(pool = new TypedParametersPool(noParameters, noParameters));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_no_candidate_when_pool_has_two_optional_parameters() {
    given(optional = newHashSet(string, blob));
    given(required = noParameters);
    given(pool = new TypedParametersPool(optional, required));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_no_candidate_when_pool_has_two_required_parameters() {
    given(optional = noParameters);
    given(required = newHashSet(stringRequired, stringRequired2));
    given(pool = new TypedParametersPool(optional, required));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_no_candidate_when_pool_has_two_required_and_one_optional_parameter() {
    given(optional = newHashSet(string));
    given(required = newHashSet(stringRequired, stringRequired2));
    given(pool = new TypedParametersPool(optional, required));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_candidate_when_pool_has_one_required_and_two_optional_parameters() {
    given(optional = newHashSet(string, blob));
    given(required = newHashSet(stringRequired));
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(stringRequired));
  }

  @Test
  public void pool_without_parameters_is_empty() throws Exception {
    given(optional = noParameters);
    given(required = noParameters);
    given(pool = new TypedParametersPool(optional, required));
    when(pool.isEmpty());
    thenReturned(true);
  }

  @Test
  public void pool_with_optional_parameter_is_not_empty() throws Exception {
    given(optional = newHashSet(string));
    given(required = noParameters);
    given(pool = new TypedParametersPool(optional, required));
    when(pool.isEmpty());
    thenReturned(false);
  }

  @Test
  public void pool_with_required_parameter_is_not_empty() throws Exception {
    given(optional = noParameters);
    given(required = newHashSet(stringRequired));
    given(pool = new TypedParametersPool(optional, required));
    when(pool.isEmpty());
    thenReturned(false);
  }
}
