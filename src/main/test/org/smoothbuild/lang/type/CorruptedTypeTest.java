package org.smoothbuild.lang.type;

import static com.google.common.hash.HashCode.fromInt;
import static org.smoothbuild.db.hashed.Hash.integer;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.lang.type.TestingTypes.personType;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDbException;

import com.google.common.hash.HashCode;

import okio.ByteString;

public class CorruptedTypeTest {
  protected HashedDb hashedDb;
  protected TypesDb typesDb;
  protected ConcreteType type;
  protected HashCode hash;
  private TypeType typeType;
  private HashCode dataHash;
  private HashCode fieldListHash;
  private HashCode fieldHash;

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
  public void learning_test_create_struct_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(
        hashedDb.writeString("Person"),
        hashedDb.writeHashes(
            hashedDb.writeHashes(
                hashedDb.writeString("firstName"),
                typesDb.string().hash()
            ),
            hashedDb.writeHashes(
                hashedDb.writeString("lastName"),
                typesDb.string().hash()
            )
        )));
    when(hashedDb.writeHashes(typeType.hash(), dataHash));
    thenReturned(personType(typesDb).hash());
  }

  // testing merkle tree of type root node

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

  // testing merkle tree of name (type name or struct field name) node

  @Test
  public void merkle_tree_for_type_with_name_that_is_not_legal_utf8_sequence_causes_exception() throws
      Exception {
    given(() -> typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hash(ByteString.of((byte) -64))));
    given(hash = hashedDb.writeHashes(typeType.hash(), dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash,
        "It is an instance of a Type which name cannot be decoded using UTF-8 encoding.")));
  }

  @Test
  public void merkle_tree_for_struct_type_with_field_name_that_is_not_legal_utf8_sequence_causes_exception() throws
      Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type in
     * HashedDb.
     */
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(
        hashedDb.writeString("Person"),
        hashedDb.writeHashes(
            hashedDb.writeHashes(
                hashedDb.writeString("firstName"),
                typesDb.string().hash()
            ),
            hashedDb.writeHashes(
                hash(ByteString.of((byte) -64)),
                typesDb.string().hash()
            )
        )));
    given(hash = hashedDb.writeHashes(typeType.hash(), dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash, "It is an instance of a struct Type which " +
        "field name cannot be decoded using UTF-8 encoding.")));
  }

  private HashCode hash(ByteString byteString) throws IOException {
    try (Marshaller marshaller = hashedDb.newMarshaller()) {
      marshaller.sink().write(byteString);
      marshaller.close();
      return marshaller.hash();
    }
  }

  // testing merkle tree of type data

  @Test
  public void merkle_root_of_data_of_bool_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("Bool");
  }

  @Test
  public void merkle_root_of_data_of_string_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("String");
  }

  @Test
  public void merkle_root_of_data_of_blob_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("Blob");
  }

  @Test
  public void merkle_root_of_data_of_nothing_type_with_two_children_causes_exception()
      throws Exception {
    doTestCorruptedBasicType("Nothing");
  }

  private void doTestCorruptedBasicType(String typeName) throws IOException {
    given(typeType = typesDb.type());
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString(typeName), fromInt(0)));
    given(hash = hashedDb.writeHashes(typeType.hash(), dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash,
        "It is " + typeName + " type but its Merkle tree has unnecessary children.")));
  }

  @Test
  public void merkle_root_of_data_of_struct_type_with_one_children_causes_exception()
      throws Exception {
    given(typeType = typesDb.type());
    given(fieldHash = hashedDb.writeHashes(hashedDb.writeString("fname"), typesDb.string().hash()));
    given(fieldListHash = hashedDb.writeHashes(fieldHash));
    given(dataHash = hashedDb.writeHashes(
        hashedDb.writeString("MyType"), fieldListHash, integer(1)));
    given(hash = hashedDb.writeHashes(typeType.hash(), dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash,
        "It is struct type but its Merkle tree has unnecessary children.")));
  }

  @Test
  public void merkle_tree_of_struct_type_field_with_more_than_2_children_causes_exception()
      throws Exception {
    given(typeType = typesDb.type());
    given(fieldHash = hashedDb.writeHashes(integer(1), integer(2), integer(3)));
    given(fieldListHash = hashedDb.writeHashes(fieldHash));
    given(dataHash = hashedDb.writeHashes(hashedDb.writeString("MyType"), fieldListHash));
    given(hash = hashedDb.writeHashes(typeType.hash(), dataHash));
    when(() -> typesDb.read(hash));
    thenThrown(exception(corruptedValueException(hash,
        "It is struct type but one of its field hashes doesn't have two children but 3.")));
  }

  private static ValuesDbException brokenTypeTypeException(HashCode hash) {
    return corruptedValueException(hash, "Expected value which is instance of 'Type' but its Merkle"
        + " tree has only one child (so it should be Type type) but it has different hash.");
  }
}
