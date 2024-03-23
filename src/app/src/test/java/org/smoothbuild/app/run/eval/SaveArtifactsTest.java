package org.smoothbuild.app.run.eval;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.app.run.eval.SaveArtifacts.FILE_STRUCT_NAME;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.testing.TestingBucket.directoryToFileMap;
import static org.smoothbuild.common.testing.TestingBucket.readFile;
import static org.smoothbuild.common.testing.TestingByteString.byteStringWithSingleByteEqualOne;
import static org.smoothbuild.common.testing.TestingByteString.byteStringWithSingleByteEqualZero;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.annotatedValueS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.arrayTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.blobTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.boolTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.nativeAnnotationS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.stringTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.structTS;
import static org.smoothbuild.evaluator.EvaluatedExprs.evaluatedExprs;

import java.io.IOException;
import java.util.Map;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.SubBucket;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.type.StructTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class SaveArtifactsTest extends TestingVirtualMachine {
  @Override
  public Bucket hashedDbBucket() {
    return new SubBucket(projectBucket(), HASHED_DB_PATH);
  }

  @Test
  void store_bool_artifact() throws Exception {
    var typeS = boolTS();
    var valueB = boolB(true);
    var valueAsByteString = byteStringWithSingleByteEqualOne();

    testValueStoring(typeS, valueB, valueAsByteString);
  }

  @Test
  void store_int_artifact() throws Exception {
    var typeS = intTS();
    var valueB = intB(7);
    var valueAsByteString = ByteString.of((byte) 7);

    testValueStoring(typeS, valueB, valueAsByteString);
  }

  @Test
  void store_string_artifact() throws Exception {
    var typeS = stringTS();
    var valueB = stringB("abc");
    var valueAsByteString = byteStringFrom("abc");

    testValueStoring(typeS, valueB, valueAsByteString);
  }

  @Test
  void store_blob_artifact() throws Exception {
    var typeS = blobTS();
    var valueAsByteString = byteStringFrom("abc");
    var valueB = blobB(valueAsByteString);

    testValueStoring(typeS, valueB, valueAsByteString);
  }

  @Test
  void store_file_artifact() throws Exception {
    var typeS = fileTS();
    var string = "abc";
    var contentAsByteString = byteStringFrom(string);
    var valueB = fileB(path("my/path"), contentAsByteString);
    var artifactRelativePath = "myValue/my/path";

    testValueStoring(typeS, valueB, contentAsByteString, artifactRelativePath);
  }

  @Test
  void store_struct_with_same_fields_as_file_is_not_using_path_as_artifact_name() throws Exception {
    var typeS = structTS("NotAFile", blobTS(), stringTS());
    var valueB = tupleB(stringB("my/path"), blobB(byteStringFrom("abc")));
    var byteString = readFile(hashedDbBucket(), HashedDb.dbPathTo(valueB.dataHash()));

    testValueStoring(typeS, valueB, byteString);
  }

  @Test
  void store_empty_bool_array_artifact() throws Exception {
    var typeS = arrayTS(boolTS());
    var valueB = arrayB(boolTB());

    testValueStoring(typeS, valueB, "myValue", Map.of());
  }

  @Test
  void store_not_empty_bool_array_artifact() throws Exception {
    var typeS = arrayTS(boolTS());
    var valueB = arrayB(boolTB(), boolB(true), boolB(false));

    testValueStoring(
        typeS,
        valueB,
        "myValue",
        Map.of(
            path("myValue/0"),
            byteStringWithSingleByteEqualOne(),
            path("myValue/1"),
            byteStringWithSingleByteEqualZero()));
  }

  @Test
  void store_empty_string_array_artifact() throws Exception {
    var typeS = arrayTS(stringTS());
    var valueB = arrayB(stringTB());

    testValueStoring(typeS, valueB, "myValue", Map.of());
  }

  @Test
  void store_not_empty_string_array_artifact() throws Exception {
    var typeS = arrayTS(stringTS());
    var valueB = arrayB(stringTB(), stringB("abc"), stringB("def"));

    testValueStoring(
        typeS,
        valueB,
        "myValue",
        Map.of(path("myValue/0"), byteStringFrom("abc"), path("myValue/1"), byteStringFrom("def")));
  }

  @Test
  void store_empty_blob_array_artifact() throws Exception {
    var typeS = arrayTS(blobTS());
    var valueB = arrayB(blobTB());

    testValueStoring(typeS, valueB, "myValue", Map.of());
  }

  @Test
  void store_not_empty_blob_array_artifact() throws Exception {
    var typeS = arrayTS(blobTS());
    var valueB = arrayB(blobTB(), blobB(7), blobB(8));

    testValueStoring(
        typeS,
        valueB,
        "myValue",
        Map.of(path("myValue/0"), ByteString.of((byte) 7), path("myValue/1"), ByteString.of((byte)
            8)));
  }

  @Test
  void store_empty_file_array_artifact() throws Exception {
    var typeS = arrayTS(fileTS());
    var valueB = arrayB(fileTB());

    testValueStoring(typeS, valueB, "myValue", Map.of());
  }

  @Test
  void store_not_empty_file_array_artifact() throws Exception {
    var typeS = arrayTS(fileTS());
    var content1 = byteStringFrom("abc");
    var content2 = byteStringFrom("def");
    var valueB = arrayB(fileTB(), fileB("dir1/file1", content1), fileB("dir2/file2", content2));

    testValueStoring(
        typeS,
        valueB,
        "myValue",
        Map.of(path("myValue/dir1/file1"), content1, path("myValue/dir2/file2"), content2));
  }

  @Test
  void store_array_of_files_with_duplicated_paths_fails() throws Exception {
    var typeS = arrayTS(fileTS());
    var content1 = byteStringFrom("abc");
    var content2 = byteStringFrom("def");
    var path = path("dir1/file1");
    var valueB = arrayB(fileTB(), fileB(path, content1), fileB(path, content2));

    assertThat(saveArtifacts(typeS, valueB).logs())
        .isEqualTo(
            list(
                error(
                    """
                Can't store array of Files as it contains files with duplicated paths:
                  'dir1/file1'""")));
  }

  @Test
  void info_about_stored_artifacts_is_printed_to_console_in_alphabetical_order() throws Exception {
    var saveArtifacts = new SaveArtifacts(projectBucket());
    List<ExprS> exprSs = list(
        instantiateS(stringTS(), "myValue1"),
        instantiateS(stringTS(), "myValue2"),
        instantiateS(stringTS(), "myValue3"));
    List<BValue> bValues = list(stringB(), stringB(), stringB());
    var stringTry = saveArtifacts.apply(evaluatedExprs(exprSs, bValues));
    assertThat(stringTry)
        .isEqualTo(success(
            null,
            info("myValue1 -> '.smooth/artifacts/myValue1'"),
            info("myValue2 -> '.smooth/artifacts/myValue2'"),
            info("myValue3 -> '.smooth/artifacts/myValue3'")));
  }

  private void testValueStoring(TypeS typeS, BValue value, ByteString valueAsByteString)
      throws Exception {
    testValueStoring(typeS, value, valueAsByteString, "myValue");
  }

  private void testValueStoring(
      TypeS typeS, BValue value, ByteString valueAsByteString, String artifactRelativePath)
      throws IOException {
    var expectedDirectoryMap = Map.of(path(artifactRelativePath), valueAsByteString);
    testValueStoring(typeS, value, artifactRelativePath, expectedDirectoryMap);
  }

  private void testValueStoring(
      TypeS typeS,
      BValue value,
      String artifactRelativePath,
      Map<Path, ByteString> expectedDirectoryMap)
      throws IOException {
    var result = saveArtifacts(typeS, value);

    assertThat(result)
        .isEqualTo(
            success(null, info("myValue -> '.smooth/artifacts/" + artifactRelativePath + "'")));
    assertThat(directoryToFileMap(projectBucket(), ARTIFACTS_PATH)).isEqualTo(expectedDirectoryMap);
  }

  private Try<Void> saveArtifacts(TypeS typeS, BValue value) {
    var saveArtifacts = new SaveArtifacts(projectBucket());
    ExprS instantiateS = instantiateS(typeS, "myValue");
    return saveArtifacts.apply(evaluatedExprs(list(instantiateS), list(value)));
  }

  private static ByteString byteStringFrom(String string) {
    return ByteString.encodeUtf8(string);
  }

  private static InstantiateS instantiateS(TypeS typeS, String name) {
    return TestingExpressionS.instantiateS(
        list(), annotatedValueS(nativeAnnotationS(), typeS, name, location()));
  }

  public static StructTS fileTS() {
    return structTS(FILE_STRUCT_NAME, blobTS(), stringTS());
  }
}
