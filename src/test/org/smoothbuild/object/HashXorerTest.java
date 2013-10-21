package org.smoothbuild.object;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.hash.HashCode;

public class HashXorerTest {
  HashXorer hashXorer = new HashXorer(2);

  @Test
  public void xor_with_only_one_hash_is_equal_to_hash() {
    given(hashXorer).xorWith(hash("1234"));
    when(hashXorer).hash();
    thenReturned(hash("1234"));
  }

  @Test
  public void xoring_zero_with_zerp() {
    given(hashXorer).xorWith(hash("0000"));
    given(hashXorer).xorWith(hash("0000"));
    when(hashXorer).hash();
    thenReturned(hash("0000"));
  }

  @Test
  public void xoring_zero_with_one() {
    given(hashXorer).xorWith(hash("0000"));
    given(hashXorer).xorWith(hash("0001"));
    when(hashXorer).hash();
    thenReturned(hash("0001"));
  }

  @Test
  public void xoring_one_with_zero() {
    given(hashXorer).xorWith(hash("0000"));
    given(hashXorer).xorWith(hash("0001"));
    when(hashXorer).hash();
    thenReturned(hash("0001"));
  }

  @Test
  public void xoring_one_with_one() {
    given(hashXorer).xorWith(hash("0001"));
    given(hashXorer).xorWith(hash("0001"));
    when(hashXorer).hash();
    thenReturned(hash("0000"));
  }

  private static HashCode hash(String value) {
    return HashCode.fromString(value);
  }
}
