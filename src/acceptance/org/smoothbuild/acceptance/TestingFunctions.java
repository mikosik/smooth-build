package org.smoothbuild.acceptance;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
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
    public static SString stringIdentity(Container container, @Name("string") SString string) {
      return string;
    }
  }

  public static class TwoStrings {
    @SmoothFunction
    public static SString twoStrings(Container container, @Name("stringA") SString stringA,
        @Name("stringB") SString stringB) {
      return container.create().string(stringA.value() + ":" + stringB.value());
    }
  }

  public static class BlobIdentity {
    @SmoothFunction
    public static Blob blobIdentity(Container container, @Name("blob") Blob blob) {
      return blob;
    }
  }

  public static class TwoBlobs {
    @SmoothFunction
    public static Blob twoBlobs(Container container, @Name("blob1") Blob blob1,
        @Name("blob2") Blob blob2) {
      return blob1;
    }
  }

  public static class FileIdentity {
    @SmoothFunction
    public static SFile fileIdentity(Container container, @Name("file") SFile file) {
      return file;
    }
  }

  public static class StringArrayIdentity {
    @SmoothFunction
    public static Array<SString> stringArrayIdentity(Container container,
        @Name("stringArray") Array<SString> stringArray) {
      return stringArray;
    }
  }

  public static class FileAndBlob {
    @SmoothFunction
    public static SString fileAndBlob(Container container, @Name("file") SFile file,
        @Name("blob") Blob blob) throws IOException {
      InputStream fileStream = file.content().openInputStream();
      InputStream blobStream = blob.openInputStream();
      String fileString = CharStreams.toString(new InputStreamReader(fileStream));
      String blobString = CharStreams.toString(new InputStreamReader(blobStream));

      return container.create().string(fileString + ":" + blobString);
    }
  }

  public static class OneRequired {
    @SmoothFunction
    public static SString oneRequired(Container container,
        @Required @Name("string") SString stringA) {
      return stringA;
    }
  }

  public static class TwoRequired {
    @SmoothFunction
    public static SString twoRequired(Container container,
        @Required @Name("stringA") SString stringA, @Required @Name("stringB") SString stringB) {
      return container.create().string(stringA.value() + ":" + stringB.value());
    }
  }

  public static class OneOptionalOneRequired {
    @SmoothFunction
    public static SString oneOptionalOneRequired(Container container,
        @Name("stringA") SString stringA, @Required @Name("stringB") SString stringB) {
      return container.create().string(stringA.value() + ":" + stringB.value());
    }
  }

  public static class CacheableRandom {
    @SmoothFunction
    public static SString cacheableRandom(Container container) {
      long randomLong = new Random().nextLong();
      return container.create().string(Long.toString(randomLong));
    }
  }

  public static class NotCacheableRandom {
    @SmoothFunction
    @NotCacheable
    public static SString notCacheableRandom(Container container) {
      long randomLong = new Random().nextLong();
      return container.create().string(Long.toString(randomLong));
    }
  }

  public static class TempFilePath {
    @SmoothFunction
    public static SString tempFilePath(Container container) {
      TempDirectory tempDirectory = container.createTempDirectory();
      String osPath = tempDirectory.asOsPath(path("file.txt"));
      new File(osPath).mkdirs();
      return container.create().string(osPath);
    }
  }
}
