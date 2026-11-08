public interface exprParser {
    NrComplex[] parseNumbers(String[] args);
    Operation[] parseOperations(String[] args);
    boolean isValid(String[] args);
}