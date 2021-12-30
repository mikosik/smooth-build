package org.smoothbuild.bytecode.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

import com.google.common.truth.Truth;

import okio.ByteString;

public class ObjBStableHashTest extends TestingContext {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() {
      Truth.assertThat(arrayB(blobTB()).hash())
          .isEqualTo(Hash.decode("fd11e3a7a0eca23315da184c8ce90c051724e90b"));
    }

    @Test
    public void non_empty_blob_array() {
      Truth.assertThat(arrayB(blobB(ByteString.of())).hash())
          .isEqualTo(Hash.decode("f3515ba2d7af14cb995b07179ff3f3e9a97b52a9"));
    }

    @Test
    public void empty_bool_array() {
      Truth.assertThat(arrayB(boolTB()).hash())
          .isEqualTo(Hash.decode("e4478355c1f6ea8d4a9edef0a7213cb1aa5b44d4"));
    }

    @Test
    public void non_empty_bool_array() {
      Truth.assertThat(arrayB(boolB(true)).hash())
          .isEqualTo(Hash.decode("20fbf2c6d0b35a92bb1552a309ef724ce722a1e4"));
    }

    @Test
    public void empty_nothing_array() {
      Truth.assertThat(arrayB(nothingTB()).hash())
          .isEqualTo(Hash.decode("fc21e8e3b2e04f46fa35cdd3fa0b932cb59c38a0"));
    }

    @Test
    public void empty_string_array() {
      Truth.assertThat(arrayB(stringTB()).hash())
          .isEqualTo(Hash.decode("5e8edad671513a73f56c6bb216789c6f85de320f"));
    }

    @Test
    public void non_empty_string_array() {
      Truth.assertThat(arrayB(stringB("")).hash())
          .isEqualTo(Hash.decode("c4e16e36229651682138e517ed97491946e3e3b9"));
    }

    @Test
    public void empty_tuple_array() {
      Truth.assertThat(arrayB(personTB()).hash())
          .isEqualTo(Hash.decode("a9688c71bf2aae5f4a8e219ce92e8ab8e6b46e27"));
    }

    @Test
    public void non_empty_tuple_array() {
      Truth.assertThat(arrayB(personB("John", "Doe")).hash())
          .isEqualTo(Hash.decode("4f98d430a9c3b36fec6a89ad64e19bef31da7a39"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() {
      Truth.assertThat(blobBBuilder().build().hash())
          .isEqualTo(Hash.decode("080d59deb0dab374a40f4919fed3fa0cd6bcbded"));
    }

    @Test
    public void some_blob() {
      Truth.assertThat(blobB(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("669cf1da5b2b0a7791089a2da12213dde61e8b8b"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      Truth.assertThat(boolB(true).hash())
          .isEqualTo(Hash.decode("721efcc95000fe4bf3bb4985b456bac91c8db596"));
    }

    @Test
    public void false_bool() {
      Truth.assertThat(boolB(false).hash())
          .isEqualTo(Hash.decode("b8f423d48c90466a4ec449b70c22a526841a1bb1"));
    }
  }

  @Nested
  class _func {
    @Test
    public void with_no_params() {
      var defFunc = funcB(funcTB(intTB(), list()), intB(1));
      Truth.assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("998efe35e88cbf9dc973880f96f5fc5dea3f2664"));
    }

    @Test
    public void with_one_param() {
      var defFunc = funcB(funcTB(intTB(), list(intTB())), intB(1));
      Truth.assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("5a69dadd0f5d07869ba7676dc2d1f25500d7a1a0"));
    }

    @Test
    public void with_two_params() {
      var defFunc =
          funcB(funcTB(intTB(), list(intTB(), stringTB())), intB(1));
      Truth.assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("8450b8f6b33d766600b980d143f24090f5c54936"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      Truth.assertThat(intB(0).hash())
          .isEqualTo(Hash.decode("3ce602708b7be67d736ece10605f5bccb2469b19"));
    }

    @Test
    public void positive_int() {
      Truth.assertThat(intB(123).hash())
          .isEqualTo(Hash.decode("1bb0ed034cf637193455b30239ba8f0dc2f2bd74"));
    }

    @Test
    public void negative_int() {
      Truth.assertThat(intB(-123).hash())
          .isEqualTo(Hash.decode("0e29da471c4e2cc3ef8bf161a9d1406a7204709a"));
    }
  }

  @Nested
  class _method {
    @Test
    public void method() {
      Truth.assertThat(
          methodB(methodTB(intTB(), list(boolTB())), blobB(1), stringB("cbn"), boolB(true)).hash())
          .isEqualTo(Hash.decode("1590b8ef2a27b91e1036df55d513e2423f196d89"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      Truth.assertThat(stringB("").hash())
          .isEqualTo(Hash.decode("707159057595e7c06a56fc09167364493620aa38"));
    }

    @Test
    public void some_string() {
      Truth.assertThat(stringB("abc").hash())
          .isEqualTo(Hash.decode("2f79dcd289cb78ca01f0f24a250203987145d9fa"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void empty_tuple() {
      Truth.assertThat(tupleBEmpty().hash())
          .isEqualTo(Hash.decode("54d8451fd0f31c5111433cddf501fabd26e2a9ab"));
    }

    @Test
    public void some_tuple() {
      Truth.assertThat(personB("John", "Doe").hash())
          .isEqualTo(Hash.decode("b83eb2d3c38918c2a563c693d817c6c09589bb28"));
    }
  }

  // expressions

  @Nested
  class _call {
    @Test
    public void call_with_one_arg() {
      assertThat(callB(funcB(list(stringTB()), intB()), list(stringB("abc"))).hash())
          .isEqualTo(Hash.decode("2f8d0f28aa7697573153900439e8c5888aa5b427"));
    }

    @Test
    public void call_without_args() {
      var type = funcTB(intTB(), list(stringTB()));
      var defFunc = funcB(type, intB());
      assertThat(callB(defFunc, list(stringB("abc"))).hash())
          .isEqualTo(Hash.decode("2f8d0f28aa7697573153900439e8c5888aa5b427"));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine_with_one_arg() {
      Truth.assertThat(combineB(list(intB(1))).hash())
          .isEqualTo(Hash.decode("16913cad3fbbe28270c3e51765de40c636944d0f"));
    }

    @Test
    public void combine_without_args() {
      Truth.assertThat(combineB(list()).hash())
          .isEqualTo(Hash.decode("3b2048ce36decfa28bc39b783dd88502103c73fd"));
    }
  }

  @Nested
  class _if {
    @Test
    public void if_() {
      Truth.assertThat(ifB(boolB(true), intB(1), intB(2)).hash())
          .isEqualTo(Hash.decode("d2fadb5c97a33675d3854149734c8a0bca9779d1"));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void invoke() {
      assertThat(invokeB(methodB(methodTB())).hash())
          .isEqualTo(Hash.decode("3e7615f6c89c3cf6776649c90585e5f58fd38ec8"));
    }
  }

  @Nested
  class _map {
    @Test
    public void map() {
      Truth.assertThat(mapB(arrayB(intB(1)), funcB(list(intTB()), intB(1))).hash())
          .isEqualTo(Hash.decode("c09ea55c05c98df2689ae12009448b3ee27a2378"));
    }
  }

  @Nested
  class _order {
    @Test
    public void empty_order() {
      Truth.assertThat(orderB(stringTB(), list()).hash())
          .isEqualTo(Hash.decode("f37765f87d6b95ddd98af9fcf7a1e3e9554cbe3b"));
    }

    @Test
    public void order() {
      Truth.assertThat(orderB(list(intB(1))).hash())
          .isEqualTo(Hash.decode("f05ca9450196cec30ab9ac0bc06bcf2c182f7434"));
    }
  }

  @Nested
  class _param_ref {
    @Test
    public void zero_ref() {
      Truth.assertThat(paramRefB(intTB(), 0).hash())
          .isEqualTo(Hash.decode("c41708244b3367007e1a216c7712cb2235371707"));
    }

    @Test
    public void positive_ref() {
      Truth.assertThat(paramRefB(intTB(), 123).hash())
          .isEqualTo(Hash.decode("130112c820a58abd4b90086ff7abfcf29f39a8e8"));
    }

    @Test
    public void negative_ref() {
      Truth.assertThat(paramRefB(intTB(), -123).hash())
          .isEqualTo(Hash.decode("f7ccbc4c95d78604753662395b5c5357f833fd64"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() {
      Truth.assertThat(selectB(animalB(), intB(0)).hash())
          .isEqualTo(Hash.decode("43cea8a5fe2387317f40c1ba960f0753c952909e"));
    }
  }
}
