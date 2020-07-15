package org.smoothbuild.lang.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectStableHashTest extends TestingContext {
  @Test
  public void hash_of_true_bool_is_stable() {
    assertThat(bool(true).hash())
        .isEqualTo(Hash.decode("1b2cc6d5b65563f4fad11e636aa0ceeaa495dcd5"));
  }

  @Test
  public void hash_of_false_bool_is_stable() {
    assertThat(bool(false).hash())
        .isEqualTo(Hash.decode("908f5b97e65d0f37dc47aea545f01d170030afea"));
  }

  @Test
  public void hash_of_empty_string_is_stable() {
    assertThat(string("").hash())
        .isEqualTo(Hash.decode("fb2fbb7cd2d45c8cf05e6d36f1625fe4d5abf184"));
  }

  @Test
  public void hash_of_some_string_is_stable() {
    assertThat(string("abc").hash())
        .isEqualTo(Hash.decode("fe3f9608bd1039a1c57c9e5ddaeb3ec40c726a86"));
  }

  @Test
  public void hash_of_empty_blob_is_stable() throws Exception {
    assertThat(blobBuilder().build().hash())
        .isEqualTo(Hash.decode("e435c7df508352d6323f415b83b0b774368393a1"));
  }

  @Test
  public void hash_of_some_blob_is_stable() {
    assertThat(blob(ByteString.encodeUtf8("aaa")).hash())
        .isEqualTo(Hash.decode("0371e591163d9903e066311778b278ba01166abd"));
  }

  @Test
  public void hash_of_empty_struct_is_stable() {
    assertThat(empty().hash())
        .isEqualTo(Hash.decode("3119eb20fa9ca28c9903eec9d66d1e47eeb84a2b"));
  }

  @Test
  public void hash_of_some_struct_is_stable() {
    assertThat(person("John", "Doe").hash())
        .isEqualTo(Hash.decode("8fd0a5ea3fa2fe3b5976e9899fb291e19c89fcad"));
  }

  @Test
  public void hash_of_empty_bool_array_is_stable() {
    assertThat(emptyArrayOf(boolType()).hash())
        .isEqualTo(Hash.decode("827cfcc22b8b410a19f7a01ff9d0357768dde8ab"));
  }

  @Test
  public void hash_of_non_empty_bool_array_is_stable() {
    assertThat(arrayBuilder(boolType()).add(bool(true)).build().hash())
        .isEqualTo(Hash.decode("94e663261b24e8d6ea20b94c0f77b814f9bfc9ce"));
  }

  @Test
  public void hash_of_empty_string_array_is_stable() {
    assertThat(emptyArrayOf(stringType()).hash())
        .isEqualTo(Hash.decode("a6e806a46d0b1cead4799969c4b1e72308cfd6f2"));
  }

  @Test
  public void hash_of_non_empty_string_array_is_stable() {
    assertThat(arrayBuilder(stringType()).add(string("")).build().hash())
        .isEqualTo(Hash.decode("3f7f52c110494a4b1ae31d7cbd3f48b54bd37d3d"));
  }

  @Test
  public void hash_of_empty_blob_array_is_stable() {
    assertThat(emptyArrayOf(blobType()).hash())
        .isEqualTo(Hash.decode("e7eb1c317bb961731786e2b0284a8a3bd8274028"));
  }

  @Test
  public void hash_of_non_empty_blob_array_is_stable() {
    assertThat(arrayBuilder(blobType()).add(blob(ByteString.of())).build().hash())
        .isEqualTo(Hash.decode("f547cabc9d16af42d05327e45f2ebd2ef4cc5039"));
  }

  @Test
  public void hash_of_empty_struct_array_is_stable() {
    assertThat(emptyArrayOf(personType()).hash())
        .isEqualTo(Hash.decode("accb4e3e6419fda6b0bf85a685e6f98cf2d80892"));
  }

  @Test
  public void hash_of_non_empty_struct_array_is_stable() {
    assertThat(arrayBuilder(personType()).add(person("John", "Doe")).build().hash())
        .isEqualTo(Hash.decode("feb7e2f8f2370a972d305393f926cfdaea04608e"));
  }

  @Test
  public void hash_of_empty_nothing_array_is_stable() {
    assertThat(emptyArrayOf(nothingType()).hash())
        .isEqualTo(Hash.decode("6bb1e56cac86965238be0be6f6994543a4c9d2ed"));
  }

  private Array emptyArrayOf(ConcreteType elemType) {
    return arrayBuilder(elemType).build();
  }
}
