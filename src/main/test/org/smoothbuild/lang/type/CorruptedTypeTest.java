package org.smoothbuild.lang.type;

import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDbException;

import com.google.common.hash.HashCode;

public class CorruptedTypeTest {
  protected HashedDb hashedDb;
  protected TypesDb typesDb;
  protected ConcreteType type;
  protected HashCode hash;
  private TypeType typeType;
  private HashCode dataHash;

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
    when(hashedDb.writeHashes(typeType.hash(), dataHash));
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
    when(hashedDb.writeHashes(dataHash));
    thenReturned(typesDb.type().hash());
  }

  @Test
  public void merkle_root_with_three_children_causes_exception() throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(hash = hashedDb.writeHashes(typeType.hash(), dataHash, dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash, "Its Merkle tree root has 3 children.")));
  }

  @Test
  public void merkle_root_with_two_children_when_first_one_is_not_type_causes_exception()
      throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(hash = hashedDb.writeHashes(dataHash, dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash, "Expected value which is instance of "
        + "'Type' but its Merkle tree's first child is not Type type.")));
  }

  @Test
  public void merkle_root_with_one_children_causes_exception() throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("String")));
    given(hash = hashedDb.writeHashes(dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(brokenTypeTypeException(hash)));
  }

  @Test
  public void merkle_tree_for_type_type_but_with_wrong_name_causes_exception() throws Exception {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("TypeX")));
    given(hash = hashedDb.writeHashes(dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(brokenTypeTypeException(hash)));
  }

  private static ValuesDbException brokenTypeTypeException(HashCode hash) {
    return corruptedValueException(hash, "Expected value which is instance of 'Type' but its Merkle"
        + " tree has only one child (so it should be Type type) but it has different hash.");
  }
}
