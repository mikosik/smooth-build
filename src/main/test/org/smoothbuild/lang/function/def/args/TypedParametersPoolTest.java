package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Param.param;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;

import com.google.common.collect.Sets;

public class TypedParametersPoolTest {
  Param string = param(STRING, "string1", false);
  Param blob = param(BLOB, "blob", false);

  Param stringRequired = param(STRING, "stringRequired", true);
  Param stringRequired2 = param(STRING, "stringRequired2", true);

  Set<Param> optionalParams = Sets.newHashSet();
  Set<Param> requiredParams = Sets.newHashSet();
  TypedParametersPool pool = new TypedParametersPool(optionalParams, requiredParams);

  @Test
  public void requiredParams() throws Exception {
    optionalParams.add(string);
    requiredParams.add(stringRequired);

    assertThat(pool.requiredParams()).containsOnly(stringRequired);
  }

  @Test
  public void optionalParams() throws Exception {
    optionalParams.add(string);
    requiredParams.add(stringRequired);

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
    requiredParams.add(stringRequired);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOneOptionalParam() throws Exception {
    optionalParams.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(string);
  }

  @Test
  public void hasCandidateForOptionalAndOneNonRequiredParam() throws Exception {
    requiredParams.add(stringRequired);
    optionalParams.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void doesNotHaveCandidateWhenNoParamExist() throws Exception {
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoNonRequiredParamsExist() throws Exception {
    optionalParams.add(string);
    optionalParams.add(blob);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoRequiredParamsExist() throws Exception {
    requiredParams.add(stringRequired);
    requiredParams.add(stringRequired2);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoRequiredAndOneNonRequiredParamsExist() throws Exception {
    requiredParams.add(stringRequired);
    requiredParams.add(stringRequired2);
    optionalParams.add(string);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void haveCandidateWhenOptionalAndTwoNonRequiredParamsExist() throws Exception {
    requiredParams.add(stringRequired);
    optionalParams.add(string);
    optionalParams.add(blob);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void sizeOfEmptyPoolIsZero() throws Exception {
    assertThat(pool.size()).isEqualTo(0);
  }

  @Test
  public void size() throws Exception {
    optionalParams.add(string);
    requiredParams.add(stringRequired);

    assertThat(pool.size()).isEqualTo(2);
  }
}
