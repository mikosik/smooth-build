package org.smoothbuild.parse;

import static org.smoothbuild.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.parse.AssignNatives.assignNatives;
import static org.smoothbuild.parse.ConstructorLoader.loadConstructor;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.function.Native;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.util.Maybe;

public class ModuleLoader {
  private final SRuntime runtime;
  private final AssignTypes assignTypes;
  private final FunctionLoader functionLoader;

  @Inject
  public ModuleLoader(SRuntime runtime, AssignTypes assignTypes, FunctionLoader functionLoader) {
    this.runtime = runtime;
    this.assignTypes = assignTypes;
    this.functionLoader = functionLoader;
  }

  public List<? extends Object> loadModule(Path script) {
    Maybe<Map<String, Native>> natives = findNatives(changeExtension(script, "jar"));
    return parseScript(script)
        .mapValue(moduleContext -> AstCreator.fromParseTree(script, moduleContext))
        .invoke(ast -> findSemanticErrors(runtime, ast))
        .invoke(ast -> ast.sortFuncsByDependencies(runtime.functions()))
        .invoke(ast -> ast.sortTypesByDependencies(runtime.types()))
        .invoke(ast -> assignTypes.assignTypes(runtime.functions(), ast))
        .invoke(ast -> assignArgsToParams(runtime.functions(), ast))
        .invoke(natives, (ast, n) -> assignNatives(ast, n))
        .invokeConsumer(ast -> loadFunctions(ast))
        .errors();
  }

  private void loadFunctions(Ast ast) {
    for (StructNode struct : ast.structs()) {
      runtime.functions().add(loadConstructor(struct));
    }
    for (FuncNode func : ast.funcs()) {
      runtime.functions().add(functionLoader.loadFunction(runtime.functions(), func));
    }
  }
}
