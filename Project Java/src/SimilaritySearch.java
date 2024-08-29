// Almis Ali 300317688
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimilaritySearch {
    public static void main(String[] args) {
        int d=3;
        int depth=24;
        String pathD = ".";
        String file = args[0]; //Reads first command-line argument
        String folder = args[1];//Reads second command-line argument
        String folderPath = Paths.get(pathD, folder).toString();
        String folderPathQ = ".\\queryImages";
        String path = Paths.get(folderPathQ, file).toString();
       // String outputFile = "C:\\Users\\almis\\IdeaProjects\\CSI 2520\\Assignment1 Java\\Output.txt";
        ColorImage instanceCI = new ColorImage(path,depth,d);//Create instance to read the .ppm image


        try {
            List<ComparisonResult> closestComparisons = new ArrayList<>();
            instanceCI.readPPM(path);
            instanceCI.reduceColor(d);
            ColorHistogram histogramI=new ColorHistogram(d);
            histogramI.setImage(instanceCI);
            histogramI.histogramCalculator(instanceCI.getReducedPixel());//Calculates histogram
           // histogramI.saveHistogram(histogramI.getHistogram(),outputFile);
            Files.walk(Paths.get(folderPath))//Traverse folder
                    .filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().toLowerCase().endsWith(".txt"))
                    .forEach(filePath -> {// selects each file
                        ColorHistogram instanceH = new ColorHistogram(filePath.toString());//creates an instance for each file
                        String i=filePath.toString();
                        instanceH.readHistogram(filePath.toString());
                        double comparisonResult = instanceH.compare(instanceH.getHistogram(), histogramI.getHistogram());
                        closestComparisons.add(new ComparisonResult(filePath.getFileName().toString(), comparisonResult));//returns 5 closest images
                    });
            closestComparisons.sort(Comparator.comparingDouble(result -> Math.abs(result.getComparisonResult() - 1.0)));


            for (int i = 0; i < Math.min(5, closestComparisons.size()); i++) {
                System.out.println(closestComparisons.get(i).getFilePath());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}