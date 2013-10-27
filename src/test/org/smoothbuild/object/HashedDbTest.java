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
import org.smoothbuild.hash.Hash;
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
  public void new_instance_does_not_contain_any_file() throws Exception {
    given(hashedDb = new HashedDb(fileSystem));
    when(hashedDb.contains(HashCode.fromInt(33)));
    thenReturned(false);
  }

  @Test
  public void hashed_db_contains_added_bytes() throws Exception {
    given(hash = hashedDb.store(bytes1));
    when(hashedDb.contains(hash));
    thenReturned(true);
  }

  @Test
  public void bytes_stored_can_be_read_back() throws IOException {
    given(hash = hashedDb.store(bytes1));
    when(inputStreamToBytes(hashedDb.openInputStream(hash)));
    thenReturned(bytes1);
  }

  @Test
  public void empty_byte_array_stored_can_be_read_back() throws IOException {
    given(bytes1 = new byte[] {});
    given(hash = hashedDb.store(bytes1));
    when(inputStreamToBytes(hashedDb.openInputStream(hash)));
    thenReturned(bytes1);
  }

  @Test
  public void bytes_stored_at_given_hash_can_be_read_back() throws IOException {
    given(hash = Hash.function().hashInt(33));
    given(hashedDb.store(hash, bytes1));
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
  public void storing_bytes_at_already_used_hash_is_ignored() throws IOException {
    given(hash = hashedDb.store(bytes1));
    given(hashedDb.store(Hash.function().hashInt(33), bytes2));
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
