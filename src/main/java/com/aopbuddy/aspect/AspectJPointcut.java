package com.aopbuddy.aspect;


import com.aopbuddy.retransform.Pointcut;

public class AspectJPointcut extends Pointcut {
    protected static final CustomPointcutParser PARSER =
            CustomPointcutParser.getPointcutParserSupportingAllPrimitives();


    protected CustomPointcutExpression customPointcutExpression;

    public AspectJPointcut(String expression) {
        super(expression);
        this.customPointcutExpression = PARSER.parsePointcutExpression(expression);
    }

    public static AspectJPointcut of(String expression) {
        return new AspectJPointcut(expression);
    }

    @Override
    public boolean matches(ClassObject clz) {
        return customPointcutExpression.couldMatchJoinPointsInType(clz.getName());
    }

    @Override
    public boolean matches(MethodObject method) {
        return customPointcutExpression.matchesMethodExecution(method.getClassName(), method.getName(),
                method.getDescriptor());
    }
}
