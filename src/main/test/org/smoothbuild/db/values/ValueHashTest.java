package org.smoothbuild.db.values;

import static org.smoothbuild.db.hashed.Hash.decodeHex;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ValueHashTest extends TestingContext {
  private Bool bool;
  private SString sstring;
  private Blob blob;
  private Struct struct;
  private Array array;

  @Test
  public void hash_of_true_bool_is_stable() throws Exception {
    given(bool = bool(true));
    when(bool).hash();
    thenReturned(decodeHex("86ade1928ff1d0175a33d59080289212f79ff921"));
  }

  @Test
  public void hash_of_false_bool_is_stable() throws Exception {
    given(bool = bool(false));
    when(bool).hash();
    thenReturned(decodeHex("fe2c2d262ca0d3325738e492c11ef37523469d99"));
  }

  @Test
  public void hash_of_empty_string_is_stable() throws Exception {
    given(sstring = string(""));
    when(sstring).hash();
    thenReturned(decodeHex("0af6d4b68d3dadc04f1fd8d207702afd4809a0c2"));
  }

  @Test
  public void hash_of_some_string_is_stable() throws Exception {
    given(sstring = string("abc"));
    when(sstring).hash();
    thenReturned(decodeHex("4d3465c4280a64e3a6c2272fc48e971d102eac93"));
  }

  @Test
  public void hash_of_empty_blob_is_stable() throws Exception {
    given(blob = blobBuilder().build());
    when(() -> blob.hash());
    thenReturned(decodeHex("6915b0de6d40fc94df5c229b46da15e34d7c232b"));
  }

  @Test
  public void hash_of_some_blob_is_stable() throws Exception {
    given(blob = blob(ByteString.encodeUtf8("aaa")));
    when(() -> blob.hash());
    thenReturned(decodeHex("72ae3804e4c12bbd45e26440e9817a5bf3ca0811"));
  }

  @Test
  public void hash_of_empty_struct_is_stable() throws Exception {
    given(struct = person("John", "Doe"));
    when(() -> struct.hash());
    thenReturned(decodeHex("a94de85056893e6464baa8ded12a9a765c5b56fc"));
  }

  @Test
  public void hash_of_some_struct_is_stable() throws Exception {
    given(struct = person("John", "Doe"));
    when(() -> struct.hash());
    thenReturned(decodeHex("a94de85056893e6464baa8ded12a9a765c5b56fc"));
  }

  @Test
  public void hash_of_empty_bool_array_is_stable() throws Exception {
    given(array = arrayBuilder(boolType()).build());
    when(() -> array.hash());
    thenReturned(decodeHex("ddc07c59607a4a56f2d34b6cc8fef9459c51f3f3"));
  }

  @Test
  public void hash_of_non_empty_bool_array_is_stable() throws Exception {
    given(array = arrayBuilder(boolType()).add(bool(true)).build());
    when(() -> array.hash());
    thenReturned(decodeHex("394b5eddb75a2279b8fcf4ab238d8d0258cdac39"));
  }

  @Test
  public void hash_of_empty_string_array_is_stable() throws Exception {
    given(array = arrayBuilder(stringType()).build());
    when(() -> array.hash());
    thenReturned(decodeHex("b2d4a44801204a93da825d1b4db4ef4af2787d82"));
  }

  @Test
  public void hash_of_non_empty_string_array_is_stable() throws Exception {
    given(array = arrayBuilder(stringType()).add(string("")).build());
    when(() -> array.hash());
    thenReturned(decodeHex("98370fae56927d0832578f133ca73ff1f58fe415"));
  }

  @Test
  public void hash_of_empty_blob_array_is_stable() throws Exception {
    given(array = arrayBuilder(blobType()).build());
    when(() -> array.hash());
    thenReturned(decodeHex("000c25ccefc9fbd916400c36eb99bd2610f507ea"));
  }

  @Test
  public void hash_of_non_empty_blob_array_is_stable() throws Exception {
    given(array = arrayBuilder(blobType()).add(
        blob(ByteString.of())).build());
    when(() -> array.hash());
    thenReturned(decodeHex("3dd2f9efc115cc922304f91df183bc8359df9ec8"));
  }

  @Test
  public void hash_of_empty_struct_array_is_stable() throws Exception {
    given(struct = person("John", "Doe"));
    given(array = arrayBuilder(personType()).build());
    when(() -> array.hash());
    thenReturned(decodeHex("21187af9783689f62abc2f1e10fb76233d167af8"));
  }

  @Test
  public void hash_of_non_empty_struct_array_is_stable() throws Exception {
    given(struct = person("John", "Doe"));
    given(array = arrayBuilder(personType()).add(struct).build());
    when(() -> array.hash());
    thenReturned(decodeHex("28c181c073e198d5ca5f233ac682a7a09f0aeb63"));
  }

  @Test
  public void hash_of_empty_nothing_array_is_stable() throws Exception {
    given(array = arrayBuilder(nothingType()).build());
    when(() -> array.hash());
    thenReturned(decodeHex("034da224a1b3f7e2d2702ce8c5dd986f11b9b08a"));
  }
}
