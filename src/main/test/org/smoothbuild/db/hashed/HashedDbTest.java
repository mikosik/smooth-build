package org.smoothbuild.db.hashed;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.common.hash.HashCode;

public class HashedDbTest {
  private final byte[] bytes1 = new byte[] { 1 };
  private final byte[] bytes2 = new byte[] { 1, 2 };
  private HashCode hash;
  private HashedDb hashedDb;
  private Marshaller marshaller;
  private HashCode hashId;
  private Unmarshaller unmarshaller;
  private MemoryFileSystem fileSystem;

  @Before
  public void before() {
    given(fileSystem = new MemoryFileSystem());
    given(hashedDb = new HashedDb(fileSystem, Path.root(), new TempManager(fileSystem)));
  }

  @Test
  public void db_doesnt_contain_not_stored_data() throws Exception {
    when(hashedDb.contains(HashCode.fromInt(33)));
    thenReturned(false);
  }

  @Test
  public void db_contains_added_data() throws Exception {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).close();
    when(hashedDb.contains(marshaller.hash()));
    thenReturned(true);
  }

  @Test
  public void written_single_byte_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(17);
    given(marshaller).close();
    when(hashedDb.newUnmarshaller(marshaller.hash()).read());
    thenReturned(17);
  }

  @Test
  public void written_byte_array_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(bytes1);
    given(marshaller).close();
    when(toByteArray(hashedDb.newUnmarshaller(marshaller.hash())));
    thenReturned(bytes1);
  }

  @Test
  public void written_byte_array_with_range_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(new byte[] { 1, 2, 3, 4, 5 }, 1, 3);
    given(marshaller).close();
    when(toByteArray(hashedDb.newUnmarshaller(marshaller.hash())));
    thenReturned(new byte[] { 2, 3, 4 });
  }

  @Test
  public void written_empty_byte_array_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).close();
    when(toByteArray(hashedDb.newUnmarshaller(marshaller.hash())));
    thenReturned(new byte[] {});
  }

  @Test
  public void written_hash_can_be_read_back() {
    given(hash = Hash.integer(17));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).writeHash(hash);
    given(marshaller).close();
    when(hashedDb.newUnmarshaller(marshaller.hash()).readHash());
    thenReturned(hash);
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).writeInt(0x12345678);
    given(marshaller).close();
    when(hashedDb.newUnmarshaller(marshaller.hash()).readInt());
    thenReturned(0x12345678);
  }

  @Test
  public void reading_int_when_db_has_too_few_bytes_causes_exception() throws Exception {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(new byte[1]);
    given(marshaller).close();
    given(hashId = marshaller.hash());
    given(unmarshaller = hashedDb.newUnmarshaller(hashId));
    when(unmarshaller).readInt();
    thenThrown(exception(new HashedDbException(corruptedMessage("int", hashId, 4, 1))));
  }

  @Test
  public void written_byte_array_at_given_hash_can_be_read_back() throws IOException {
    given(hashId = Hash.integer(33));
    given(marshaller = hashedDb.newMarshaller(hashId));
    given(marshaller).write(bytes1);
    given(marshaller).close();
    when(toByteArray(hashedDb.newUnmarshaller(marshaller.hash())));
    thenReturned(bytes1);
  }

  @Test
  public void bytes_written_twice_can_be_read_back() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(bytes1);
    given(marshaller).close();
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(bytes1);
    given(marshaller).close();
    when(toByteArray(hashedDb.newUnmarshaller(marshaller.hash())));
    thenReturned(bytes1);
  }

  @Test
  public void storing_bytes_at_already_used_hash_is_ignored() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(bytes1);
    given(marshaller).close();
    given(marshaller = hashedDb.newMarshaller(marshaller.hash()));
    given(marshaller).write(bytes2);
    given(marshaller).close();
    when(toByteArray(hashedDb.newUnmarshaller(marshaller.hash())));
    thenReturned(bytes1);
  }

  @Test
  public void hases_for_different_data_are_different() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(bytes1);
    given(marshaller).close();
    given(hashId = marshaller.hash());
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(bytes2);
    given(marshaller).close();
    when(marshaller.hash());
    thenReturned(not(hashId));
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    given(hashId = HashCode.fromInt(33));
    when(hashedDb).newUnmarshaller(hashId);
    thenThrown(exception(new HashedDbException("Could not find " + hashId + " object.")));
  }

  @Test
  public void written_data_is_not_visible_until_close_is_invoked() throws Exception {
    given(hashId = Hash.integer(17));
    given(marshaller = hashedDb.newMarshaller(hashId));
    given(marshaller).write(new byte[1024 * 1024]);
    when(hashedDb).newUnmarshaller(hashId);
    thenThrown(exception(new HashedDbException("Could not find " + hashId + " object.")));
  }

  @Test
  public void reading_hash_when_db_has_too_few_bytes_causes_exception() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(new byte[1]);
    given(marshaller).close();
    given(hashId = marshaller.hash());
    when(hashedDb.newUnmarshaller(hashId)).readHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hashId, 20, 1))));
  }

  @Test
  public void reading_hash_when_db_has_zero_bytes_causes_exception() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(new byte[0]);
    given(marshaller).close();
    given(hashId = marshaller.hash());
    when(hashedDb.newUnmarshaller(hashId)).readHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hashId, 20, 0))));
  }

  @Test
  public void trying_to_read_hash_when_db_has_zero_bytes_returns_null() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(new byte[0]);
    given(marshaller).close();
    when(hashedDb.newUnmarshaller(marshaller.hash())).tryReadHash();
    thenReturned(null);
  }

  @Test
  public void trying_to_read_hash_when_db_has_too_few_bytes_causes_exception() throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).write(new byte[1]);
    given(marshaller).close();
    given(hashId = marshaller.hash());
    when(hashedDb.newUnmarshaller(hashId)).tryReadHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hashId, 20, 1))));
  }

  @Test
  public void trying_to_read_hash_twice_when_only_one_is_stored_returns_null_second_time()
      throws IOException {
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller).writeHash(Hash.integer(17));
    given(marshaller).close();
    given(unmarshaller = hashedDb.newUnmarshaller(marshaller.hash()));
    given(unmarshaller).tryReadHash();
    when(unmarshaller).tryReadHash();
    thenReturned(null);
  }

  private static String corruptedMessage(String valueName, HashCode hash, int expected,
      int available) {
    return "Corrupted " + hash + " object. Value " + valueName + " has expected size = " + expected
        + " but only " + available + " is available.";
  }
}
