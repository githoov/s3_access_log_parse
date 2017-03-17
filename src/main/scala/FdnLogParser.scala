package com.githoov.FdnLogParser

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}
import com.typesafe.config.ConfigFactory

/*
 *
 * This job looks for FDN-file access logs in S3, adds structure to them, and writes the output
 * to a different S3 bucket as gzipped json.
 *
 * Example log line:
 *    d9ccd9beaaf35bc3e192707a7f0e79193414f2884eb4ba27f7037fbf30baeef githoov-ds1-customers-home [15/Feb/2017:17:05:21 +0000] 10.181.157.208 arn:aws:iam::494544507972:user/c_some_bucket_name D06E6DDDE1D88170 REST.GET.OBJECT uwn2-some_bucket/evt_new_1773865498974_1773840223122_oevkd1_177413975782_36_13970.txt "GET /uwn2-some_bucket/evt_new_177385498974_177840223122_oevk1_174139757862_36_13970.fdn HTTP/1.1" 206 - 409600 9377280 17 13 "-" "githoov/1.0" -
 *
 * Notes: 
 * 1. `sbt assembly` to build an uber jar.
 * 2. To submit this to Spark, issue the following:
 *      `bin/spark-submit --class com.githoov.FdnLogParser.FdnLogParser /path/to/FdnLogParser-assembly-1.0.jar batchInterval S3OutBucket`
 *
 */


object FdnLogParser {

  def main(argv: Array[String]): Unit = {

   if (argv.length != 2) {
      System.err.println("Please provide 2 parameters: <batch-interval> <s3-output-location>")
      System.exit(1)
    }
    
    // parse command-line arguments
    val Array(batchInterval, outPath) = argv

    // fire up spark and config classes
    val config        = new Settings(ConfigFactory.load())
    val conf          = new SparkConf().setAppName("FdnLogParser")
    val sc            = new SparkContext(conf)
    val hadoopConf    = sc.hadoopConfiguration

    // file-system configuration
    hadoopConf.set("fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    hadoopConf.set("fs.s3n.awsAccessKeyId", config.key)
    hadoopConf.set("fs.s3n.awsSecretAccessKey", config.secret)

    // fire up spark-streaming context
    val ssc = new org.apache.spark.streaming.StreamingContext(sc, Seconds(batchInterval.toLong))

    // open stream to S3
    val lines = ssc.textFileStream(config.bucket)
    
    // fire up LogParser class
    val parser = new LogParser()

    lines.foreachRDD { (rdd: RDD[String]) =>

      // get the singleton instance of SparkSession (required for toDF() method)
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._
      
      // parse line in rdd, extract values, throw into structured class, convert to dataframe
      val dataFrame = rdd.map(line => Some(parser.extractValues(line)).get).toDF()

      // append dataframe to 
      dataFrame.coalesce(1).write.mode("append").json(outPath)
    }
    
    ssc.start()
    ssc.awaitTermination()

  }

}