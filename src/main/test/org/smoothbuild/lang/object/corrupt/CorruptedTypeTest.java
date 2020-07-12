package org.smoothbuild.lang.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectDbException;

import okio.ByteString;

public class CorruptedTypeTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_array_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth
     * array type in HashedDb.
     */
    assertThat(
        hash(
            hash(typeType()),
            hash(
                hash(""),
                hash(stringType())
            )
        ))
        .isEqualTo(arrayType(stringType()).hash());
  }

  @Test
  public void learning_test_create_string_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save basic smooth
     * type in HashedDb.
     */
    assertThat(
        hash(
            hash(typeType()),
            hash(
                hash("String"))))
        .isEqualTo(stringType().hash());
  }

  @Test
  public void learning_test_create_struct_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save struct smooth
     * type in HashedDb.
     */
    assertThat(
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash(stringType()),
                    hash(stringType())
                ))))
        .isEqualTo(personType().hash());
  }

  @Test
  public void learning_test_create_type_type() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth type
     * type in HashedDb.
     */
    assertThat(
        hash(
            hash(
                hash("Type"))))
        .isEqualTo(typeType().hash());
  }

  // testing Merkle tree of type root node

  @Test
  public void merkle_root_with_three_children_causes_exception() throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("String")),
            hash("corrupted"));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash, null, null));
  }

  @Test
  public void merkle_root_with_two_children_when_first_one_is_not_type_causes_exception()
      throws Exception {
    Hash instanceHash =
        hash(
            hash("corrupted"),
            hash(
                hash("String")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(ObjectDbException.class);
  }

  @Test
  public void merkle_root_with_one_children_causes_exception() throws Exception {
    Hash instanceHash =
        hash(
            hash(
                hash("String")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(brokenTypeTypeException(instanceHash));
  }

  @Test
  public void merkle_tree_for_type_type_but_with_wrong_name_causes_exception() throws Exception {
    Hash instanceHash =
        hash(
            hash(
                hash("TypeX")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(brokenTypeTypeException(instanceHash));
  }

  // testing Merkle tree of name (type name or struct field name) node

  @Test
  public void merkle_tree_for_type_with_name_that_is_not_legal_utf8_sequence_causes_exception()
      throws Exception {
    Hash notStringHash = hash(ByteString.of((byte) -64));
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                notStringHash));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new DecodingStringException(notStringHash, null));
  }

  // testing Merkle tree of type data

  @ParameterizedTest
  @ValueSource(strings = {"Bool", "String", "Blob", "Nothing"})
  public void corrupted_basic_type(String typeName) throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(typeName),
                hash("corrupted")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash, "It is '" + typeName +
            "' type but its Merkle root has 2 children when 1 is expected."));
  }

  @Test
  public void struct_type_with_merkle_tree_with_data_with_one_children_causes_exception()
      throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash,
        "It is 'Person' type but its Merkle root has 1 children when 2 is expected."));
  }

  @Test
  public void struct_type_with_merkle_tree_with_data_with_second_children_not_being_array_causes_exception()
      throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(stringType())
                ));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash, fieldReadingErrorMessage(1)));
  }

  @Test
  public void struct_type_with_merkle_tree_with_data_with_second_children_being_array_of_non_type_causes_exception()
      throws Exception {
    Hash stringHash = hash(string("abc"));
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    stringHash
                )));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash, fieldReadingErrorMessage(0)))
        .withCause(new ObjectDbException(stringHash,
            "Expected object which is instance of 'Type' type but its Merkle tree's first child" +
                " is not 'Type' type."));
  }

  @Test
  public void merkle_root_of_data_of_array_type_with_one_children_causes_exception()
      throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash,
            "It is '[]' type but its Merkle root has 1 children when 2 is expected."));
  }

  // corrupted dependent types

  @Test
  public void merkle_tree_of_struct_type_with_corrupted_field_type_causes_exception()
      throws Exception {
    Hash hash = Hash.of("not a type");
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash("Person"),
                hash(
                    hash,
                    hash(stringType()))));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash, fieldReadingErrorMessage(0)))
        .withCause(new ObjectDbException(hash));
  }

  @Test
  public void merkle_tree_of_array_type_with_corrupted_element_type_causes_exception()
      throws Exception {
    Hash hash = Hash.of("not a type");
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(""),
                hash));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new ObjectDbException(hash));
  }

  private static ObjectDbException brokenTypeTypeException(Hash hash) {
    return new ObjectDbException(hash, "Expected object which is instance of 'Type' type but " +
        "its Merkle tree has only one child (so it should be 'Type' type) " +
        "but it has different hash.");
  }

  private static String fieldReadingErrorMessage(int index) {
    return "It is a Struct Type and reading field type at index " + index + " caused error.";
  }
}
