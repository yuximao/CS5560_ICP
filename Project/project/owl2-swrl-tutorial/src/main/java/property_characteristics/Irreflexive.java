package property_characteristics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJ Yuhn on 11/15/2018
 */
public class Irreflexive {
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

        ArrayList<String> irreflexiveArray = new ArrayList<>();
        // Irreflexive
        for(int i = 0; i < tripletsArrayList.size(); i++) {
            String[] splitTriplet = tripletsArrayList.get(i).split("\t");
            for (int j = i + 1; j < tripletsArrayList.size(); j++) {
                String[] checkSplitTriplet = tripletsArrayList.get(j).split("\t");
                if(splitTriplet.length == 3 && checkSplitTriplet.length == 3) {
                    if (checkSplitTriplet[0].equals(splitTriplet[0]) && checkSplitTriplet[1].equals(splitTriplet[1])
                            && checkSplitTriplet[2].equals(splitTriplet[0])) {
                        irreflexiveArray.add(tripletsArrayList.get(i) + "\t => \t" + tripletsArrayList.get(j) + "\n");
                    }
                }
            }
        }

        BufferedWriter irreflexiveWriter = new BufferedWriter(new FileWriter("output/irreflexive.txt"));
        for(String symmetric: irreflexiveArray) {
            irreflexiveWriter.append(symmetric);
        }
        irreflexiveWriter.close();

    }
}
