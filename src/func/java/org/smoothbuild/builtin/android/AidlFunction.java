package org.smoothbuild.builtin.android;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.builtin.android.err.AidlBinaryReturnedNonZeroCodeError;
import org.smoothbuild.builtin.android.err.AidlShouldOutputExactlyOneFileError;
import org.smoothbuild.builtin.android.err.RunningAidlBinaryFailedError;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.util.CommandExecutor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class AidlFunction {

  public interface AidlParameters {
    @Required
    public SString apiLevel();

    @Required
    public SString buildToolsVersion();

    @Required
    public SFile interfaceFile();
  }

  @SmoothFunction
  public static SFile aidl(NativeApi nativeApi, AidlParameters params) throws InterruptedException {
    String buildToolsVersion = params.buildToolsVersion().value();
    String apiLevel = params.apiLevel().value();
    SFile interfaceFile = params.interfaceFile();

    return execute(nativeApi, buildToolsVersion, apiLevel, interfaceFile);
  }

  private static SFile execute(NativeApi nativeApi, String buildToolsVersion, String apiLevel,
      SFile interfaceFile) throws InterruptedException {
    String aidlBinary = AndroidSdk.getAidlBinaryPath(buildToolsVersion).toString();
    String frameworkAidl = AndroidSdk.getFrameworkAidl(apiLevel).toString();

    TempDirectory inputFilesDir = nativeApi.createTempDirectory();
    inputFilesDir.writeFile(interfaceFile);

    TempDirectory outputFilesDir = nativeApi.createTempDirectory();

    List<String> command = Lists.newArrayList();
    command.add(aidlBinary);
    command.add("-p" + frameworkAidl);
    command.add("-o" + outputFilesDir.rootOsPath());
    command.add(inputFilesDir.asOsPath(interfaceFile.path()));

    executeCommand(command);

    Array<SFile> outputFiles = outputFilesDir.readFiles();
    if (Iterables.size(outputFiles) != 1) {
      throw new AidlShouldOutputExactlyOneFileError(outputFiles);
    }

    return outputFiles.iterator().next();
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
