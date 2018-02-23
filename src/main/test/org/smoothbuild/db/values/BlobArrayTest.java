package org.smoothbuild.db.values;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobArrayTest {
  private ValuesDb valuesDb;
  private Array array;
  private HashCode hash;
  private TypesDb typesDb;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void type_of_blob_array_is_blob_array() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.blob()).build());
    when(array.type());
    thenReturned(typesDb.array(typesDb.blob()));
  }

  @Test
  public void reading_elements_from_not_stored_blob_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = typesDb.array(typesDb.blob()).newValue(hash));
    when(array).asIterable(Blob.class);
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
