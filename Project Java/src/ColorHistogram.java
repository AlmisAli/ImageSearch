// Almis Ali 300317688
import java.io.*;
import java.util.ArrayList;

public class ColorHistogram {
    int d;
    String fileName;
    ColorImage image;
    private int totalPixel;
    private ArrayList<Integer> histogramQuery = new ArrayList<>(512);
    private final ArrayList<Integer> histogramData = new ArrayList<>();


    public ColorHistogram (int d){
        this.d=d;
    }
    public ColorHistogram(String fileName){
        this.fileName=fileName;
    }
    public void setImage(ColorImage image){
        this.image=image;
    }
    public void histogramCalculator(ArrayList<ArrayList<Integer>> reducedPixel)throws IOException {

        // Initialize counts for each RGB value to zero
        for (int i = 0; i < 512; i++) {
            histogramQuery.add(0);
        }

        // Loop through each pixel in the reduced pixel data
        for (ArrayList<Integer> pixel : reducedPixel) {
            // Extract RGB values from the pixel
            int red = pixel.get(0);
            int green = pixel.get(1);
            int blue = pixel.get(2);

            int index = ((red<<(2*d)) + (green<<d) + (blue));// Get histogram index

            histogramQuery.set(index, histogramQuery.get(index) + 1);
        }
        setHistogramQ(histogramQuery);
    }
    public void readHistogram(String fileName){// Read histograms
        try{
            BufferedReader reader=new BufferedReader(new FileReader(fileName));
            String line;
            reader.readLine();
            while((line = reader.readLine())!=null){
                String[] element= line.split(" ");
                for(String color : element){
                    int integerElement= Integer.parseInt(color);
                    histogramData.add(integerElement);//Stores histogram data into an arraylist
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setHistogramQ(histogramData);
    }



    public void setHistogramQ(ArrayList<Integer> histogram) {
        this.histogramQuery = histogram;
    }

    public ArrayList<Integer> getHistogram() {
        return histogramQuery;
    }

    public double compare( ArrayList<Integer> dataSetHistogram,ArrayList<Integer> histogramQ){//Calculates intersection
        double intersection=0;
        ArrayList<Double> normalizedHistD=normalizedHistogram(dataSetHistogram);
        ArrayList<Double> normalizedHistQ=normalizedHistogram(histogramQ);
        for(int i=0;i<dataSetHistogram.size();i++){
            double minCount = Math.min(normalizedHistD.get(i), normalizedHistQ.get(i));
            intersection += minCount;
        }

        return intersection;
    }
    public ArrayList<Double> normalizedHistogram(ArrayList<Integer>Histogram){
        ArrayList<Double> normalizedHist = new ArrayList<>();
        for(Integer element:Histogram){
            double doubleValue=(double)element;
            normalizedHist.add(doubleValue/172800);
        }

        return normalizedHist;
    }
    public void saveHistogram (ArrayList<Integer>Histogram,String filename1){//Saves histogram to text file
        try{
            BufferedWriter writer= new BufferedWriter(new FileWriter(filename1));
            for(Integer element:Histogram) {
                writer.write(element.toString());
                writer.write(" ");
            }
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
