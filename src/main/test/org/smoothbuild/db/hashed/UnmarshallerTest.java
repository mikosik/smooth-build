package org.smoothbuild.db.hashed;

import static java.util.Arrays.asList;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.After;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.CorruptedBoolException;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashException;
import org.smoothbuild.db.hashed.err.TooFewBytesToUnmarshallValueException;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class UnmarshallerTest {
  private final ValuesDb valuesDb = valuesDb();
  private final FileSystem fileSystem = new MemoryFileSystem();
  private final HashedDb hashedDb = new HashedDb(fileSystem);
  private SString hashed1;
  private SString hashed2;
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
  public void marshalled_hashed_list_can_be_unmarshalled() {
    given(hashed1 = valuesDb.string("abc"));
    given(hashed2 = valuesDb.string("def"));
    given(marshaller = new Marshaller());
    given(marshaller).write(asList(hashed1, hashed2));
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(marshaller.getBytes())));
    when(unmarshaller.readHashList());
    thenReturned(asList(hashed1.hash(), hashed2.hash()));
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
