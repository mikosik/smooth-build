package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class BlobTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private final ByteString otherBytes = ByteString.encodeUtf8("bbb");

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    Blob blob = blobBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void spec_of_blob_is_blob() {
    assertThat(blobVal(bytes).spec())
        .isEqualTo(blobSpec());
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    Blob blob = blobBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    Blob blob = blobVal(bytes);
    assertThat(blob.source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void blobs_with_equal_content_are_equal() {
    assertThat(blobVal(bytes))
        .isEqualTo(blobVal(bytes));
  }

  @Test
  public void blobs_with_different_content_are_not_equal() {
    assertThat(blobVal(bytes))
        .isNotEqualTo(blobVal(otherBytes));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() {
    assertThat(blobVal(bytes).hash())
        .isEqualTo(blobVal(bytes).hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() {
    assertThat(blobVal(bytes).hash())
        .isNotEqualTo(blobVal(otherBytes).hash());
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() {
    assertThat(blobVal(bytes).hashCode())
        .isEqualTo(blobVal(bytes).hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() {
    assertThat(blobVal(bytes).hashCode())
        .isNotEqualTo(blobVal(otherBytes).hashCode());
  }

  @Test
  public void blob_can_be_read_by_hash() {
    Blob blob = blobVal(bytes);
    Hash hash = blob.hash();
    assertThat(objectDbOther().get(hash))
        .isEqualTo(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    Blob blob = blobVal(bytes);
    Hash hash = blob.hash();
    assertThat(((Blob) objectDbOther().get(hash)).source().readByteString())
        .isEqualTo(blob.source().readByteString());
  }

  @Test
  public void to_string() {
    Blob blob = blobVal(bytes);
    assertThat(blob.toString())
        .isEqualTo("0x??:" + blob.hash());
  }
}
