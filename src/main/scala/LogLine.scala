package com.githoov.FdnLogParser

case class LogLine (
  owner: String,
  bucket: String,
  created_at: String,
  ip: String,
  requester: String,
  request_id: String,
  operation: String,
  key: String,
  uri: String,
  status: Integer,
  error: String,
  bytes_sent: Integer,
  object_size: Integer,
  total_time: Integer,
  turnaround_time: Integer,
  referrer: String,
  user_agent: String,
  version_id: String
)