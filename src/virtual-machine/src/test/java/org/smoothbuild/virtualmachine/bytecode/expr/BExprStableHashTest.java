package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.testing.TestingVm;

public class BExprStableHashTest extends TestingVm {
  @Nested
  class _array {
    @Test
    void empty_blob_array() throws Exception {
      assertThat(bArray(bBlobType()).hash())
          .isEqualTo(
              Hash.decode("1bfa1ae95dc2a3ca9458c6d2b38c6d05439ce691a485d258bf9010b2ac1e16f0"));
    }

    @Test
    void non_empty_blob_array() throws Exception {
      assertThat(bArray(bBlob(ByteString.of())).hash())
          .isEqualTo(
              Hash.decode("a4b52d82018ca42d6bf6ed5a77f7fe66709ef440d662d437a8ed9b7812109866"));
    }

    @Test
    void empty_bool_array() throws Exception {
      assertThat(bArray(bBoolType()).hash())
          .isEqualTo(
              Hash.decode("22f6d2608e16d5b80c986563a360927a7801d7cdeefee907290355be74e1fa3b"));
    }

    @Test
    void non_empty_bool_array() throws Exception {
      assertThat(bArray(bBool(true)).hash())
          .isEqualTo(
              Hash.decode("f7394c245b6a5b500d82b5925257f3b59a7119b974e31dce63973786b96fd9b6"));
    }

    @Test
    void empty_string_array() throws Exception {
      assertThat(bArray(bStringType()).hash())
          .isEqualTo(
              Hash.decode("f7ae3ca1694466f1f034be44211f2e9c65be6617c16ea6ac0231a2b5c9d0cee9"));
    }

    @Test
    void non_empty_string_array() throws Exception {
      assertThat(bArray(bString("")).hash())
          .isEqualTo(
              Hash.decode("e229203f16b09823af2f831aa4621bc7b2ed2236d45dce1d3fddd8e0ee90cad6"));
    }

    @Test
    void empty_tuple_array() throws Exception {
      assertThat(bArray(bPersonType()).hash())
          .isEqualTo(
              Hash.decode("220cca21e0674fd38a47e01629aa37759ae2b87abb7aeee414505e48be920038"));
    }

    @Test
    void non_empty_tuple_array() throws Exception {
      assertThat(bArray(bPerson("John", "Doe")).hash())
          .isEqualTo(
              Hash.decode("6cdf46c7a05665c63a20eb85ee86c947b9accefebb0638ffc2e9c456df7729d3"));
    }
  }

  @Nested
  class _blob {
    @Test
    void empty_blob() throws Exception {
      try (var blobBBuilder = bBlobBuilder()) {
        assertThat(blobBBuilder.build().hash())
            .isEqualTo(
                Hash.decode("72a41db3104c7b18b2a606f85daa5f8dd160d2a25a34a1a838d682a3064fa568"));
      }
    }

    @Test
    void some_blob() throws Exception {
      assertThat(bBlob(ByteString.encodeUtf8("aaa")).hash())
          .isEqualTo(
              Hash.decode("b55447cffca08d7fa9f4ee62686e803009872477df8a2b1b58c7934b3d3de25c"));
    }
  }

  @Nested
  class _bool {
    @Test
    void true_bool() throws Exception {
      assertThat(bBool(true).hash())
          .isEqualTo(
              Hash.decode("e9585a54d9f08cc32a4c31683378c0fdc64e7b8fb6af4eb92ba3c9cf8911e8ba"));
    }

    @Test
    void false_bool() throws Exception {
      assertThat(bBool(false).hash())
          .isEqualTo(
              Hash.decode("68a7bfdeda08d1242f5130cd8ae33d6edf2c8c9dc5f3dedd66cd898674a00516"));
    }
  }

  @Nested
  class _lambda {
    @Test
    void with_zero_params() throws Exception {
      var lambdaB = bLambda(bLambdaType(bIntType()), bInt(1));
      assertThat(lambdaB.hash())
          .isEqualTo(
              Hash.decode("cbebb7b4c0262db1ee75678b2cf4f8d0b4d587e53c9d0167234d3f75b5c82e45"));
    }

    @Test
    void with_one_param() throws Exception {
      var lambdaB = bLambda(bLambdaType(bBlobType(), bIntType()), bInt(1));
      assertThat(lambdaB.hash())
          .isEqualTo(
              Hash.decode("8ce918aa234ebc3ab7a490fd559d31b4be576a465c4f3307c476bcf280872d5a"));
    }
  }

  @Nested
  class _int {
    @Test
    void zero_int() throws Exception {
      assertThat(bInt(0).hash())
          .isEqualTo(
              Hash.decode("7188b43d5debd8d65201a289a38515321a8419bc78b29e75675211deff8b08ba"));
    }

    @Test
    void positive_int() throws Exception {
      assertThat(bInt(123).hash())
          .isEqualTo(
              Hash.decode("93732fe5fe66367d4161983150001f77efefc7b98a4d965769e20ee7abb1fa46"));
    }

    @Test
    void negative_int() throws Exception {
      assertThat(bInt(-123).hash())
          .isEqualTo(
              Hash.decode("c92d9f238b61fe6cfb646e49512ad028ef954cfa0948d0289bade500b7bb5261"));
    }
  }

  @Nested
  class _invoke {
    @Test
    void invoke() throws Exception {
      var method = bMethodTuple(bBlob(1), bString("cbn"));
      assertThat(bInvoke(bBoolType(), method, bBool(true), bTuple()).hash())
          .isEqualTo(
              Hash.decode("88c48c17f85b313ad685177d3e37583d09a5518ddecaabbea5f5f647b5897218"));
    }
  }

  @Nested
  class _string {
    @Test
    void empty_string() throws Exception {
      assertThat(bString("").hash())
          .isEqualTo(
              Hash.decode("dc264d87acfd92c16aeaba68e2173a31c2fca74db513e6ca80064c6b550faa9d"));
    }

    @Test
    void some_string() throws Exception {
      assertThat(bString("abc").hash())
          .isEqualTo(
              Hash.decode("a8290d3ebf36fd0cda7c9e3e5e4a81199d86c6ed3585c073502313f03bdf9986"));
    }
  }

  @Nested
  class _tuple {
    @Test
    void empty_tuple() throws Exception {
      assertThat(bTuple().hash())
          .isEqualTo(
              Hash.decode("ee57e38b2618df7dd0a7e4d108c6ff3faf256ed3eec2501702683eb9c9993251"));
    }

    @Test
    void some_tuple() throws Exception {
      assertThat(bPerson("John", "Doe").hash())
          .isEqualTo(
              Hash.decode("73d8d48ae8b9dd946be996c3d2d0997dd3810e8cf3bff5b2e55da3be08e29f9e"));
    }
  }

  // operations

  @Nested
  class _call {
    @Test
    void call_without_args() throws Exception {
      var type = bLambdaType(bIntType());
      var lambda = bLambda(type, bInt());
      assertThat(bCall(lambda).hash())
          .isEqualTo(
              Hash.decode("5fde6253d2a350e28562f50735745f89d40e8c471cbc67264cdd6a47751ceb24"));
    }

    @Test
    void call_with_one_arg() throws Exception {
      var lambdaB = bLambda(list(bStringType()), bInt());
      assertThat(bCall(lambdaB, bString("abc")).hash())
          .isEqualTo(
              Hash.decode("3b252948eca848beff114959af5f9fe1e39fbf0a01279dd66b52d5f008f2fcc9"));
    }
  }

  @Nested
  class _combine {
    @Test
    void combine_with_one_arg() throws Exception {
      assertThat(bCombine(bInt(1)).hash())
          .isEqualTo(
              Hash.decode("06a264a951d27e6953fa12a624922cea7cbfd03ff7af071c9b7464990b20dc3b"));
    }

    @Test
    void combine_without_args() throws Exception {
      assertThat(bCombine().hash())
          .isEqualTo(
              Hash.decode("1493f172bdb322c5b42eadc79333661a72295286e56f7107a1435e2e651e2a57"));
    }
  }

  @Nested
  class _if_operation {
    @Test
    void if_operation() throws Exception {
      assertThat(bIf(bBool(true), bInt(1), bInt(2)).hash())
          .isEqualTo(
              Hash.decode("ae2abb3bad2420d56b3777571d60d2b9086d8153d22d332e7da71918e7f830de"));
    }
  }

  @Nested
  class _map_operation {
    @Test
    void map_operation() throws Exception {
      assertThat(bMap(bArray(bInt()), bIntIdLambda()).hash())
          .isEqualTo(
              Hash.decode("03dfaa1ba8fb539e33af03360e83237a69d3152fd8c4181eefd3166280c25574"));
    }
  }

  @Nested
  class _order {
    @Test
    void empty_order() throws Exception {
      assertThat(bOrder(bStringType()).hash())
          .isEqualTo(
              Hash.decode("e1f4fed2bf56965e2f8f965835f8a01685736939c5b154f114e63c2f1fcf522f"));
    }

    @Test
    void order() throws Exception {
      assertThat(bOrder(bInt(1)).hash())
          .isEqualTo(
              Hash.decode("32525892ab4d75f2b1f23293d34118c444fa06fe837ee9efaa2072032c879054"));
    }
  }

  @Nested
  class _pick {
    @Test
    void pick() throws Exception {
      assertThat(bPick(bArray(bInt(7)), bInt(0)).hash())
          .isEqualTo(
              Hash.decode("febe7ecdc696ca5264129f451b3ecd00948b10dbda13a6cd8e880538f3a719b6"));
    }
  }

  @Nested
  class _reference {
    @Test
    void zero_reference() throws Exception {
      assertThat(bReference(bIntType(), 0).hash())
          .isEqualTo(
              Hash.decode("ddeb39ceb0da343b6e43e79988a72b9022c6326834faa98bc6386a63f6250b47"));
    }

    @Test
    void positive_reference() throws Exception {
      assertThat(bReference(bIntType(), 123).hash())
          .isEqualTo(
              Hash.decode("835fd9277c4aa2efb281e2e777cc65e74be8d939758e7454a4a6041c47aa4887"));
    }

    @Test
    void negative_reference() throws Exception {
      assertThat(bReference(bIntType(), -123).hash())
          .isEqualTo(
              Hash.decode("c73a5244ba1777a62f7d35167d267791cfacd2b222b4fc52f3bea334f19c809a"));
    }
  }

  @Nested
  class _select {
    @Test
    void select() throws Exception {
      assertThat(bSelect(bAnimal(), bInt(0)).hash())
          .isEqualTo(
              Hash.decode("b4c6333d5e5eddbaf6cc10f5f2ea298d7b7c163f71632dc7842c306f5f896d66"));
    }
  }
}
