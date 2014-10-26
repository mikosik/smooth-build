package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.parameter;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Parameter;

import com.google.common.collect.Sets;

public class TypedParametersPoolTest {
  Parameter string = parameter(STRING, "string1", false);
  Parameter blob = parameter(BLOB, "blob", false);

  Parameter stringRequired = parameter(STRING, "stringRequired", true);
  Parameter stringRequired2 = parameter(STRING, "stringRequired2", true);

  Set<Parameter> optionalParameters = Sets.newHashSet();
  Set<Parameter> requiredParameters = Sets.newHashSet();
  TypedParametersPool pool = new TypedParametersPool(optionalParameters, requiredParameters);

  @Test
  public void requiredParams() throws Exception {
    optionalParameters.add(string);
    requiredParameters.add(stringRequired);

    assertThat(pool.requiredParams()).containsOnly(stringRequired);
  }

  @Test
  public void optionalParams() throws Exception {
    optionalParameters.add(string);
    requiredParameters.add(stringRequired);

    assertThat(pool.optionalParams()).containsOnly(string);
  }

  @Test
  public void requiredParamsIsEmptyWhenNoParamWasAdded() throws Exception {
    assertThat(pool.requiredParams()).isEmpty();
  }

  @Test
  public void optionalParamsIsEmptyWhenNoParamWasAdded() throws Exception {
    assertThat(pool.optionalParams()).isEmpty();
  }

  @Test
  public void hasCandidateForOptionalParam() throws Exception {
    requiredParameters.add(stringRequired);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOneOptionalParam() throws Exception {
    optionalParameters.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(string);
  }

  @Test
  public void hasCandidateForOptionalAndOneNonRequiredParam() throws Exception {
    requiredParameters.add(stringRequired);
    optionalParameters.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void doesNotHaveCandidateWhenNoParamExist() throws Exception {
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoNonRequiredParamsExist() throws Exception {
    optionalParameters.add(string);
    optionalParameters.add(blob);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoRequiredParamsExist() throws Exception {
    requiredParameters.add(stringRequired);
    requiredParameters.add(stringRequired2);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoRequiredAndOneNonRequiredParamsExist() throws Exception {
    requiredParameters.add(stringRequired);
    requiredParameters.add(stringRequired2);
    optionalParameters.add(string);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void haveCandidateWhenOptionalAndTwoNonRequiredParamsExist() throws Exception {
    requiredParameters.add(stringRequired);
    optionalParameters.add(string);
    optionalParameters.add(blob);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void sizeOfEmptyPoolIsZero() throws Exception {
    assertThat(pool.size()).isEqualTo(0);
  }

  @Test
  public void size() throws Exception {
    optionalParameters.add(string);
    requiredParameters.add(stringRequired);

    assertThat(pool.size()).isEqualTo(2);
  }
}
