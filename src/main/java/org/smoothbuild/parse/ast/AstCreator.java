package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.parse.ast.Ast.ast;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.SmoothParser.BasicTypeContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExprContext;
import org.smoothbuild.antlr.SmoothParser.FuncContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.antlr.SmoothParser.ParamContext;
import org.smoothbuild.antlr.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.antlr.SmoothParser.TypeContext;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.parse.Dependency;

public class AstCreator {
  public static Ast fromParseTree(ModuleContext module) {
    List<FunctionNode> nodes = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      Set<Dependency> currentDependencies = new HashSet<>();

      public Void visitFunc(FuncContext context) {
        NameContext nameContext = context.name();
        Name name = name(nameContext.getText());
        List<ParamNode> params = convertParams(context.paramList());
        ExprNode pipe = convertPipe(context.pipe());
        visitChildren(context);
        nodes.add(new FunctionNode(name, params, pipe, currentDependencies,
            locationOf(nameContext)));
        return null;
      }

      private List<ParamNode> convertParams(ParamListContext context) {
        ArrayList<ParamNode> result = new ArrayList<>();
        if (context != null) {
          for (ParamContext param : sane(context.param())) {
            TypeNode type = convertType(param.type());
            String name = param.name().getText();
            CodeLocation codeLocation = locationOf(param);
            result.add(new ParamNode(type, name, codeLocation));
          }
        }
        return result;
      }

      private ExprNode convertPipe(PipeContext context) {
        ExprContext initialExpression = context.expr();
        ExprNode result = convertExpression(initialExpression);
        List<CallContext> calls = context.call();
        for (int i = 0; i < calls.size(); i++) {
          CallContext call = calls.get(i);
          // nameless piped argument's location is set to the pipe character '|'
          CodeLocation codeLocation = locationOf(context.p.get(i));
          List<ArgNode> args = new ArrayList<>();
          args.add(new ArgNode(0, null, result, codeLocation));
          args.addAll(convertArgList(call.argList()));
          result = new CallNode(call.name().getText(), args, locationOf(call.name()));
        }
        return result;
      }

      private ExprNode convertExpression(ExprContext context) {
        if (context.array() != null) {
          List<ExprNode> elements = map(context.array().expr(), this::convertExpression);
          return new ArrayNode(elements, locationOf(context));
        }
        if (context.call() != null) {
          CallContext call = context.call();
          List<ArgNode> args = convertArgList(call.argList());
          return new CallNode(call.name().getText(), args, locationOf(call.name()));
        }
        if (context.STRING() != null) {
          String quotedString = context.STRING().getText();
          return new StringNode(quotedString.substring(1, quotedString.length() - 1),
              locationOf(context));
        }
        throw new RuntimeException("Illegal parse tree: " + ExprContext.class.getSimpleName()
            + " without children.");
      }

      private List<ArgNode> convertArgList(ArgListContext context) {
        List<ArgNode> result = new ArrayList<>();
        if (context != null) {
          List<ArgContext> argContexts = context.arg();
          for (int i = 0; i < argContexts.size(); i++) {
            ArgContext argContext = argContexts.get(i);
            ExprContext exprContext = argContext.expr();
            NameContext nameContext = argContext.name();
            String name = nameContext == null ? null : nameContext.getText();
            ExprNode exprNode = convertExpression(exprContext);
            result.add(new ArgNode(i + 1, name, exprNode, locationOf(argContext)));
          }
        }
        return result;
      }

      private TypeNode convertType(TypeContext context) {
        if (context.basicType() != null) {
          return convertBasicType(context.basicType());
        }
        if (context.arrayType() != null) {
          return convertArrayType(context.arrayType());
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode convertBasicType(BasicTypeContext context) {
        return new TypeNode(context.getText(), locationOf(context));
      }

      private TypeNode convertArrayType(ArrayTypeContext context) {
        TypeNode elementType = convertType(context.type());
        return new ArrayTypeNode(elementType, locationOf(context));
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
