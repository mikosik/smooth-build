package org.smoothbuild.builtin.android;

import static org.smoothbuild.builtin.android.AndroidSdk.AIDL_BINARY;
import static org.smoothbuild.builtin.util.Exceptions.stackTraceToString;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.CommandExecutor;

public class AidlFunction {

  @SmoothFunction
  public static SFile aidl(
      Container container,
      @Name("apiLevel") SString apiLevel,
      @Name("buildToolsVersion") SString buildToolsVersion,
      @Name("interfaceFile") SFile interfaceFile) throws InterruptedException {
    return execute(container, buildToolsVersion.value(), apiLevel.value(), interfaceFile);
  }

  private static SFile execute(Container container, String buildToolsVersion, String apiLevel,
      SFile interfaceFile) throws InterruptedException {
    String aidlBinary = AndroidSdk.getAidlBinaryPath(buildToolsVersion).toString();
    String frameworkAidl = AndroidSdk.getFrameworkAidl(apiLevel).toString();

    TempDir inputFilesDir = container.createTempDir();
    inputFilesDir.writeFile(interfaceFile);

    TempDir outputFilesDir = container.createTempDir();

    List<String> command = new ArrayList<>();
    command.add(aidlBinary);
    command.add("-p" + frameworkAidl);
    command.add("-o" + outputFilesDir.rootOsPath());
    command.add(inputFilesDir.asOsPath(path(interfaceFile.path().value())));

    executeCommand(command);
    return onlyElement(outputFilesDir.readFiles());
  }

  private static SFile onlyElement(Array<SFile> outputFiles) {
    Iterator<SFile> iterator = outputFiles.iterator();
    if (!iterator.hasNext()) {
      throw new ErrorMessage(AIDL_BINARY
          + " binary should return exactly one file but returned zero.");
    }
    SFile result = iterator.next();
    if (iterator.hasNext()) {
      StringBuilder builder = new StringBuilder();
      builder.append(AIDL_BINARY);
      builder.append("binary should return exactly one file but it returned following files:\n");
      for (SFile file : outputFiles) {
        builder.append(file.path().value());
        builder.append("\n");
      }
      throw new ErrorMessage(builder.toString());
    }
    return result;
  }

  private static void executeCommand(List<String> command) throws InterruptedException {
    try {
      int exitValue = CommandExecutor.execute(command);
      if (exitValue != 0) {
        throw new ErrorMessage(AIDL_BINARY + " binary returned non zero exit value = "
            + exitValue);
      }
    } catch (IOException e) {
      throw new ErrorMessage(binaryFailedMessage(command, e));
    }
  }

  private static String binaryFailedMessage(List<String> command, IOException e) {
    return "Following command line failed:\n"
        + join(command) + "\n"
        + "stack trace is:\n"
        + stackTraceToString(e);
  }

  private static String join(List<String> command) {
    return command.stream().collect(Collectors.joining(" "));
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
