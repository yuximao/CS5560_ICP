package property_characteristics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ Yuhn on 11/15/2018
 */
public class Asymmetric {
    public static void main(String[] args) throws IOException {
        List<String> tripletsArrayList = new ArrayList<>();
        try {
            File file=new File("input/allTriplets.txt");
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String triplet;

            while ((triplet=bufferedReader.readLine()) != null) {
                tripletsArrayList.add(triplet);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<String> asymmetricArray = new ArrayList<>();
        // Asymmetric
        for(int i = 0; i < tripletsArrayList.size(); i++) {
            String[] splitTriplet = tripletsArrayList.get(i).split("\t");
            for (int j = i + 1; j < tripletsArrayList.size(); j++) {
                String[] checkSplitTriplet = tripletsArrayList.get(j).split("\t");
                if(splitTriplet.length == 3 && checkSplitTriplet.length == 3) {
                    if (splitTriplet[2].contains(checkSplitTriplet[0]) && splitTriplet[1].contains(checkSplitTriplet[1])
                            && splitTriplet[0].contains(checkSplitTriplet[2])) {
                        asymmetricArray.add(tripletsArrayList.get(i) + "\t => \t" + tripletsArrayList.get(j) + "\n");
                    }
                }
            }
        }

        BufferedWriter asymmetricWriter = new BufferedWriter(new FileWriter("output/asymmetric.txt"));
        for(String symmetric: asymmetricArray) {
            asymmetricWriter.append(symmetric);
        }
        asymmetricWriter.close();

    }
}
