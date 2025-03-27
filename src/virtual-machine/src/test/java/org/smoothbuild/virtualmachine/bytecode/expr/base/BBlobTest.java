package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;

import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BBlobTest extends VmTestContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  void creating_blob_without_content_creates_empty_blob() throws Exception {
    try (var builder = bBlobBuilder()) {
      var blob = builder.build();
      try (var source = buffer(blob.source())) {
        assertThat(source.readByteString()).isEqualTo(ByteString.of());
      }
    }
  }

  @Test
  void type_of_blob_is_blob_type() throws Exception {
    assertThat(bBlob(bytes).kind()).isEqualTo(bBlobType());
  }

  @Test
  void blob_has_content_passed_to_builder() throws Exception {
    var blob = bBlob(bytes);
    try (var source = buffer(blob.source())) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BBlob> {
    @Override
    protected List<BBlob> equalExprs() throws Exception {
      return list(bBlob(ByteString.encodeUtf8("aaa")), bBlob(ByteString.encodeUtf8("aaa")));
    }

    @Override
    protected List<BBlob> nonEqualExprs() throws Exception {
      return list(
          bBlob(ByteString.encodeUtf8("")),
          bBlob(ByteString.encodeUtf8("aaa")),
          bBlob(ByteString.encodeUtf8("bbb")));
    }
  }

  @Test
  void blob_can_be_read_by_hash() throws Exception {
    var blob = bBlob(bytes);
    var hash = blob.hash();
    assertThat(exprDbOther().get(hash)).isEqualTo(blob);
  }

  @Test
  void blob_read_by_hash_has_same_content() throws Exception {
    var blob = bBlob(bytes);
    var hash = blob.hash();
    try (var source = buffer(blob.source())) {
      try (var otherSource = buffer(((BBlob) exprDbOther().get(hash)).source())) {
        assertThat(otherSource.readByteString()).isEqualTo(source.readByteString());
      }
    }
  }

  @Test
  void to_string() throws Exception {
    var blob = bBlob(bytes);
    assertThat(blob.toString())
        .isEqualTo(
            """
        BBlob(
          hash = b55447cffca08d7fa9f4ee62686e803009872477df8a2b1b58c7934b3d3de25c
          evaluationType = Blob
          value = 0x616161
        )""");
  }
}
