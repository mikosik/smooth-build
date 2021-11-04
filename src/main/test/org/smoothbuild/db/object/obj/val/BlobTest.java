package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContextImpl;

import okio.ByteString;

public class BlobTest extends TestingContextImpl {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private final ByteString otherBytes = ByteString.encodeUtf8("bbb");

  @Test
  public void creating_blob_without_content_creates_empty_blob() throws Exception {
    Blob blob = blobBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void type_of_blob_is_blob_type() {
    assertThat(blob(bytes).type())
        .isEqualTo(blobOT());
  }

  @Test
  public void empty_blob_is_empty() throws Exception {
    Blob blob = blobBuilder().build();
    assertThat(blob.source().readByteString())
        .isEqualTo(ByteString.of());
  }

  @Test
  public void blob_has_content_passed_to_builder() throws Exception {
    Blob blob = blob(bytes);
    assertThat(blob.source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void blobs_with_equal_content_are_equal() {
    assertThat(blob(bytes))
        .isEqualTo(blob(bytes));
  }

  @Test
  public void blobs_with_different_content_are_not_equal() {
    assertThat(blob(bytes))
        .isNotEqualTo(blob(otherBytes));
  }

  @Test
  public void hash_of_blobs_with_equal_content_is_the_same() {
    assertThat(blob(bytes).hash())
        .isEqualTo(blob(bytes).hash());
  }

  @Test
  public void hash_of_blobs_with_different_content_is_not_the_same() {
    assertThat(blob(bytes).hash())
        .isNotEqualTo(blob(otherBytes).hash());
  }

  @Test
  public void hash_code_of_blob_with_equal_content_is_the_same() {
    assertThat(blob(bytes).hashCode())
        .isEqualTo(blob(bytes).hashCode());
  }

  @Test
  public void hash_code_of_blobs_with_different_values_is_not_the_same() {
    assertThat(blob(bytes).hashCode())
        .isNotEqualTo(blob(otherBytes).hashCode());
  }

  @Test
  public void blob_can_be_read_by_hash() {
    Blob blob = blob(bytes);
    Hash hash = blob.hash();
    assertThat(objectDbOther().get(hash))
        .isEqualTo(blob);
  }

  @Test
  public void blob_read_by_hash_has_same_content() throws Exception {
    Blob blob = blob(bytes);
    Hash hash = blob.hash();
    assertThat(((Blob) objectDbOther().get(hash)).source().readByteString())
        .isEqualTo(blob.source().readByteString());
  }

  @Test
  public void to_string() {
    Blob blob = blob(bytes);
    assertThat(blob.toString())
        .isEqualTo("0x??@" + blob.hash());
  }
}
