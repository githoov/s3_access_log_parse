package com.githoov.FdnLogParser

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf


object SparkSessionSingleton {

  @transient private var instance: SparkSession = _

  def getInstance(sparkConf: SparkConf): SparkSession = {
    if (instance == null) {
      instance = SparkSession
        .builder
        .config(sparkConf)
        .getOrCreate()
    }
    instance
  }
}