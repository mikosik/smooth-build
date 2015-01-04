package org.smoothbuild.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.io.CharStreams;

public class TestingFunctions {

  public interface StringIdentityParams {
    public SString string();
  }

  public static class StringIdentity {
    @SmoothFunctionLegacy
    public static SString stringIdentity(NativeApi nativeApi, StringIdentityParams params) {
      return params.string();
    }
  }

  public interface TwoStringsParams {
    public SString stringA();

    public SString stringB();
  }

  public static class TwoStrings {
    @SmoothFunctionLegacy
    public static SString twoStrings(NativeApi nativeApi, TwoStringsParams params) {
      String s1 = params.stringA().value();
      String s2 = params.stringB().value();
      return nativeApi.string(s1 + ":" + s2);
    }
  }

  public interface BlobParams {
    public Blob blob();
  }

  public static class BlobIdentity {
    @SmoothFunctionLegacy
    public static Blob blobIdentity(NativeApi nativeApi, BlobParams params) {
      return params.blob();
    }
  }

  public interface TwoBlobsParams {
    public Blob blob1();

    public Blob blob2();
  }

  public static class TwoBlobs {
    @SmoothFunctionLegacy
    public static Blob twoBlobs(NativeApi nativeApi, TwoBlobsParams params) {
      return params.blob1();
    }
  }

  public interface FileIdentityParams {
    public SFile file();
  }

  public static class FileIdentity {
    @SmoothFunctionLegacy
    public static SFile fileIdentity(NativeApi nativeApi, FileIdentityParams params) {
      return params.file();
    }
  }

  public interface StringArrayIdentityParams {
    public Array<SString> stringArray();
  }

  public static class StringArrayIdentity {
    @SmoothFunctionLegacy
    public static Array<SString> stringArrayIdentity(NativeApi nativeApi,
        StringArrayIdentityParams params) {
      return params.stringArray();
    }
  }

  public interface FileAndBlobParams {
    public SFile file();

    public Blob blob();
  }

  public static class FileAndBlob {
    @SmoothFunctionLegacy
    public static SString fileAndBlob(NativeApi nativeApi, FileAndBlobParams params)
        throws IOException {
      InputStream fileStream = params.file().content().openInputStream();
      InputStream blobStream = params.blob().openInputStream();
      String file = CharStreams.toString(new InputStreamReader(fileStream));
      String blob = CharStreams.toString(new InputStreamReader(blobStream));

      return nativeApi.string(file + ":" + blob);
    }
  }

  public interface OneRequiredParams {
    @Required
    public SString string();
  }

  public static class OneRequired {
    @SmoothFunctionLegacy
    public static SString oneRequired(NativeApi nativeApi, OneRequiredParams params) {
      return params.string();
    }
  }

  public interface TwoRequiredParams {
    @Required
    public SString stringA();

    @Required
    public SString stringB();
  }

  public static class TwoRequired {
    @SmoothFunctionLegacy
    public static SString twoRequired(NativeApi nativeApi, TwoRequiredParams params) {
      return nativeApi.string(params.stringA() + ":" + params.stringB());
    }
  }

  public interface OneOptionalOneRequiredParams {
    @Required
    public SString stringA();

    public SString stringB();
  }

  public static class OneOptionalOneRequired {
    @SmoothFunctionLegacy
    public static SString oneOptionalOneRequired(NativeApi nativeApi,
        OneOptionalOneRequiredParams params) {
      return nativeApi.string(params.stringA() + ":" + params.stringB());
    }
  }
}
