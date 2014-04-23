package org.smoothbuild.lang.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import org.junit.Test;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class ConversionsTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  @Test
  public void testCanConvert() throws Exception {
    assertThat(Conversions.canConvert(STRING, STRING)).isTrue();
    assertThat(Conversions.canConvert(STRING, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING, BLOB)).isFalse();
    assertThat(Conversions.canConvert(STRING, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING, FILE)).isFalse();
    assertThat(Conversions.canConvert(STRING, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING, NIL)).isFalse();

    assertThat(Conversions.canConvert(BLOB, STRING)).isFalse();
    assertThat(Conversions.canConvert(BLOB, BLOB)).isTrue();
    assertThat(Conversions.canConvert(BLOB, FILE)).isFalse();
    assertThat(Conversions.canConvert(BLOB, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB, NIL)).isFalse();

    assertThat(Conversions.canConvert(FILE, STRING)).isFalse();
    assertThat(Conversions.canConvert(FILE, BLOB)).isTrue();
    assertThat(Conversions.canConvert(FILE, FILE)).isTrue();
    assertThat(Conversions.canConvert(FILE, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE, NIL)).isFalse();

    assertThat(Conversions.canConvert(STRING_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, STRING_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(STRING_ARRAY, BLOB_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(STRING_ARRAY, NIL)).isFalse();

    assertThat(Conversions.canConvert(BLOB_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(BLOB_ARRAY, FILE_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(BLOB_ARRAY, NIL)).isFalse();

    assertThat(Conversions.canConvert(FILE_ARRAY, STRING)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, BLOB)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, FILE)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, STRING_ARRAY)).isFalse();
    assertThat(Conversions.canConvert(FILE_ARRAY, BLOB_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(FILE_ARRAY, FILE_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(FILE_ARRAY, NIL)).isFalse();

    assertThat(Conversions.canConvert(NIL, STRING)).isFalse();
    assertThat(Conversions.canConvert(NIL, BLOB)).isFalse();
    assertThat(Conversions.canConvert(NIL, FILE)).isFalse();
    assertThat(Conversions.canConvert(NIL, STRING_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(NIL, BLOB_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(NIL, FILE_ARRAY)).isTrue();
    assertThat(Conversions.canConvert(NIL, NIL)).isTrue();
  }

  @Test
  public void superTypes() throws Exception {
    assertThat(Conversions.superTypesOf(STRING)).containsOnly();
    assertThat(Conversions.superTypesOf(BLOB)).containsOnly();
    assertThat(Conversions.superTypesOf(FILE)).containsOnly(BLOB);

    assertThat(Conversions.superTypesOf(STRING_ARRAY)).containsOnly();
    assertThat(Conversions.superTypesOf(BLOB_ARRAY)).containsOnly();
    assertThat(Conversions.superTypesOf(FILE_ARRAY)).containsOnly(BLOB_ARRAY);
    assertThat(Conversions.superTypesOf(NIL)).containsOnly(STRING_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  }

  @Test
  public void convertFileToBlob() throws Exception {
    SFile file = objectsDb.file(path("abc"));
    SBlob blob = Conversions.converter(FILE, BLOB).convert(new FakeObjectsDb(), file);
    assertThat(blob).isSameAs(file.content());
  }

  @Test
  public void convertFileArrayToBlobArray() throws Exception {
    SFile file1 = objectsDb.file(path("abc"));
    SFile file2 = objectsDb.file(path("def"));

    SArray<SFile> fileArray = objectsDb.array(FILE_ARRAY, file1, file2);
    Converter<SArray<SFile>, SArray<SBlob>> converter =
        Conversions.converter(FILE_ARRAY, BLOB_ARRAY);
    SArray<SBlob> blobArray = converter.convert(new FakeObjectsDb(), fileArray);
    assertThat(blobArray).containsExactly(file1.content(), file2.content());
  }

  @Test
  public void convertEmptyArrayToStringArray() throws Exception {
    Converter<SArray<SNothing>, SArray<SString>> converter =
        Conversions.converter(NIL, STRING_ARRAY);
    SArray<SNothing> nil = objectsDb.arrayBuilder(NIL).build();
    SArray<SString> array = converter.convert(objectsDb, nil);
    assertThat(array).isEmpty();
  }

  @Test
  public void convertEmptyArrayToBlobArray() throws Exception {
    Converter<SArray<SNothing>, SArray<SBlob>> converter = Conversions.converter(NIL, BLOB_ARRAY);
    SArray<SNothing> nil = objectsDb.arrayBuilder(NIL).build();
    SArray<SBlob> array = converter.convert(objectsDb, nil);
    assertThat(array).isEmpty();
  }

  @Test
  public void convertEmptyArrayToFileArray() throws Exception {
    Converter<SArray<SNothing>, SArray<SFile>> converter = Conversions.converter(NIL, FILE_ARRAY);
    SArray<SNothing> nil = objectsDb.arrayBuilder(NIL).build();
    SArray<SFile> array = converter.convert(objectsDb, nil);
    assertThat(array).isEmpty();
  }
}
