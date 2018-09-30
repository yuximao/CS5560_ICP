import org.apache.spark.{SparkConf, SparkContext}
import java.io.{BufferedWriter, FileWriter}

object bionlpscala{
  def main(args: Array[String]): Unit = {

    // For Windows Users
    System.setProperty("hadoop.home.dir", "D:\\winutils")
    // Configuration
    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)

    //val input=sc.textFile("data/abstracts",minPartitions = 10)D




    val input=sc.textFile("examples_data/ex.pmid")

    val wc=input.map(id=>{
      val meWords = RESTClientGet.returnMedical(id).mkString("\n")
      meWords
    }).cache()

    wc.saveAsTextFile("output")
    val o=wc.collect()
    //val medicalWordsWriter = new BufferedWriter(new FileWriter("finalData/medWords.txt"))

    o.foreach(list => {
      //medicalWordsWriter.append(list)
      print(list)
    })

    //medicalWordsWriter.close()



  }

}