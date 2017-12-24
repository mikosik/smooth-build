package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.collect.ImmutableMap;

public class TypesDbTest {
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private ImmutableMap<String, Type> fields;
  private Type type;

  @Before
  public void before() {
    given(hashedDb = new HashedDb());
    given(typesDb = new TypesDb(hashedDb));
  }

  @Test
  public void type_type_can_be_read_back() throws Exception {
    given(type = typesDb.type());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void string_type_can_be_read_back() throws Exception {
    given(type = typesDb.string());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void blob_type_can_be_read_back() throws Exception {
    given(type = typesDb.blob());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void nothing_type_can_be_read_back() throws Exception {
    given(type = typesDb.nothing());
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void array_of_string_type_can_be_read_back() throws Exception {
    given(type = typesDb.array(typesDb.string()));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void array_of_array_of_string_type_can_be_read_back() throws Exception {
    given(type = typesDb.array(typesDb.array(typesDb.string())));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void struct_type_can_be_read_back() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(type = typesDb.struct("TypeName", fields));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void two_level_deep_struct_type_can_be_read_back() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(type = typesDb.struct("TypeName", fields));
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", type));
    given(type = typesDb.struct("TypeName2", fields));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }
}
