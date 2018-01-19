package org.smoothbuild.builtin.android;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static org.smoothbuild.builtin.android.EnvironmentVariable.environmentVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;

public class AndroidSdk {
  private static final EnvironmentVariable ANDROID_SDK_ROOT = environmentVariable("ANDROID_SDK");
  private static final String BUILD_TOOLS = "build-tools";
  private static final String PLATFORMS = "platforms";
  public static final String AIDL_BINARY = "aidl";
  private static final String FRAMEWORK_AIDL = "framework.aidl";

  public static Path getFrameworkAidl(NativeApi nativeApi, String apiLevel) {
    String androidApiLevel = "android-" + apiLevel;
    Path fileSubPath = Paths.get(PLATFORMS, androidApiLevel, FRAMEWORK_AIDL);
    return getFullPath(nativeApi, fileSubPath);
  }

  public static Path getAidlBinaryPath(NativeApi nativeApi, String buildToolsVersion) {
    Path fileSubPath = Paths.get(BUILD_TOOLS, buildToolsVersion, AIDL_BINARY);
    return getFullPath(nativeApi, fileSubPath);
  }

  private static Path getFullPath(NativeApi nativeApi, Path fileSubPath) {
    Path fullPath = getSdkDir(nativeApi).resolve(fileSubPath);
    if (!isRegularFile(fullPath)) {
      nativeApi.log().error(fileNotFoundMessage(ANDROID_SDK_ROOT, fileSubPath));
      throw new AbortException();
    }
    return fullPath;
  }

  private static String fileNotFoundMessage(EnvironmentVariable androidSdkVar,
      Path requiredSdkFile) {
    StringBuilder builder = new StringBuilder();
    builder.append("Can't find " + requiredSdkFile + " file in android sdk.\n");
    builder.append("Path to Android SDK was set by the following environment variable:\n");
    builder.append(androidSdkVar.toString() + "\n");
    return builder.toString();
  }

  public static Path getSdkDir(NativeApi nativeApi) {
    if (!ANDROID_SDK_ROOT.isSet()) {
      nativeApi.log().error("Environment variable " + ANDROID_SDK_ROOT.name() + " is not set.\n"
          + "It should contain absolute path to android SDK.");
      throw new AbortException();
    }
    Path sdkRoot = Paths.get(ANDROID_SDK_ROOT.value());
    if (!isDirectory(sdkRoot)) {
      nativeApi.log().error("Environment variable " + ANDROID_SDK_ROOT.name()
          + "should contain absolute path to android SDK dir.\n"
          + "It is set to '" + ANDROID_SDK_ROOT.value() + "'\n"
          + "but such dir doesn't exist.\n");
      throw new AbortException();
    }
    return sdkRoot;
  }
}
