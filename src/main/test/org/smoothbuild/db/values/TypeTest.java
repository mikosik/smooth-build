package org.smoothbuild.db.values;

import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;

public class TypeTest {
  private TypeSystem typeSystem;
  private ValuesDb valuesDb;

  @Before
  public void before() {
    HashedDb hashedDb = new HashedDb();
    typeSystem = new TypeSystem(new TypesDb(hashedDb));
    valuesDb = new ValuesDb(hashedDb, typeSystem);
  }

  @Test
  public void type_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.type());
  }

  @Test
  public void string_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.string());
  }

  @Test
  public void blob_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.blob());
  }

  @Test
  public void file_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.file());
  }

  @Test
  public void nothing_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.nothing());
  }

  @Test
  public void array_of_types_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.array(typeSystem.type()));
  }

  @Test
  public void array_of_strings_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.array(typeSystem.string()));
  }

  @Test
  public void array_of_blobs_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.array(typeSystem.blob()));
  }

  @Test
  public void array_of_files_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.array(typeSystem.file()));
  }

  @Test
  public void array_of_nothings_type_can_be_get_from_values_db_via_hash() throws Exception {
    assertTypeCanBeRetrievedByHash(typeSystem.array(typeSystem.nothing()));
  }

  private void assertTypeCanBeRetrievedByHash(Type type) {
    when(() -> valuesDb.get(type.hash()));
    thenReturned(type);
  }
}
