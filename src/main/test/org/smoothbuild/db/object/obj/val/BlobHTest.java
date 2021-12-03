package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class BlobHTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private final ByteString otherBytes = ByteString.encodeUtf8("bbb");

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    BlobH blob = blobHBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void type_of_blob_is_blob_type() {
    assertThat(blobH(bytes).cat())
        .isEqualTo(blobTH());
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    BlobH blob = blobHBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    BlobH blob = blobH(bytes);
    assertThat(blob.source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void blobs_with_equal_content_are_equal() {
    assertThat(blobH(bytes))
        .isEqualTo(blobH(bytes));
  }

  @Test
  public void blobs_with_different_content_are_not_equal() {
    assertThat(blobH(bytes))
        .isNotEqualTo(blobH(otherBytes));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() {
    assertThat(blobH(bytes).hash())
        .isEqualTo(blobH(bytes).hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() {
    assertThat(blobH(bytes).hash())
        .isNotEqualTo(blobH(otherBytes).hash());
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() {
    assertThat(blobH(bytes).hashCode())
        .isEqualTo(blobH(bytes).hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() {
    assertThat(blobH(bytes).hashCode())
        .isNotEqualTo(blobH(otherBytes).hashCode());
  }

  @Test
  public void blob_can_be_read_by_hash() {
    BlobH blob = blobH(bytes);
    Hash hash = blob.hash();
    assertThat(objDbOther().get(hash))
        .isEqualTo(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    BlobH blob = blobH(bytes);
    Hash hash = blob.hash();
    assertThat(((BlobH) objDbOther().get(hash)).source().readByteString())
        .isEqualTo(blob.source().readByteString());
  }

  @Test
  public void to_string() {
    BlobH blob = blobH(bytes);
    assertThat(blob.toString())
        .isEqualTo("0x??@" + blob.hash());
  }
}
