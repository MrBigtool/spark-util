package com.es.test

import com.spark.es.util.ElasticsearchManagerTool
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.action.search.SearchType
import org.apache.spark.SparkContext
import org.elasticsearch.spark._
import org.apache.spark.SparkConf
import org.apache.hadoop.conf.Configuration
import org.elasticsearch.hadoop.mr.EsInputFormat
import org.apache.hadoop.io.NullWritable
import org.elasticsearch.hadoop.mr.LinkedMapWritable
object Test {
  val address = "192.168.10.115,192.168.10.110,192.168.10.81"
  val clusterName = "zhiziyun"
  lazy val client = ElasticsearchManagerTool.getESClient(address, clusterName)
  def main(args: Array[String]): Unit = {
    val confs = new SparkConf()
      .setAppName("esRDDtest")
      .setMaster("local")
    confs.set("es.nodes", "192.168.10.115,192.168.10.110,192.168.10.81")
    confs.set("es.port", "9200")
    confs.set("cluster.name", clusterName)
    val sc = new SparkContext(confs)
    func2(sc)

  }
  def func1(sc: SparkContext) {
    val conf = new Configuration
    conf.set("es.nodes", "192.168.10.115,192.168.10.110,192.168.10.81")
    conf.setInt("es.port", 9200)
    conf.set("cluster.name", clusterName)
    conf.set("es.resource", "dataexchange_device_visit_store/deviceVisit")
    conf.set("es.query", "?q=_id:13__f8758896aa94__20171110");
    sc.newAPIHadoopRDD(conf, classOf[EsInputFormat[NullWritable, LinkedMapWritable]], classOf[NullWritable], classOf[LinkedMapWritable])
      .map { x => x._2 }
      .foreach(println)
  }
  def func2(sc: SparkContext) {
    val query = s"""{"query":${getQuery}}"""
    println(query)
    //val q = s"""{"query":{"match":{"_id":"http%3A%2F%2Fbbs.zhan.com%2Fthread-337326-1-1.html"}}}"""
    sc.esRDD("dataexchange_device_tags/deviceTags", query)
      .foreach(println)
  }
  /**
   * 拼接处一个query语句
   * 
   */
  def getQuery() = {
    /*QueryBuilders
    .matchQuery("_id", "9492bc7ab90b")
    .toString()*/
    QueryBuilders.regexpQuery("", "6001941c2d66")
    QueryBuilders.prefixQuery("_id", "3__")
    .toString()
    /*QueryBuilders.rangeQuery("creattime")
    .from("2018-01-03 17:37:47")
    .to("2018-01-04 17:37:47")
    .toString()*/
    /*QueryBuilders
      .andQuery(QueryBuilders.prefixQuery("_id", "9492bc7ab90b"))
      .add(QueryBuilders.matchQuery("_id", "9492bc7ab90b"))
      .add(QueryBuilders.rangeQuery("creattime").from("2018-01-03 17:37:47").to("2018-01-04 17:37:47"))
      .toString()*/
  }

  def esTest() {
    val r = client.prepareGet("sdr_urlinfo", "urlinfo", "abcedsa.afaf")
    println(r.get.getSource)
    val response = client
      .prepareSearch("sdr_urlinfo")
      .setTypes("urlinfo")
      //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
      .setQuery(QueryBuilders.prefixQuery("url", "abcedsa.afaf")) // Query
      //.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
      .setFrom(0)
      .setSize(60)
      .setExplain(true)
      .get();
    println(response)

  }
}