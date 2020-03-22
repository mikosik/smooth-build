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
        .isEqualTo(Hash.decode("86ade1928ff1d0175a33d59080289212f79ff921"));
  }

  @Test
  public void hash_of_false_bool_is_stable() {
    assertThat(bool(false).hash())
        .isEqualTo(Hash.decode("fe2c2d262ca0d3325738e492c11ef37523469d99"));
  }

  @Test
  public void hash_of_empty_string_is_stable() {
    assertThat(string("").hash())
        .isEqualTo(Hash.decode("0af6d4b68d3dadc04f1fd8d207702afd4809a0c2"));
  }

  @Test
  public void hash_of_some_string_is_stable() {
    assertThat(string("abc").hash())
        .isEqualTo(Hash.decode("4d3465c4280a64e3a6c2272fc48e971d102eac93"));
  }

  @Test
  public void hash_of_empty_blob_is_stable() throws Exception {
    assertThat(blobBuilder().build().hash())
        .isEqualTo(Hash.decode("6915b0de6d40fc94df5c229b46da15e34d7c232b"));
  }

  @Test
  public void hash_of_some_blob_is_stable() {
    assertThat(blob(ByteString.encodeUtf8("aaa")).hash())
        .isEqualTo(Hash.decode("72ae3804e4c12bbd45e26440e9817a5bf3ca0811"));
  }

  @Test
  public void hash_of_empty_struct_is_stable() {
    assertThat(empty().hash())
        .isEqualTo(Hash.decode("051da6959e63ecd62e25f64a30ac667b3811609d"));
  }

  @Test
  public void hash_of_some_struct_is_stable() {
    assertThat(person("John", "Doe").hash())
        .isEqualTo(Hash.decode("a94de85056893e6464baa8ded12a9a765c5b56fc"));
  }

  @Test
  public void hash_of_empty_bool_array_is_stable() {
    assertThat(emptyArrayOf(boolType()).hash())
        .isEqualTo(Hash.decode("ddc07c59607a4a56f2d34b6cc8fef9459c51f3f3"));
  }

  @Test
  public void hash_of_non_empty_bool_array_is_stable() {
    assertThat(arrayBuilder(boolType()).add(bool(true)).build().hash())
        .isEqualTo(Hash.decode("394b5eddb75a2279b8fcf4ab238d8d0258cdac39"));
  }

  @Test
  public void hash_of_empty_string_array_is_stable() {
    assertThat(emptyArrayOf(stringType()).hash())
        .isEqualTo(Hash.decode("b2d4a44801204a93da825d1b4db4ef4af2787d82"));
  }

  @Test
  public void hash_of_non_empty_string_array_is_stable() {
    assertThat(arrayBuilder(stringType()).add(string("")).build().hash())
        .isEqualTo(Hash.decode("98370fae56927d0832578f133ca73ff1f58fe415"));
  }

  @Test
  public void hash_of_empty_blob_array_is_stable() {
    assertThat(emptyArrayOf(blobType()).hash())
        .isEqualTo(Hash.decode("000c25ccefc9fbd916400c36eb99bd2610f507ea"));
  }

  @Test
  public void hash_of_non_empty_blob_array_is_stable() {
    assertThat(arrayBuilder(blobType()).add(blob(ByteString.of())).build().hash())
        .isEqualTo(Hash.decode("3dd2f9efc115cc922304f91df183bc8359df9ec8"));
  }

  @Test
  public void hash_of_empty_struct_array_is_stable() {
    assertThat(emptyArrayOf(personType()).hash())
        .isEqualTo(Hash.decode("21187af9783689f62abc2f1e10fb76233d167af8"));
  }

  @Test
  public void hash_of_non_empty_struct_array_is_stable() {
    assertThat(arrayBuilder(personType()).add(person("John", "Doe")).build().hash())
        .isEqualTo(Hash.decode("28c181c073e198d5ca5f233ac682a7a09f0aeb63"));
  }

  @Test
  public void hash_of_empty_nothing_array_is_stable() {
    assertThat(emptyArrayOf(nothingType()).hash())
        .isEqualTo(Hash.decode("034da224a1b3f7e2d2702ce8c5dd986f11b9b08a"));
  }

  private Array emptyArrayOf(ConcreteType elemType) {
    return arrayBuilder(elemType).build();
  }
}
