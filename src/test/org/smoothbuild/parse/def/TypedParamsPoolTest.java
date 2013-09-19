package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;

import org.junit.Test;
import org.smoothbuild.function.base.Param;

public class TypedParamsPoolTest {
  Param string = param(STRING, "string1");
  Param string2 = param(STRING, "string2");
  Param stringRequired = param(STRING, "stringRequired", true);
  Param stringRequired2 = param(STRING, "stringRequired2", true);

  TypedParamsPool pool = new TypedParamsPool();

  TypedParamsPool pool1 = new TypedParamsPool();
  TypedParamsPool pool2 = new TypedParamsPool();
  TypedParamsPool combined = new TypedParamsPool(pool1, pool2);

  @Test
  public void requiredParams() throws Exception {
    pool.add(string);
    pool.add(string2);
    pool.add(stringRequired);
    pool.add(stringRequired2);

    assertThat(pool.requiredParams()).containsOnly(stringRequired, stringRequired2);
  }

  @Test
  public void requiredParamsIsEmptyWhenNoParamWasAdded() throws Exception {
    assertThat(pool.requiredParams()).isEmpty();
  }

  @Test
  public void removeReturnsTrueWhenParamWasPresent() throws Exception {
    pool.add(string);
    assertThat(pool.remove(string)).isTrue();
  }

  @Test
  public void removeReturnsFalseWhenParamWasPresent() throws Exception {
    assertThat(pool.remove(string)).isFalse();
  }

  @Test
  public void removedRequiredParam() throws Exception {
    pool.add(stringRequired);
    pool.add(stringRequired2);

    pool.remove(stringRequired);

    assertThat(pool.requiredParams()).containsOnly(stringRequired2);
  }

  @Test
  public void removedNonRequiredParam() throws Exception {
    pool.add(string);
    pool.add(string2);

    assertThat(pool.remove(string)).isTrue();
  }

  @Test
  public void hasCandidateForOneRequiredParam() throws Exception {
    pool.add(stringRequired);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOneNonRequiredParam() throws Exception {
    pool.add(string);

    assertThat(pool.hasCandidate()).isTrue();
    assertThat(pool.candidate()).isSameAs(string);
  }

  @Test
  public void hasCandidateForOneRequiredAndOneNonRequiredParam() throws Exception {
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
  public void haveCandidateWhenOneRequiredAndTwoNonRequiredParamsExist() throws Exception {
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

    assertThat(combined.requiredParams()).containsOnly(stringRequired, stringRequired2);
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
  public void hasCandidateForOneRequiredParamInFirstSubPool() throws Exception {
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
  public void doesNotHaveCandidateForOneRequiredParamInEachSubPool() throws Exception {
    pool1.add(stringRequired);
    pool2.add(stringRequired2);
    assertThat(combined.hasCandidate()).isFalse();
  }

  @Test
  public void doesNotHaveCandidateForOneNonRequiredParamInEachSubPool() throws Exception {
    pool1.add(string);
    pool2.add(string2);
    assertThat(combined.hasCandidate()).isFalse();
  }

  @Test
  public void hasCandidateForOneRequiredParamInFirstAndOneNonRequiredParamInSecondSubPool()
      throws Exception {
    pool1.add(stringRequired);
    pool2.add(string);

    assertThat(combined.hasCandidate()).isTrue();
    assertThat(combined.candidate()).isSameAs(stringRequired);
  }

  @Test
  public void hasCandidateForOneRequiredParamInFirstAndTwoNonRequiredParamInSecondSubPool()
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
