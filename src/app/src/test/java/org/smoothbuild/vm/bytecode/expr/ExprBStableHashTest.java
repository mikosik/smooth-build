package org.smoothbuild.vm.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.hashed.Hash;

import okio.ByteString;

public class ExprBStableHashTest extends TestContext {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() {
      assertThat(arrayB(blobTB()).hash())
          .isEqualTo(
              Hash.decode("1bfa1ae95dc2a3ca9458c6d2b38c6d05439ce691a485d258bf9010b2ac1e16f0"));
    }

    @Test
    public void non_empty_blob_array() {
      assertThat(arrayB(blobB(ByteString.of())).hash())
          .isEqualTo(
              Hash.decode("a4b52d82018ca42d6bf6ed5a77f7fe66709ef440d662d437a8ed9b7812109866"));
    }

    @Test
    public void empty_bool_array() {
      assertThat(arrayB(boolTB()).hash())
          .isEqualTo(
              Hash.decode("22f6d2608e16d5b80c986563a360927a7801d7cdeefee907290355be74e1fa3b"));
    }

    @Test
    public void non_empty_bool_array() {
      assertThat(arrayB(boolB(true)).hash())
          .isEqualTo(
              Hash.decode("f7394c245b6a5b500d82b5925257f3b59a7119b974e31dce63973786b96fd9b6"));
    }

    @Test
    public void empty_string_array() {
      assertThat(arrayB(stringTB()).hash())
          .isEqualTo(
              Hash.decode("f7ae3ca1694466f1f034be44211f2e9c65be6617c16ea6ac0231a2b5c9d0cee9"));
    }

    @Test
    public void non_empty_string_array() {
      assertThat(arrayB(stringB("")).hash())
          .isEqualTo(
              Hash.decode("e229203f16b09823af2f831aa4621bc7b2ed2236d45dce1d3fddd8e0ee90cad6"));
    }

    @Test
    public void empty_tuple_array() {
      assertThat(arrayB(personTB()).hash())
          .isEqualTo(
              Hash.decode("220cca21e0674fd38a47e01629aa37759ae2b87abb7aeee414505e48be920038"));
    }

    @Test
    public void non_empty_tuple_array() {
      assertThat(arrayB(personB("John", "Doe")).hash())
          .isEqualTo(
              Hash.decode("6cdf46c7a05665c63a20eb85ee86c947b9accefebb0638ffc2e9c456df7729d3"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() {
      assertThat(blobBBuilder().build().hash())
          .isEqualTo(
              Hash.decode("72a41db3104c7b18b2a606f85daa5f8dd160d2a25a34a1a838d682a3064fa568"));
    }

    @Test
    public void some_blob() {
      assertThat(blobB(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(
              Hash.decode("b55447cffca08d7fa9f4ee62686e803009872477df8a2b1b58c7934b3d3de25c"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() {
      assertThat(boolB(true).hash())
          .isEqualTo(
              Hash.decode("e9585a54d9f08cc32a4c31683378c0fdc64e7b8fb6af4eb92ba3c9cf8911e8ba"));
    }

    @Test
    public void false_bool() {
      assertThat(boolB(false).hash())
          .isEqualTo(
              Hash.decode("68a7bfdeda08d1242f5130cd8ae33d6edf2c8c9dc5f3dedd66cd898674a00516"));
    }
  }

  @Nested
  class _closure {
    @Test
    public void with_zero_envs_zero_params() {
      var closureB = closureB(combineB(), intB(1));
      assertThat(closureB.hash())
          .isEqualTo(
              Hash.decode("e0d85630cf0df448a25ca0ce39e173296288c690825f048d6be1006950834ffc"));
    }

    @Test
    public void with_zero_envs_one_param() {
      var closureB = closureB(combineB(), intB(1));
      assertThat(closureB.hash())
          .isEqualTo(
              Hash.decode("e0d85630cf0df448a25ca0ce39e173296288c690825f048d6be1006950834ffc"));
    }

    @Test
    public void with_one_env_zero_params() {
      var closureB = closureB(combineB(stringB("abc")), intB(1));
      assertThat(closureB.hash())
          .isEqualTo(
              Hash.decode("edfa4870e5543ea76bb2e8f8ccb84ea94710ca487f2afa421fd86ff1e120bbd5"));
    }

    @Test
    public void with_one_env_one_params() {
      var closureB = closureB(combineB(stringB("abc")), intB(1));
      assertThat(closureB.hash())
          .isEqualTo(
              Hash.decode("edfa4870e5543ea76bb2e8f8ccb84ea94710ca487f2afa421fd86ff1e120bbd5"));
    }
  }

  @Nested
  class _closurize {
    @Test
    public void closurize() {
      var closurizeB = closurizeB(exprFuncB(list(intTB()), stringB("abc")));
      assertThat(closurizeB.hash())
          .isEqualTo(
              Hash.decode("c55358a8eca397f9d4bfdf0f646b16132187e003c82ce8576afde0c978455f09"));
    }
  }

  @Nested
  class _expr_func {
    @Test
    public void with_zero_params() {
      var exprFuncB = exprFuncB(funcTB(intTB()), intB(1));
      assertThat(exprFuncB.hash())
          .isEqualTo(
              Hash.decode("faef46528811128130c0e03c968815ca0078f0c5625d57f133adf0df15f59d01"));
    }

    @Test
    public void with_one_param() {
      var exprFuncB = exprFuncB(funcTB(blobTB(), intTB()), intB(1));
      assertThat(exprFuncB.hash())
          .isEqualTo(
              Hash.decode("52cbdcec2cc3c7faa0f79c17a74cfb27b3826f7ceb9b38c1e8743bca16674df2"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() {
      assertThat(intB(0).hash())
          .isEqualTo(
              Hash.decode("7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba"));
    }

    @Test
    public void positive_int() {
      assertThat(intB(123).hash())
          .isEqualTo(
              Hash.decode("93732fe5fe66367d4161983150001f77efefc7b98a4d965769e20ee7abb1fa46"));
    }

    @Test
    public void negative_int() {
      assertThat(intB(-123).hash())
          .isEqualTo(
              Hash.decode("c92d9f238b61fe6cfb646e49512ad028ef954cfa0948d0289bade500b7bb5261"));
    }
  }

  @Nested
  class _native_func {
    @Test
    public void native_func() {
      assertThat(
          nativeFuncB(funcTB(boolTB(), intTB()), blobB(1), stringB("cbn"), boolB(true)).hash())
          .isEqualTo(
              Hash.decode("a86b59d69a244eeb96c059bc97ac7212445c45d37cb6346b27af19b78ecb0e98"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() {
      assertThat(stringB("").hash())
          .isEqualTo(
              Hash.decode("dc264d87acfd92c16aeaba68e2173a31c2fca74db513e6ca80064c6b550faa9d"));
    }

    @Test
    public void some_string() {
      assertThat(stringB("abc").hash())
          .isEqualTo(
              Hash.decode("a8290d3ebf36fd0cda7c9e3e5e4a81199d86c6ed3585c073502313f03bdf9986"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void empty_tuple() {
      assertThat(tupleB().hash())
          .isEqualTo(
              Hash.decode("ee57e38b2618df7dd0a7e4d108c6ff3faf256ed3eec2501702683eb9c9993251"));
    }

    @Test
    public void some_tuple() {
      assertThat(personB("John", "Doe").hash())
          .isEqualTo(
              Hash.decode("73d8d48ae8b9dd946be996c3d2d0997dd3810e8cf3bff5b2e55da3be08e29f9e"));
    }
  }

  // operations

  @Nested
  class _call {
    @Test
    public void call_without_args() {
      var type = funcTB(intTB());
      var exprFuncB = exprFuncB(type, intB());
      assertThat(callB(exprFuncB).hash())
          .isEqualTo(
              Hash.decode("b1fcd1d80fdd7dedd2fb319d8ce4d016d5cd5dedbd7f63e0a76fbe71591fd1cf"));
    }

    @Test
    public void call_with_one_arg() {
      var exprFuncB = exprFuncB(list(stringTB()), intB());
      assertThat(callB(exprFuncB, stringB("abc")).hash())
          .isEqualTo(
              Hash.decode("acf47e5b971e5fd421c94eb6d1ca328fa72d107ab7f473e0e8010d2db631b894"));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine_with_one_arg() {
      assertThat(combineB(intB(1)).hash())
          .isEqualTo(
              Hash.decode("06a264a951d27e6953fa12a624922cea7cbfd03ff7af071c9b7464990b20dc3b"));
    }

    @Test
    public void combine_without_args() {
      assertThat(combineB().hash())
          .isEqualTo(
              Hash.decode("1493f172bdb322c5b42eadc79333661a72295286e56f7107a1435e2e651e2a57"));
    }
  }

  @Nested
  class _if_func {
    @Test
    public void if_func() {
      assertThat(ifFuncB(intTB()).hash())
          .isEqualTo(
              Hash.decode("6e98e4543dc7381224cfc05eb232f1f6d63d71cc8445d564b1d35e3aaa522618"));
    }
  }

  @Nested
  class _map_func {
    @Test
    public void map_func() {
      assertThat(mapFuncB(intTB(), stringTB()).hash())
          .isEqualTo(
              Hash.decode("e19869a3801eb1b732bc8800aad5ff3756f16729d52c9a4d15efd831c20c3380"));
    }
  }

  @Nested
  class _order {
    @Test
    public void empty_order() {
      assertThat(orderB(stringTB()).hash())
          .isEqualTo(
              Hash.decode("e1f4fed2bf56965e2f8f965835f8a01685736939c5b154f114e63c2f1fcf522f"));
    }

    @Test
    public void order() {
      assertThat(orderB(intB(1)).hash())
          .isEqualTo(
              Hash.decode("32525892ab4d75f2b1f23293d34118c444fa06fe837ee9efaa2072032c879054"));
    }
  }

  @Nested
  class _pick {
    @Test
    public void pick() {
      assertThat(pickB(arrayB(intB(7)), intB(0)).hash())
          .isEqualTo(
              Hash.decode("febe7ecdc696ca5264129f451b3ecd00948b10dbda13a6cd8e880538f3a719b6"));
    }
  }

  @Nested
  class _reference {
    @Test
    public void zero_reference() {
      assertThat(varB(intTB(), 0).hash())
          .isEqualTo(
              Hash.decode("ddeb39ceb0da343b6e43e79988a72b9022c6326834faa98bc6386a63f6250b47"));
    }

    @Test
    public void positive_reference() {
      assertThat(varB(intTB(), 123).hash())
          .isEqualTo(
              Hash.decode("835fd9277c4aa2efb281e2e777cc65e74be8d939758e7454a4a6041c47aa4887"));
    }

    @Test
    public void negative_reference() {
      assertThat(varB(intTB(), -123).hash())
          .isEqualTo(
              Hash.decode("c73a5244ba1777a62f7d35167d267791cfacd2b222b4fc52f3bea334f19c809a"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() {
      assertThat(selectB(animalB(), intB(0)).hash())
          .isEqualTo(
              Hash.decode("b4c6333d5e5eddbaf6cc10f5f2ea298d7b7c163f71632dc7842c306f5f896d66"));
    }
  }
}
