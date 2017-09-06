package org.smoothbuild.parse.ast;

import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.List;

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
import org.smoothbuild.lang.message.Location;

public class AstCreator {
  public static Ast fromParseTree(ModuleContext module) {
    List<FuncNode> nodes = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      public Void visitFunc(FuncContext context) {
        NameContext nameContext = context.name();
        Name name = new Name(nameContext.getText());
        List<ParamNode> params = convertParams(context.paramList());
        ExprNode pipe = convertPipe(context.pipe());
        visitChildren(context);
        nodes.add(new FuncNode(name, params, pipe, locationOf(nameContext)));
        return null;
      }

      private List<ParamNode> convertParams(ParamListContext context) {
        ArrayList<ParamNode> result = new ArrayList<>();
        if (context != null) {
          for (ParamContext param : sane(context.param())) {
            TypeNode type = convertType(param.type());
            Name name = new Name(param.name().getText());
            Location location = locationOf(param);
            result.add(new ParamNode(type, name, location));
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
          Location location = locationOf(context.p.get(i));
          List<ArgNode> args = new ArrayList<>();
          args.add(new ArgNode(0, null, result, location));
          args.addAll(convertArgList(call.argList()));
          Name name = new Name(call.name().getText());
          boolean hasParentheses = call.p != null;
          result = new CallNode(name, args, hasParentheses, locationOf(call.name()));
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
          Name name = new Name(call.name().getText());
          List<ArgNode> args = convertArgList(call.argList());
          Location location = locationOf(call.name());
          boolean hasParentheses = call.p != null;
          return new CallNode(name, args, hasParentheses, location);
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
            Name name = nameContext == null ? null : new Name(nameContext.getText());
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
    }.visit(module);
    return new Ast(nodes);
  }
}
