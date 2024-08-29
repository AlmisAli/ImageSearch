// Almis Ali 300317688
public class ComparisonResult {
    private final String filePath;
    private final double comparisonResult;

    public ComparisonResult(String filePath, double comparisonResult) {
        this.filePath = filePath;
        this.comparisonResult = comparisonResult;
    }

    public String getFilePath() {
        return filePath;
    }

    public double getComparisonResult() {
        return comparisonResult;
    }
}
