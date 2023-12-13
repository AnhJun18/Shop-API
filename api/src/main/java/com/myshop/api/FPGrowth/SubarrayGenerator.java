package com.myshop.api.FPGrowth;
import com.myshop.api.service.fpgrowth.FPGrowthService;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SubarrayGenerator {
  public static Map<String, Integer> frequentItems;
  public static Map<Set<String>, Integer> listFreq;

  public static List<String> response= new ArrayList<>();
  public static List<String> responseTransaction= new ArrayList<>();


  public static Double minConf=0.5;


  public static void generateSubarrays(List<String> input, int currentIndex, List<String> currentSubarray) {
    int n = input.size();
    if (currentIndex == n) {
      if (!currentSubarray.isEmpty() && !currentSubarray.equals(input)) {
        List<String> remainingElements = new ArrayList<>(input);
        remainingElements.removeAll(currentSubarray);
        Double kq= calSup(input)*1.0 / calSup(currentSubarray);
//        System.out.println(currentSubarray + " -> " + remainingElements +": Conf = "+ Precision.round(kq,3));
        if(kq>=minConf){
          response.add(String.format("%30s - %5s",genSpace("Luật: "+currentSubarray + " -> " + remainingElements) ," (Conf = "+ Precision.round(kq,3)+")"));
          System.out.println(String.format("%30s - %5s",genSpace("Luật: "+currentSubarray + " -> " + remainingElements) ," (Conf = "+ Precision.round(kq,3)+")"));
          FPGrowthService.hashCache.put( "_" + String.join("_", currentSubarray) + "_",remainingElements);
        }
//          response.add(String.format("%30s - %5s",genSpace("Luật: "+currentSubarray + " -> " + remainingElements) ," (Conf = "+ Precision.round(kq,3)+")"));
        System.out.println("//////////////////////////////////////");
      }
    } else {
      currentSubarray.add(input.get(currentIndex));
      generateSubarrays(input, currentIndex + 1, currentSubarray);
      currentSubarray.remove(currentSubarray.size() - 1);
      generateSubarrays(input, currentIndex + 1, currentSubarray);
    }
  }
public  static String genSpace(String str){
    int num =30 -str.length();
    String res= str;
    for (int i=0;i<num;i++){
      res+=" ";
    }
    return res;
}
  public static int calSup(List<String> input) {
    Set<String> toCheck = new HashSet<>(input);
    if(listFreq.containsKey(toCheck)){
      return listFreq.get(toCheck);
    }
    int minSup =Integer.MAX_VALUE;
    for (String item: input) {
      if(frequentItems.get(item)<minSup)
        minSup = frequentItems.get(item);
    }
    return minSup;
  }
}


