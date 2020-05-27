package com.rachit.streaming

import org.apache.spark.sql.SparkSession

object SocketStreaming {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    val lines = spark.readStream.format("socket").option("host", "localhost").option("port", 9999).load()

    val words = lines.as[String].flatMap(_.split(" "))

    val wordCounts = words.groupBy("value").count()

    val query = wordCounts.writeStream.outputMode("complete").format("console").start()

    query.awaitTermination()
  }

}
