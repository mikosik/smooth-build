package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.emptyIterable;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class NilTest {
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private Array array;

  @Before
  public void before() {
    hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.generic("b")).build());
    when(array.type());
    thenReturned(typesDb.array(typesDb.generic("b")));
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(() -> valuesDb.arrayBuilder(typesDb.generic("b")).build().asIterable(Value.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_can_be_read_by_hash() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.generic("b")).build());
    when(() -> new TestingValuesDb(hashedDb).get(array.hash()));
    thenReturned(array);
  }

  @Test
  public void nil_read_by_hash_has_no_elements() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.generic("b")).build());
    when(() -> ((Array) new TestingValuesDb(hashedDb).get(array.hash())).asIterable(Value.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_to_string() throws Exception {
    given(array = valuesDb.arrayBuilder(typesDb.generic("b")).build());
    when(() -> array.toString());
    thenReturned("[b](...):" + array.hash());
  }
}
