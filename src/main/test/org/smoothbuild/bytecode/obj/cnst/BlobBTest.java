package org.smoothbuild.bytecode.obj.cnst;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.ObjBTestCase;
import org.smoothbuild.db.Hash;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class BlobBTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    BlobB blob = blobBBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void type_of_blob_is_blob_type() {
    assertThat(blobB(bytes).cat())
        .isEqualTo(blobTB());
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    BlobB blob = blobBBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    BlobB blob = blobB(bytes);
    assertThat(blob.source().readByteString())
        .isEqualTo(bytes);
  }

  @Nested
  class _equals_hash_hashcode extends ObjBTestCase<BlobB> {
    @Override
    protected List<BlobB> equalValues() {
      return list(
          blobB(ByteString.encodeUtf8("aaa")),
          blobB(ByteString.encodeUtf8("aaa"))
      );
    }

    @Override
    protected List<BlobB> nonEqualValues() {
      return list(
          blobB(ByteString.encodeUtf8("")),
          blobB(ByteString.encodeUtf8("aaa")),
          blobB(ByteString.encodeUtf8("bbb"))
      );
    }
  }

  @Test
  public void blob_can_be_read_by_hash() {
    BlobB blob = blobB(bytes);
    Hash hash = blob.hash();
    assertThat(objDbOther().get(hash))
        .isEqualTo(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    BlobB blob = blobB(bytes);
    Hash hash = blob.hash();
    assertThat(((BlobB) objDbOther().get(hash)).source().readByteString())
        .isEqualTo(blob.source().readByteString());
  }

  @Test
  public void to_string() {
    BlobB blob = blobB(bytes);
    assertThat(blob.toString())
        .isEqualTo("0x??@" + blob.hash());
  }
}
