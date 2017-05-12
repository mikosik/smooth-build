package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.ast.Ast.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.antlr.SmoothParser.ParamContext;
import org.smoothbuild.antlr.SmoothParser.ParamListContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.parse.Dependency;

public class AstCreator {
  public static Ast fromParseTree(ModuleContext module) {
    List<FunctionNode> nodes = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      Set<Dependency> currentDependencies = new HashSet<>();

      public Void visitFunction(FunctionContext context) {
        NameContext nameContext = context.name();
        Name name = name(nameContext.getText());
        List<ParamNode> params = convertParams(context.paramList());
        visitChildren(context);
        nodes.add(new FunctionNode(name, params, context, currentDependencies, locationOf(
            nameContext)));
        return null;
      }

      private List<ParamNode> convertParams(ParamListContext paramList) {
        ArrayList<ParamNode> result = new ArrayList<>();
        if (paramList != null) {
          for (ParamContext param : paramList.param()) {
            CodeLocation codeLocation = locationOf(param);
            result.add(new ParamNode(param.type().getText(), param.name().getText(), codeLocation));
          }
        }
        return result;
      }

      public Void visitCall(CallContext call) {
        NameContext functionName = call.name();
        Name name = name(functionName.getText());
        CodeLocation location = locationOf(functionName);
        currentDependencies.add(new Dependency(location, name));
        return visitChildren(call);
      }
    }.visit(module);
    return ast(nodes);
  }
}
