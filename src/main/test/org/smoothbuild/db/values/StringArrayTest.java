package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class StringArrayTest {
  private ValuesDb valuesDb;
  private Array<?> array;
  private HashCode hash;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestValuesDbModule());
    valuesDb = injector.getInstance(ValuesDb.class);
  }

  @Test
  public void type_of_string_array_is_string_array() throws Exception {
    given(array = valuesDb.arrayBuilder(SString.class).build());
    when(array.type());
    thenReturned(STRING_ARRAY);
  }

  @Test
  public void reading_elements_from_not_stored_string_array_fails() throws Exception {
    given(hash = HashCode.fromInt(33));
    given(array = (Array<SString>) valuesDb.read(STRING_ARRAY, hash));
    when(array).iterator();
    thenThrown(exception(new HashedDbException("Could not find " + hash + " object.")));
  }
}
