package org.smoothbuild.lang.type;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.CorruptedValueException;

import com.google.common.hash.HashCode;

public class CorruptedTypeTest {
  protected HashedDb hashedDb;
  protected TypesDb typesDb;
  protected ConcreteType type;
  protected HashCode hash;
  private TypeType typeType;
  private HashCode dataHash;
  private Marshaller marshaller;

  @Before
  public void before() {
    given(hashedDb = new TestingHashedDb());
    given(typesDb = new TypesDb(hashedDb));
  }

  @Test
  public void learning_test_create_string_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller.writeHash(typeType.hash()));
    given(marshaller.writeHash(dataHash));
    given(marshaller).close();
    when(() -> marshaller.hash());
    thenReturned(typesDb.string().hash());
  }

  @Test
  public void learning_test_create_type_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("Type")));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller.writeHash(dataHash));
    given(marshaller).close();
    when(() -> marshaller.hash());
    thenReturned(typesDb.type().hash());
  }

  @Test
  public void merkle_root_with_three_children_causes_exception() throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller.writeHash(typeType.hash()));
    given(marshaller.writeHash(dataHash));
    given(marshaller.writeHash(dataHash));
    given(marshaller).close();
    when(() -> typesDb.read(marshaller.hash()));
    thenThrown(CorruptedValueException.class);
  }

  @Test
  public void merkle_root_with_two_children_when_first_one_is_not_type_causes_exception()
      throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller.writeHash(dataHash));
    given(marshaller.writeHash(dataHash));
    given(marshaller).close();
    when(() -> typesDb.read(marshaller.hash()));
    thenThrown(CorruptedValueException.class);
  }

  @Test
  public void merkle_root_with_one_children_causes_exception() throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller.writeHash(dataHash));
    given(marshaller).close();
    when(() -> typesDb.read(marshaller.hash()));
    thenThrown(CorruptedValueException.class);
  }

  @Test
  public void merkle_tree_for_type_type_but_with_wrong_name_causes_exception() throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("TypeX")));
    given(marshaller = hashedDb.newMarshaller());
    given(marshaller.writeHash(dataHash));
    given(marshaller).close();
    when(() -> typesDb.read(marshaller.hash()));
    thenThrown(CorruptedValueException.class);
  }
}
