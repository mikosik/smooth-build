package org.smoothbuild.db.values;

import static org.hamcrest.Matchers.emptyIterable;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

public class NilTest {
  private HashedDb hashedDb;
  private TypeSystem typeSystem;
  private ValuesDb valuesDb;
  private Array array;

  @Before
  public void before() {
    hashedDb = new HashedDb();
    typeSystem = new TypeSystem(new TypesDb(hashedDb));
    valuesDb = new ValuesDb(hashedDb, typeSystem);
  }

  @Test
  public void type_of_nil_is_nil() throws Exception {
    given(array = valuesDb.arrayBuilder(typeSystem.nothing()).build());
    when(array.type());
    thenReturned(typeSystem.array(typeSystem.nothing()));
  }

  @Test
  public void nil_array_is_empty() throws Exception {
    when(() -> valuesDb.arrayBuilder(typeSystem.nothing()).build().asIterable(Value.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_can_be_read_by_hash() throws Exception {
    given(array = valuesDb.arrayBuilder(typeSystem.nothing()).build());
    when(() -> new ValuesDb(hashedDb).get(array.hash()));
    thenReturned(array);
  }

  @Test
  public void nil_read_by_hash_has_no_elements() throws Exception {
    given(array = valuesDb.arrayBuilder(typeSystem.nothing()).build());
    when(() -> ((Array) new ValuesDb(hashedDb).get(array.hash())).asIterable(Value.class));
    thenReturned(emptyIterable());
  }

  @Test
  public void nil_to_string() throws Exception {
    given(array = valuesDb.arrayBuilder(typeSystem.nothing()).build());
    when(() -> array.toString());
    thenReturned("[Nothing](...):" + array.hash());
  }
}
