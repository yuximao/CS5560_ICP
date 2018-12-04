package property_characteristics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ Yuhn on 11/15/2018
 */
public class Transitive {
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

        ArrayList<String> transitiveArray = new ArrayList<>();
        // Transitive
        for(int i = 0; i < tripletsArrayList.size(); i++) {
            String[] splitTriplet = tripletsArrayList.get(i).split("\t");
            for (int j = i + 1; j < tripletsArrayList.size(); j++) {
                String[] checkSplitTriplet = tripletsArrayList.get(j).split("\t");
                if(splitTriplet.length == 3 && checkSplitTriplet.length == 3) {
                    if (splitTriplet[1].equals(checkSplitTriplet[1])) {
                        transitiveArray.add(tripletsArrayList.get(i) + " || "
                                + tripletsArrayList.get(j) + " => "
                                + splitTriplet[0] + "\t"
                                + splitTriplet[1] + "\t"
                                + checkSplitTriplet[2] + "\n"
                        );
                    }
                }
            }
        }

        BufferedWriter transitiveWriter = new BufferedWriter(new FileWriter("output/transitive.txt"));
        for(String symmetric: transitiveArray) {
            transitiveWriter.append(symmetric);
        }
        transitiveWriter.close();

    }
}
