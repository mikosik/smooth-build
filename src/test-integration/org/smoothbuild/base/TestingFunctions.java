package org.smoothbuild.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

import com.google.common.io.CharStreams;

public class TestingFunctions {

  public interface StringIdentityParams {
    public SString string();
  }

  public static class StringIdentity {
    @SmoothFunction
    public static SString stringIdentity(NativeApi nativeApi, StringIdentityParams params) {
      return params.string();
    }
  }

  public interface TwoStringsParams {
    public SString stringA();

    public SString stringB();
  }

  public static class TwoStrings {
    @SmoothFunction
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
    @SmoothFunction
    public static Blob blobIdentity(NativeApi nativeApi, BlobParams params) {
      return params.blob();
    }
  }

  public interface TwoBlobsParams {
    public Blob blob1();

    public Blob blob2();
  }

  public static class TwoBlobs {
    @SmoothFunction
    public static Blob twoBlobs(NativeApi nativeApi, TwoBlobsParams params) {
      return params.blob1();
    }
  }

  public interface StringArrayIdentityParams {
    public Array<SString> stringArray();
  }

  public static class StringArrayIdentity {
    @SmoothFunction
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
    @SmoothFunction
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
    @SmoothFunction
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
    @SmoothFunction
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
    @SmoothFunction
    public static SString oneOptionalOneRequired(NativeApi nativeApi,
        OneOptionalOneRequiredParams params) {
      return nativeApi.string(params.stringA() + ":" + params.stringB());
    }
  }
}
