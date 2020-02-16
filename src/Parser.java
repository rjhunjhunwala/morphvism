import java.beans.Expression;
import java.util.*;

public class Parser {
    public static final HashMap<String, Double> vars = new HashMap<>();
    {
        vars.put("pi", Math.PI);
        vars.put("e", Math.E);
    }
    public static final HashMap<String, BinaryOperator> OPERATORS = new HashMap<>();
    public static abstract class Expression {
        public abstract double evaluate(HashMap<String, Double> variables);
    }
    public static abstract class BinaryOperator{
        public abstract double apply(double left, double right);

    }

    public static class AddOperator extends BinaryOperator{
        public double apply(double left, double right){
            // System.out.println(left +"|" + right);
            return left + right;
        }
    }
    static {
        OPERATORS.put('+'+"", new AddOperator());
    }


    public static class SubOperator extends BinaryOperator{
        public double apply(double left, double right){
            // System.out.println(left +"|" + right);
            return left - right;
        }
    }
    static {
        OPERATORS.put('-'+"", new SubOperator());
    }


    public static class MulOperator extends BinaryOperator{
        public double apply(double left, double right){
            // System.out.println(left +"|" + right);
            return left * right;
        }
    }
    static {
        OPERATORS.put('*'+"", new MulOperator());
    }

    public static class DivOperator extends BinaryOperator{
        public double apply(double left, double right){
            // System.out.println(left +"|" + right);
            return left / right;
        }
    }
    static {
        OPERATORS.put('/'+"", new DivOperator());
    }

    public static class PowOperator extends BinaryOperator{
        public double apply(double left, double right){
            // System.out.println(left +"|" + right);
            return Math.pow(left, right);
        }
    }
    static {
        OPERATORS.put('^'+"", new PowOperator());
    }


    public static class MultipleOperatorExpression extends Expression{
        ArrayList<Expression> expressions;
        ArrayList<BinaryOperator> operators;
        public MultipleOperatorExpression(ArrayList<Expression> expressions, ArrayList<BinaryOperator> operators){
            this.expressions = expressions;
            this.operators = operators;
        }
        public  double evaluate(HashMap<String, Double> vals){
            List<Double> values = new ArrayList<>(), newValues = new ArrayList<>();
            for(Expression e:expressions){
                values.add(e.evaluate(vals));
            }
            ArrayList<BinaryOperator> tempOperators = new ArrayList<>(operators.size()), newOps =   new ArrayList<>(operators.size());
            // System.out.println(operators);
            for(BinaryOperator b: operators){
                tempOperators.add(b);
            }
           boolean[] shouldRemove = new boolean[values.size()];

            for(int i = 0;i<tempOperators.size();i++){
                BinaryOperator op = tempOperators.get(i);
                // System.out.println("WTF!");
                if(op instanceof PowOperator){
                    shouldRemove[i] = true;
                    // System.out.println("here!");
                    values.set(i + 1, op.apply(values.get(i), values.get(i+1)));
                }
            }

            // System.out.println(values);
            for(int i = 0;i<shouldRemove.length;i++){
                if(!shouldRemove[i]){
                    newValues.add(values.get(i));
                    if(i<tempOperators.size()){
                        newOps.add(tempOperators.get(i));
                    }
                }
            }

            tempOperators = newOps;
            values = newValues;

            newOps =   new ArrayList<>(operators.size());
            newValues = new ArrayList<>();


            shouldRemove = new boolean[values.size()];

            for(int i = 0;i<tempOperators.size();i++){
                BinaryOperator op = tempOperators.get(i);
                // System.out.println("WTF!");
                if(op instanceof MulOperator || op instanceof DivOperator){
                    shouldRemove[i] = true;
                    // System.out.println("here!");
                    values.set(i + 1, op.apply(values.get(i), values.get(i+1)));
                }
            }

            // System.out.println(values);
            for(int i = 0;i<shouldRemove.length;i++){
                if(!shouldRemove[i]){
                    newValues.add(values.get(i));
                    if(i<tempOperators.size()){
                        newOps.add(tempOperators.get(i));
                    }
                }
            }




            tempOperators = newOps;
            values = newValues;

            newOps =   new ArrayList<>(operators.size());
            newValues = new ArrayList<>();

            shouldRemove = new boolean[values.size()];

            for(int i = 0;i<tempOperators.size();i++){
                BinaryOperator op = tempOperators.get(i);
                // System.out.println("WTF!");
                if(op instanceof AddOperator || op instanceof SubOperator){
                    shouldRemove[i] = true;
                    // System.out.println("here!");
                    values.set(i + 1, op.apply(values.get(i), values.get(i+1)));
                }
            }

            // System.out.println(values);
            for(int i = 0;i<shouldRemove.length;i++){
                if(!shouldRemove[i]){
                    newValues.add(values.get(i));
                    if(i<tempOperators.size()){
                        newOps.add(tempOperators.get(i));
                    }
                }
            }
            tempOperators = newOps;
            values = newValues;



            newOps =   new ArrayList<>(operators.size());
            newValues = new ArrayList<>();


            newValues = new ArrayList<>();
            newOps = new ArrayList<>();

            return values.get(0);

        }


    }

    //Unary Expressions





    public static Expression parseExpression(String s){
        s = s.replaceAll(" ", "");
        try {
            int[] matchingMap = new int[s.length()];
            java.util.Stack<Integer> stack = new java.util.Stack<>();
            for (int i = 0; i < s.length(); i++) {
                if(s.charAt(i)== '('){
                    stack.push(i);
                }else if(s.charAt(i)== ')'){
                    int index = stack.pop();
                    matchingMap[index] = i;
                    matchingMap[i] = index;
                }
            }
           // System.out.println(java.util.Arrays.toString(matchingMap));

            return parseExpression(s.toCharArray(), 0, s.length(), matchingMap);
        }catch(Exception ex){
            ex.printStackTrace();
            System.err.println(ex);
            return parseExpression("1");
        }
    }
    public static class ConstExpression extends Expression{
        double val;
        public ConstExpression(double val){
            this.val = val;
        }
        public double evaluate(HashMap<String, Double> vars){
            return val;
        }
    }
    public static class VarExpression extends Expression{
        String varName;
        public VarExpression(String s){
            this.varName = s;
            System.out.println(s);
        }
        public double evaluate(HashMap<String, Double> vars){
            return vars.get(varName);
        }
    }

    public static Expression parseExpression(char[] s, int start, int end, int[] matchingMap){
        ArrayList<Expression> things = new ArrayList<>();
        ArrayList<BinaryOperator> operators = new ArrayList<>();

        // System.out.println((new String(s)).substring(start, end));
        // System.out.println(start >= s.length);
        int positionToLook = start;
        while(true){
            if(s[positionToLook] == '('){
                things.add(parseExpression(s, positionToLook + 1, matchingMap[positionToLook], matchingMap));
                positionToLook = matchingMap[positionToLook] + 1;

            } else if(false){

            }else{
                int nextOperator = positionToLook + 1;
                for(;nextOperator < end && !"+-*/^".contains(s[nextOperator]+"");nextOperator++){

                }
                String[] letters = new String[nextOperator - positionToLook];
                for(int i = positionToLook;i<nextOperator;i++){
                    letters[i - positionToLook] = s[i] +"";
                }
                String value  = "".join("", letters);
                if(value.matches("[0123456789]*\\Q.\\E?[0123456789]*")){
                    // System.out.println(value);
                    things.add(new ConstExpression(Double.parseDouble(value)));
                }else{
                    // System.exit(69);
                    things.add(new VarExpression(value));
                }
                positionToLook = nextOperator;
            }
            if(positionToLook < end){

                operators.add(OPERATORS.get(s[positionToLook]+""));
                // System.out.println(operators.get(0));
                positionToLook += 1;
            }else{
                break;
            }

        }



        MultipleOperatorExpression out =  new MultipleOperatorExpression(things, operators);

        // System.out.println(out);
        return out;

    }
    public static void main(String[] args){
        System.out.println(parseExpression("2 + 2 * 2 + 3^3 + 3/3").evaluate(vars));
        System.out.println(parseExpression("2 + 2").evaluate(vars));
        System.out.println(parseExpression("5 + (((1 + 1)) + (1 + 2 + 3 + 4) + (((2)+(2)))) + ((1 + 1)) + (1 + 2 + 3 + 4) + (((2)+(2)))").evaluate(vars));
        System.out.println(parseExpression("2 + 2^2 + 3*3 -3/3 + 4^3").evaluate(vars));
        vars.put("x", 420.0);
        vars.put("y", 69.0);

        System.out.println(parseExpression("x/y + y + 2^(y/10)").evaluate(vars));
        ObjectFileMaker.makeObjectFile("x^2+y^2 + .1 * (x * y)^3", "x", "y", vars, -10, 10, -10, 10);
    }
}
