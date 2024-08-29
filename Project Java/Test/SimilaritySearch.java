import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SimilaritySearch {
    public static void main(String[] args) {
        int d = 3;
        int depth = 24;
        String path = "C:\\Users\\yahya\\IdeaProjects\\P2\\imagesimilar\\queryImages\\queryImages";
       // String file = args[0];
       // String folder = args[1];
        String folderPath = "C:\\Users\\almis\\IdeaProjects\\CSI 2520\\Projects\\Project Java\\Test\\imageDataset2_15_20";
        String queryfolderPath = "C:\\Users\\almis\\IdeaProjects\\CSI 2520\\Projects\\Project Java\\Test\\queryImages\\q00.ppm";
       // String pathToFolder = Paths.get(queryfolderPath, file).toString();
        ColorImage InstanceImage = new ColorImage(queryfolderPath, depth, d);
        try {
            List<String> closestFilePaths = new ArrayList<>();
            InstanceImage .readPPM(queryfolderPath);
            InstanceImage .reduceColor(d);
            ColorHistogram Instanceh=new ColorHistogram(d);
            Instanceh.setImage(InstanceImage );
            Instanceh.histogramCalculator(InstanceImage.getReducedPixel());
            Map<String, Double> comparisonResultsMap = new HashMap<>();
            Files.walk(Paths.get(folderPath))//Traverse folder
                    .filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toString().toLowerCase().endsWith(".txt"))
                    .forEach(filePath -> {// selects each file
                        ColorHistogram instanceH = new ColorHistogram(filePath.toString());//creates an instance for each file
                        instanceH.readHistogram(filePath.toString());
                        double comparisonResult = instanceH.compare(instanceH.getHistogram(), Instanceh.getHistogram());
                        comparisonResultsMap.put(filePath.getFileName().toString(), comparisonResult);//associates file path with comparison result
                    });

            List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(comparisonResultsMap.entrySet());
            sortedEntries.sort((entry1, entry2) -> Double.compare(Math.abs(entry1.getValue() - 1.0), Math.abs(entry2.getValue() - 1.0)));

            for (int i = 0; i < Math.min(5, sortedEntries.size()); i++) {
                System.out.println(sortedEntries.get(i).getKey());
                closestFilePaths.add(sortedEntries.get(i).getKey());
            }
        }
        catch(
    IOException e)

    {
        e.printStackTrace();
    }
}

}
