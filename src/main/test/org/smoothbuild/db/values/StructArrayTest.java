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
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TestingTypes;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Struct;

import com.google.common.hash.HashCode;

public class StructArrayTest {
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
  public void type_of_struct_array_is_struct_array() throws Exception {
    given(array = valuesDb.arrayBuilder(personType()).build());
    when(array.type());
    thenReturned(typesDb.array(personType()));
  }

  @Test
  public void reading_elements_from_not_stored_struct_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = typesDb.array(personType()).newValue(hash));
    when(array).asIterable(Struct.class);
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }

  private StructType personType() {
    return TestingTypes.personType(typesDb);
  }
}
