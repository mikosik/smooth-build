package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import java.util.List;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.AbstractBExprTestSuite;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BBlobTest extends TestingVirtualMachine {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    try (var builder = bBlobBuilder()) {
      var blob = builder.build();
      try (var source = blob.source()) {
        assertThat(source.readByteString()).isEqualTo(ByteString.of());
      }
    }
  }

  @Test
  public void type_of_blob_is_blob_type() throws Exception {
    assertThat(bBlob(bytes).kind()).isEqualTo(bBlobType());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    var blob = bBlob(bytes);
    try (var source = blob.source()) {
      assertThat(source.readByteString()).isEqualTo(bytes);
    }
  }

  @Nested
  class _equals_hash_hashcode extends AbstractBExprTestSuite<BBlob> {
    @Override
    protected List<BBlob> equalExprs() throws BytecodeException {
      return list(bBlob(ByteString.encodeUtf8("aaa")), bBlob(ByteString.encodeUtf8("aaa")));
    }

    @Override
    protected List<BBlob> nonEqualExprs() throws BytecodeException {
      return list(
          bBlob(ByteString.encodeUtf8("")),
          bBlob(ByteString.encodeUtf8("aaa")),
          bBlob(ByteString.encodeUtf8("bbb")));
    }
  }

  @Test
  public void blob_can_be_read_by_hash() throws Exception {
    var blob = bBlob(bytes);
    var hash = blob.hash();
    assertThat(exprDbOther().get(hash)).isEqualTo(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    var blob = bBlob(bytes);
    var hash = blob.hash();
    try (var source = blob.source()) {
      try (var otherSource = ((BBlob) exprDbOther().get(hash)).source()) {
        assertThat(otherSource.readByteString()).isEqualTo(source.readByteString());
      }
    }
  }

  @Test
  public void to_string() throws Exception {
    var blob = bBlob(bytes);
    assertThat(blob.toString()).isEqualTo("0x??@" + blob.hash());
  }
}
