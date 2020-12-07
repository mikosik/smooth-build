package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

import okio.ByteString;

public class ObjectStableHashTest extends TestingContext {
  @Nested
  class _any {
    @Test
    public void hash_of_some_any_is_stable() {
      Truth.assertThat(any(Hash.of(12345)).hash())
          .isEqualTo(Hash.decode("c3bf0d48a05773734060eabbcbf5ad75ebb6a095"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void hash_of_empty_blob_is_stable() throws Exception {
      Truth.assertThat(blobBuilder().build().hash())
          .isEqualTo(Hash.decode("56f9a1616c2a91bd3e7c059e885ab33d3964e759"));
    }

    @Test
    public void hash_of_some_blob_is_stable() {
      Truth.assertThat(blob(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("44894c15dc104081f903b23b0ccc28542221ca14"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void hash_of_true_bool_is_stable() {
      Truth.assertThat(bool(true).hash())
          .isEqualTo(Hash.decode("4098ba24a25839b9302d0fac6ebcd7f7aa7d5aed"));
    }

    @Test
    public void hash_of_false_bool_is_stable() {
      Truth.assertThat(bool(false).hash())
          .isEqualTo(Hash.decode("3b641feda4deab9676f58d0f62981d8593c10c08"));
    }
  }

  @Nested
  class _string {
    @Test
    public void hash_of_empty_string_is_stable() {
      assertThat(string("").hash())
          .isEqualTo(Hash.decode("34964212f1c15c635971ac31efa6187e3ad19228"));
    }

    @Test
    public void hash_of_some_string_is_stable() {
      assertThat(string("abc").hash())
          .isEqualTo(Hash.decode("d0d189f74c20f329bbec85979883b7cd6a0a8939"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void hash_of_empty_tuple_is_stable() {
      assertThat(empty().hash())
          .isEqualTo(Hash.decode("00c74cba6a50c3da116688ad977e18bc76d65ca4"));
    }

    @Test
    public void hash_of_some_tuple_is_stable() {
      assertThat(person("John", "Doe").hash())
          .isEqualTo(Hash.decode("96f78887322a24eb91f2c785b10c7a6c613a2633"));
    }
  }

  @Nested
  class _array {
    @Test
    public void hash_of_empty_any_array_is_stable() {
      assertThat(emptyArrayOf(anySpec()).hash())
          .isEqualTo(Hash.decode("7407748acd5db8248df4aef7446c06b2753c78d7"));
    }

    @Test
    public void hash_of_non_empty_any_array_is_stable() {
      Truth.assertThat(arrayBuilder(anySpec()).add(any(Hash.of(1234))).build().hash())
          .isEqualTo(Hash.decode("8ca8a3c55b082c48a2735701105485d1323ef159"));
    }

    @Test
    public void hash_of_empty_blob_array_is_stable() {
      assertThat(emptyArrayOf(blobSpec()).hash())
          .isEqualTo(Hash.decode("125abf735e023135153d0eade19b21fb2444a9cb"));
    }

    @Test
    public void hash_of_non_empty_blob_array_is_stable() {
      Truth.assertThat(arrayBuilder(blobSpec()).add(blob(ByteString.of())).build().hash())
          .isEqualTo(Hash.decode("329e4dfd95a5dcbcc2ff3d7dde3ca20f236cc0d4"));
    }

    @Test
    public void hash_of_empty_bool_array_is_stable() {
      assertThat(emptyArrayOf(boolSpec()).hash())
          .isEqualTo(Hash.decode("b9796321546d2817719aa32196a6170151a7abaa"));
    }

    @Test
    public void hash_of_non_empty_bool_array_is_stable() {
      Truth.assertThat(arrayBuilder(boolSpec()).add(bool(true)).build().hash())
          .isEqualTo(Hash.decode("fc0f1008bd3c72106e59e065fd6ba6658f2ffcba"));
    }

    @Test
    public void hash_of_empty_nothing_array_is_stable() {
      assertThat(emptyArrayOf(nothingSpec()).hash())
          .isEqualTo(Hash.decode("b739bcaa23e50d8cc43791d1f770698175916875"));
    }

    @Test
    public void hash_of_empty_string_array_is_stable() {
      assertThat(emptyArrayOf(stringSpec()).hash())
          .isEqualTo(Hash.decode("89a4f1d2b4ed44ac887c7c056d54e42c50abf96e"));
    }

    @Test
    public void hash_of_non_empty_string_array_is_stable() {
      Truth.assertThat(arrayBuilder(stringSpec()).add(string("")).build().hash())
          .isEqualTo(Hash.decode("3a631e7e0e6d858454e427003cb4685791f650ab"));
    }

    @Test
    public void hash_of_empty_tuple_array_is_stable() {
      assertThat(emptyArrayOf(personSpec()).hash())
          .isEqualTo(Hash.decode("a26b682254544d9d3b34a14f44f3b40f39f8fa1e"));
    }

    @Test
    public void hash_of_non_empty_tuple_array_is_stable() {
      Truth.assertThat(arrayBuilder(personSpec()).add(person("John", "Doe")).build().hash())
          .isEqualTo(Hash.decode("e06466885b0ff8bd31e6eb2fb5af60e35eb11888"));
    }
  }

  private Array emptyArrayOf(Spec elemSpec) {
    return arrayBuilder(elemSpec).build();
  }
}
