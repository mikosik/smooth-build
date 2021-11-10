package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectHStableHashTest extends TestingContext {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() {
      assertThat(array(blobOT()).hash())
          .isEqualTo(Hash.decode("fd11e3a7a0eca23315da184c8ce90c051724e90b"));
    }

    @Test
    public void non_empty_blob_array() {
      assertThat(array(blob(ByteString.of())).hash())
          .isEqualTo(Hash.decode("f3515ba2d7af14cb995b07179ff3f3e9a97b52a9"));
    }

    @Test
    public void empty_bool_array() {
      assertThat(array(boolOT()).hash())
          .isEqualTo(Hash.decode("e4478355c1f6ea8d4a9edef0a7213cb1aa5b44d4"));
    }

    @Test
    public void non_empty_bool_array() {
      assertThat(array(bool(true)).hash())
          .isEqualTo(Hash.decode("20fbf2c6d0b35a92bb1552a309ef724ce722a1e4"));
    }

    @Test
    public void empty_nothing_array() {
      assertThat(array(nothingOT()).hash())
          .isEqualTo(Hash.decode("fc21e8e3b2e04f46fa35cdd3fa0b932cb59c38a0"));
    }

    @Test
    public void empty_string_array() {
      assertThat(array(stringOT()).hash())
          .isEqualTo(Hash.decode("5e8edad671513a73f56c6bb216789c6f85de320f"));
    }

    @Test
    public void non_empty_string_array() {
      assertThat(array(string("")).hash())
          .isEqualTo(Hash.decode("c4e16e36229651682138e517ed97491946e3e3b9"));
    }

    @Test
    public void empty_tuple_array() {
      assertThat(array(personOT()).hash())
          .isEqualTo(Hash.decode("a9688c71bf2aae5f4a8e219ce92e8ab8e6b46e27"));
    }

    @Test
    public void non_empty_tuple_array() {
      assertThat(array(person("John", "Doe")).hash())
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
      assertThat(blob(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("669cf1da5b2b0a7791089a2da12213dde61e8b8b"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      assertThat(bool(true).hash())
          .isEqualTo(Hash.decode("721efcc95000fe4bf3bb4985b456bac91c8db596"));
    }

    @Test
    public void false_bool() {
      assertThat(bool(false).hash())
          .isEqualTo(Hash.decode("b8f423d48c90466a4ec449b70c22a526841a1bb1"));
    }
  }

  @Nested
  class _call {
    @Test
    public void call_expression_with_one_argument() {
      assertThat(call(const_(function()), list(stringExpr("abc"))).hash())
          .isEqualTo(Hash.decode("dcc7713d99ef11f816501e79c7539b800ebeddd3"));
    }

    @Test
    public void call_expression_without_arguments() {
      FunctionTypeH type = functionOT(intOT(), list(stringOT()));
      FunctionH function = function(type, intExpr());
      assertThat(call(const_(function), list(stringExpr("abc"))).hash())
          .isEqualTo(Hash.decode("dcc7713d99ef11f816501e79c7539b800ebeddd3"));
    }
  }

  @Nested
  class _const {
    @Test
    public void const_blob_expression() {
      assertThat(const_(blob(ByteString.of((byte) 1, (byte) 2, (byte) 3))).hash())
          .isEqualTo(Hash.decode("086b9fcca29ced11d99315ea6ab3a77e5940f63e"));
    }

    @Test
    public void const_bool_expression() {
      assertThat(const_(bool(true)).hash())
          .isEqualTo(Hash.decode("abf2750c6950dd0e09cb7773aacec9cb9b6c6898"));
    }

    @Test
    public void const_int_expression() {
      assertThat(intExpr(123).hash())
          .isEqualTo(Hash.decode("3c511a9e2051727c550ff2b28b6664ed9a8b0fa5"));
    }

    @Test
    public void const_string_expression() {
      assertThat(stringExpr("abc").hash())
          .isEqualTo(Hash.decode("165ef0a60178c065ddb83a209a7d4c6a6f2ed779"));
    }
  }

  @Nested
  class _function {
    @Test
    public void with_no_parameters() {
      FunctionH function =
          function(functionOT(intOT(), list()), intExpr(1));
      assertThat(function.hash())
          .isEqualTo(Hash.decode("337bd07ddf18547d72c6ad93705ef00ae087cc3b"));
    }

    @Test
    public void with_one_parameter() {
      FunctionH function =
          function(functionOT(intOT(), list(intOT())), intExpr(1));
      assertThat(function.hash())
          .isEqualTo(Hash.decode("f5ab6c8ecc79081d233c4b1df33cdd5ad5e3e937"));
    }

    @Test
    public void with_two_parameters() {
      FunctionH function =
          function(functionOT(intOT(), list(intOT(), stringOT())), intExpr(1));
      assertThat(function.hash())
          .isEqualTo(Hash.decode("004270e88a71120738cc7c9a6283b10bc92a0b57"));
    }
  }

  @Nested
  class _order {
    @Test
    public void empty_order_expression() {
      assertThat(order(list()).hash())
          .isEqualTo(Hash.decode("a0b82433f715eef1e275af8702914efa74c1bffe"));
    }

    @Test
    public void order_expression() {
      assertThat(order(list(intExpr(1))).hash())
          .isEqualTo(Hash.decode("f80cf6e3c5af6de49d7253a0bd31ba802d75ce09"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select_expression() {
      assertThat(select(const_(animal()), int_(0)).hash())
          .isEqualTo(Hash.decode("c95a9b1d53aa249f7a8e1b9c1425214d0b37cf3e"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      assertThat(int_(0).hash())
          .isEqualTo(Hash.decode("3ce602708b7be67d736ece10605f5bccb2469b19"));
    }

    @Test
    public void positive_int() {
      assertThat(int_(123).hash())
          .isEqualTo(Hash.decode("1bb0ed034cf637193455b30239ba8f0dc2f2bd74"));
    }

    @Test
    public void negative_int() {
      assertThat(int_(-123).hash())
          .isEqualTo(Hash.decode("0e29da471c4e2cc3ef8bf161a9d1406a7204709a"));
    }
  }

  @Nested
  class _native_method {
    @Test
    public void native_method() {
      assertThat(nativeMethod(blob(), string()).hash())
          .isEqualTo(Hash.decode("e01f5fc6688c69549cea6a3ca230e26edb1fea52"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      assertThat(string("").hash())
          .isEqualTo(Hash.decode("707159057595e7c06a56fc09167364493620aa38"));
    }

    @Test
    public void some_string() {
      assertThat(string("abc").hash())
          .isEqualTo(Hash.decode("2f79dcd289cb78ca01f0f24a250203987145d9fa"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void empty_tuple() {
      assertThat(tupleEmpty().hash())
          .isEqualTo(Hash.decode("54d8451fd0f31c5111433cddf501fabd26e2a9ab"));
    }

    @Test
    public void some_tuple() {
      assertThat(person("John", "Doe").hash())
          .isEqualTo(Hash.decode("b83eb2d3c38918c2a563c693d817c6c09589bb28"));
    }
  }

  @Nested
  class _ref {
    @Test
    public void zero_ref() {
      assertThat(ref(intOT(), 0).hash())
          .isEqualTo(Hash.decode("c41708244b3367007e1a216c7712cb2235371707"));
    }

    @Test
    public void positive_ref() {
      assertThat(ref(intOT(), 123).hash())
          .isEqualTo(Hash.decode("130112c820a58abd4b90086ff7abfcf29f39a8e8"));
    }

    @Test
    public void negative_ref() {
      assertThat(ref(intOT(), -123).hash())
          .isEqualTo(Hash.decode("f7ccbc4c95d78604753662395b5c5357f833fd64"));
    }
  }
}