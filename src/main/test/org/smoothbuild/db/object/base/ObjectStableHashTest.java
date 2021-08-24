package org.smoothbuild.db.object.base;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectStableHashTest extends TestingContext {
  @Nested
  class _blob {
    @Test
    public void hash_of_empty_blob_is_stable() {
      assertThat(blobBuilder().build().hash())
          .isEqualTo(Hash.decode("56f9a1616c2a91bd3e7c059e885ab33d3964e759"));
    }

    @Test
    public void hash_of_some_blob_is_stable() {
      assertThat(blobV(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("44894c15dc104081f903b23b0ccc28542221ca14"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void hash_of_true_bool_is_stable() {
      assertThat(boolV(true).hash())
          .isEqualTo(Hash.decode("4098ba24a25839b9302d0fac6ebcd7f7aa7d5aed"));
    }

    @Test
    public void hash_of_false_bool_is_stable() {
      assertThat(boolV(false).hash())
          .isEqualTo(Hash.decode("3b641feda4deab9676f58d0f62981d8593c10c08"));
    }
  }

  @Nested
  class _int {
    @Test
    public void hash_of_zero_int_is_stable() {
      assertThat(intV(0).hash())
          .isEqualTo(Hash.decode("a74ab70e73150249cbeab4a98b3724191de5b765"));
    }

    @Test
    public void hash_of_positive_int_is_stable() {
      assertThat(intV(123).hash())
          .isEqualTo(Hash.decode("499c2f0fa7eec7f337bb8f70eb20d66fd38b89a3"));
    }

    @Test
    public void hash_of_negative_int_is_stable() {
      assertThat(intV(-123).hash())
          .isEqualTo(Hash.decode("60eeed22193aef6ae9f60990cbb7b974a7059340"));
    }
  }

  @Nested
  class _string {
    @Test
    public void hash_of_empty_string_is_stable() {
      assertThat(strV("").hash())
          .isEqualTo(Hash.decode("34964212f1c15c635971ac31efa6187e3ad19228"));
    }

    @Test
    public void hash_of_some_string_is_stable() {
      assertThat(strV("abc").hash())
          .isEqualTo(Hash.decode("d0d189f74c20f329bbec85979883b7cd6a0a8939"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void hash_of_empty_tuple_is_stable() {
      assertThat(emptyTupleV().hash())
          .isEqualTo(Hash.decode("00c74cba6a50c3da116688ad977e18bc76d65ca4"));
    }

    @Test
    public void hash_of_some_tuple_is_stable() {
      assertThat(personV("John", "Doe").hash())
          .isEqualTo(Hash.decode("96f78887322a24eb91f2c785b10c7a6c613a2633"));
    }
  }

  @Nested
  class _array {
    @Test
    public void hash_of_empty_blob_array_is_stable() {
      assertThat(emptyArrayOf(blobS()).hash())
          .isEqualTo(Hash.decode("125abf735e023135153d0eade19b21fb2444a9cb"));
    }

    @Test
    public void hash_of_non_empty_blob_array_is_stable() {
      assertThat(arrayBuilder(blobS()).add(blobV(ByteString.of())).build().hash())
          .isEqualTo(Hash.decode("329e4dfd95a5dcbcc2ff3d7dde3ca20f236cc0d4"));
    }

    @Test
    public void hash_of_empty_bool_array_is_stable() {
      assertThat(emptyArrayOf(boolS()).hash())
          .isEqualTo(Hash.decode("b9796321546d2817719aa32196a6170151a7abaa"));
    }

    @Test
    public void hash_of_non_empty_bool_array_is_stable() {
      assertThat(arrayBuilder(boolS()).add(boolV(true)).build().hash())
          .isEqualTo(Hash.decode("fc0f1008bd3c72106e59e065fd6ba6658f2ffcba"));
    }

    @Test
    public void hash_of_empty_nothing_array_is_stable() {
      assertThat(emptyArrayOf(nothingS()).hash())
          .isEqualTo(Hash.decode("b739bcaa23e50d8cc43791d1f770698175916875"));
    }

    @Test
    public void hash_of_empty_string_array_is_stable() {
      assertThat(emptyArrayOf(strS()).hash())
          .isEqualTo(Hash.decode("89a4f1d2b4ed44ac887c7c056d54e42c50abf96e"));
    }

    @Test
    public void hash_of_non_empty_string_array_is_stable() {
      assertThat(arrayBuilder(strS()).add(strV("")).build().hash())
          .isEqualTo(Hash.decode("3a631e7e0e6d858454e427003cb4685791f650ab"));
    }

    @Test
    public void hash_of_empty_tuple_array_is_stable() {
      assertThat(emptyArrayOf(personS()).hash())
          .isEqualTo(Hash.decode("a26b682254544d9d3b34a14f44f3b40f39f8fa1e"));
    }

    @Test
    public void hash_of_non_empty_tuple_array_is_stable() {
      assertThat(arrayBuilder(personS()).add(personV("John", "Doe")).build().hash())
          .isEqualTo(Hash.decode("e06466885b0ff8bd31e6eb2fb5af60e35eb11888"));
    }
  }

  private Array emptyArrayOf(Spec elemSpec) {
    return arrayBuilder(elemSpec).build();
  }
}
