package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Type.STRING;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;

public class TypedParamsPoolTest {
  Param string = param(STRING, "string1");
  Param string2 = param(STRING, "string2");
  Param string3 = param(STRING, "string3");

  Param stringRequired = param(STRING, "stringRequired", true);
  Param stringRequired2 = param(STRING, "stringRequired2", true);
  Param stringRequired3 = param(STRING, "stringRequired3", true);

  TypedParamsPool pool = new TypedParamsPool();

  TypedParamsPool pool1 = new TypedParamsPool();
  TypedParamsPool pool2 = new TypedParamsPool();
  TypedParamsPool pool3 = new TypedParamsPool();
  TypedParamsPool combined = new TypedParamsPool(pool1, pool2, pool3);

  @Test
  public void requiredParams() throws Exception {
    pool.add(string);
    pool.add(string2);
    pool.add(string3);
    pool.add(stringRequired);
    pool.add(stringRequired2);
    pool.add(stringRequired3);

    assertThat(pool.requiredParams())
        .containsOnly(stringRequired, stringRequired2, stringRequired3);
  }

  @Test
  public void optionalParams() throws Exception {
    pool.add(string);
    pool.add(string2);
    pool.add(string3);
    pool.add(stringRequired);
    pool.add(stringRequired2);
    pool.add(stringRequired3);

    assertThat(pool.optionalParams()).containsOnly(string, string2, string3);
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
  public void removeReturnsTrueWhenRequiredParamWasPresent() throws Exception {
    pool.add(stringRequired);
    assertThat(pool.remove(stringRequired)).isTrue();
  }

  @Test
  public void removeReturnsFalseWhenRequiredParamWasNotPresent() throws Exception {
    assertThat(pool.remove(stringRequired)).isFalse();
  }

  @Test
  public void removedRequiredParam() throws Exception {
    pool.add(stringRequired);
    pool.add(stringRequired2);

    pool.remove(stringRequired);

    assertThat(pool.requiredParams()).containsOnly(stringRequired2);
  }

  @Test
  public void removeReturnsTrueWhenOptionalParamWasPresent() throws Exception {
    pool.add(string);
    assertThat(pool.remove(string)).isTrue();
  }

  @Test
  public void removeReturnsFalseWhenOptionalParamWasNotPresent() throws Exception {
    assertThat(pool.remove(string)).isFalse();
  }

  @Test
  public void removedOptionalParam() throws Exception {
    pool.add(string);
    pool.add(string2);

    pool.remove(string);

    assertThat(pool.optionalParams()).containsOnly(string2);
  }

  @Test
  public void hasCandidateForOptionalParam() throws Exception {
    pool.add(stringRequired);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOneOptionalParam() throws Exception {
    pool.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(string);
  }

  @Test
  public void hasCandidateForOptionalAndOneNonRequiredParam() throws Exception {
    pool.add(stringRequired);
    pool.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void doesNotHaveCandidateWhenNoParamExist() throws Exception {
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoNonRequiredParamsExist() throws Exception {
    pool.add(string);
    pool.add(string2);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoRequiredParamsExist() throws Exception {
    pool.add(stringRequired);
    pool.add(stringRequired2);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateWhenTwoRequiredAndOneNonRequiredParamsExist() throws Exception {
    pool.add(stringRequired);
    pool.add(stringRequired2);
    pool.add(string);
    assertThat(pool.hasCandidate()).isFalse();
  }

  @Test
  public void haveCandidateWhenOptionalAndTwoNonRequiredParamsExist() throws Exception {
    pool.add(stringRequired);
    pool.add(string);
    pool.add(string2);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void sizeOfEmptyPoolIsZero() throws Exception {
    assertThat(pool.size()).isEqualTo(0);
  }

  @Test
  public void size() throws Exception {
    pool.add(string);
    pool.add(stringRequired);

    assertThat(pool.size()).isEqualTo(2);
  }

  // test of combined version

  @Test
  public void combinedRequiredParams() throws Exception {
    pool1.add(string);
    pool1.add(stringRequired);
    pool2.add(string2);
    pool2.add(stringRequired2);
    pool3.add(string3);
    pool3.add(stringRequired3);

    assertThat(combined.requiredParams()).containsOnly(stringRequired, stringRequired2,
        stringRequired3);
  }

  @Test
  public void removeDoesNotWorkOnCombined() throws Exception {
    pool1.add(string);
    try {
      combined.remove(string);
      fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  @Test
  public void hasCandidateForOptionalParamInFirstSubPool() throws Exception {
    pool1.add(stringRequired);

    assertThat(combined.hasCandidate()).isTrue();
    assertThat(combined.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOneNonRequiredParamInFirstSubPool() throws Exception {
    pool1.add(string);

    assertThat(combined.hasCandidate()).isTrue();
    assertThat(combined.candidate()).isSameAs(string);
  }

  @Test
  public void doesNotHaveCandidateForOptionalParamInEachSubPool() throws Exception {
    pool1.add(stringRequired);
    pool2.add(stringRequired2);
    pool3.add(stringRequired3);

    assertThat(combined.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateForOneNonRequiredParamInEachSubPool() throws Exception {
    pool1.add(string);
    pool2.add(string2);
    pool3.add(string3);
    assertThat(combined.hasCandidate()).isFalse();
  }

  @Test
  public void hasCandidateForOptionalParamInFirstAndOneNonRequiredParamInSecondSubPool()
      throws Exception {
    pool1.add(stringRequired);
    pool2.add(string);

    assertThat(combined.hasCandidate()).isTrue();
    assertThat(combined.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOptionalParamInFirstAndTwoNonRequiredParamInSecondSubPool()
      throws Exception {
    pool1.add(stringRequired);
    pool2.add(string);
    pool2.add(string2);

    assertThat(combined.hasCandidate()).isTrue();
    assertThat(combined.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void doesNotHaveCandidateWhenNoParamIsAdded() throws Exception {
    assertThat(combined.hasCandidate()).isFalse();
  }

  @Test
  public void sizeOfEmptyCombinedPoolIsZero() throws Exception {
    assertThat(combined.size()).isEqualTo(0);
  }

  @Test
  public void sizeOfCombinedPool() throws Exception {
    pool1.add(string);
    pool2.add(stringRequired);

    assertThat(combined.size()).isEqualTo(2);
  }
}
