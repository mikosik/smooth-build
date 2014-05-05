package org.smoothbuild.builtin;

import org.smoothbuild.builtin.android.AidlFunction;
import org.smoothbuild.builtin.blob.ConcatenateBlobsFunction;
import org.smoothbuild.builtin.blob.ToFileFunction;
import org.smoothbuild.builtin.blob.ToStringFunction;
import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.builtin.compress.ZipFunction;
import org.smoothbuild.builtin.file.ConcatenateFilesFunction;
import org.smoothbuild.builtin.file.ContentFunction;
import org.smoothbuild.builtin.file.FileFunction;
import org.smoothbuild.builtin.file.FilesFunction;
import org.smoothbuild.builtin.file.FilterFunction;
import org.smoothbuild.builtin.file.PathFunction;
import org.smoothbuild.builtin.java.JarFunction;
import org.smoothbuild.builtin.java.JarjarFunction;
import org.smoothbuild.builtin.java.UnjarFunction;
import org.smoothbuild.builtin.java.javac.JavacFunction;
import org.smoothbuild.builtin.java.junit.JunitFunction;
import org.smoothbuild.builtin.string.ToBlobFunction;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;

public class BuiltinSmoothModule {

  // string related

  public interface ToBlobParameters {
    @Required
    public SString string();
  }

  @SmoothFunction(name = "toBlob")
  public static SBlob execute(NativeApi nativeApi, ToBlobParameters params) {
    return ToBlobFunction.execute(nativeApi, params);
  }

  // Blob related

  public interface ConcatenateBlobsParameters {
    @Required
    public SArray<SBlob> blobs();

    @Required
    public SArray<SBlob> with();
  }

  @SmoothFunction(name = "concatenateBlobs")
  public static SArray<SBlob> execute(NativeApi nativeApi, ConcatenateBlobsParameters params) {
    return ConcatenateBlobsFunction.execute(nativeApi, params);
  }

  public interface ToFileParameters {
    @Required
    public SString path();

    @Required
    public SBlob content();
  }

  @SmoothFunction(name = "toFile")
  public static SFile execute(NativeApi nativeApi, ToFileParameters params) {
    return ToFileFunction.execute(nativeApi, params);
  }

  public interface ToStringParameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "toString")
  public static SString execute(NativeApi nativeApi, ToStringParameters params) {
    return ToStringFunction.execute(nativeApi, params);
  }

  // File related

  public interface FileParameters {
    @Required
    public SString path();
  }

  @SmoothFunction(name = "file", cacheable = false)
  public static SFile execute(NativeApiImpl nativeApi, FileParameters params) {
    return FileFunction.execute(nativeApi, params);
  }

  public interface FilesParameters {
    @Required
    public SString dir();
  }

  @SmoothFunction(name = "files", cacheable = false)
  public static SArray<SFile> execute(NativeApiImpl nativeApi, FilesParameters params) {
    return FilesFunction.execute(nativeApi, params);
  }

  public interface ContentParameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "content")
  public static SBlob execute(NativeApi nativeApi, ContentParameters params) {
    return ContentFunction.execute(nativeApi, params);
  }

  public interface PathParameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "path")
  public static SString execute(NativeApi nativeApi, PathParameters params) {
    return PathFunction.execute(nativeApi, params);
  }

  public interface ConcatenateFilesParameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SArray<SFile> with();
  }

  @SmoothFunction(name = "concatenateFiles")
  public static SArray<SFile> execute(NativeApi nativeApi, ConcatenateFilesParameters params) {
    return ConcatenateFilesFunction.execute(nativeApi, params);
  }

  public interface FilterParameters {
    @Required
    public SArray<SFile> files();

    @Required
    public SString include();
  }

  @SmoothFunction(name = "filter")
  public static SArray<SFile> execute(NativeApi nativeApi, FilterParameters params) {
    return FilterFunction.execute(nativeApi, params);
  }

  // java related

  public interface JavacParameters {
    @Required
    SArray<SFile> sources();

    SArray<SBlob> libs();

    SString source();

    SString target();
  }

  @SmoothFunction(name = "javac")
  public static SArray<SFile> execute(NativeApi nativeApi, JavacParameters params) {
    return JavacFunction.execute(nativeApi, params);
  }

  public interface JarParameters {
    @Required
    public SArray<SFile> files();

    public SBlob manifest();
  }

  @SmoothFunction(name = "jar")
  public static SBlob execute(NativeApi nativeApi, JarParameters params) {
    return JarFunction.execute(nativeApi, params);
  }

  public interface JarjarParameters {
    @Required
    public SString rules();

    @Required
    public SBlob in();
  }

  @SmoothFunction(name = "jarjar")
  public static SBlob execute(NativeApi nativeApi, JarjarParameters params) {
    return JarjarFunction.execute(nativeApi, params);
  }

  public interface UnjarParameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "unjar")
  public static SArray<SFile> execute(NativeApi nativeApi, UnjarParameters params) {
    return UnjarFunction.execute(nativeApi, params);
  }

  public interface JunitParameters {
    SArray<SBlob> libs();

    SString include();
  }

  @SmoothFunction(name = "junit")
  public static SString execute(NativeApi nativeApi, JunitParameters params) {
    return JunitFunction.execute(nativeApi, params);
  }

  // compression related

  public interface ZipParameters {
    @Required
    public SArray<SFile> files();

    // add missing parameters: level, comment, method
  }

  @SmoothFunction(name = "zip")
  public static SBlob execute(NativeApi nativeApi, ZipParameters params) {
    return ZipFunction.execute(nativeApi, params);
  }

  public interface UnzipParameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "unzip")
  public static SArray<SFile> execute(NativeApi nativeApi, UnzipParameters params) {
    return UnzipFunction.execute(nativeApi, params);
  }

  // android related

  public interface AidlParameters {
    @Required
    public SString apiLevel();

    @Required
    public SString buildToolsVersion();

    @Required
    public SFile interfaceFile();
  }

  @SmoothFunction(name = "aidl")
  public static SFile execute(NativeApi nativeApi, AidlParameters params)
      throws InterruptedException {
    return AidlFunction.execute(nativeApi, params);
  }
}
