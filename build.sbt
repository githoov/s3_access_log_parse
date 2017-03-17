lazy val root = (project in file(".")).
  settings(
    name := "FdnLogParser",
    version := "1.0",
    scalaVersion := "2.11.0",
    mainClass in Compile := Some("FdnLogParser")
  )

libraryDependencies ++= Seq(
  "com.typesafe"           %  "config"                            % "1.2.1",
  "org.apache.hadoop"      %  "hadoop-aws"                        % "2.6.0",
  "org.apache.spark"       %% "spark-core"                        % "2.0.0"   % "provided",
  "org.apache.spark"       %% "spark-sql"                         % "2.0.0"   % "provided",
  "org.apache.spark"       %% "spark-streaming"                   % "2.0.0"   % "provided"
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
   {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
   }
}