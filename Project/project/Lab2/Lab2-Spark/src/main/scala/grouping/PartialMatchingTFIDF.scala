package grouping

import java.io.{BufferedWriter, FileWriter}

import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by DJ on 11/6/2018.
  */
object PartialMatchingTFIDF {
  def main(args: Array[String]): Unit = {

    // Path containing top selected TF_IDF
    val tf_idfPath = "data/selectedTFIDF/topWordsWord2Vec.txt"

    System.setProperty("hadoop.home.dir", "C:\\winutils")

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")
      .set("spark.driver.memory", "4g").set("spark.executor.memory", "4g")

    val sc = new SparkContext(sparkConf)

    val tfIDFFile = sc.textFile(tf_idfPath)

    val separateTF_IDF = tfIDFFile.map(line => {
      val splitLine = line.split("\t")
      val newTabs = splitLine(0) + "\t" + splitLine(1)
      newTabs
    }).cache()

    val tfCol = separateTF_IDF.collect()
    val tfDist = separateTF_IDF.map(line => {
      val splitLine = line.split("\t")
      splitLine(0)
    }).distinct().collect()

    val distinctCategoriesWriter = new BufferedWriter(new FileWriter("data/ontology/categories/customCategories.txt"))
    tfDist.foreach(line => {
      if (!line.equals(""))
        distinctCategoriesWriter.append(line).append("\n")
    })
    distinctCategoriesWriter.close()


    val tfIDFMap = scala.collection.mutable.Map[String, ArrayBuffer[String]]()

    tfCol.foreach(line => {
      val splitLine = line.split("\t")
      if (tfIDFMap.keySet.contains(splitLine(0))) {
        tfIDFMap(splitLine(0)).append(splitLine(1))
      }
      else {
        tfIDFMap.put(splitLine(0), new ArrayBuffer[String]())
        tfIDFMap(splitLine(0)).append(splitLine(1))
      }
    })

    val objectsCategorized = sc.textFile("data/ontology/objects/objects.txt").map(line => {
      val categorized = new mutable.StringBuilder()
      var anyTFIDFfound = false
      tfIDFMap.foreach { case (key, value) =>
        var tfidfFound = false
        if (line.contains(key)) {
          tfidfFound = true
        }
        else {
          value.foreach(word => {
            if (line.contains(word)) {
              tfidfFound = true
            }
          })
        }
        if (tfidfFound) {
          categorized.append(line).append("\t").append(key).append("\n")
          anyTFIDFfound = true
        }
      }
      if (!anyTFIDFfound) {
        categorized.append(line).append("\t").append("Other")
      }
      categorized.toString()
    })

    val subjectsCategorized = sc.textFile("data/ontology/subjects/subjects.txt").map(line => {
      val categorized = new mutable.StringBuilder()
      var anyTFIDFfound = false
      tfIDFMap.foreach { case (key, value) =>
        var tfidfFound = false
        if (line.contains(key)) {
          tfidfFound = true
        }
        else {
          value.foreach(word => {
            if (line.contains(word)) {
              tfidfFound = true
            }
          })
        }
        if (tfidfFound) {
          categorized.append(line).append("\t").append(key).append("\n")
          anyTFIDFfound = true
        }
      }
      if (!anyTFIDFfound) {
        categorized.append(line).append("\t").append("Other")
      }
      categorized.toString()
    })

    val subjectCategorizedWriter = new BufferedWriter(new FileWriter("data/ontology/subjects/customGroupSubjects.txt"))
    val objectsCategorizedWriter = new BufferedWriter(new FileWriter("data/ontology/objects/customGroupObjects.txt"))

    subjectsCategorized.collect().foreach(subject => {
      val splitSubjectLines = subject.split("\n")
      splitSubjectLines.foreach(line => {
        if (!line.equals(""))
          subjectCategorizedWriter.append(line).append("\n")
      })
    })
    subjectCategorizedWriter.close()

    objectsCategorized.collect().foreach(objects => {
      val splitObjectLines = objects.split("\n")
      splitObjectLines.foreach(line => {
        if (!line.equals(""))
          objectsCategorizedWriter.append(line).append("\n")
      })
    })
    objectsCategorizedWriter.close()

  }
}
