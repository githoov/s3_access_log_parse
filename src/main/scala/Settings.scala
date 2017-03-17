package com.githoov.FdnLogParser

import com.typesafe.config.Config

class Settings(config: Config) {
  val key = config.getString("aws.key")
  val secret = config.getString("aws.secret")
  val bucket = config.getString("aws.bucket")
}