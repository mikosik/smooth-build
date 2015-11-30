package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.HashedDb.memoryHashedDb;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.After;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.CorruptedBoolException;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashException;
import org.smoothbuild.db.hashed.err.TooFewBytesToUnmarshallValueException;

import com.google.common.hash.HashCode;

public class MarshallingTest {
  private HashedDb hashedDb;
  private Marshaller marshaller;
  private HashCode hashCode;
  private HashCode hash;
  private Unmarshaller unmarshaller;

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
    given(marshaller = new Marshaller());
    given(marshaller).write(hashCode);
    given(hash = hashedDb.write(marshaller.getBytes()));
    when(new Unmarshaller(hashedDb, hash).readHash());
    thenReturned(hashCode);
  }

  @Test
  public void too_short_hash_in_db_causes_exception() {
    given(hashedDb = memoryHashedDb());
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(0x12345678);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValueException.class);
  }

  private static HashCode hashOfProperSize() {
    return HashCode.fromBytes(new byte[Hash.size()]);
  }

  @Test
  public void marshalling_byte() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 123);
    given(hash = hashedDb.write(marshaller.getBytes()));
    when(new Unmarshaller(hashedDb, hash).readByte());
    thenReturned((byte) 123);
  }

  @Test
  public void marshalling_true_boolean() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller());
    given(marshaller).write(true);
    given(hash = hashedDb.write(marshaller.getBytes()));
    when(new Unmarshaller(hashedDb, hash).readBool());
    thenReturned(true);
  }

  @Test
  public void marshalling_false_boolean() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller());
    given(marshaller).write(false);
    given(hash = hashedDb.write(marshaller.getBytes()));
    when(new Unmarshaller(hashedDb, hash).readBool());
    thenReturned(false);
  }

  @Test
  public void unmarshalling_corrupted_bool_throws_exception() {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller());
    given(marshaller).write(33);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenThrown(CorruptedBoolException.class);
  }

  @Test
  public void marshalling_ints() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller());
    given(marshaller).write(0x12345678);
    given(hash = hashedDb.write(marshaller.getBytes()));
    when(new Unmarshaller(hashedDb, hash).readInt());
    thenReturned(0x12345678);
  }

  @Test
  public void too_short_int_in_db_causes_exception() throws Exception {
    given(hashedDb = memoryHashedDb());
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 1);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValueException.class);
  }

  @Test()
  public void unmarshallling_not_stored_value_fails() throws Exception {
    given(hashedDb = memoryHashedDb());
    when(() -> new Unmarshaller(hashedDb, Hash.string("abc")));
    thenThrown(NoObjectWithGivenHashException.class);
  }
}
