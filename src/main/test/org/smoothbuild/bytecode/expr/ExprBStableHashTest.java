package org.smoothbuild.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.testing.TestContext;

import okio.ByteString;

public class ExprBStableHashTest extends TestContext {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() {
      assertThat(arrayB(blobTB()).hash())
          .isEqualTo(Hash.decode("7991c72100363a6b211441ddd7604cf8c2475319"));
    }

    @Test
    public void non_empty_blob_array() {
      assertThat(arrayB(blobB(ByteString.of())).hash())
          .isEqualTo(Hash.decode("3888153634fcae728a3c223d1ac426b3c7e8070e"));
    }

    @Test
    public void empty_bool_array() {
      assertThat(arrayB(boolTB()).hash())
          .isEqualTo(Hash.decode("e73d629f082f0e2e59c24b375133f1ea56690be6"));
    }

    @Test
    public void non_empty_bool_array() {
      assertThat(arrayB(boolB(true)).hash())
          .isEqualTo(Hash.decode("f0ba42371214f2f9cc8bd94c2a97cbb1a63e2a50"));
    }

    @Test
    public void empty_string_array() {
      assertThat(arrayB(stringTB()).hash())
          .isEqualTo(Hash.decode("e345567c0a165254a6798f84d865715e95146e33"));
    }

    @Test
    public void non_empty_string_array() {
      assertThat(arrayB(stringB("")).hash())
          .isEqualTo(Hash.decode("1fcfa71539f86e48e089a7e2763574ed8f6537f2"));
    }

    @Test
    public void empty_tuple_array() {
      assertThat(arrayB(personTB()).hash())
          .isEqualTo(Hash.decode("2d1cbc2ca0f62157a707d2035cf1b74af9b39715"));
    }

    @Test
    public void non_empty_tuple_array() {
      assertThat(arrayB(personB("John", "Doe")).hash())
          .isEqualTo(Hash.decode("1fd6edc1e024b0b77157b26f7b2e10e363252b84"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() {
      assertThat(blobBBuilder().build().hash())
          .isEqualTo(Hash.decode("268479f0b3b4c136f72395d6c7f7eaf32ca5fb63"));
    }

    @Test
    public void some_blob() {
      assertThat(blobB(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(Hash.decode("8fa2712b2a42f360e1b5642311ac51b83c918043"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      assertThat(boolB(true).hash())
          .isEqualTo(Hash.decode("1ed40755143539e64cb53054c4f783ad4b912d48"));
    }

    @Test
    public void false_bool() {
      assertThat(boolB(false).hash())
          .isEqualTo(Hash.decode("64c2a127c558f460cc9b696d2324fd79b3486914"));
    }
  }

  @Nested
  class _closure {
    @Test
    public void with_zero_envs_zero_params() {
      var defFunc = closureB(funcTB(intTB()), combineB(), intB(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("608d8569e03180b3d47e763aa8a0a4aa41cba88d"));
    }

    @Test
    public void with_zero_envs_one_param() {
      var defFunc = closureB(funcTB(intTB(), blobTB()), combineB(), intB(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("6d183c1d5df711652425f5a25e9481f0278e7b4e"));
    }

    @Test
    public void with_one_env_zero_params() {
      var defFunc = closureB(funcTB(intTB()), combineB(stringB("abc")), intB(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("09e01279c26aafb84495c16b696036898c468133"));
    }

    @Test
    public void with_one_env_one_params() {
      var defFunc = closureB(funcTB(intTB(), blobTB()), combineB(stringB("abc")), intB(1));
      assertThat(defFunc.hash())
          .isEqualTo(Hash.decode("71ee4726ea6c2f90a2993bf60c3cee23c8ff1ec3"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      assertThat(intB(0).hash())
          .isEqualTo(Hash.decode("b8f423d48c90466a4ec449b70c22a526841a1bb1"));
    }

    @Test
    public void positive_int() {
      assertThat(intB(123).hash())
          .isEqualTo(Hash.decode("2a4bd1678aca4e88e27e33fceff0ac21312b2a16"));
    }

    @Test
    public void negative_int() {
      assertThat(intB(-123).hash())
          .isEqualTo(Hash.decode("6e33fe5b02ad6547407cc4cb5ca5cbc2a0ef1fdc"));
    }
  }

  @Nested
  class _nat_func {
    @Test
    public void nat_func() {
      assertThat(
          natFuncB(funcTB(intTB(), boolTB()), blobB(1), stringB("cbn"), boolB(true)).hash())
          .isEqualTo(Hash.decode("bb4fab02e531f5ed1cf67856ab50aeff12b437cc"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      assertThat(stringB("").hash())
          .isEqualTo(Hash.decode("a53684bb4332e7cf8d14528b86b9e88f53e4b46e"));
    }

    @Test
    public void some_string() {
      assertThat(stringB("abc").hash())
          .isEqualTo(Hash.decode("1d628f3fabf68bdce58564a788e8d547a7f8b38f"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void empty_tuple() {
      assertThat(tupleB().hash())
          .isEqualTo(Hash.decode("c36dc381590b0735064d2d5c9aab8a67a2a5f8f9"));
    }

    @Test
    public void some_tuple() {
      assertThat(personB("John", "Doe").hash())
          .isEqualTo(Hash.decode("8579dac659c7caf963b02ad39a8afb8df0bb8d8e"));
    }
  }

  // operations

  @Nested
  class _call {
    @Test
    public void call_without_args() {
      var type = funcTB(intTB());
      var defFunc = defFuncB(type, intB());
      assertThat(callB(defFunc).hash())
          .isEqualTo(Hash.decode("76c9083998f0619052d2f099575c89a5c49bb2e1"));
    }

    @Test
    public void call_with_one_arg() {
      var defFuncB = defFuncB(list(stringTB()), intB());
      assertThat(callB(defFuncB, stringB("abc")).hash())
          .isEqualTo(Hash.decode("9d01d1484fd8853f3a194132295684553d32e82b"));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine_with_one_arg() {
      assertThat(combineB(intB(1)).hash())
          .isEqualTo(Hash.decode("0c03a905f857e031ad7b04e8930ff64b0748d145"));
    }

    @Test
    public void combine_without_args() {
      assertThat(combineB().hash())
          .isEqualTo(Hash.decode("1fb99d59d6f07ce00139990658a49150e22fff2d"));
    }
  }

  @Nested
  class _if_func {
    @Test
    public void if_func() {
      assertThat(ifFuncB(intTB()).hash())
          .isEqualTo(Hash.decode("9b525dac777f1f1b4e1a73a9b11f228309d57d17"));
    }
  }

  @Nested
  class _map_func {
    @Test
    public void map_func() {
      assertThat(mapFuncB(intTB(), stringTB()).hash())
          .isEqualTo(Hash.decode("870fb7677d8f5eb447ecfc4bc9183b5eee25eb9e"));
    }
  }

  @Nested
  class _order {
    @Test
    public void empty_order() {
      assertThat(orderB(stringTB()).hash())
          .isEqualTo(Hash.decode("dd887811d106b4c25ca092c1d84edd18a491da6d"));
    }

    @Test
    public void order() {
      assertThat(orderB(intB(1)).hash())
          .isEqualTo(Hash.decode("0b3210b4fdd1a3c5e872060ce84f9b274f7baf23"));
    }
  }

  @Nested
  class _pick {
    @Test
    public void pick() {
      assertThat(pickB(arrayB(intB(7)), intB(0)).hash())
          .isEqualTo(Hash.decode("a56b3c2c161368750a3ed903a68da4cfe7532d82"));
    }
  }

  @Nested
  class _ref {
    @Test
    public void zero_ref() {
      assertThat(refB(intTB(), 0).hash())
          .isEqualTo(Hash.decode("b4ac839b88e92ebacf9ffe244a452837a2284d57"));
    }

    @Test
    public void positive_ref() {
      assertThat(refB(intTB(), 123).hash())
          .isEqualTo(Hash.decode("bf6b5c6be0c652b2184bb3df4924f3593a9f3f5e"));
    }

    @Test
    public void negative_ref() {
      assertThat(refB(intTB(), -123).hash())
          .isEqualTo(Hash.decode("7732155785f68d3b3677753b0f8fbf2d8ec8deb7"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() {
      assertThat(selectB(animalB(), intB(0)).hash())
          .isEqualTo(Hash.decode("51b9eb2ccdb5aef1be210523bb72c0b37bf0c39a"));
    }
  }
}
