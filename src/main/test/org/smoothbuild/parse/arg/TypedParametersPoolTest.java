package org.smoothbuild.parse.arg;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.STRING;
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
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.TypedName;

public class TypedParametersPoolTest {
  private final TypedName string1 = new TypedName(STRING, new Name("string1"));
  private final TypedName string2 = new TypedName(STRING, new Name("string2"));
  private final TypedName string3 = new TypedName(STRING, new Name("string3"));
  private final TypedName blob = new TypedName(BLOB, new Name("blob"));

  private Set<TypedName> optional;
  private Set<TypedName> required;
  private Set<TypedName> noParameters;
  private TypedParametersPool pool;

  @Before
  public void before() {
    givenTest(this);
    given(noParameters = unmodifiableSet(new HashSet<Parameter>()));
  }

  @Test
  public void parameters_getter() {
    given(optional = new HashSet<>(asList(string1)));
    given(required = new HashSet<>(asList(string2)));
    when(pool = new TypedParametersPool(optional, required));
    thenEqual(pool.optionalParameters(), new HashSet<>(asList(string1)));
    thenEqual(pool.requiredParameters(), new HashSet<>(asList(string2)));
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
    given(required = new HashSet<>(asList(string1)));
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(string1));
  }

  @Test
  public void has_candidate_when_pool_has_one_optional_parameter() {
    given(optional = new HashSet<>(asList(string1)));
    given(required = noParameters);
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(string1));
  }

  @Test
  public void has_candidate_when_pool_has_one_optional_and_one_required_parameter() {
    given(optional = new HashSet<>(asList(string1)));
    given(required = new HashSet<>(asList(string2)));
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(string2));
  }

  @Test
  public void has_no_candidate_when_pool_is_empty() {
    given(pool = new TypedParametersPool(noParameters, noParameters));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_no_candidate_when_pool_has_two_optional_parameters() {
    given(optional = new HashSet<>(asList(string1, blob)));
    given(required = noParameters);
    given(pool = new TypedParametersPool(optional, required));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_no_candidate_when_pool_has_two_required_parameters() {
    given(optional = noParameters);
    given(required = new HashSet<>(asList(string1, string2)));
    given(pool = new TypedParametersPool(optional, required));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_no_candidate_when_pool_has_two_required_and_one_optional_parameter() {
    given(optional = new HashSet<>(asList(string1)));
    given(required = new HashSet<>(asList(string2, string3)));
    given(pool = new TypedParametersPool(optional, required));
    when(pool.hasCandidate());
    thenReturned(false);
  }

  @Test
  public void has_candidate_when_pool_has_one_required_and_two_optional_parameters() {
    given(optional = new HashSet<>(asList(string1, blob)));
    given(required = new HashSet<>(asList(string2)));
    when(pool = new TypedParametersPool(optional, required));
    then(pool.hasCandidate());
    then(pool.candidate(), sameInstance(string2));
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
    given(optional = new HashSet<>(asList(string1)));
    given(required = noParameters);
    given(pool = new TypedParametersPool(optional, required));
    when(pool.isEmpty());
    thenReturned(false);
  }

  @Test
  public void pool_with_required_parameter_is_not_empty() throws Exception {
    given(optional = noParameters);
    given(required = new HashSet<>(asList(string1)));
    given(pool = new TypedParametersPool(optional, required));
    when(pool.isEmpty());
    thenReturned(false);
  }
}
