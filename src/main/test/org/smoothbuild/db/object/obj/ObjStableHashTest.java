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
          .isEqualTo(Hash.decode("fd11e3a7a0eca23315da184c8ce90c051724e90b"));
    }

    @Test
    public void non_empty_blob_array() {
      assertThat(arrayVal(blobVal(ByteString.of())).hash())
          .isEqualTo(Hash.decode("f3515ba2d7af14cb995b07179ff3f3e9a97b52a9"));
    }

    @Test
    public void empty_bool_array() {
      assertThat(arrayVal(boolSpec()).hash())
          .isEqualTo(Hash.decode("e4478355c1f6ea8d4a9edef0a7213cb1aa5b44d4"));
    }

    @Test
    public void non_empty_bool_array() {
      assertThat(arrayVal(boolVal(true)).hash())
          .isEqualTo(Hash.decode("20fbf2c6d0b35a92bb1552a309ef724ce722a1e4"));
    }

    @Test
    public void empty_nothing_array() {
      assertThat(arrayVal(nothingSpec()).hash())
          .isEqualTo(Hash.decode("fc21e8e3b2e04f46fa35cdd3fa0b932cb59c38a0"));
    }

    @Test
    public void empty_string_array() {
      assertThat(arrayVal(strSpec()).hash())
          .isEqualTo(Hash.decode("5e8edad671513a73f56c6bb216789c6f85de320f"));
    }

    @Test
    public void non_empty_string_array() {
      assertThat(arrayVal(strVal("")).hash())
          .isEqualTo(Hash.decode("c4e16e36229651682138e517ed97491946e3e3b9"));
    }

    @Test
    public void empty_rec_array() {
      assertThat(arrayVal(personSpec()).hash())
          .isEqualTo(Hash.decode("a9688c71bf2aae5f4a8e219ce92e8ab8e6b46e27"));
    }

    @Test
    public void non_empty_rec_array() {
      assertThat(arrayVal(personVal("John", "Doe")).hash())
          .isEqualTo(Hash.decode("4f98d430a9c3b36fec6a89ad64e19bef31da7a39"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() {
      assertThat(blobBuilder().build().hash())
          .isEqualTo(Hash.decode("080d59deb0dab374a40f4919fed3fa0cd6bcbded"));
    }

    @Test
    public void some_blob() {
      assertThat(blobVal(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("669cf1da5b2b0a7791089a2da12213dde61e8b8b"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      assertThat(boolVal(true).hash())
          .isEqualTo(Hash.decode("721efcc95000fe4bf3bb4985b456bac91c8db596"));
    }

    @Test
    public void false_bool() {
      assertThat(boolVal(false).hash())
          .isEqualTo(Hash.decode("b8f423d48c90466a4ec449b70c22a526841a1bb1"));
    }
  }

  @Nested
  class _call {
    @Test
    public void call_expression_with_one_argument() {
      assertThat(callExpr(constExpr(intVal(1)), list(constExpr(intVal(2)))).hash())
          .isEqualTo(Hash.decode("e0e21ce30882fe5f2ba9072361d0583beb2814b8"));
    }

    @Test
    public void call_expression_without_arguments() {
      assertThat(callExpr(constExpr(intVal(1)), list()).hash())
          .isEqualTo(Hash.decode("ef133b46f9d47306db02b15144c1b9e8b4198afd"));
    }
  }

  @Nested
  class _const {
    @Test
    public void const_blob_expression() {
      assertThat(constExpr(blobVal(ByteString.of((byte) 1, (byte) 2, (byte) 3))).hash())
          .isEqualTo(Hash.decode("989ae98ed49e9e70fdddac97b5502e084d9c5d8b"));
    }

    @Test
    public void const_bool_expression() {
      assertThat(constExpr(boolVal(true)).hash())
          .isEqualTo(Hash.decode("66ffb0169e5cb88dafda1053531c645c2dbeb34f"));
    }

    @Test
    public void const_int_expression() {
      assertThat(constExpr(intVal(123)).hash())
          .isEqualTo(Hash.decode("bc6eec3c6bb2efbb1788bd82846855dcccec3aff"));
    }

    @Test
    public void const_string_expression() {
      assertThat(constExpr(strVal("abc")).hash())
          .isEqualTo(Hash.decode("2e8735ebf379d2f06d95ef7a0f7d69766f80b943"));
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
          .isEqualTo(Hash.decode("ea1ca60f92be113137fce1d7d56cd22ee428afb0"));
    }

    @Test
    public void with_one_default_argument() {
      DefinedLambda definedLambda =
          definedLambdaVal(
              definedLambdaSpec(intSpec(), blobSpec()),
              constExpr(intVal(1)),
              list(constExpr(intVal(2))));
      assertThat(definedLambda.hash())
          .isEqualTo(Hash.decode("baefa5d6cd1f072a9ff9ab8dd5f95ccdcf8bf758"));
    }

    @Test
    public void with_two_default_arguments() {
      DefinedLambda definedLambda =
          definedLambdaVal(
              definedLambdaSpec(intSpec(), blobSpec(), strSpec()),
              constExpr(intVal(1)),
              list(constExpr(intVal(2)), constExpr(intVal(3))));
      assertThat(definedLambda.hash())
          .isEqualTo(Hash.decode("5e123797ae5c75807cbca53e9b608e1c17c84fc0"));
    }
  }

  @Nested
  class _earray {
    @Test
    public void empty_earray_expression() {
      assertThat(eArrayExpr(list()).hash())
          .isEqualTo(Hash.decode("d5f74d989d3a909fc9c07a13bdd45f12bf1d38ea"));
    }

    @Test
    public void earray_expression() {
      assertThat(eArrayExpr(list(constExpr(intVal(1)))).hash())
          .isEqualTo(Hash.decode("41ff0b1b274fd1c8e75b150e9b08f0ac3ab8b40b"));
    }
  }

  @Nested
  class _field_read {
    @Test
    public void field_read_expression() {
      assertThat(fieldReadExpr(constExpr(intVal(1)), intVal(2)).hash())
          .isEqualTo(Hash.decode("5dd765ae12bf26f6780a8a5d8027ce215ad48861"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      assertThat(intVal(0).hash())
          .isEqualTo(Hash.decode("3ce602708b7be67d736ece10605f5bccb2469b19"));
    }

    @Test
    public void positive_int() {
      assertThat(intVal(123).hash())
          .isEqualTo(Hash.decode("1bb0ed034cf637193455b30239ba8f0dc2f2bd74"));
    }

    @Test
    public void negative_int() {
      assertThat(intVal(-123).hash())
          .isEqualTo(Hash.decode("0e29da471c4e2cc3ef8bf161a9d1406a7204709a"));
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
          .isEqualTo(Hash.decode("e425765fc7b205943b2a786ac9652957e7d392f2"));
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
          .isEqualTo(Hash.decode("43b4e1ade9c059bb71951eba163098213a500320"));
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
          .isEqualTo(Hash.decode("bb5176f441c035b2a62f6129a0982d10087578d9"));
    }
  }

  @Nested
  class _null {
    @Test
    public void null_expression() {
      assertThat(nullExpr().hash())
          .isEqualTo(Hash.decode("8d0c075d09869c59f496f145dc507d6b3f1b7c52"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      assertThat(strVal("").hash())
          .isEqualTo(Hash.decode("707159057595e7c06a56fc09167364493620aa38"));
    }

    @Test
    public void some_string() {
      assertThat(strVal("abc").hash())
          .isEqualTo(Hash.decode("2f79dcd289cb78ca01f0f24a250203987145d9fa"));
    }
  }

  @Nested
  class _rec {
    @Test
    public void empty_rec() {
      assertThat(emptyRecVal().hash())
          .isEqualTo(Hash.decode("54d8451fd0f31c5111433cddf501fabd26e2a9ab"));
    }

    @Test
    public void some_rec() {
      assertThat(personVal("John", "Doe").hash())
          .isEqualTo(Hash.decode("b83eb2d3c38918c2a563c693d817c6c09589bb28"));
    }
  }

  @Nested
  class _ref {
    @Test
    public void zero_ref() {
      assertThat(refExpr(0).hash())
          .isEqualTo(Hash.decode("c5b88a14bf94baea2c1fef9bf7eadff06eb75978"));
    }

    @Test
    public void positive_ref() {
      assertThat(refExpr(123).hash())
          .isEqualTo(Hash.decode("f00f77e46c95a228ca6f5e4697d0928bff880555"));
    }

    @Test
    public void negative_ref() {
      assertThat(refExpr(-123).hash())
          .isEqualTo(Hash.decode("26a280fe9e61e44f3fb5ec9434266d3ccbe2552b"));
    }
  }
}
