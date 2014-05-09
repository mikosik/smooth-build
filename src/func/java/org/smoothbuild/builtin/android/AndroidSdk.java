package org.smoothbuild.builtin.android;

import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static org.smoothbuild.builtin.android.EnvironmentVariable.environmentVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.smoothbuild.builtin.android.err.AndroidSdkLacksFileError;
import org.smoothbuild.builtin.android.err.AndroidSdkRootDoesNotExistError;
import org.smoothbuild.builtin.android.err.AndroidSdkVariableNotSetError;

public class AndroidSdk {
  private static final EnvironmentVariable ANDROID_SDK_ROOT = environmentVariable("ANDROID_SDK");
  private static final String BUILD_TOOLS = "build-tools";
  private static final String PLATFORMS = "platforms";
  public static final String AIDL_BINARY = "aidl";
  private static final String FRAMEWORK_AIDL = "framework.aidl";

  public static Path getFrameworkAidl(String apiLevel) {
    String androidApiLevel = "android-" + apiLevel;
    Path fileSubPath = Paths.get(PLATFORMS, androidApiLevel, FRAMEWORK_AIDL);
    return getFullPath(fileSubPath);
  }

  public static Path getAidlBinaryPath(String buildToolsVersion) {
    Path fileSubPath = Paths.get(BUILD_TOOLS, buildToolsVersion, AIDL_BINARY);
    return getFullPath(fileSubPath);
  }

  private static Path getFullPath(Path fileSubPath) {
    Path fullPath = getSdkDir().resolve(fileSubPath);
    if (!isRegularFile(fullPath)) {
      throw new AndroidSdkLacksFileError(ANDROID_SDK_ROOT, fileSubPath);
    }
    return fullPath;
  }

  public static Path getSdkDir() {
    if (!ANDROID_SDK_ROOT.isSet()) {
      throw new AndroidSdkVariableNotSetError(ANDROID_SDK_ROOT);
    }
    Path sdkRoot = Paths.get(ANDROID_SDK_ROOT.value());
    if (!isDirectory(sdkRoot)) {
      throw new AndroidSdkRootDoesNotExistError(ANDROID_SDK_ROOT);
    }
    return sdkRoot;
  }
}
