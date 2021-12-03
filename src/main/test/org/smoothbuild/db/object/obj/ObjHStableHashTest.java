package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjHStableHashTest extends TestingContext {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() {
      assertThat(arrayH(blobTH()).hash())
          .isEqualTo(Hash.decode("fd11e3a7a0eca23315da184c8ce90c051724e90b"));
    }

    @Test
    public void non_empty_blob_array() {
      assertThat(arrayH(blobH(ByteString.of())).hash())
          .isEqualTo(Hash.decode("f3515ba2d7af14cb995b07179ff3f3e9a97b52a9"));
    }

    @Test
    public void empty_bool_array() {
      assertThat(arrayH(boolTH()).hash())
          .isEqualTo(Hash.decode("e4478355c1f6ea8d4a9edef0a7213cb1aa5b44d4"));
    }

    @Test
    public void non_empty_bool_array() {
      assertThat(arrayH(boolH(true)).hash())
          .isEqualTo(Hash.decode("20fbf2c6d0b35a92bb1552a309ef724ce722a1e4"));
    }

    @Test
    public void empty_nothing_array() {
      assertThat(arrayH(nothingTH()).hash())
          .isEqualTo(Hash.decode("fc21e8e3b2e04f46fa35cdd3fa0b932cb59c38a0"));
    }

    @Test
    public void empty_string_array() {
      assertThat(arrayH(stringTH()).hash())
          .isEqualTo(Hash.decode("5e8edad671513a73f56c6bb216789c6f85de320f"));
    }

    @Test
    public void non_empty_string_array() {
      assertThat(arrayH(stringH("")).hash())
          .isEqualTo(Hash.decode("c4e16e36229651682138e517ed97491946e3e3b9"));
    }

    @Test
    public void empty_tuple_array() {
      assertThat(arrayH(personTH()).hash())
          .isEqualTo(Hash.decode("a9688c71bf2aae5f4a8e219ce92e8ab8e6b46e27"));
    }

    @Test
    public void non_empty_tuple_array() {
      assertThat(arrayH(personH("John", "Doe")).hash())
          .isEqualTo(Hash.decode("4f98d430a9c3b36fec6a89ad64e19bef31da7a39"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() {
      assertThat(blobHBuilder().build().hash())
          .isEqualTo(Hash.decode("080d59deb0dab374a40f4919fed3fa0cd6bcbded"));
    }

    @Test
    public void some_blob() {
      assertThat(blobH(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("669cf1da5b2b0a7791089a2da12213dde61e8b8b"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      assertThat(boolH(true).hash())
          .isEqualTo(Hash.decode("721efcc95000fe4bf3bb4985b456bac91c8db596"));
    }

    @Test
    public void false_bool() {
      assertThat(boolH(false).hash())
          .isEqualTo(Hash.decode("b8f423d48c90466a4ec449b70c22a526841a1bb1"));
    }
  }

  @Nested
  class _call {
    @Test
    public void call_expression_with_one_arg() {
      assertThat(callH(defFuncH(list(stringTH()), intH()), list(stringH("abc"))).hash())
          .isEqualTo(Hash.decode("2f8d0f28aa7697573153900439e8c5888aa5b427"));
    }

    @Test
    public void call_expression_without_args() {
      var type = defFuncTH(intTH(), list(stringTH()));
      var defFunc = defFuncH(type, intH());
      assertThat(callH(defFunc, list(stringH("abc"))).hash())
          .isEqualTo(Hash.decode("2f8d0f28aa7697573153900439e8c5888aa5b427"));
    }
  }

  @Nested
  class _func {
    @Test
    public void with_no_params() {
      var defFunc = defFuncH(defFuncTH(intTH(), list()), intH(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("998efe35e88cbf9dc973880f96f5fc5dea3f2664"));
    }

    @Test
    public void with_one_param() {
      var defFunc = defFuncH(defFuncTH(intTH(), list(intTH())), intH(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("5a69dadd0f5d07869ba7676dc2d1f25500d7a1a0"));
    }

    @Test
    public void with_two_params() {
      var defFunc =
          defFuncH(defFuncTH(intTH(), list(intTH(), stringTH())), intH(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("8450b8f6b33d766600b980d143f24090f5c54936"));
    }
  }

  @Nested
  class _if_func {
    @Test
    public void if_func() {
      assertThat(ifFuncH().hash())
          .isEqualTo(Hash.decode("71f3c48cb3cdea01e0a16281045dcd44648ed967"));
    }
  }

  @Nested
  class _map_func {
    @Test
    public void map_func() {
      assertThat(mapFuncH().hash())
          .isEqualTo(Hash.decode("42652402e5d992d158c23d2225223a49d304e423"));
    }
  }

  @Nested
  class _order {
    @Test
    public void empty_order_expression() {
      assertThat(orderH(list()).hash())
          .isEqualTo(Hash.decode("a0b82433f715eef1e275af8702914efa74c1bffe"));
    }

    @Test
    public void order_expression() {
      assertThat(orderH(list(intH(1))).hash())
          .isEqualTo(Hash.decode("f05ca9450196cec30ab9ac0bc06bcf2c182f7434"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select_expression() {
      assertThat(selectH(animalH(), intH(0)).hash())
          .isEqualTo(Hash.decode("43cea8a5fe2387317f40c1ba960f0753c952909e"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      assertThat(intH(0).hash())
          .isEqualTo(Hash.decode("3ce602708b7be67d736ece10605f5bccb2469b19"));
    }

    @Test
    public void positive_int() {
      assertThat(intH(123).hash())
          .isEqualTo(Hash.decode("1bb0ed034cf637193455b30239ba8f0dc2f2bd74"));
    }

    @Test
    public void negative_int() {
      assertThat(intH(-123).hash())
          .isEqualTo(Hash.decode("0e29da471c4e2cc3ef8bf161a9d1406a7204709a"));
    }
  }

  @Nested
  class _nat_func {
    @Test
    public void nat_func() {
      assertThat(natFuncH(blobH(), stringH()).hash())
          .isEqualTo(Hash.decode("7f1ca2cc91e3ccddb77ef9881acc6588d2f90385"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      assertThat(stringH("").hash())
          .isEqualTo(Hash.decode("707159057595e7c06a56fc09167364493620aa38"));
    }

    @Test
    public void some_string() {
      assertThat(stringH("abc").hash())
          .isEqualTo(Hash.decode("2f79dcd289cb78ca01f0f24a250203987145d9fa"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void empty_tuple() {
      assertThat(tupleHEmpty().hash())
          .isEqualTo(Hash.decode("54d8451fd0f31c5111433cddf501fabd26e2a9ab"));
    }

    @Test
    public void some_tuple() {
      assertThat(personH("John", "Doe").hash())
          .isEqualTo(Hash.decode("b83eb2d3c38918c2a563c693d817c6c09589bb28"));
    }
  }

  @Nested
  class _ref {
    @Test
    public void zero_ref() {
      assertThat(paramRefH(intTH(), 0).hash())
          .isEqualTo(Hash.decode("c41708244b3367007e1a216c7712cb2235371707"));
    }

    @Test
    public void positive_ref() {
      assertThat(paramRefH(intTH(), 123).hash())
          .isEqualTo(Hash.decode("130112c820a58abd4b90086ff7abfcf29f39a8e8"));
    }

    @Test
    public void negative_ref() {
      assertThat(paramRefH(intTH(), -123).hash())
          .isEqualTo(Hash.decode("f7ccbc4c95d78604753662395b5c5357f833fd64"));
    }
  }
}
