package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
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
      assertThat(arrayVal(perso_Spec()).hash())
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
      assertThat(callExpr(constExpr(lambdaVal()), list(strExpr("abc"))).hash())
          .isEqualTo(Hash.decode("f4b7bc2125ffa4571067bfd85390bcf46584da07"));
    }

    @Test
    public void call_expression_without_arguments() {
      LambdaSpec spec = lambdaSpec(intSpec(), list(strSpec()));
      Lambda lambda = lambdaVal(spec, intExpr(), list(strExpr()));
      assertThat(callExpr(constExpr(lambda), list(strExpr("abc"))).hash())
          .isEqualTo(Hash.decode("f4b7bc2125ffa4571067bfd85390bcf46584da07"));
    }
  }

  @Nested
  class _const {
    @Test
    public void const_blob_expression() {
      assertThat(constExpr(blobVal(ByteString.of((byte) 1, (byte) 2, (byte) 3))).hash())
          .isEqualTo(Hash.decode("086b9fcca29ced11d99315ea6ab3a77e5940f63e"));
    }

    @Test
    public void const_bool_expression() {
      assertThat(constExpr(boolVal(true)).hash())
          .isEqualTo(Hash.decode("abf2750c6950dd0e09cb7773aacec9cb9b6c6898"));
    }

    @Test
    public void const_int_expression() {
      assertThat(intExpr(123).hash())
          .isEqualTo(Hash.decode("3c511a9e2051727c550ff2b28b6664ed9a8b0fa5"));
    }

    @Test
    public void const_string_expression() {
      assertThat(strExpr("abc").hash())
          .isEqualTo(Hash.decode("165ef0a60178c065ddb83a209a7d4c6a6f2ed779"));
    }
  }

  @Nested
  class _lambda {
    @Test
    public void with_no_parameters() {
      Lambda lambda =
          lambdaVal(
              lambdaSpec(intSpec(), list()),
              intExpr(1),
              list());
      assertThat(lambda.hash())
          .isEqualTo(Hash.decode("e1c7825345302089d7e28e815eac72245200b795"));
    }

    @Test
    public void with_one_parameter() {
      Lambda lambda =
          lambdaVal(
              lambdaSpec(intSpec(), list(intSpec())),
              intExpr(1),
              list(intExpr(2)));
      assertThat(lambda.hash())
          .isEqualTo(Hash.decode("ff158bd0763f8c71e1d86765c412da59fb2610c3"));
    }

    @Test
    public void with_two_parameters() {
      Lambda lambda =
          lambdaVal(
              lambdaSpec(intSpec(), list(intSpec(), strSpec())),
              intExpr(1),
              list(intExpr(2), strExpr("abc")));
      assertThat(lambda.hash())
          .isEqualTo(Hash.decode("c4dc7e349abd8e3bd85e4cccad58a7343db5b2ab"));
    }
  }

  @Nested
  class _array_expr {
    @Test
    public void empty_array_expression() {
      assertThat(arrayExpr(list()).hash())
          .isEqualTo(Hash.decode("a0b82433f715eef1e275af8702914efa74c1bffe"));
    }

    @Test
    public void array_expression() {
      assertThat(arrayExpr(list(intExpr(1))).hash())
          .isEqualTo(Hash.decode("f80cf6e3c5af6de49d7253a0bd31ba802d75ce09"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select_expression() {
      assertThat(selectExpr(constExpr(animalVal()), intVal(0)).hash())
          .isEqualTo(Hash.decode("ab8f773b5ba544efcddbe2460f1e2dada4d82054"));
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
  class _invoke {
    @Test
    public void invoke_expression() {
      assertThat(invokeExpr(blobVal(), strVal(), intSpec()).hash())
          .isEqualTo(Hash.decode("d4bb2cdcf619918e9d5372d68075a3b8ec6495de"));
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
  class _struct {
    @Test
    public void empty_struct() {
      assertThat(structVal(structSpec(list(), list()), list()).hash())
          .isEqualTo(Hash.decode("8c2c19d776a34cc6258dc97c7ef844bf843415c4"));
    }

    @Test
    public void some_struct() {
      assertThat(structVal(structSpec(), list(intVal())).hash())
          .isEqualTo(Hash.decode("7f88fa64f4abd3ccf87a9d5d253893ef2a881d9b"));
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
      assertThat(refExpr(intSpec(), 0).hash())
          .isEqualTo(Hash.decode("c41708244b3367007e1a216c7712cb2235371707"));
    }

    @Test
    public void positive_ref() {
      assertThat(refExpr(intSpec(), 123).hash())
          .isEqualTo(Hash.decode("130112c820a58abd4b90086ff7abfcf29f39a8e8"));
    }

    @Test
    public void negative_ref() {
      assertThat(refExpr(intSpec(), -123).hash())
          .isEqualTo(Hash.decode("f7ccbc4c95d78604753662395b5c5357f833fd64"));
    }
  }
}
