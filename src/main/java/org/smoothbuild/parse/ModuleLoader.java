package org.smoothbuild.parse;

import static org.smoothbuild.parse.DefinedFunctionLoader.loadDefinedFunction;
import static org.smoothbuild.parse.FunctionContextCollector.collectFunctionContexts;
import static org.smoothbuild.parse.Maybe.error;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.value;
import static org.smoothbuild.parse.ScriptParser.parseScript;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.def.DefinedFunction;

public class ModuleLoader {
  private final FileSystem fileSystem;

  @Inject
  public ModuleLoader(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Maybe<Functions> loadFunctions(Functions functions, Path smoothFile) {
    Maybe<InputStream> inputStream = scriptInputStream(smoothFile);
    return invoke(inputStream, is -> loadFunctions(functions, is, smoothFile));
  }

  private Maybe<InputStream> scriptInputStream(Path scriptFile) {
    try {
      return value(fileSystem.openInputStream(scriptFile));
    } catch (FileSystemException e) {
      return error("error: Cannot read build script file " + scriptFile + ". " + e.getMessage());
    }
  }

  private Maybe<Functions> loadFunctions(Functions functions, InputStream inputStream,
      Path scriptFile) {
    Maybe<ModuleContext> module = parseScript(inputStream, scriptFile);
    Maybe<List<FunctionContext>> sortedFunctionContexts = invoke(module,
        m -> collectFunctionContexts(m, functions));
    return invoke(sortedFunctionContexts, sfcs -> loadDefinedFunctions(functions, sfcs));
  }

  private Maybe<Functions> loadDefinedFunctions(Functions functions,
      List<FunctionContext> functionContexts) {
    Maybe<Functions> justLoaded = value(new Functions());
    for (FunctionContext functionContext : functionContexts) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<DefinedFunction> function = invoke(all,
          a -> loadDefinedFunction(a, functionContext));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }
}
