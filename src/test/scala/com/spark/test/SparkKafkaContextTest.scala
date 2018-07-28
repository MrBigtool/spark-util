package com.spark.test

import org.apache.spark.core.SparkKafkaContext
import org.apache.spark.SparkConf
import kafka.serializer.StringDecoder
import org.apache.spark.rdd.RDD
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
object SparkKafkaContextTest {
  /**
   * 离线方式 读取kafka数据
   * 测试 SparkKafkaContext类
   */
  def main(args: Array[String]): Unit = {
    val groupId = "dataflow-tg"
     val topics = Set("TG_TOPIC")
    val kp = SparkKafkaContext.getKafkaParam(
      brokers,
      groupId,
      "earliest", // last/consum/custom/earliest
      "earliest" //wrong_from
    )
    val skc = new SparkKafkaContext(kp, new SparkConf()
      .setMaster("local")
      .set(SparkKafkaContext.MAX_RATE_PER_PARTITION, "1")
      .setAppName("SparkKafkaContextTest"))
      println( kp.+((SparkKafkaContext.AUTO_OFFSET_RESET_CONFIG,"none")).toMap[String, Object].asJava)
   
    val kafkadataRdd = skc.kafkaRDD[String, String](topics)
    kafkadataRdd.foreach(println)

    //RDD.rddToPairRDDFunctions(kafkadataRdd)
    //kafkadataRdd.reduceByKey(_+_)
    // kafkadataRdd.map(f)

    //kafkadataRdd.updateOffsets(kp)

  }
}