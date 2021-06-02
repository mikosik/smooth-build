package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

public class AnyTest extends TestingContext {
  @Test
  public void spec_of_any_is_any() {
    assertThat(any(Hash.of(1234)).spec())
        .isEqualTo(anySpec());
  }

  @Test
  public void any_has_hash_passed_to_factory_method() {
    Hash value = Hash.of(1234);
    Any any = any(value);
    assertThat(any.wrappedHash())
        .isEqualTo(value);
  }

  @Test
  public void anys_with_equal_content_are_equal() {
    assertThat(any(Hash.of(1234)))
        .isEqualTo(any(Hash.of(1234)));
  }

  @Test
  public void anys_with_different_content_are_not_equal() {
    assertThat(any(Hash.of(1234)))
        .isNotEqualTo(any(Hash.of(4321)));
  }

  @Test
  public void hash_of_anys_with_equal_content_is_the_same() {
    assertThat(any(Hash.of(1234)).hash())
        .isEqualTo(any(Hash.of(1234)).hash());
  }

  @Test
  public void hash_of_anys_with_different_content_is_not_the_same() {
    assertThat(any(Hash.of(1234)).hash())
        .isNotEqualTo(any(Hash.of(4321)).hash());
  }

  @Test
  public void hash_code_of_anys_with_equal_content_is_the_same() {
    assertThat(any(Hash.of(1234)).hashCode())
        .isEqualTo(any(Hash.of(1234)).hashCode());
  }

  @Test
  public void hash_code_of_anys_with_different_values_is_not_the_same() {
    assertThat(any(Hash.of(1234)).hashCode())
        .isNotEqualTo(any(Hash.of(4321)).hashCode());
  }

  @Test
  public void any_can_be_read_by_hash() {
    Any any = any(Hash.of(1234));
    Hash hash = any.hash();
    assertThat(objectDbOther().get(hash))
        .isEqualTo(any);
  }

  @Test
  public void any_read_by_hash_has_same_content() {
    Any any = any(Hash.of(1234));
    Hash hash = any.hash();
    assertThat(((Any) objectDbOther().get(hash)).wrappedHash())
        .isEqualTo(any.wrappedHash());
  }

  @Test
  public void to_string() {
    Hash wrappedHash = Hash.of(1234);
    Any any = any(wrappedHash);
    assertThat(any.toString())
        .isEqualTo("Any(" + wrappedHash + "):" + any.hash());
  }
}
