package org.smoothbuild.acceptance;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.io.CharStreams;

public class TestingFunctions {

  @SmoothFunction
  public static SString stringIdentity(Container container, SString string) {
    return string;
  }

  @SmoothFunction
  public static SString twoStrings(Container container, SString stringA, SString stringB) {
    return container.create().string(stringA.value() + ":" + stringB.value());
  }

  @SmoothFunction
  public static Blob blobIdentity(Container container, Blob blob) {
    return blob;
  }

  @SmoothFunction
  public static Blob twoBlobs(Container container, Blob blob1, Blob blob2) {
    return blob1;
  }

  @SmoothFunction
  public static SFile fileIdentity(Container container, SFile file) {
    return file;
  }

  @SmoothFunction
  public static Array<SString> stringArrayIdentity(Container container,
      Array<SString> stringArray) {
    return stringArray;
  }

  @SmoothFunction
  public static Nothing nothingIdentity(Container container, Nothing nothing) {
    return nothing;
  }

  @SmoothFunction
  public static Array<Nothing> nothingArrayIdentity(Container container,
      Array<Nothing> nothingArray) {
    return nothingArray;
  }

  @SmoothFunction
  public static SString fileAndBlob(Container container, SFile file, Blob blob)
      throws IOException {
    InputStream fileStream = file.content().openInputStream();
    InputStream blobStream = blob.openInputStream();
    String fileString = CharStreams.toString(new InputStreamReader(fileStream));
    String blobString = CharStreams.toString(new InputStreamReader(blobStream));

    return container.create().string(fileString + ":" + blobString);
  }

  @SmoothFunction
  public static SString oneOptionalOneRequired(Container container, SString stringA,
      @Required SString stringB) {
    return container.create().string(stringA.value() + ":" + stringB.value());
  }

  @SmoothFunction
  public static SString tempFilePath(Container container) {
    TempDir tempDir = container.createTempDir();
    String osPath = tempDir.asOsPath(path("file.txt"));
    new File(osPath).mkdirs();
    return container.create().string(osPath);
  }

  @SmoothFunction
  public static SString ambiguousArguments(Container container, SString param1,
      Array<SString> param2, SFile param3, Array<SFile> param4, Array<Blob> param5) {
    return container.create().string("");
  }

  @SmoothFunction
  public static SString throwFileSystemException(Container container, SString string) {
    long randomLong = new Random().nextLong();
    throw new FileSystemException(string.toString() + randomLong + " ");
  }

  @SmoothFunction
  public static SString throwRuntimeException(Container container, SString string) {
    long randomLong = new Random().nextLong();
    throw new RuntimeException(string.toString() + randomLong + " ");
  }
}
