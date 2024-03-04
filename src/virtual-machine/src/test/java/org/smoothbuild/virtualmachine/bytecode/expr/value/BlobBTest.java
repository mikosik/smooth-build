package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractExprBTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BlobBTest extends TestingVirtualMachine {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    try (var builder = blobBBuilder()) {
      var blobB = builder.build();
      try (var source = blobB.source()) {
        assertThat(source.readByteString()).isEqualTo(ByteString.of());
      }
    }
  }

  @Test
  public void type_of_blob_is_blob_type() throws Exception {
    assertThat(blobB(bytes).category()).isEqualTo(blobTB());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    var blobB = blobB(bytes);
    try (var source = blobB.source()) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }

  @Nested
  class _equals_hash_hashcode extends AbstractExprBTestSuite<BlobB> {
    @Override
    protected List<BlobB> equalExprs() throws BytecodeException {
      return list(blobB(ByteString.encodeUtf8("aaa")), blobB(ByteString.encodeUtf8("aaa")));
    }

    @Override
    protected List<BlobB> nonEqualExprs() throws BytecodeException {
      return list(
          blobB(ByteString.encodeUtf8("")),
          blobB(ByteString.encodeUtf8("aaa")),
          blobB(ByteString.encodeUtf8("bbb")));
    }
  }

  @Test
  public void blob_can_be_read_by_hash() throws Exception {
    BlobB blob = blobB(bytes);
    Hash hash = blob.hash();
    assertThat(exprDbOther().get(hash)).isEqualTo(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    var blobB = blobB(bytes);
    var hash = blobB.hash();
    try (var source = blobB.source()) {
      try (var otherSource = ((BlobB) exprDbOther().get(hash)).source()) {
        assertThat(otherSource.readByteString()).isEqualTo(source.readByteString());
      }
    }
  }

  @Test
  public void to_string() throws Exception {
    BlobB blob = blobB(bytes);
    assertThat(blob.toString()).isEqualTo("0x??@" + blob.hash());
  }
}
