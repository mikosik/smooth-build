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
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class BoolArrayTtest {
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
  public void type_of_bool_array_is_bool_array() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.bool()).build());
    when(array.type());
    thenReturned(typesDb.array(typesDb.bool()));
  }

  @Test
  public void reading_elements_from_not_stored_bool_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = typesDb.array(typesDb.bool()).newValue(hash));
    when(array).asIterable(Bool.class);
    thenThrown(ValuesDbException.class);
  }
}