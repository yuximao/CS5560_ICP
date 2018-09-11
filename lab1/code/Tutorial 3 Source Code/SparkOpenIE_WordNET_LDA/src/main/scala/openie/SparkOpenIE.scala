package openie

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Mayanka on 27-Jun-16.
  */
object SparkOpenIE {

  def main(args: Array[String]) {
    // Configuration
    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    // For Windows Users
    System.setProperty("hadoop.home.dir", "D:\\winutils\\winutils")


    // Turn off Info Logger for Console
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val input = sc.textFile("data/1.txt").map(line => {
      //Getting OpenIE Form of the word using lda.CoreNLP

      val t=CoreNLP.returnTriplets(line)
      t
    })

//   println(CoreNLP.returnTriplets("Cancer precision medicine largely relies on knowledge about genetic aberrations in tumors and next-generation-sequencing studies have shown a high mutational complexity in many cancers. ."))
//
   println(input.collect().mkString("\n"))



  }

}
