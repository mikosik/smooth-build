package org.smoothbuild.object;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

import com.google.common.hash.HashCode;

public class HashedDbTest {
  byte[] bytes1 = new byte[] { 1 };
  byte[] bytes2 = new byte[] { 1, 2 };
  HashCode hash;

  FakeFileSystem fileSystem = new FakeFileSystem();
  HashedDb hashedDb = new HashedDb(fileSystem);

  @Test
  public void stored_bytes_can_be_read_back() throws IOException {
    given(hash = hashedDb.store(bytes1));
    when(inputStreamToBytes(hashedDb.openInputStream(hash)));
    thenReturned(bytes1);
  }

  @Test
  public void bytes_stored_twice_can_be_read_back() throws IOException {
    given(hash = hashedDb.store(bytes1));
    given(hash = hashedDb.store(bytes1));
    when(inputStreamToBytes(hashedDb.openInputStream(hash)));
    thenReturned(bytes1);
  }

  @Test
  public void hases_for_different_bytes_are_different() throws IOException {
    given(hash = hashedDb.store(bytes1));
    when(hashedDb.store(bytes2));
    thenReturned(not(hash));
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    when(hashedDb).openInputStream(HashCode.fromInt(33));
    thenThrown(containsInstanceOf(NoObjectWithGivenHashError.class));
  }
}
