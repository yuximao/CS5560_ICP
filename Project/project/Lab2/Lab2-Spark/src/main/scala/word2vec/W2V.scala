package word2vec

import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.rdd.RDD


/**
  * Created by Mayanka on 19-06-2017.
  */
object W2V {
  def main(args: Array[String]): Unit = {

    // Change input path of abstracts here
    val inputPath = "mental_illness_abstracts"

    // Path containing top TF_IDF words
    val tf_idfPath = "data/TF_IDF/wordStats/"

    System.setProperty("hadoop.home.dir", "C:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
      .set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")

    val sc = new SparkContext(sparkConf)

    //Reading the Text File
    val documents = sc.wholeTextFiles(inputPath, 10)
    val abstracts = documents.map(abs => {
      abs._2
    }).cache()

    // Stop words
    val stopwords = sc.textFile("data/stopwords.txt").collect()
    val stopWordBroadCast = sc.broadcast(stopwords)

    //Getting the words from the text files, remove stop words.
    val abstractsWords = abstracts.map(f => {
      val words = f.split(" ").filter(!stopWordBroadCast.value.contains(_))
      words.toSeq
    })

    // Get lemmatized words from the abstracts
    val lemWords = abstracts
        .map { content => {
          val lemmatised = CoreNLP.returnLemma(content)
          val splitString = lemmatised.split(" ")
          splitString.toSeq
        }}

    // Specify paths containing top TF-IDF words
    val topWordsPath = tf_idfPath.toString + "topWords.txt"
    val topLemPath = tf_idfPath.toString + "topLemWords.txt"

    // Specify folders for the Models
    val modelFolder = new File("output/myModels/myWordsModel")
    val modelFolderLem = new File("output/myModels/myLemModel")
    // Specify output folder for the Word2Vec results
    val topWordsWord2VecPath = "data/Word2Vec/topWordsWord2Vec.txt"
    val topLemWord2VecPath = "data/Word2Vec/topLemWord2Vec.txt"

    doWord2Vec(abstractsWords, modelFolder, topWordsPath, sc, topWordsWord2VecPath)
    doWord2Vec(lemWords, modelFolderLem, topLemPath, sc, topLemWord2VecPath)
  }

  def doWord2Vec(rdd: RDD[Seq[String]], modelPath: File, topWordsPath: String, sc: SparkContext, outputPath: String): Unit = {
    if (modelPath.exists()) {
      val topWordsLines = Source.fromFile(topWordsPath).getLines()
      val topWords = topWordsLines.map(line => {
        val temp = line.split(",")
        temp(0)
      })

      val sameModel = Word2VecModel.load(sc, modelPath.toString)

      val outputWriter = new BufferedWriter(new FileWriter(outputPath))

      topWords.foreach(word => {
        val synonyms = sameModel.findSynonyms(word, 30)

        for ((synonym, cosineSimilarity) <- synonyms) {
          println(word + s": $synonym $cosineSimilarity")
          outputWriter.write(word + s"\t$synonym\t$cosineSimilarity" + "\n")
        }
      })

      outputWriter.close()

      sameModel.getVectors.foreach(f => println(f._1 + ":" + f._2.length))
    }
    else {
      val word2vec = new Word2Vec().setVectorSize(200).setMinCount(4)

      val topWordsLines = Source.fromFile(topWordsPath).getLines()
      val topWords = topWordsLines.map(line => {
        val temp = line.split(",")
        temp(0)
      })

      val model = word2vec.fit(rdd)

      val outputWriter = new BufferedWriter(new FileWriter(outputPath))

      topWords.foreach(word => {
        val synonyms = model.findSynonyms(word, 20)

        for ((synonym, cosineSimilarity) <- synonyms) {
          println(word + s": $synonym $cosineSimilarity")
          outputWriter.write(word + s"\t$synonym\t$cosineSimilarity" + "\n")
        }
      })

      outputWriter.close()

      model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))

      // Save and load model
      model.save(sc, modelPath.toString)

    }



  }
}
