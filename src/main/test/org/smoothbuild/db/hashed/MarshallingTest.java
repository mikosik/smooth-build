package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.HashedDb.memoryHashedDb;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.After;
import org.junit.Test;

import com.google.common.hash.HashCode;

public class MarshallingTest {
  private HashedDb hashedDb;
  private Marshaller marshaller;
  private HashCode hashCode;
  private HashCode hash;
  private Unmarshaller unmarshaller;
  private HashCode hashId;

  @After
  public void after() {
    if (unmarshaller != null) {
      unmarshaller.close();
    }
  }

  @Test
  public void marshalling_hash() {
    given(hashedDb = memoryHashedDb());
    given(hashCode = hashOfProperSize());
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(hashCode);
    given(hash = marshaller.close());
    when(new Unmarshaller(hashedDb, hash).readHash());
    thenReturned(hashCode);
  }

  @Test
  public void marshalling_hash_and_storing_at_given_hash() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(hashCode = hashOfProperSize());
    given(hashId = Hash.integer(33));
    given(marshaller = new Marshaller(hashedDb, hashId));
    given(marshaller).write(hashCode);
    when(marshaller.close());
    thenReturned(hashId);
    thenEqual(new Unmarshaller(hashedDb, hashId).readHash(), hashCode);
  }

  @Test
  public void reading_hash_when_db_has_too_few_bytes_causes_exception() {
    given(hashedDb = memoryHashedDb());
    given(hash = hashedDb.write(new byte[1]));
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    when(unmarshaller).readHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hash, 20, 1))));
  }

  @Test
  public void reading_hash_when_db_has_zero_bytes_causes_exception() {
    given(hashedDb = memoryHashedDb());
    given(hash = hashedDb.write(new byte[0]));
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    when(unmarshaller).readHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hash, 20, 0))));
  }

  @Test
  public void trying_to_read_hash_when_db_has_zero_bytes_returns_null() {
    given(hashedDb = memoryHashedDb());
    given(hash = hashedDb.write(new byte[0]));
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    when(unmarshaller).tryReadHash();
    thenReturned(null);
  }

  @Test
  public void trying_to_read_hash_when_db_has_too_few_bytes_causes_exception() {
    given(hashedDb = memoryHashedDb());
    given(hash = hashedDb.write(new byte[1]));
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    when(unmarshaller).tryReadHash();
    thenThrown(exception(new HashedDbException(corruptedMessage("hash", hash, 20, 1))));
  }

  @Test
  public void trying_to_read_hash_twice_when_only_one_is_stored_returns_null_second_time() {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(hashOfProperSize());
    given(hash = marshaller.close());
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    given(unmarshaller).tryReadHash();
    when(unmarshaller).tryReadHash();
    thenReturned(null);
  }

  @Test
  public void marshalling_int() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(0x12345678);
    given(hash = marshaller.close());
    when(new Unmarshaller(hashedDb, hash).readInt());
    thenReturned(0x12345678);
  }

  @Test
  public void too_short_int_in_db_causes_exception() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(hash = hashedDb.write(new byte[0]));
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    when(unmarshaller).readInt();
    thenThrown(exception(new HashedDbException(corruptedMessage("int", hash, 4, 0))));
  }

  @Test()
  public void unmarshallling_not_stored_value_fails() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(hash = Hash.string("abc"));
    when(() -> new Unmarshaller(hashedDb, hash));
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }

  private static HashCode hashOfProperSize() {
    return HashCode.fromBytes(new byte[Hash.size()]);
  }

  private static String corruptedMessage(String valueName, HashCode hash, int expected,
      int available) {
    return "Corrupted " + hash + " object. Value " + valueName + " has expected size = " + expected
        + " but only " + available + " is available.";
  }
}
