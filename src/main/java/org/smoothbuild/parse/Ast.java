package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final List<FunctionNode> functions;

  private Ast(List<FunctionNode> functions) {
    this.functions = ImmutableList.copyOf(functions);
  }

  public List<FunctionNode> functions() {
    return functions;
  }

  public static Ast create(ModuleContext module) {
    List<FunctionNode> nodes = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      Set<Dependency> currentDependencies = new HashSet<>();

      public Void visitFunction(FunctionContext context) {
        NameContext nameContext = context.name();
        Name name = name(nameContext.getText());
        visitChildren(context);
        nodes.add(new FunctionNode(name, context, currentDependencies, locationOf(nameContext)));
        return null;
      }

      public Void visitCall(CallContext call) {
        NameContext functionName = call.name();
        Name name = name(functionName.getText());
        CodeLocation location = locationOf(functionName);
        currentDependencies.add(new Dependency(location, name));
        return visitChildren(call);
      }

    }.visit(module);
    return new Ast(nodes);
  }
}
