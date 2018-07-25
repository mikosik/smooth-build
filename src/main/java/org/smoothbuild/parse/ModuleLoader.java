package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.parse.FindNatives.findNatives;
import static org.smoothbuild.parse.FindSemanticErrors.findSemanticErrors;
import static org.smoothbuild.parse.InferTypesAndParamAssignment.inferTypesAndParamAssignment;
import static org.smoothbuild.parse.ScriptParser.parseScript;
import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstCreator;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.StructNode;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableList;

public class ModuleLoader {
  private final FunctionLoader functionLoader;

  @Inject
  public ModuleLoader(FunctionLoader functionLoader) {
    this.functionLoader = functionLoader;
  }

  public List<? extends Object> loadModule(SRuntime runtime, Path script) {
    Maybe<Natives> natives = findNatives(changeExtension(script, "jar"));
    return parseScript(script)
        .mapValue(moduleContext -> AstCreator.fromParseTree(script, moduleContext))
        .invoke(ast -> findSemanticErrors(runtime, ast))
        .invoke(ast -> ast.sortFuncsByDependencies(runtime.functions()))
        .invoke(ast -> ast.sortTypesByDependencies(runtime.types()))
        .invoke(ast -> inferTypesAndParamAssignment(runtime, ast))
        .invoke(natives, (ast, n) -> n.assignNatives(ast))
        .invokeConsumer(ast -> loadFunctions(runtime, ast))
        .errors();
  }

  private void loadFunctions(SRuntime runtime, Ast ast) {
    for (StructNode struct : ast.structs()) {
      runtime.functions().add(loadConstructor(struct));
    }
    for (FuncNode func : ast.funcs()) {
      runtime.functions().add(functionLoader.loadFunction(runtime.functions(), func));
    }
  }

  private static Constructor loadConstructor(StructNode struct) {
    ImmutableList<Parameter> parameters = struct
        .fields()
        .stream()
        .map(f -> new Parameter(f.get(Type.class), f.name(), null))
        .collect(toImmutableList());
    Signature signature = new Signature(struct.get(Type.class), struct.name(), parameters);
    return new Constructor(signature, struct.location());
  }
}
