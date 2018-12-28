package org.smoothbuild.builtin.android;

import static java.lang.String.join;
import static org.smoothbuild.builtin.android.AndroidSdk.AIDL_BINARY;
import static org.smoothbuild.builtin.util.Exceptions.stackTraceToString;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.util.CommandExecutor;

public class AidlFunction {
  @SmoothFunction("aidl")
  public static Struct aidl(NativeApi nativeApi, SString apiLevel, SString buildToolsVersion,
      Struct interfaceFile) throws InterruptedException, IOException {
    return execute(nativeApi, buildToolsVersion.data(), apiLevel.data(), interfaceFile);
  }

  private static Struct execute(NativeApi nativeApi, String buildToolsVersion, String apiLevel,
      Struct interfaceFile) throws InterruptedException, IOException {
    String aidlBinary = AndroidSdk.getAidlBinaryPath(nativeApi, buildToolsVersion).toString();
    String frameworkAidl = AndroidSdk.getFrameworkAidl(nativeApi, apiLevel).toString();

    TempDir inputFilesDir = nativeApi.createTempDir();
    inputFilesDir.writeFile(interfaceFile);

    TempDir outputFilesDir = nativeApi.createTempDir();

    List<String> command = new ArrayList<>();
    command.add(aidlBinary);
    command.add("-p" + frameworkAidl);
    command.add("-o" + outputFilesDir.rootOsPath());
    command.add(inputFilesDir.asOsPath(path(((SString) interfaceFile.get("path")).data())));

    if (executeCommand(nativeApi, command)) {
      return null;
    }
    return onlyElement(nativeApi, outputFilesDir.readFiles());
  }

  private static Struct onlyElement(NativeApi nativeApi, Array outputFiles) {
    Iterator<Struct> iterator = outputFiles.asIterable(Struct.class).iterator();
    if (!iterator.hasNext()) {
      nativeApi.log().error(AIDL_BINARY
          + " binary should return exactly one file but returned zero.");
      return null;
    }
    Struct result = iterator.next();
    if (iterator.hasNext()) {
      StringBuilder builder = new StringBuilder();
      builder.append(AIDL_BINARY);
      builder.append("binary should return exactly one file but it returned following files:\n");
      for (Struct file : outputFiles.asIterable(Struct.class)) {
        builder.append(((SString) file.get("path")).data());
        builder.append("\n");
      }
      nativeApi.log().error(builder.toString());
      return null;
    }
    return result;
  }

  private static boolean executeCommand(NativeApi nativeApi, List<String> command)
      throws InterruptedException {
    try {
      int exitValue = CommandExecutor.execute(command);
      if (exitValue != 0) {
        nativeApi.log().error(AIDL_BINARY + " binary returned non zero exit value = " + exitValue);
        return true;
      }
    } catch (IOException e) {
      nativeApi.log().error(binaryFailedMessage(command, e));
      return true;
    }
    return false;
  }

  private static String binaryFailedMessage(List<String> command, IOException e) {
    return "Following command line failed:\n"
        + join(" ", command) + "\n"
        + "stack trace is:\n"
        + stackTraceToString(e);
  }

  // Documentation copy/pasted from aidl command line tool:
  //
  // usage: aidl OPTIONS INPUT [OUTPUT]
  // aidl --preprocess OUTPUT INPUT...
  //
  // OPTIONS:
  // -I<DIR> search path for import statements.
  // -d<FILE> generate dependency file.
  // -a generate dependency file next to the output file with the name based on
  // the input file.
  // -p<FILE> file created by --preprocess to import.
  // -o<FOLDER> base output folder for generated files.
  // -b fail when trying to compile a parcelable.
  //
  // INPUT:
  // An aidl interface file.
  //
  // OUTPUT:
  // The generated interface files.
  // If omitted and the -o option is not used, the input filename is used, with
  // the .aidl extension changed to a .java extension.
  // If the -o option is used, the generated files will be placed in the base
  // output folder, under their package folder
}
