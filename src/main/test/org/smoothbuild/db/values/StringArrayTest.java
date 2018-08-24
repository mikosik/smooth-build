package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class StringArrayTest {
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private Array array;
  private HashCode hash;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void type_of_string_array_is_string_array() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.string()).build());
    when(array.type());
    thenReturned(typesDb.array(typesDb.string()));
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = typesDb.array(typesDb.string()).newValue(hash));
    when(array).asIterable(SString.class);
    thenThrown(ValuesDbException.class);
  }
}
