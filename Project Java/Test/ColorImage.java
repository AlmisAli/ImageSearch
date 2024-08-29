
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;



public class ColorImage {
    private ArrayList<ArrayList<Integer>> reducepixel=new ArrayList<>();
    public ArrayList<ArrayList<Integer>> image = new ArrayList<>();
    private int width;
    private int height;
    private final int depth;
    String fileName;
    int d;
public ColorImage(String fileName,int depth,int d){
    this.fileName=fileName;
    this.depth=depth;
    this.d=d;
}
    public void readPPM(String fileName) {
        ArrayList row= new ArrayList<Integer>();
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;
            for(int i=0;i<2;i++){
                reader.readLine();
            }
            String[] parts = reader.readLine().split(" ");
            int[] dimension = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                dimension[i] = Integer.parseInt(parts[i]);
            }
            setWidth(dimension[0]);
            setHeight(dimension[1]);
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                int count=0;
                String[] elements= line.split(" ");
                for (String element : elements) {
                    row.add(Integer.parseInt(element));
                    count++;
                    if(count==3){
                        image.add(new ArrayList<>(row));
                        row.clear();
                        count=0;
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void reduceColor(int d) {
        if (d < 1 || d > 8) {
            throw new IllegalArgumentException("d must be between 1 and 8");
        }
        System.out.println(image.size());
            ArrayList<ArrayList<Integer>> reducedPixel = new ArrayList<>(image.size());
            for (ArrayList<Integer> row : image) {
                ArrayList<Integer> reducedRow = new ArrayList<>(row.size());
                for (Integer pixel : row) {
                    int reducedPixelValue = (pixel >> (8 - d));
                    reducedRow.add(reducedPixelValue);
                }
                reducedPixel.add(reducedRow);
            }
            setReducedPixel(reducedPixel);

    }
    public int[] getPixel(int i, int j){
        int[] getPixel=new int[3];
        getPixel[0]=image.get(j).get(0);
        getPixel[1]=image.get(j).get(1);
        getPixel[2]=image.get(j).get(2);
        return getPixel;
    }

    public void setReducedPixel(ArrayList<ArrayList<Integer>> reducedPixel) {

    this.reducepixel = reducedPixel;
    }



    public int getDepth() {
        return depth;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
    public void setHeight(int height) {
        this.height = height;

    }


    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<ArrayList<Integer>> getReducedPixel() {

        return reducepixel;
    }


}
