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
      public Void visitFunc(FuncContext func) {
        NameContext nameContext = func.name();
        Name name = new Name(nameContext.getText());
        List<ParamNode> params = createParams(func.paramList());
        ExprNode pipe = createPipe(func.pipe());
        visitChildren(func);
        nodes.add(new FuncNode(name, params, pipe, locationOf(nameContext)));
        return null;
      }

      private List<ParamNode> createParams(ParamListContext paramList) {
        ArrayList<ParamNode> result = new ArrayList<>();
        if (paramList != null) {
          for (ParamContext param : sane(paramList.param())) {
            result.add(createParam(param));
          }
        }
        return result;
      }

      private ParamNode createParam(ParamContext param) {
        TypeNode type = createType(param.type());
        Name name = new Name(param.name().getText());
        Location location = locationOf(param);
        ExprNode defaultValue = param.expr() != null
            ? createExpr(param.expr())
            : null;
        return new ParamNode(type, name, defaultValue, location);
      }

      private ExprNode createPipe(PipeContext pipe) {
        ExprContext initialExpression = pipe.expr();
        ExprNode result = createExpr(initialExpression);
        List<CallContext> calls = pipe.call();
        for (int i = 0; i < calls.size(); i++) {
          CallContext call = calls.get(i);
          // nameless piped argument's location is set to the pipe character '|'
          Location location = locationOf(pipe.p.get(i));
          List<ArgNode> args = new ArrayList<>();
          args.add(new ArgNode(0, null, result, location));
          args.addAll(createArgList(call.argList()));
          Name name = new Name(call.name().getText());
          boolean hasParentheses = call.p != null;
          result = new CallNode(name, args, hasParentheses, locationOf(call.name()));
        }
        return result;
      }

      private ExprNode createExpr(ExprContext expr) {
        if (expr.array() != null) {
          List<ExprNode> elements = map(expr.array().expr(), this::createExpr);
          return new ArrayNode(elements, locationOf(expr));
        }
        if (expr.call() != null) {
          CallContext call = expr.call();
          Name name = new Name(call.name().getText());
          List<ArgNode> args = createArgList(call.argList());
          Location location = locationOf(call.name());
          boolean hasParentheses = call.p != null;
          return new CallNode(name, args, hasParentheses, location);
        }
        if (expr.STRING() != null) {
          String quotedString = expr.STRING().getText();
          return new StringNode(quotedString.substring(1, quotedString.length() - 1),
              locationOf(expr));
        }
        throw new RuntimeException("Illegal parse tree: " + ExprContext.class.getSimpleName()
            + " without children.");
      }

      private List<ArgNode> createArgList(ArgListContext argList) {
        List<ArgNode> result = new ArrayList<>();
        if (argList != null) {
          List<ArgContext> args = argList.arg();
          for (int i = 0; i < args.size(); i++) {
            ArgContext arg = args.get(i);
            ExprContext expr = arg.expr();
            NameContext nameContext = arg.name();
            Name name = nameContext == null ? null : new Name(nameContext.getText());
            ExprNode exprNode = createExpr(expr);
            result.add(new ArgNode(i + 1, name, exprNode, locationOf(arg)));
          }
        }
        return result;
      }

      private TypeNode createType(TypeContext type) {
        if (type.basicType() != null) {
          return createBasicType(type.basicType());
        }
        if (type.arrayType() != null) {
          return createArrayType(type.arrayType());
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode createBasicType(BasicTypeContext basicType) {
        return new TypeNode(basicType.getText(), locationOf(basicType));
      }

      private TypeNode createArrayType(ArrayTypeContext arrayType) {
        TypeNode elementType = createType(arrayType.type());
        return new ArrayTypeNode(elementType, locationOf(arrayType));
      }
    }.visit(module);
    return new Ast(nodes);
  }
}
