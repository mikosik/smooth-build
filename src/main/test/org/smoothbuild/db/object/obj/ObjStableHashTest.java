package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjStableHashTest extends TestingContext {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() {
      assertThat(arrayVal(blobSpec()).hash())
          .isEqualTo(Hash.decode("125abf735e023135153d0eade19b21fb2444a9cb"));
    }

    @Test
    public void non_empty_blob_array() {
      assertThat(arrayVal(blobVal(ByteString.of())).hash())
          .isEqualTo(Hash.decode("329e4dfd95a5dcbcc2ff3d7dde3ca20f236cc0d4"));
    }

    @Test
    public void empty_bool_array() {
      assertThat(arrayVal(boolSpec()).hash())
          .isEqualTo(Hash.decode("b9796321546d2817719aa32196a6170151a7abaa"));
    }

    @Test
    public void non_empty_bool_array() {
      assertThat(arrayVal(boolVal(true)).hash())
          .isEqualTo(Hash.decode("fc0f1008bd3c72106e59e065fd6ba6658f2ffcba"));
    }

    @Test
    public void empty_nothing_array() {
      assertThat(arrayVal(nothingSpec()).hash())
          .isEqualTo(Hash.decode("b739bcaa23e50d8cc43791d1f770698175916875"));
    }

    @Test
    public void empty_string_array() {
      assertThat(arrayVal(strSpec()).hash())
          .isEqualTo(Hash.decode("89a4f1d2b4ed44ac887c7c056d54e42c50abf96e"));
    }

    @Test
    public void non_empty_string_array() {
      assertThat(arrayVal(strVal("")).hash())
          .isEqualTo(Hash.decode("3a631e7e0e6d858454e427003cb4685791f650ab"));
    }

    @Test
    public void empty_rec_array() {
      assertThat(arrayVal(personSpec()).hash())
          .isEqualTo(Hash.decode("a26b682254544d9d3b34a14f44f3b40f39f8fa1e"));
    }

    @Test
    public void non_empty_rec_array() {
      assertThat(arrayVal(personVal("John", "Doe")).hash())
          .isEqualTo(Hash.decode("e06466885b0ff8bd31e6eb2fb5af60e35eb11888"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() {
      assertThat(blobBuilder().build().hash())
          .isEqualTo(Hash.decode("56f9a1616c2a91bd3e7c059e885ab33d3964e759"));
    }

    @Test
    public void some_blob() {
      assertThat(blobVal(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("44894c15dc104081f903b23b0ccc28542221ca14"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      assertThat(boolVal(true).hash())
          .isEqualTo(Hash.decode("4098ba24a25839b9302d0fac6ebcd7f7aa7d5aed"));
    }

    @Test
    public void false_bool() {
      assertThat(boolVal(false).hash())
          .isEqualTo(Hash.decode("3b641feda4deab9676f58d0f62981d8593c10c08"));
    }
  }

  @Nested
  class _call {
    @Test
    public void call_expression_with_one_argument() {
      assertThat(callExpr(constExpr(intVal(1)), list(constExpr(intVal(2)))).hash())
          .isEqualTo(Hash.decode("01f702aed0ff152251af37d7b0a1ae2bc323b930"));
    }

    @Test
    public void call_expression_without_arguments() {
      assertThat(callExpr(constExpr(intVal(1)), list()).hash())
          .isEqualTo(Hash.decode("180fabed4d0cf3ffaa1f6fded51061c6ec9d81cd"));
    }
  }

  @Nested
  class _const {
    @Test
    public void const_blob_expression() {
      assertThat(constExpr(blobVal(ByteString.of((byte) 1, (byte) 2, (byte) 3))).hash())
          .isEqualTo(Hash.decode("efdb16653cd81c43502b42264557b3a383a348d1"));
    }

    @Test
    public void const_bool_expression() {
      assertThat(constExpr(boolVal(true)).hash())
          .isEqualTo(Hash.decode("696913ab01c1488de7161d69fc7b59da61368944"));
    }

    @Test
    public void const_int_expression() {
      assertThat(constExpr(intVal(123)).hash())
          .isEqualTo(Hash.decode("2a7928b15367b26c71416079f93c6aa0bf37bc65"));
    }

    @Test
    public void const_string_expression() {
      assertThat(constExpr(strVal("abc")).hash())
          .isEqualTo(Hash.decode("6f932b1bd1eb5a5fa55d82ee9d0c5130899bfbb7"));
    }
  }

  @Nested
  class _defined_lambda {
    @Test
    public void with_no_default_arguments() {
      DefinedLambda definedLambda =
          definedLambdaVal(
              definedLambdaSpec(intSpec()),
              constExpr(intVal(1)),
              list());
      assertThat(definedLambda.hash())
          .isEqualTo(Hash.decode("61439f09a617ec4fbee62bb30325e73932480f2e"));
    }

    @Test
    public void with_one_default_argument() {
      DefinedLambda definedLambda =
          definedLambdaVal(
              definedLambdaSpec(intSpec(), blobSpec()),
              constExpr(intVal(1)),
              list(constExpr(intVal(2))));
      assertThat(definedLambda.hash())
          .isEqualTo(Hash.decode("bfaa02fbd3141b5790090bd58b4b4f589bdd390c"));
    }

    @Test
    public void with_two_default_arguments() {
      DefinedLambda definedLambda =
          definedLambdaVal(
              definedLambdaSpec(intSpec(), blobSpec(), strSpec()),
              constExpr(intVal(1)),
              list(constExpr(intVal(2)), constExpr(intVal(3))));
      assertThat(definedLambda.hash())
          .isEqualTo(Hash.decode("ee0037d1ba30afb35d7d169309f60fade8bcfba9"));
    }
  }

  @Nested
  class _earray {
    @Test
    public void empty_earray_expression() {
      assertThat(eArrayExpr(list()).hash())
          .isEqualTo(Hash.decode("83f658942c9dd57f75ebf537bc9880c9e22fa85d"));
    }

    @Test
    public void earray_expression() {
      assertThat(eArrayExpr(list(constExpr(intVal(1)))).hash())
          .isEqualTo(Hash.decode("9042b984055b94da27c36572c7dc2873c03041e3"));
    }
  }

  @Nested
  class _field_read {
    @Test
    public void field_read_expression() {
      assertThat(fieldReadExpr(constExpr(intVal(1)), intVal(2)).hash())
          .isEqualTo(Hash.decode("de8a53138bd2e0422424bc438a6732140964b966"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      assertThat(intVal(0).hash())
          .isEqualTo(Hash.decode("a74ab70e73150249cbeab4a98b3724191de5b765"));
    }

    @Test
    public void positive_int() {
      assertThat(intVal(123).hash())
          .isEqualTo(Hash.decode("499c2f0fa7eec7f337bb8f70eb20d66fd38b89a3"));
    }

    @Test
    public void negative_int() {
      assertThat(intVal(-123).hash())
          .isEqualTo(Hash.decode("60eeed22193aef6ae9f60990cbb7b974a7059340"));
    }
  }

  @Nested
  class _native_lambda {
    @Test
    public void with_no_default_arguments() {
      NativeLambda nativeLambda =
          nativeLambdaVal(
              nativeLambdaSpec(intSpec()),
              strVal("classBinaryName"),
              blobVal(ByteString.encodeUtf8("native jar")),
              list());
      assertThat(nativeLambda.hash())
          .isEqualTo(Hash.decode("6ebec9da3afee81df2dab1e252b1c29a590d2104"));
    }

    @Test
    public void with_one_default_argument() {
      NativeLambda nativeLambda =
          nativeLambdaVal(
              nativeLambdaSpec(intSpec(), blobSpec()),
              strVal("classBinaryName"),
              blobVal(ByteString.encodeUtf8("native jar")),
              list(constExpr(intVal(2))));
      assertThat(nativeLambda.hash())
          .isEqualTo(Hash.decode("43cc10ffdfc3d4f7025a7ac6ed70f29c487c82dd"));
    }

    @Test
    public void with_two_default_arguments() {
      NativeLambda nativeLambda =
          nativeLambdaVal(
              nativeLambdaSpec(intSpec(), blobSpec(), strSpec()),
              strVal("classBinaryName"),
              blobVal(ByteString.encodeUtf8("native jar")),
              list(constExpr(intVal(2)), constExpr(intVal(3))));
      assertThat(nativeLambda.hash())
          .isEqualTo(Hash.decode("094c736266c0de47ba8fe0513e421843291614b0"));
    }
  }

  @Nested
  class _null {
    @Test
    public void null_expression() {
      assertThat(nullExpr().hash())
          .isEqualTo(Hash.decode("478390ef5ac40e11c7919fc5353d0c57f34ed2fd"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      assertThat(strVal("").hash())
          .isEqualTo(Hash.decode("34964212f1c15c635971ac31efa6187e3ad19228"));
    }

    @Test
    public void some_string() {
      assertThat(strVal("abc").hash())
          .isEqualTo(Hash.decode("d0d189f74c20f329bbec85979883b7cd6a0a8939"));
    }
  }

  @Nested
  class _rec {
    @Test
    public void empty_rec() {
      assertThat(emptyRecVal().hash())
          .isEqualTo(Hash.decode("00c74cba6a50c3da116688ad977e18bc76d65ca4"));
    }

    @Test
    public void some_rec() {
      assertThat(personVal("John", "Doe").hash())
          .isEqualTo(Hash.decode("96f78887322a24eb91f2c785b10c7a6c613a2633"));
    }
  }

  @Nested
  class _ref {
    @Test
    public void zero_ref() {
      assertThat(refExpr(0).hash())
          .isEqualTo(Hash.decode("ccedb09376a4b5f61b33197fad22716da57ef853"));
    }

    @Test
    public void positive_ref() {
      assertThat(refExpr(123).hash())
          .isEqualTo(Hash.decode("68f2d3bee8fcd5862301890e6ed21cdd46c3fbd1"));
    }

    @Test
    public void negative_ref() {
      assertThat(refExpr(-123).hash())
          .isEqualTo(Hash.decode("e85f399d1fdef3e8663295c12a9cafbf1a0ef64f"));
    }
  }
}
