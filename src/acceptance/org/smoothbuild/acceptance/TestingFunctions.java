package org.smoothbuild.acceptance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.io.CharStreams;

public class TestingFunctions {

  public static class StringIdentity {
    @SmoothFunction
    public static SString stringIdentity(NativeApi nativeApi, @Name("string") SString string) {
      return string;
    }
  }

  public static class TwoStrings {
    @SmoothFunction
    public static SString twoStrings(NativeApi nativeApi, @Name("stringA") SString stringA,
        @Name("stringB") SString stringB) {
      return nativeApi.string(stringA.value() + ":" + stringB.value());
    }
  }

  public static class BlobIdentity {
    @SmoothFunction
    public static Blob blobIdentity(NativeApi nativeApi, @Name("blob") Blob blob) {
      return blob;
    }
  }

  public static class TwoBlobs {
    @SmoothFunction
    public static Blob twoBlobs(NativeApi nativeApi, @Name("blob1") Blob blob1,
        @Name("blob2") Blob blob2) {
      return blob1;
    }
  }

  public static class FileIdentity {
    @SmoothFunction
    public static SFile fileIdentity(NativeApi nativeApi, @Name("file") SFile file) {
      return file;
    }
  }

  public static class StringArrayIdentity {
    @SmoothFunction
    public static Array<SString> stringArrayIdentity(NativeApi nativeApi,
        @Name("stringArray") Array<SString> stringArray) {
      return stringArray;
    }
  }

  public static class FileAndBlob {
    @SmoothFunction
    public static SString fileAndBlob(NativeApi nativeApi, @Name("file") SFile file,
        @Name("blob") Blob blob) throws IOException {
      InputStream fileStream = file.content().openInputStream();
      InputStream blobStream = blob.openInputStream();
      String fileString = CharStreams.toString(new InputStreamReader(fileStream));
      String blobString = CharStreams.toString(new InputStreamReader(blobStream));

      return nativeApi.string(fileString + ":" + blobString);
    }
  }

  public static class OneRequired {
    @SmoothFunction
    public static SString oneRequired(NativeApi nativeApi,
        @Required @Name("string") SString stringA) {
      return stringA;
    }
  }

  public static class TwoRequired {
    @SmoothFunction
    public static SString twoRequired(NativeApi nativeApi,
        @Required @Name("stringA") SString stringA, @Required @Name("stringB") SString stringB) {
      return nativeApi.string(stringA.value() + ":" + stringB.value());
    }
  }

  public static class OneOptionalOneRequired {
    @SmoothFunction
    public static SString oneOptionalOneRequired(NativeApi nativeApi,
        @Name("stringA") SString stringA, @Required @Name("stringB") SString stringB) {
      return nativeApi.string(stringA.value() + ":" + stringB.value());
    }
  }

  public static class CacheableRandom {
    @SmoothFunction
    public static SString cacheableRandom(NativeApi nativeApi) {
      long randomLong = new Random().nextLong();
      return nativeApi.string(Long.toString(randomLong));
    }
  }

  public static class NotCacheableRandom {
    @SmoothFunction
    @NotCacheable
    public static SString notCacheableRandom(NativeApi nativeApi) {
      long randomLong = new Random().nextLong();
      return nativeApi.string(Long.toString(randomLong));
    }
  }
}
