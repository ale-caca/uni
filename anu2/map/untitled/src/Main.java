public class Main {
    public static void main(String[] args) {
        exprParser parser = new SimpleParser();

        if (!parser.isValid(args)) {
            System.out.println("Expresie invalida!");
            return;
        }

        NrComplex[] numbers = parser.parseNumbers(args);
        Operation[] operations = parser.parseOperations(args);

        NrComplex result = numbers[0];
        for (int i = 0; i < operations.length; i++) {
            result = operations[i].execute(result, numbers[i + 1]);
        }

        System.out.println(result.toString());
    }
}