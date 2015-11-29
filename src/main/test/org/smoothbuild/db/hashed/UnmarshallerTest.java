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

public class UnmarshallerTest {
  private final HashedDb hashedDb = memoryHashedDb();
  private Marshaller marshaller;
  private Unmarshaller unmarshaller;
  private HashCode hash;
  private byte myByte;
  private int myInt;

  @After
  public void after() {
    if (unmarshaller != null) {
      unmarshaller.close();
    }
  }

  @Test
  public void marshalled_hash_can_be_unmarshalled() {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(hash);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenReturned(hash);
  }

  @Test
  public void too_short_hash_in_db_causes_exception() {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(0x12345678);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValueException.class);
  }

  @Test
  public void marshalled_false_bool_value_can_be_unmarshalled() {
    given(marshaller = new Marshaller());
    given(marshaller).write(false);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenReturned(false);
  }

  @Test
  public void marshalled_true_bool_value_can_be_unmarshalled() {
    given(marshaller = new Marshaller());
    given(marshaller).write(true);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenReturned(true);
  }

  @Test
  public void unmarshalling_corrupted_bool_throws_exception() {
    given(marshaller = new Marshaller());
    given(marshaller).write(33);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readBool();
    thenThrown(CorruptedBoolException.class);
  }

  @Test
  public void marshalled_byte_can_be_unmarshalled() {
    given(myByte = 123);
    given(marshaller = new Marshaller());
    given(marshaller).write(myByte);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readByte();
    thenReturned((byte) 123);
  }

  @Test
  public void marshalled_int_can_be_unmarshalled() {
    given(myInt = 0x12345678);
    given(marshaller = new Marshaller());
    given(marshaller).write(myInt);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readInt();
    thenReturned(myInt);
  }

  @Test
  public void too_short_int_in_db_causes_exception() throws Exception {
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write((byte) 1);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValueException.class);
  }

  @Test
  public void marshalled_all_type_of_objects_can_be_unmarshalled() {
    given(myInt = 0x12345678);
    given(myByte = 123);
    given(hash = Hash.integer(33));
    given(marshaller = new Marshaller());
    given(marshaller).write(myInt);
    given(marshaller).write(myByte);
    given(marshaller).write(hash);
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller).readInt();
    thenReturned(myInt);
    when(unmarshaller).readByte();
    thenReturned(myByte);
    when(unmarshaller).readHash();
    thenReturned(hash);
  }

  @SuppressWarnings("resource")
  @Test(expected = NoObjectWithGivenHashException.class)
  public void unmarshallling_not_stored_value_fails() throws Exception {
    new Unmarshaller(hashedDb, Hash.string("abc"));
  }
}
