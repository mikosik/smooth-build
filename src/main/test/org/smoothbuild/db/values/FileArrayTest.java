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
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Struct;

import com.google.common.hash.HashCode;

public class FileArrayTest {
  private ValuesDb valuesDb;
  private Array array;
  private HashCode hash;
  private TypeSystem typeSystem;

  @Before
  public void before() {
    HashedDb hashedDb = new HashedDb();
    typeSystem = new TypeSystem(new TypesDb(hashedDb));
    valuesDb = new ValuesDb(hashedDb, typeSystem);
  }

  @Test
  public void type_of_file_array_is_file_array() throws Exception {
    given(array = valuesDb.arrayBuilder(typeSystem.file()).build());
    when(array.type());
    thenReturned(typeSystem.array(typeSystem.file()));
  }

  @Test
  public void reading_elements_from_not_stored_file_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = valuesDb.read(typeSystem.array(typeSystem.file()), hash));
    when(array).asIterable(Struct.class);
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
