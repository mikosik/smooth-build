package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.HashedDb.memoryHashedDb;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.After;
import org.junit.Test;
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
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(hashCode);
    given(hash = marshaller.close());
    when(new Unmarshaller(hashedDb, hash).readHash());
    thenReturned(hashCode);
  }

  private static HashCode hashOfProperSize() {
    return HashCode.fromBytes(new byte[Hash.size()]);
  }

  @Test
  public void too_short_hash_in_db_causes_exception() {
    given(hashedDb = memoryHashedDb());
    given(marshaller = new Marshaller(hashedDb));
    given(marshaller).write(0x12345678);
    given(hash = marshaller.close());
    given(unmarshaller = new Unmarshaller(hashedDb, hash));
    when(unmarshaller).readHash();
    thenThrown(TooFewBytesToUnmarshallValueException.class);
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
    given(unmarshaller = new Unmarshaller(hashedDb, hashedDb.write(new byte[1])));
    when(unmarshaller).readInt();
    thenThrown(TooFewBytesToUnmarshallValueException.class);
  }

  @Test()
  public void unmarshallling_not_stored_value_fails() throws Exception {
    given(hashedDb = memoryHashedDb());
    when(() -> new Unmarshaller(hashedDb, Hash.string("abc")));
    thenThrown(NoObjectWithGivenHashException.class);
  }
}
