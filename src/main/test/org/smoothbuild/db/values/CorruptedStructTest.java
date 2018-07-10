package org.smoothbuild.db.values;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.TestingStructType;

import com.google.common.hash.HashCode;

public class CorruptedStructTest {
  private HashedDb hashedDb;
  private TypesDb typesDb;
  private ValuesDb valuesDb;
  private HashCode dataHash;
  private SString lastName;
  private SString firstName;
  private HashCode structHash;
  private Struct struct;
  private Blob blob;

  @Before
  public void before() {
    hashedDb = new TestingHashedDb();
    typesDb = new TypesDb(hashedDb);
    valuesDb = new ValuesDb(hashedDb, typesDb);
  }

  @Test
  public void learning_test_create_struct() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth struct
     * in HashedDb.
     */
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(dataHash = hashedDb.writeHashes(firstName.hash(), lastName.hash()));
    when(() -> hashedDb.writeHashes(personType().hash(), dataHash));
    thenReturned(person(firstName, lastName).hash());
  }

  @Test
  public void struct_with_too_few_fields_is_corrupted() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(dataHash = hashedDb.writeHashes(firstName.hash()));
    given(structHash = hashedDb.writeHashes(personType().hash(), dataHash));
    given(struct = (Struct) valuesDb.get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(CorruptedValueException.class);
  }

  @Test
  public void struct_with_too_many_fields_is_corrupted() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(lastName = valuesDb.string("Doe"));
    given(dataHash = hashedDb.writeHashes(firstName.hash(), lastName.hash(), lastName.hash()));
    given(structHash = hashedDb.writeHashes(personType().hash(), dataHash));
    given(struct = (Struct) valuesDb.get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(CorruptedValueException.class);
  }

  @Test
  public void struct_with_field_of_wrong_type_is_corrupted() throws Exception {
    given(firstName = valuesDb.string("John"));
    given(blob = valuesDb.blobBuilder().build());
    given(dataHash = hashedDb.writeHashes(firstName.hash(), blob.hash()));
    given(structHash = hashedDb.writeHashes(personType().hash(), dataHash));
    given(struct = (Struct) valuesDb.get(structHash));
    when(() -> struct.get("firstName"));
    thenThrown(CorruptedValueException.class);
  }

  private Struct person(SString firstName, SString lastName) {
    return valuesDb.structBuilder(personType())
        .set("firstName", firstName)
        .set("lastName", lastName)
        .build();
  }

  private StructType personType() {
    return TestingStructType.personType(typesDb);
  }
}
