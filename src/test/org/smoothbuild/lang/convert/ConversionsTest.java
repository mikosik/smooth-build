package org.smoothbuild.lang.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.testing.lang.type.FakeArray.fakeArray;

import org.junit.Test;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.testing.lang.type.FakeArray;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.task.exec.FakePluginApi;

public class ConversionsTest {

  @Test
  public void testCanConvert() throws Exception {
    assertThat(Conversions.canConvert(STRING, STRING)).isTrue();
    assertThat(Conversions.canConvert(STRING, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING, BLOB)).isFalse();
    assertThat(Conversions.canConvert(STRING, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING, FILE)).isFalse();
    assertThat(Conversions.canConvert(STRING, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING, EMPTY_ARRAY)).isFalse();

    assertThat(Conversions.canConvert(BLOB, STRING)).isFalse();
    assertThat(Conversions.canConvert(BLOB, BLOB)).isTrue();
    assertThat(Conversions.canConvert(BLOB, FILE)).isFalse();
    assertThat(Conversions.canConvert(BLOB, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB, EMPTY_ARRAY)).isFalse();

    assertThat(Conversions.canConvert(FILE, STRING)).isFalse();
    assertThat(Conversions.canConvert(FILE, BLOB)).isTrue();
    assertThat(Conversions.canConvert(FILE, FILE)).isTrue();
    assertThat(Conversions.canConvert(FILE, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE, EMPTY_ARRAY)).isFalse();

    assertThat(Conversions.canConvert(STRING_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, STRING_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(STRING_ARRAY, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, EMPTY_ARRAY)).isFalse();

    assertThat(Conversions.canConvert(BLOB_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(BLOB_ARRAY, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, EMPTY_ARRAY)).isFalse();

    assertThat(Conversions.canConvert(FILE_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(FILE_ARRAY, FILE_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(FILE_ARRAY, EMPTY_ARRAY)).isFalse();

    assertThat(Conversions.canConvert(EMPTY_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(EMPTY_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(EMPTY_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(EMPTY_ARRAY, STRING_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(EMPTY_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(EMPTY_ARRAY, FILE_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(EMPTY_ARRAY, EMPTY_ARRAY)).isTrue();
  }

  @Test
  public void superTypes() throws Exception {
    assertThat(Conversions.superTypesOf(STRING)).containsOnly();
    assertThat(Conversions.superTypesOf(BLOB)).containsOnly();
    assertThat(Conversions.superTypesOf(FILE)).containsOnly(BLOB);

    assertThat(Conversions.superTypesOf(STRING_ARRAY)).containsOnly();
    assertThat(Conversions.superTypesOf(BLOB_ARRAY)).containsOnly();
    assertThat(Conversions.superTypesOf(FILE_ARRAY)).containsOnly(BLOB_ARRAY);
    assertThat(Conversions.superTypesOf(EMPTY_ARRAY)).containsOnly(STRING_ARRAY, BLOB_ARRAY,
        FILE_ARRAY);
  }

  @Test
  public void convertFileToBlob() throws Exception {
    FakeFile file = new FakeFile(path("abc"));
    Object blob = Conversions.converter(FILE, BLOB).convert(new FakePluginApi(), file);
    assertThat(blob).isSameAs(file.content());
  }

  @Test
  public void convertFileArrayToBlobArray() throws Exception {
    FakeFile file1 = new FakeFile(path("abc"));
    FakeFile file2 = new FakeFile(path("abc"));

    FakeArray<SFile> fileArray = fakeArray(FILE_ARRAY, file1, file2);
    Converter<?> converter = Conversions.converter(FILE_ARRAY, BLOB_ARRAY);
    @SuppressWarnings("unchecked")
    SArray<SBlob> blobArray = (SArray<SBlob>) converter.convert(new FakePluginApi(), fileArray);
    assertThat(blobArray).containsExactly(file1.content(), file2.content());
  }

  @Test
  public void convertEmptyArrayToStringArray() throws Exception {
    FakePluginApi pluginApi = new FakePluginApi();

    Converter<?> converter = Conversions.converter(EMPTY_ARRAY, STRING_ARRAY);
    @SuppressWarnings("unchecked")
    SArray<SString> array = (SArray<SString>) converter.convert(pluginApi, pluginApi.emptyArray());
    assertThat(array).isEmpty();
  }

  @Test
  public void convertEmptyArrayToBlobArray() throws Exception {
    FakePluginApi pluginApi = new FakePluginApi();

    Converter<?> converter = Conversions.converter(EMPTY_ARRAY, BLOB_ARRAY);
    @SuppressWarnings("unchecked")
    SArray<SBlob> array = (SArray<SBlob>) converter.convert(pluginApi, pluginApi.emptyArray());
    assertThat(array).isEmpty();
  }

  @Test
  public void convertEmptyArrayToFileArray() throws Exception {
    FakePluginApi pluginApi = new FakePluginApi();

    Converter<?> converter = Conversions.converter(EMPTY_ARRAY, FILE_ARRAY);
    @SuppressWarnings("unchecked")
    SArray<SFile> array = (SArray<SFile>) converter.convert(pluginApi, pluginApi.emptyArray());
    assertThat(array).isEmpty();
  }
}
