package org.smoothbuild.lang.builtin.android;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.lang.builtin.android.err.AidlBinaryReturnedNonZeroCodeError;
import org.smoothbuild.lang.builtin.android.err.AidlShouldOutputExactlyOneFileError;
import org.smoothbuild.lang.builtin.android.err.RunningAidlBinaryFailedError;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.util.CommandExecutor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class AidlFunction {
  public interface Parameters {
    @Required
    public SString apiLevel();

    @Required
    public SString buildToolsVersion();

    @Required
    public SFile interfaceFile();
  }

  @SmoothFunction(name = "aidl")
  public static SFile execute(PluginApi pluginApi, Parameters params) throws InterruptedException {
    String buildToolsVersion = params.buildToolsVersion().value();
    String apiLevel = params.apiLevel().value();
    SFile interfaceFile = params.interfaceFile();

    return execute(pluginApi, buildToolsVersion, apiLevel, interfaceFile);
  }

  private static SFile execute(PluginApi pluginApi, String buildToolsVersion, String apiLevel,
      SFile interfaceFile) throws InterruptedException {
    String aidlBinary = AndroidSdk.getAidlBinaryPath(buildToolsVersion).toString();
    String frameworkAidl = AndroidSdk.getFrameworkAidl(apiLevel).toString();

    TempDirectory inputFilesDir = pluginApi.createTempDirectory();
    inputFilesDir.writeFile(interfaceFile);

    TempDirectory outputFilesDir = pluginApi.createTempDirectory();

    List<String> command = Lists.newArrayList();
    command.add(aidlBinary);
    command.add("-p" + frameworkAidl);
    command.add("-o" + outputFilesDir.rootOsPath());
    command.add(inputFilesDir.asOsPath(interfaceFile.path()));

    executeCommand(command);

    SArray<SFile> outputFiles = outputFilesDir.readFiles();
    if (Iterables.size(outputFiles) != 1) {
      throw new ErrorMessageException(new AidlShouldOutputExactlyOneFileError(outputFiles));
    }

    return outputFiles.iterator().next();
  }

  private static void executeCommand(List<String> command) throws InterruptedException {
    try {
      int exitValue = CommandExecutor.execute(command);
      if (exitValue != 0) {
        throw new ErrorMessageException(new AidlBinaryReturnedNonZeroCodeError(exitValue));
      }
    } catch (IOException e) {
      throw new ErrorMessageException(new RunningAidlBinaryFailedError(command, e));
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
