package org.smoothbuild.db.objects;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Nothing;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class NilTest {
  private ObjectsDb objectsDb;
  private Array<?> array;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestObjectsDbModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
  }

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = objectsDb.arrayBuilder(Nothing.class).build());
    when(array.type());
    thenReturned(NIL);
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(objectsDb.arrayBuilder(Nothing.class).build());
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_to_string_contains_square_brackets() throws Exception {
    when(objectsDb.arrayBuilder(Nothing.class).build().toString());
    thenReturned("[]");
  }

}
