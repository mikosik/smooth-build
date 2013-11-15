package org.smoothbuild.testing.lang.plugin;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.testing.lang.function.value.FakeHashed;

public class FakeHashedTest {
  FakeHashed fakeHashed;
  FakeHashed fakeHashed2;

  @Test
  public void fake_hashed_objects_built_with_different_strings_have_different_hash() {
    given(fakeHashed = new FakeHashed("abc"));
    given(fakeHashed2 = new FakeHashed("def"));
    when(fakeHashed.hash());
    thenReturned(not(fakeHashed2.hash()));
  }

  @Test
  public void fake_hashed_objects_built_with_different_bytes_have_different_hash() {
    given(fakeHashed = new FakeHashed(new byte[] { 1, 2, 3 }));
    given(fakeHashed2 = new FakeHashed(new byte[] { 4, 5, 6 }));
    when(fakeHashed.hash());
    thenReturned(not(fakeHashed2.hash()));
  }
}
