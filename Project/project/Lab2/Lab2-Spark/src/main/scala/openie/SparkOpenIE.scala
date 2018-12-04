package openie

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable

/**
  * Created by Mayanka on 27-Jun-16.
  */
object SparkOpenIE {

  def main(args: Array[String]) {

    // For Windows Users
    System.setProperty("hadoop.home.dir", "C:\\winutils")

    // Configuration
    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]").set("spark.executor.memory", "4g").set("spark.driver.memory", "2g")

    val sc = new SparkContext(sparkConf)

    val inputf = sc.wholeTextFiles("mental_illness_abstracts", 50)
    val input = inputf.map(abs => {
      abs._2
    }).cache()

//    val allTriplets = input.flatMap(abstracts=> {abstracts.split("\n")}).map(singleAbs => {
//      CoreNLP.returnSentences(singleAbs)
//    }).map(sentences => {
//      CoreNLP.returnTriplets(sentences)
//    }).cache()

    val allTriplets = input.map(singleAbs => {
      CoreNLP.returnLongestTriplet(singleAbs)
    }).cache()

    allTriplets.saveAsTextFile("output/triplets")

    val collect = allTriplets.collect()

    val predicateWriter = new BufferedWriter(new FileWriter("data/ontology/predicates/predicates.txt"))
    val subjectWriter = new BufferedWriter(new FileWriter("data/ontology/subjects/subjects.txt"))
    val objectWriter = new BufferedWriter(new FileWriter("data/ontology/objects/objects.txt"))
    val allTripletsWriter = new BufferedWriter(new FileWriter("data/ontology/triplets/allTriplets.txt"))

    var counter = 0
    collect.foreach(triplets => {
      val tripletWriter = new BufferedWriter(new FileWriter("data/triplets/triplets_abstract_" + counter + ".txt"))
      val separateTriplets = triplets.split("\n")

      separateTriplets.foreach(triplet => {
        println(triplet)
        val splitTriplet = triplet.split("\t")
        if (splitTriplet.size > 2) {
          predicateWriter.append(splitTriplet(1)).append("\n")
          subjectWriter.append(splitTriplet(0)).append("\n")
          objectWriter.append(splitTriplet(2)).append("\n")
          tripletWriter.append(triplet).append("\n")
          allTripletsWriter.append(triplet).append("\n")
        }
      })
      tripletWriter.close()
      counter = counter + 1
      })
    predicateWriter.close()
    objectWriter.close()
    subjectWriter.close()
    allTripletsWriter.close()
  }
}
