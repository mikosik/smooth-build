package org.smoothbuild.record.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.record.type.TypeKind.ARRAY;
import static org.smoothbuild.record.type.TypeKind.BLOB;
import static org.smoothbuild.record.type.TypeKind.BOOL;
import static org.smoothbuild.record.type.TypeKind.NOTHING;
import static org.smoothbuild.record.type.TypeKind.STRING;
import static org.smoothbuild.record.type.TypeKind.TUPLE;
import static org.smoothbuild.record.type.TypeKind.TYPE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.db.ObjectDbException;
import org.smoothbuild.record.type.TypeKind;

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
                hash(ARRAY.marker()),
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
                hash(STRING.marker()))))
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
                hash(TUPLE.marker()),
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
                hash(TYPE.marker()))))
        .isEqualTo(typeType().hash());
  }

  // testing Merkle tree of type root node

  @Test
  public void merkle_root_with_three_children_causes_exception() throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(STRING.marker())),
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
  public void merkle_tree_for_type_type_but_with_wrong_marker_causes_exception() throws Exception {
    Hash instanceHash =
        hash(
            hash(
                hash((byte) 33)));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(brokenTypeTypeException(instanceHash));
  }

  // testing Merkle tree of name (type name or struct field name) node

  @Test
  public void merkle_tree_for_type_with_name_that_is_not_known_type()
      throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash((byte) 33)));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(
            instanceHash, "It is instance of type but it has illegal TypeKind marker = 33."));
  }

  // testing Merkle tree of type data

  @ParameterizedTest
  @MethodSource("basicTypes")
  public void corrupted_basic_type(TypeKind typeKind) throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(typeKind.marker()),
                hash("corrupted")));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash, "It is " + typeKind +
            " type but its Merkle root has 2 children when 1 is expected."));
  }

  private static Stream<Arguments> basicTypes() {
    return Stream.of(
        Arguments.of(NOTHING),
        Arguments.of(BLOB),
        Arguments.of(BOOL),
        Arguments.of(STRING)
    );
  }

  @Test
  public void struct_type_with_merkle_tree_with_data_with_one_children_causes_exception()
      throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(TUPLE.marker())));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash,
        "It is TUPLE type but its Merkle root has 1 children when 2 is expected."));
  }

  @Test
  public void struct_type_with_merkle_tree_with_data_with_second_children_not_being_array_causes_exception()
      throws Exception {
    Hash instanceHash =
        hash(
            hash(typeType()),
            hash(
                hash(TUPLE.marker()),
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
                hash(TUPLE.marker()),
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
                hash(ARRAY.marker())));
    assertCall(() -> objectDb().get(instanceHash))
        .throwsException(new ObjectDbException(instanceHash,
            "It is ARRAY type but its Merkle root has 1 children when 2 is expected."));
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
                hash(TUPLE.marker()),
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
                hash(ARRAY.marker()),
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
