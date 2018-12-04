package word2vec

import java.io.{BufferedWriter, FileWriter}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF}

import scala.collection.immutable.HashMap

/**
  * Created by Mayanka on 19-06-2017.
  */
object TF_IDF {
  def main(args: Array[String]): Unit = {

    // Change input path here
    val inputPath = "mental_illness_abstracts"
    // Change output path here
    val outputPath = "data/TF_IDF/wordStats"


    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    //Reading the Text File
    val documents = sc.wholeTextFiles(inputPath, 10)
    val abstracts = documents.map(abs => {
      abs._2
    }).cache()

    // Stop words
    val stopwords = sc.textFile("data/stopwords.txt").collect()
    val stopWordBroadCast = sc.broadcast(stopwords)

    //Getting the Lemmatised form of the words from text files
    val abstractsLem = abstracts.map(f => {
      val lemmatised = CoreNLP.returnLemma(f)
      val splitString = lemmatised.split(" ").filter(!stopWordBroadCast.value.contains(_))
      splitString.toSeq
    })

    //Getting the words from the text files, remove stop words.
    val abstractsWords = abstracts.map(f => {
      val words = f.split(" ").filter(!stopWordBroadCast.value.contains(_))
      words.toSeq
    })

    // To save as Output to textfile, uncomment below.
//    abstractsLem.saveAsTextFile("output/outputLem")
//    abstractsWords.saveAsTextFile("output/outputWords")

    //Creating an object of HashingTF Class
    val hashingTFLem = new HashingTF()
    val hashingTFWords = new HashingTF()

    //Creating Term Frequency of the lemmatized words
    val tfLem = hashingTFLem.transform(abstractsLem)
    tfLem.cache()

    //Creating Term Frequency of the words
    val tfWords = hashingTFWords.transform(abstractsWords)
    tfWords.cache()

    val idfLem = new IDF().fit(tfLem)
    val idfWords = new IDF().fit(tfWords)

    //Creating Inverse Document Frequency
    val tfidfLem = idfLem.transform(tfLem)
    val tfidfWords = idfWords.transform(tfWords)

    // Obtain Lem values
    val tfidfvaluesLem = tfidfLem.flatMap(f => {
      val ff = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    // Obtain Lem indices
    val tfidfindexLem = tfidfLem.flatMap(f => {
      val ff = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    // Obtain Words values
    val tfidfvaluesWords = tfidfWords.flatMap(f => {
      val ff = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    // Obtain Words indices
    val tfidfindexWords = tfidfWords.flatMap(f => {
      val ff = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    val tfidfDataLem = tfidfindexLem.zip(tfidfvaluesLem)
    val tfidfDataWords = tfidfindexWords.zip(tfidfvaluesWords)

    var hmLem = new HashMap[String, Double]
    var hmWords = new HashMap[String, Double]

    tfidfDataLem.collect().foreach(f => {
      hmLem += f._1 -> f._2.toDouble
    })

    tfidfDataWords.collect().foreach(f => {
      hmWords += f._1 -> f._2.toDouble
    })

    val mappLem = sc.broadcast(hmLem)
    val mappWords = sc.broadcast(hmWords)

    val documentDataLem = abstractsLem.flatMap(_.toList)
    val ddLem = documentDataLem.map(f => {
      val i = hashingTFLem.indexOf(f)
      val h = mappLem.value
      (f, h(i.toString))
    })

    val documentDataWords = abstractsWords.flatMap(_.toList)
    val ddWords = documentDataWords.map(f => {
      val i = hashingTFWords.indexOf(f)
      val h = mappWords.value
      (f, h(i.toString))
    })


    val topLemWriter = new BufferedWriter(new FileWriter(outputPath + "/topLemWords.txt"))
    val topWordWriter = new BufferedWriter(new FileWriter(outputPath + "/topWords.txt"))

    val dd1 = ddLem.distinct().sortBy(_._2, false)
    dd1.take(40).foreach(f => {
      println(f)
      topLemWriter.write(f._1 + ", " + f._2 + "\n")
    })
    topLemWriter.close()

    val dd2 = ddWords.distinct().sortBy(_._2, false)
    dd2.take(40).foreach(f => {
      println(f)
      topWordWriter.write(f._1 + ", " + f._2 + "\n")
    })
    topWordWriter.close()
  }
}
