package org.smoothbuild.builtin.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.builtin.android.err.AidlBinaryReturnedNonZeroCodeError;
import org.smoothbuild.builtin.android.err.AidlShouldOutputExactlyOneFileError;
import org.smoothbuild.builtin.android.err.RunningAidlBinaryFailedError;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.CommandExecutor;

public class AidlFunction {

  @SmoothFunction
  public static SFile aidl(
      Container container,
      @Required @Name("apiLevel") SString apiLevel,
      @Required @Name("buildToolsVersion") SString buildToolsVersion,
      @Required @Name("interfaceFile") SFile interfaceFile) throws InterruptedException {
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
    command.add(inputFilesDir.asOsPath(interfaceFile.path()));

    executeCommand(command);
    return onlyElement(outputFilesDir.readFiles());
  }

  private static SFile onlyElement(Array<SFile> outputFiles) {
    Iterator<SFile> iterator = outputFiles.iterator();
    if (!iterator.hasNext()) {
      throw new AidlShouldOutputExactlyOneFileError(outputFiles);
    }
    SFile result = iterator.next();
    if (iterator.hasNext()) {
      throw new AidlShouldOutputExactlyOneFileError(outputFiles);
    }
    return result;
  }

  private static void executeCommand(List<String> command) throws InterruptedException {
    try {
      int exitValue = CommandExecutor.execute(command);
      if (exitValue != 0) {
        throw new AidlBinaryReturnedNonZeroCodeError(exitValue);
      }
    } catch (IOException e) {
      throw new RunningAidlBinaryFailedError(command, e);
    }
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
