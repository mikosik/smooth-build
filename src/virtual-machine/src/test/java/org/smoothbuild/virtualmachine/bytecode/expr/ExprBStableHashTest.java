package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ExprBStableHashTest extends TestingVirtualMachine {
  @Nested
  class _array {
    @Test
    public void empty_blob_array() throws Exception {
      assertThat(arrayB(blobTB()).hash())
          .isEqualTo(
              Hash.decode("1bfa1ae95dc2a3ca9458c6d2b38c6d05439ce691a485d258bf9010b2ac1e16f0"));
    }

    @Test
    public void non_empty_blob_array() throws Exception {
      assertThat(arrayB(blobB(ByteString.of())).hash())
          .isEqualTo(
              Hash.decode("a4b52d82018ca42d6bf6ed5a77f7fe66709ef440d662d437a8ed9b7812109866"));
    }

    @Test
    public void empty_bool_array() throws Exception {
      assertThat(arrayB(boolTB()).hash())
          .isEqualTo(
              Hash.decode("22f6d2608e16d5b80c986563a360927a7801d7cdeefee907290355be74e1fa3b"));
    }

    @Test
    public void non_empty_bool_array() throws Exception {
      assertThat(arrayB(boolB(true)).hash())
          .isEqualTo(
              Hash.decode("f7394c245b6a5b500d82b5925257f3b59a7119b974e31dce63973786b96fd9b6"));
    }

    @Test
    public void empty_string_array() throws Exception {
      assertThat(arrayB(stringTB()).hash())
          .isEqualTo(
              Hash.decode("f7ae3ca1694466f1f034be44211f2e9c65be6617c16ea6ac0231a2b5c9d0cee9"));
    }

    @Test
    public void non_empty_string_array() throws Exception {
      assertThat(arrayB(stringB("")).hash())
          .isEqualTo(
              Hash.decode("e229203f16b09823af2f831aa4621bc7b2ed2236d45dce1d3fddd8e0ee90cad6"));
    }

    @Test
    public void empty_tuple_array() throws Exception {
      assertThat(arrayB(personTB()).hash())
          .isEqualTo(
              Hash.decode("220cca21e0674fd38a47e01629aa37759ae2b87abb7aeee414505e48be920038"));
    }

    @Test
    public void non_empty_tuple_array() throws Exception {
      assertThat(arrayB(personB("John", "Doe")).hash())
          .isEqualTo(
              Hash.decode("6cdf46c7a05665c63a20eb85ee86c947b9accefebb0638ffc2e9c456df7729d3"));
    }
  }

  @Nested
  class _blob {
    @Test
    public void empty_blob() throws Exception {
      try (var blobBBuilder = blobBBuilder()) {
        assertThat(blobBBuilder.build().hash())
            .isEqualTo(
                Hash.decode("72a41db3104c7b18b2a606f85daa5f8dd160d2a25a34a1a838d682a3064fa568"));
      }
    }

    @Test
    public void some_blob() throws Exception {
      assertThat(blobB(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(
              Hash.decode("b55447cffca08d7fa9f4ee62686e803009872477df8a2b1b58c7934b3d3de25c"));
    }
  }

  @Nested
  class _bool {
    @Test
    public void true_bool() throws Exception {
      assertThat(boolB(true).hash())
          .isEqualTo(
              Hash.decode("e9585a54d9f08cc32a4c31683378c0fdc64e7b8fb6af4eb92ba3c9cf8911e8ba"));
    }

    @Test
    public void false_bool() throws Exception {
      assertThat(boolB(false).hash())
          .isEqualTo(
              Hash.decode("68a7bfdeda08d1242f5130cd8ae33d6edf2c8c9dc5f3dedd66cd898674a00516"));
    }
  }

  @Nested
  class _lambda {
    @Test
    public void with_zero_params() throws Exception {
      var lambdaB = lambdaB(funcTB(intTB()), intB(1));
      assertThat(lambdaB.hash())
          .isEqualTo(
              Hash.decode("ca7332dde14f5b0385f6919dd5a0bb7516b985218cb5b3ab7fe231b02827a615"));
    }

    @Test
    public void with_one_param() throws Exception {
      var lambdaB = lambdaB(funcTB(blobTB(), intTB()), intB(1));
      assertThat(lambdaB.hash())
          .isEqualTo(
              Hash.decode("b4fe4202b949e583a75d441a2f7654fd94ff5c616f206343f707fc24df25031a"));
    }
  }

  @Nested
  class _int {
    @Test
    public void zero_int() throws Exception {
      assertThat(intB(0).hash())
          .isEqualTo(
              Hash.decode("7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba"));
    }

    @Test
    public void positive_int() throws Exception {
      assertThat(intB(123).hash())
          .isEqualTo(
              Hash.decode("93732fe5fe66367d4161983150001f77efefc7b98a4d965769e20ee7abb1fa46"));
    }

    @Test
    public void negative_int() throws Exception {
      assertThat(intB(-123).hash())
          .isEqualTo(
              Hash.decode("c92d9f238b61fe6cfb646e49512ad028ef954cfa0948d0289bade500b7bb5261"));
    }
  }

  @Nested
  class _native_func {
    @Test
    public void native_func() throws Exception {
      assertThat(nativeFuncB(funcTB(boolTB(), intTB()), blobB(1), stringB("cbn"), boolB(true))
              .hash())
          .isEqualTo(
              Hash.decode("a86b59d69a244eeb96c059bc97ac7212445c45d37cb6346b27af19b78ecb0e98"));
    }
  }

  @Nested
  class _string {
    @Test
    public void empty_string() throws Exception {
      assertThat(stringB("").hash())
          .isEqualTo(
              Hash.decode("dc264d87acfd92c16aeaba68e2173a31c2fca74db513e6ca80064c6b550faa9d"));
    }

    @Test
    public void some_string() throws Exception {
      assertThat(stringB("abc").hash())
          .isEqualTo(
              Hash.decode("a8290d3ebf36fd0cda7c9e3e5e4a81199d86c6ed3585c073502313f03bdf9986"));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void empty_tuple() throws Exception {
      assertThat(tupleB().hash())
          .isEqualTo(
              Hash.decode("ee57e38b2618df7dd0a7e4d108c6ff3faf256ed3eec2501702683eb9c9993251"));
    }

    @Test
    public void some_tuple() throws Exception {
      assertThat(personB("John", "Doe").hash())
          .isEqualTo(
              Hash.decode("73d8d48ae8b9dd946be996c3d2d0997dd3810e8cf3bff5b2e55da3be08e29f9e"));
    }
  }

  // operations

  @Nested
  class _call {
    @Test
    public void call_without_args() throws Exception {
      var type = funcTB(intTB());
      var funcB = lambdaB(type, intB());
      assertThat(callB(funcB).hash())
          .isEqualTo(
              Hash.decode("ad58f94040c8d5a6551a1ada94aab077ea710a97b364428ebc07e4c066a5916b"));
    }

    @Test
    public void call_with_one_arg() throws Exception {
      var lambdaB = lambdaB(list(stringTB()), intB());
      assertThat(callB(lambdaB, stringB("abc")).hash())
          .isEqualTo(
              Hash.decode("a09ab8dc2cda31a74cb0f2cde880a272f7e6e5d6e506a744402d4a0afe0c20ba"));
    }
  }

  @Nested
  class _combine {
    @Test
    public void combine_with_one_arg() throws Exception {
      assertThat(combineB(intB(1)).hash())
          .isEqualTo(
              Hash.decode("06a264a951d27e6953fa12a624922cea7cbfd03ff7af071c9b7464990b20dc3b"));
    }

    @Test
    public void combine_without_args() throws Exception {
      assertThat(combineB().hash())
          .isEqualTo(
              Hash.decode("1493f172bdb322c5b42eadc79333661a72295286e56f7107a1435e2e651e2a57"));
    }
  }

  @Nested
  class _if_func {
    @Test
    public void if_func() throws Exception {
      assertThat(ifFuncB(intTB()).hash())
          .isEqualTo(
              Hash.decode("6e98e4543dc7381224cfc05eb232f1f6d63d71cc8445d564b1d35e3aaa522618"));
    }
  }

  @Nested
  class _map_func {
    @Test
    public void map_func() throws Exception {
      assertThat(mapFuncB(intTB(), stringTB()).hash())
          .isEqualTo(
              Hash.decode("e19869a3801eb1b732bc8800aad5ff3756f16729d52c9a4d15efd831c20c3380"));
    }
  }

  @Nested
  class _order {
    @Test
    public void empty_order() throws Exception {
      assertThat(orderB(stringTB()).hash())
          .isEqualTo(
              Hash.decode("e1f4fed2bf56965e2f8f965835f8a01685736939c5b154f114e63c2f1fcf522f"));
    }

    @Test
    public void order() throws Exception {
      assertThat(orderB(intB(1)).hash())
          .isEqualTo(
              Hash.decode("32525892ab4d75f2b1f23293d34118c444fa06fe837ee9efaa2072032c879054"));
    }
  }

  @Nested
  class _pick {
    @Test
    public void pick() throws Exception {
      assertThat(pickB(arrayB(intB(7)), intB(0)).hash())
          .isEqualTo(
              Hash.decode("febe7ecdc696ca5264129f451b3ecd00948b10dbda13a6cd8e880538f3a719b6"));
    }
  }

  @Nested
  class _reference {
    @Test
    public void zero_reference() throws Exception {
      assertThat(varB(intTB(), 0).hash())
          .isEqualTo(
              Hash.decode("ddeb39ceb0da343b6e43e79988a72b9022c6326834faa98bc6386a63f6250b47"));
    }

    @Test
    public void positive_reference() throws Exception {
      assertThat(varB(intTB(), 123).hash())
          .isEqualTo(
              Hash.decode("835fd9277c4aa2efb281e2e777cc65e74be8d939758e7454a4a6041c47aa4887"));
    }

    @Test
    public void negative_reference() throws Exception {
      assertThat(varB(intTB(), -123).hash())
          .isEqualTo(
              Hash.decode("c73a5244ba1777a62f7d35167d267791cfacd2b222b4fc52f3bea334f19c809a"));
    }
  }

  @Nested
  class _select {
    @Test
    public void select() throws Exception {
      assertThat(selectB(animalB(), intB(0)).hash())
          .isEqualTo(
              Hash.decode("b4c6333d5e5eddbaf6cc10f5f2ea298d7b7c163f71632dc7842c306f5f896d66"));
    }
  }
}
