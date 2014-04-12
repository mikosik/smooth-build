package org.smoothbuild.io.cache.value.build;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.lang.type.FileArrayMatchers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class SValueBuildersImplTest {
  String content = "content";
  Path path1 = path("my/path/file1.txt");
  Path path2 = path("my/path/file2.txt");
  FakeValueDb valueDb = new FakeValueDb();

  SValueBuildersImpl sValueBuilders = new SValueBuildersImpl(valueDb);

  @Test
  public void file_array_builder_stores_files_in_value_db() throws Exception {
    BlobBuilder blobBuilder = sValueBuilders.blobBuilder();
    StreamTester.writeAndClose(blobBuilder.openOutputStream(), content);
    SBlob blob = blobBuilder.build();

    FileBuilder fileBuilder = sValueBuilders.fileBuilder();
    fileBuilder.setPath(path1);
    fileBuilder.setContent(blob);
    SFile file = fileBuilder.build();

    ArrayBuilder<SFile> builder = sValueBuilders.arrayBuilder(FILE_ARRAY);
    builder.add(file);
    HashCode hash = builder.build().hash();

    SArray<SFile> fileArray = valueDb.read(FILE_ARRAY, hash);
    MatcherAssert.assertThat(fileArray, FileArrayMatchers.containsFileContaining(path1, content));
    assertThat(Iterables.size(fileArray)).isEqualTo(1);
  }

  @Test
  public void file_array_builder_can_store_empty_array() throws Exception {
    HashCode hash = sValueBuilders.arrayBuilder(FILE_ARRAY).build().hash();
    SArray<SFile> fileArray = valueDb.read(FILE_ARRAY, hash);
    assertThat(fileArray).isEmpty();
  }

  @Test
  public void blob_array_builder_stores_blobs_in_value_db() throws Exception {
    BlobBuilder blobBuilder = sValueBuilders.blobBuilder();
    StreamTester.writeAndClose(blobBuilder.openOutputStream(), content);
    SBlob blob = blobBuilder.build();

    ArrayBuilder<SBlob> builder = sValueBuilders.arrayBuilder(BLOB_ARRAY);
    builder.add(blob);
    HashCode hash = builder.build().hash();

    SArray<SBlob> fileArray = valueDb.read(BLOB_ARRAY, hash);
    assertThat(Iterables.size(fileArray)).isEqualTo(1);
    assertThat(inputStreamToString(fileArray.iterator().next().openInputStream())).isEqualTo(
        content);
  }

  @Test
  public void blob_array_builder_can_store_empty_array() throws Exception {
    HashCode hash = sValueBuilders.arrayBuilder(BLOB_ARRAY).build().hash();
    SArray<SBlob> fileArray = valueDb.read(BLOB_ARRAY, hash);
    assertThat(fileArray).isEmpty();
  }

  @Test
  public void string_array_builder_stores_files_in_value_db() throws Exception {
    String jdkString1 = "my string 1";
    String jdkString2 = "my string 2";

    SString string1 = sValueBuilders.string(jdkString1);
    SString string2 = sValueBuilders.string(jdkString2);

    ArrayBuilder<SString> builder = sValueBuilders.arrayBuilder(STRING_ARRAY);
    builder.add(string1);
    builder.add(string2);
    SArray<SString> stringArray = builder.build();

    SArray<SString> stringArrayRead = valueDb.read(STRING_ARRAY, stringArray.hash());
    List<String> strings = Lists.newArrayList();
    for (SString string : stringArrayRead) {
      strings.add(string.value());
    }

    assertThat(strings).containsOnly(jdkString1, jdkString2);
  }

  @Test
  public void string_array_builder_can_store_empty_array() throws Exception {
    HashCode hash = sValueBuilders.arrayBuilder(STRING_ARRAY).build().hash();
    SArray<SString> stringArray = valueDb.read(STRING_ARRAY, hash);
    assertThat(stringArray).isEmpty();
  }

  @Test
  public void file_builder_stores_file_in_value_db() throws Exception {
    BlobBuilder blobBuilder = sValueBuilders.blobBuilder();
    StreamTester.writeAndClose(blobBuilder.openOutputStream(), content);
    SBlob blob = blobBuilder.build();

    FileBuilder fileBuilder = sValueBuilders.fileBuilder();
    fileBuilder.setPath(path1);
    fileBuilder.setContent(blob);
    HashCode hash = fileBuilder.build().hash();

    SFile file = valueDb.read(FILE, hash);
    assertThat(file.path()).isEqualTo(path1);
    assertThat(inputStreamToString(file.content().openInputStream())).isEqualTo(content);
  }

  @Test
  public void blob_builder_stores_blob_in_value_db() throws Exception {
    BlobBuilder blobBuilder = sValueBuilders.blobBuilder();
    StreamTester.writeAndClose(blobBuilder.openOutputStream(), content);
    HashCode hash = blobBuilder.build().hash();

    SBlob blob = valueDb.read(BLOB, hash);
    assertThat(inputStreamToString(blob.openInputStream())).isEqualTo(content);
  }

  @Test
  public void string_stores_its_content_in_value_db() throws Exception {
    String jdkString = "my string";
    SString string = sValueBuilders.string(jdkString);

    SString stringRead = valueDb.read(STRING, string.hash());

    assertThat(stringRead.value()).isEqualTo(jdkString);
  }

}
