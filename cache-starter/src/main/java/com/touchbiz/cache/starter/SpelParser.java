package com.touchbiz.cache.starter;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author zhangxuezhen
 * @description: EL解析器
 * @date 2020/10/1511:13 下午
 */
public class SpelParser {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public static String getKey(String key, String[] paramsNames, Object[] args) {
        // 将key解析为EL表达式
        Expression exp = PARSER.parseExpression(key);
        // 获取解析器
        EvaluationContext context = new StandardEvaluationContext();

        if (args.length <= 0) {
            return null;
        }
        for (int i = 0; i < args.length; i++) {
            String s = args[i].toString();
            context.setVariable(paramsNames[i], s);
        }
        return exp.getValue(context, String.class);

    }
}
