package com.githoov.FdnLogParser

class LogParser extends java.io.Serializable {

  val owner, bucket, requester, request_id, key, error, referrer, user_agent, version_id = "(\\S+)"
  val created_at = "(?:\\[)(.*)(?:\\])"
  val ip         = "([0-9\\.]+)"
  val operation  = "([a-zA-Z\\_\\.]+)"
  val uri        = "(?:[A-Z\\\"]+)(?:\\s+)(.*)(?:\\s+)(?:\\S+\\\")"
  val status, bytes_sent, object_size, total_time, turnaround_time = "([0-9]+)"

  val lineMatch = s"$owner\\s+$bucket\\s+$created_at\\s+$ip\\s+$requester\\s+$request_id\\s+$operation\\s+$key\\s+$uri\\s+$status\\s+$error\\s+$bytes_sent\\s+$object_size\\s+$total_time\\s+$turnaround_time\\s+$referrer\\s+$user_agent\\s+$version_id".r

  def extractValues(line: String): Option[LogLine] = {
    line match {
      case lineMatch(owner, bucket, created_at, ip, requester, request_id, operation, key, uri, status, error, bytes_sent, object_size, total_time, turnaround_time, referrer, user_agent, version_id, _*) 
        => return Option(LogLine(owner, bucket, created_at, ip, requester, request_id, operation, key, uri, status.toInt, error, bytes_sent.toInt, object_size.toInt, total_time.toInt, turnaround_time.toInt, referrer, user_agent, version_id))
      case _ 
        =>  None
    }
  }

}