package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjStableHashTest extends TestingContext {
  @Nested
  class _array {
    @Test
    public void hash_of_empty_blob_array_is_stable() {
      assertThat(arrayVal(blobSpec()).hash())
          .isEqualTo(Hash.decode("125abf735e023135153d0eade19b21fb2444a9cb"));
    }

    @Test
    public void hash_of_non_empty_blob_array_is_stable() {
      assertThat(arrayVal(blobVal(ByteString.of())).hash())
          .isEqualTo(Hash.decode("329e4dfd95a5dcbcc2ff3d7dde3ca20f236cc0d4"));
    }

    @Test
    public void hash_of_empty_bool_array_is_stable() {
      assertThat(arrayVal(boolSpec()).hash())
          .isEqualTo(Hash.decode("b9796321546d2817719aa32196a6170151a7abaa"));
    }

    @Test
    public void hash_of_non_empty_bool_array_is_stable() {
      assertThat(arrayVal(boolVal(true)).hash())
          .isEqualTo(Hash.decode("fc0f1008bd3c72106e59e065fd6ba6658f2ffcba"));
    }

    @Test
    public void hash_of_empty_nothing_array_is_stable() {
      assertThat(arrayVal(nothingSpec()).hash())
          .isEqualTo(Hash.decode("b739bcaa23e50d8cc43791d1f770698175916875"));
    }

    @Test
    public void hash_of_empty_string_array_is_stable() {
      assertThat(arrayVal(strSpec()).hash())
          .isEqualTo(Hash.decode("89a4f1d2b4ed44ac887c7c056d54e42c50abf96e"));
    }

    @Test
    public void hash_of_non_empty_string_array_is_stable() {
      assertThat(arrayVal(strVal("")).hash())
          .isEqualTo(Hash.decode("3a631e7e0e6d858454e427003cb4685791f650ab"));
    }

    @Test
    public void hash_of_empty_rec_array_is_stable() {
      assertThat(arrayVal(personSpec()).hash())
          .isEqualTo(Hash.decode("a26b682254544d9d3b34a14f44f3b40f39f8fa1e"));
    }

    @Test
    public void hash_of_non_empty_rec_array_is_stable() {
      assertThat(arrayVal(personVal("John", "Doe")).hash())
          .isEqualTo(Hash.decode("e06466885b0ff8bd31e6eb2fb5af60e35eb11888"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void hash_of_empty_blob_is_stable() {
      assertThat(blobBuilder().build().hash())
          .isEqualTo(Hash.decode("56f9a1616c2a91bd3e7c059e885ab33d3964e759"));
    }

    @Test
    public void hash_of_some_blob_is_stable() {
      assertThat(blobVal(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("44894c15dc104081f903b23b0ccc28542221ca14"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void hash_of_true_bool_is_stable() {
      assertThat(boolVal(true).hash())
          .isEqualTo(Hash.decode("4098ba24a25839b9302d0fac6ebcd7f7aa7d5aed"));
    }

    @Test
    public void hash_of_false_bool_is_stable() {
      assertThat(boolVal(false).hash())
          .isEqualTo(Hash.decode("3b641feda4deab9676f58d0f62981d8593c10c08"));
    }
  }

  @Nested
  class _call {
    @Test
    public void hash_of_call_expression_with_one_argument_is_stable() {
      assertThat(callExpr(constExpr(intVal(1)), list(constExpr(intVal(2)))).hash())
          .isEqualTo(Hash.decode("01f702aed0ff152251af37d7b0a1ae2bc323b930"));
    }

    @Test
    public void hash_of_call_expression_without_arguments_is_stable() {
      assertThat(callExpr(constExpr(intVal(1)), list()).hash())
          .isEqualTo(Hash.decode("180fabed4d0cf3ffaa1f6fded51061c6ec9d81cd"));
    }
  }

  @Nested
  class _const {
    @Test
    public void hash_of_const_blob_expression_is_stable() {
      assertThat(constExpr(blobVal(ByteString.of((byte) 1, (byte) 2, (byte) 3))).hash())
          .isEqualTo(Hash.decode("efdb16653cd81c43502b42264557b3a383a348d1"));
    }

    @Test
    public void hash_of_const_bool_expression_is_stable() {
      assertThat(constExpr(boolVal(true)).hash())
          .isEqualTo(Hash.decode("696913ab01c1488de7161d69fc7b59da61368944"));
    }

    @Test
    public void hash_of_const_int_expression_is_stable() {
      assertThat(constExpr(intVal(123)).hash())
          .isEqualTo(Hash.decode("2a7928b15367b26c71416079f93c6aa0bf37bc65"));
    }

    @Test
    public void hash_of_const_string_expression_is_stable() {
      assertThat(constExpr(strVal("abc")).hash())
          .isEqualTo(Hash.decode("6f932b1bd1eb5a5fa55d82ee9d0c5130899bfbb7"));
    }
  }

  @Nested
  class _earray {
    @Test
    public void hash_of_empty_earray_expression_is_stable() {
      assertThat(eArrayExpr(list()).hash())
          .isEqualTo(Hash.decode("83f658942c9dd57f75ebf537bc9880c9e22fa85d"));
    }

    @Test
    public void hash_of_earray_expression_is_stable() {
      assertThat(eArrayExpr(list(constExpr(intVal(1)))).hash())
          .isEqualTo(Hash.decode("9042b984055b94da27c36572c7dc2873c03041e3"));
    }
  }

  @Nested
  class _field_read {
    @Test
    public void hash_of_field_read_expression_is_stable() {
      assertThat(fieldReadExpr(constExpr(intVal(1)), intVal(2)).hash())
          .isEqualTo(Hash.decode("de8a53138bd2e0422424bc438a6732140964b966"));
    }
  }

  @Nested
  class _int {
    @Test
    public void hash_of_zero_int_is_stable() {
      assertThat(intVal(0).hash())
          .isEqualTo(Hash.decode("a74ab70e73150249cbeab4a98b3724191de5b765"));
    }

    @Test
    public void hash_of_positive_int_is_stable() {
      assertThat(intVal(123).hash())
          .isEqualTo(Hash.decode("499c2f0fa7eec7f337bb8f70eb20d66fd38b89a3"));
    }

    @Test
    public void hash_of_negative_int_is_stable() {
      assertThat(intVal(-123).hash())
          .isEqualTo(Hash.decode("60eeed22193aef6ae9f60990cbb7b974a7059340"));
    }
  }

  @Nested
  class _string {
    @Test
    public void hash_of_empty_string_is_stable() {
      assertThat(strVal("").hash())
          .isEqualTo(Hash.decode("34964212f1c15c635971ac31efa6187e3ad19228"));
    }

    @Test
    public void hash_of_some_string_is_stable() {
      assertThat(strVal("abc").hash())
          .isEqualTo(Hash.decode("d0d189f74c20f329bbec85979883b7cd6a0a8939"));
    }
  }

  @Nested
  class _rec {
    @Test
    public void hash_of_empty_rec_is_stable() {
      assertThat(emptyRecVal().hash())
          .isEqualTo(Hash.decode("00c74cba6a50c3da116688ad977e18bc76d65ca4"));
    }

    @Test
    public void hash_of_some_rec_is_stable() {
      assertThat(personVal("John", "Doe").hash())
          .isEqualTo(Hash.decode("96f78887322a24eb91f2c785b10c7a6c613a2633"));
    }
  }
}
