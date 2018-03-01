package org.smoothbuild.parse.ast;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.AccessorContext;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExprContext;
import org.smoothbuild.antlr.SmoothParser.FieldContext;
import org.smoothbuild.antlr.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.SmoothParser.FuncContext;
import org.smoothbuild.antlr.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.SmoothParser.NameContext;
import org.smoothbuild.antlr.SmoothParser.NonArrayTypeContext;
import org.smoothbuild.antlr.SmoothParser.ParamContext;
import org.smoothbuild.antlr.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.antlr.SmoothParser.StructContext;
import org.smoothbuild.antlr.SmoothParser.TypeContext;
import org.smoothbuild.lang.message.Location;

public class AstCreator {
  public static Ast fromParseTree(Path file, ModuleContext module) {
    List<FuncNode> nodes = new ArrayList<>();
    List<StructNode> structs = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      private Set<String> visibleParams = new HashSet<>();

      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.name().getText();
        Location location = locationOf(file, struct.name());
        List<FieldNode> fields = createFields(struct.fieldList());
        structs.add(new StructNode(name, fields, location));
        return null;
      }

      private List<FieldNode> createFields(FieldListContext fieldList) {
        ArrayList<FieldNode> result = new ArrayList<>();
        if (fieldList != null) {
          for (FieldContext field : sane(fieldList.field())) {
            result.add(createField(field));
          }
        }
        return result;
      }

      private FieldNode createField(FieldContext field) {
        TypeNode type = createType(field.type());
        NameContext nameContext = field.name();
        String name = nameContext.getText();
        Location location = locationOf(file, nameContext);
        return new FieldNode(type, name, location);
      }

      @Override
      public Void visitFunc(FuncContext func) {
        TypeNode type = func.type() == null ? null : createType(func.type());
        NameContext nameContext = func.name();
        String name = nameContext.getText();
        List<ParamNode> params = createParams(func.paramList());
        visibleParams = paramNames(params);
        ExprNode pipe = func.pipe() == null ? null : createPipe(func.pipe());
        visitChildren(func);
        visibleParams = new HashSet<>();
        nodes.add(new FuncNode(type, name, params, pipe, locationOf(file, nameContext)));
        return null;
      }

      private Set<String> paramNames(List<ParamNode> params) {
        return params
            .stream()
            .map(p -> p.name())
            .collect(toSet());
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
        String name = param.name().getText();
        Location location = locationOf(file, param);
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
          Location location = locationOf(file, pipe.p.get(i));
          List<ArgNode> args = new ArrayList<>();
          args.add(new ArgNode(0, null, result, location));
          args.addAll(createArgList(call.argList()));
          String name = call.name().getText();
          result = new CallNode(name, args, locationOf(file, call.name()));
        }
        return result;
      }

      private ExprNode createExpr(ExprContext expr) {
        if (expr.expr() != null) {
          ExprNode structExpr = createExpr(expr.expr());
          AccessorContext accessor = expr.accessor();
          String name = accessor.name().getText();
          return new AccessorNode(structExpr, name, locationOf(file, accessor));
        }
        if (expr.array() != null) {
          List<ExprNode> elements = map(expr.array().expr(), this::createExpr);
          return new ArrayNode(elements, locationOf(file, expr));
        }
        if (expr.call() != null) {
          CallContext call = expr.call();
          String name = call.name().getText();
          Location location = locationOf(file, call.name());
          if (visibleParams.contains(name)) {
            boolean hasParentheses = call.p != null;
            return new RefNode(name, hasParentheses, location);
          } else {
            List<ArgNode> args = createArgList(call.argList());
            return new CallNode(name, args, location);
          }
        }
        if (expr.STRING() != null) {
          String quotedString = expr.STRING().getText();
          return new StringNode(quotedString.substring(1, quotedString.length() - 1),
              locationOf(file, expr));
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
            String name = nameContext == null ? null : nameContext.getText();
            ExprNode exprNode = createExpr(expr);
            result.add(new ArgNode(i + 1, name, exprNode, locationOf(file, arg)));
          }
        }
        return result;
      }

      private TypeNode createType(TypeContext type) {
        if (type.nonArrayType() != null) {
          return createNonArrayType(type.nonArrayType());
        }
        if (type.arrayType() != null) {
          return createArrayType(type.arrayType());
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode createNonArrayType(NonArrayTypeContext nonArrayType) {
        return new TypeNode(nonArrayType.getText(), locationOf(file, nonArrayType));
      }

      private TypeNode createArrayType(ArrayTypeContext arrayType) {
        TypeNode elementType = createType(arrayType.type());
        return new ArrayTypeNode(elementType, locationOf(file, arrayType));
      }
    }.visit(module);
    return new Ast(structs, nodes);
  }
}
