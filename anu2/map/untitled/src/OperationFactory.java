public class OperationFactory {

    public static Operation createOperation(String operator) {
        switch (operator) {
            case "+":
                return new AddOp();
            case "-":
                return new SubOp();
            case "*":
                return new MulOp();
            case "/":
                return new DivOp();
            default:
                throw new IllegalArgumentException("Operator necunoscut: " + operator);
        }
    }
}