package org.smoothbuild.db.hashed;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import com.google.common.hash.HashCode;

public class HashedDbTest {
  private final byte[] bytes1 = new byte[] { 1 };
  private final byte[] bytes2 = new byte[] { 1, 2 };
  private HashCode hash;
  private HashedDb hashedDb;
  private Marshaller marshaller;
  private HashCode hashId;
  private Unmarshaller unmarshaller;

  @Before
  public void before() {
    hashedDb = new HashedDb(new MemoryFileSystem());
  }

  @Test
  public void db_doesnt_contain_not_stored_data() throws Exception {
    given(hashedDb = new HashedDb(new MemoryFileSystem()));
    when(hashedDb.contains(HashCode.fromInt(33)));
    thenReturned(false);
  }

  @Test
  public void db_contains_added_data() throws Exception {
    given(hashId = new Marshaller(hashedDb).closeMarshaller());
    when(hashedDb.contains(hashId));
    thenReturned(true);
  }

  @Test
  public void written_single_byte_can_be_read_back() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(17);
    given(hashId = marshaller.closeMarshaller());
    when(hashedDb.newUnmarshaller(hashId).read());
    thenReturned(17);
  }

  @Test
  public void written_byte_array_can_be_read_back() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(bytes1);
    given(hashId = marshaller.closeMarshaller());
    when(inputStreamToBytes(hashedDb.newUnmarshaller(hashId)));
    thenReturned(bytes1);
  }

  @Test
  public void written_byte_array_with_range_can_be_read_back() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(new byte[] { 1, 2, 3, 4, 5 }, 1, 3);
    given(hashId = marshaller.closeMarshaller());
    when(inputStreamToBytes(hashedDb.newUnmarshaller(hashId)));
    thenReturned(new byte[] { 2, 3, 4 });
  }

  @Test
  public void written_empty_byte_array_can_be_read_back() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(hashId = marshaller.closeMarshaller());
    when(inputStreamToBytes(hashedDb.newUnmarshaller(hashId)));
    thenReturned(new byte[] {});
  }

  @Test
  public void written_hash_can_be_read_back() {
    given(hash = Hash.integer(17));
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).writeHash(hash);
    given(hashId = marshaller.closeMarshaller());
    when(hashedDb.newUnmarshaller(hashId).readHash());
    thenReturned(hash);
  }

  @Test
  public void written_int_can_be_read_back() throws Exception {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).writeInt(0x12345678);
    given(hashId = marshaller.closeMarshaller());
    when(hashedDb.newUnmarshaller(hashId).readInt());
    thenReturned(0x12345678);
  }

  @Test
  public void reading_int_when_db_has_too_few_bytes_causes_exception() throws Exception {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(new byte[1]);
    given(hashId = marshaller.closeMarshaller());
    given(unmarshaller = hashedDb.newUnmarshaller(hashId));
    when(unmarshaller).readInt();
    thenThrown(exception(new HashedDbException(corruptedMessage("int", hashId, 4, 1))));
  }

  @Test
  public void written_byte_array_at_given_hash_can_be_read_back() throws IOException {
    given(hashId = Hash.integer(33));
    given(marshaller = new Marshaller(hashedDb, hashId));
    given(marshaller).write(bytes1);
    given(marshaller.closeMarshaller());
    when(inputStreamToBytes(hashedDb.newUnmarshaller(hashId)));
    thenReturned(bytes1);
  }

  @Test
  public void bytes_written_twice_can_be_read_back() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(bytes1);
    given(marshaller.closeMarshaller());
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(bytes1);
    given(hashId = marshaller.closeMarshaller());
    when(inputStreamToBytes(hashedDb.newUnmarshaller(hashId)));
    thenReturned(bytes1);
  }

  @Test
  public void storing_bytes_at_already_used_hash_is_ignored() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(bytes1);
    given(hashId = marshaller.closeMarshaller());
    given(marshaller = new Marshaller(hashedDb, hashId));
    given(marshaller).write(bytes2);
    given(marshaller.closeMarshaller());
    when(inputStreamToBytes(hashedDb.newUnmarshaller(hashId)));
    thenReturned(bytes1);
  }

  @Test
  public void hases_for_different_data_are_different() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(bytes1);
    given(hashId = marshaller.closeMarshaller());
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(bytes2);
    when(marshaller.closeMarshaller());
    thenReturned(not(hashId));
  }

  @Test
  public void reading_not_stored_value_fails() throws Exception {
    given(hashId = HashCode.fromInt(33));
    when(hashedDb).newUnmarshaller(hashId);
    thenThrown(exception(new HashedDbException("Could not find " + hashId + " object.")));
  }

  @Test
  public void reading_hash_when_db_has_too_few_bytes_causes_exception() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(new byte[1]);
    given(hashId = marshaller.closeMarshaller());
    when(hashedDb.newUnmarshaller(hashId)).readHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hashId, 20, 1))));
  }

  @Test
  public void reading_hash_when_db_has_zero_bytes_causes_exception() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(new byte[0]);
    given(hashId = marshaller.closeMarshaller());
    when(hashedDb.newUnmarshaller(hashId)).readHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hashId, 20, 0))));
  }

  @Test
  public void trying_to_read_hash_when_db_has_zero_bytes_returns_null() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(new byte[0]);
    given(hashId = marshaller.closeMarshaller());
    when(hashedDb.newUnmarshaller(hashId)).tryReadHash();
    thenReturned(null);
  }

  @Test
  public void trying_to_read_hash_when_db_has_too_few_bytes_causes_exception() throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(new byte[1]);
    given(hashId = hashedDb.write(new byte[1]));
    when(hashedDb.newUnmarshaller(hashId)).tryReadHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hashId, 20, 1))));
  }

  @Test
  public void trying_to_read_hash_twice_when_only_one_is_stored_returns_null_second_time()
      throws IOException {
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).writeHash(Hash.integer(17));
    given(hashId = marshaller.closeMarshaller());
    given(unmarshaller = hashedDb.newUnmarshaller(hashId));
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
