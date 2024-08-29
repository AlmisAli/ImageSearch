
import java.io.*;
import java.util.ArrayList;


public class ColorHistogram {
    int d;
    String fileName;
    ColorImage image;
    private ArrayList<Integer> calculatedHistogram = new ArrayList<>(512);
    private final ArrayList<Integer> histogram = new ArrayList<>();



    public ColorHistogram (int d){

        this.d=d;
    }
    public ColorHistogram(String fileName){

        this.fileName=fileName;
    }

    public void histogramCalculator(ArrayList<ArrayList<Integer>> reducedPixel)throws IOException {

        // Initialize counts for each RGB value to zero
        for (int i = 0; i < 512; i++) {
            calculatedHistogram.add(0);
        }

        // Loop through each pixel in the reduced pixel data
        for (ArrayList<Integer> pixel : reducedPixel) {
            // Extract RGB values from the pixel
            int red = pixel.get(0);
            int green = pixel.get(1);
            int blue = pixel.get(2);

            int index = ((red<<(2*d)) + (green<<d) + (blue));// Get histogram index

            calculatedHistogram.set(index, calculatedHistogram.get(index) + 1);
        }
        setHistogramQ(calculatedHistogram);
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
                    histogram.add(integerElement);//Stores histogram data into an arraylist
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        setHistogramQ(histogram);
    }



    public void setHistogramQ(ArrayList<Integer> histogram) {
        this.calculatedHistogram = histogram;
    }

    public ArrayList<Integer> getHistogram() {
        return calculatedHistogram;
    }
    public void setImage(ColorImage image){

        this.image=image;
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
    public void saveHistogram (ArrayList<Integer>Histogram,String filename1){//Output file name
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
