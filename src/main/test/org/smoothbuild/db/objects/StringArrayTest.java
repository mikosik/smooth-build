package org.smoothbuild.db.objects;

import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class StringArrayTest {
  private ObjectsDb objectsDb;
  private Array<?> array;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void type_of_string_array_is_string_array() throws Exception {
    given(array = objectsDb.arrayBuilder(SString.class).build());
    when(array.type());
    thenReturned(STRING_ARRAY);
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(array = (Array<SString>) objectsDb.read(STRING_ARRAY, HashCode.fromInt(33)));
    when(array).iterator();
    thenThrown(NoObjectWithGivenHashError.class);
  }
}
