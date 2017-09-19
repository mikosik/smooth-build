package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignTypes.assignTypes;
import static org.smoothbuild.parse.DefinedFunctionLoader.loadDefinedFunction;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.parse.deps.SortByDependencies.sortedByDependencies;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.invoke;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.util.Maybe;

public class ModuleLoader {
  public static Maybe<Functions> loadModule(Functions functions, Path smoothFile) {
    Maybe<InputStream> inputStream = scriptInputStream(smoothFile);
    return invoke(inputStream, is -> loadFunctions(functions, is, smoothFile));
  }

  private static Maybe<InputStream> scriptInputStream(Path scriptFile) {
    try {
      return value(new BufferedInputStream(new FileInputStream(scriptFile.toFile())));
    } catch (IOException e) {
      return error("error: Cannot read build script file '" + scriptFile + "'.");
    }
  }

  private static Maybe<Functions> loadFunctions(Functions functions, InputStream inputStream,
      Path scriptFile) {
    Maybe<ModuleContext> module = parseScript(inputStream, scriptFile);
    Maybe<Ast> ast = invokeWrap(module, m -> AstCreator.fromParseTree(m));
    ast = ast.addErrors(a -> findSemanticErrors(functions, a));
    ast = invoke(ast, a -> sortedByDependencies(functions, a));
    ast = ast.addErrors(a -> assignTypes(functions, a));
    ast = ast.addErrors(a -> assignArgsToParams(functions, a));
    return invoke(ast, a -> loadDefinedFunctions(functions, a));
  }

  private static Maybe<Functions> loadDefinedFunctions(Functions functions, Ast ast) {
    Maybe<Functions> justLoaded = value(new Functions());
    for (FuncNode node : ast.functions()) {
      Maybe<Functions> all = invokeWrap(justLoaded, (j) -> j.addAll(functions));
      Maybe<DefinedFunction> function = invokeWrap(all, a -> loadDefinedFunction(a, node));
      justLoaded = invokeWrap(justLoaded, function, Functions::add);
    }
    return justLoaded;
  }
}
