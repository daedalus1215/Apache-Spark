package com.apache.spark.stuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

/**
 * This class is used in the chapter late in the course where we analyse viewing figures. You can
 * ignore until then.
 */
public class MainCourseIdWithNumberOfChapters {

  @SuppressWarnings("resource")
  public static void main(String[] args) {
    System.setProperty("hadoop.home.dir", "c:/hadoop");
    Logger.getLogger("org.apache").setLevel(Level.WARN);

    SparkConf conf = new SparkConf().setAppName("startingSpark").setMaster("local[*]");
    JavaSparkContext sc = new JavaSparkContext(conf);

    // Use true to use hardcoded data identical to that in the PDF guide.
    boolean testMode = false;

    JavaPairRDD<Integer, Integer> viewData = setUpViewDataRdd(sc, testMode);
    JavaPairRDD<Integer, Integer> chapterData = setUpChapterDataRdd(sc, testMode);
    JavaPairRDD<Integer, String> titlesData = setUpTitlesDataRdd(sc, testMode);

    JavaPairRDD<Integer, Integer> chapterCount = chapterData
        .mapToPair(row -> new Tuple2<>(row._2, 1))
        .reduceByKey(Integer::sum);

    System.out.println("courseId, views");

    JavaPairRDD<Integer, Integer> chapterAndUsers = viewData
        .distinct() // lets remove any duplicate times the user same user has watched the same video
        .mapToPair(row -> new Tuple2<Integer, Integer>(row._2,
            row._1)); // we must swap so chapterId, userId - because we are going to join on the chapterIds

    JavaPairRDD<Integer, Integer> chaptersAndViews = chapterAndUsers
        .join(chapterData) // now both have chapterId as their keys, we can
        .mapToPair(row2 ->
            new Tuple2<>(row2._2,
                1)) // swap back over the (userId, courseId) tuple with the chapterId, then replace chapterId with the integer '1'.
        .reduceByKey(Integer::sum)
        .mapToPair(row3 -> new Tuple2<>(row3._1._2,
            row3._2));// now deconstruct the inner tuple, pull second value out of it and pull out the views from the outer Tuple as well.

    JavaPairRDD<Integer, Tuple2<Integer, Integer>> join = chaptersAndViews
        .join(chapterCount);
    join.foreach(d -> System.out.println(d));

    JavaPairRDD<Integer, Double> integerDoubleJavaPairRDD = join
        .mapValues(viewsOutOf -> Double.valueOf(viewsOutOf._1) / viewsOutOf._2);
    integerDoubleJavaPairRDD.foreach(d -> System.out.println(d));

    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    sc.close();
  }

  private static JavaPairRDD<Integer, String> setUpTitlesDataRdd(JavaSparkContext sc,
      boolean testMode) {

    if (testMode) {
      // (chapterId, title)
      List<Tuple2<Integer, String>> rawTitles = new ArrayList<>();
      rawTitles.add(new Tuple2<>(1, "How to find a better job"));
      rawTitles.add(new Tuple2<>(2, "Work faster harder smarter until you drop"));
      rawTitles.add(new Tuple2<>(3, "Content Creation is a Mug's Game"));
      return sc.parallelizePairs(rawTitles);
    }
    return sc.textFile("src/main/resources/viewing figures/titles.csv")
        .mapToPair(commaSeparatedLine -> {
          String[] cols = commaSeparatedLine.split(",");
          return new Tuple2<Integer, String>(new Integer(cols[0]), cols[1]);
        });
  }

  private static JavaPairRDD<Integer, Integer> setUpChapterDataRdd(JavaSparkContext sc,
      boolean testMode) {

    if (testMode) {
      // (chapterId, (courseId, courseTitle))
      List<Tuple2<Integer, Integer>> rawChapterData = new ArrayList<>();
      rawChapterData.add(new Tuple2<>(96, 1));
      rawChapterData.add(new Tuple2<>(97, 1));
      rawChapterData.add(new Tuple2<>(98, 1));
      rawChapterData.add(new Tuple2<>(99, 2));
      rawChapterData.add(new Tuple2<>(100, 3));
      rawChapterData.add(new Tuple2<>(101, 3));
      rawChapterData.add(new Tuple2<>(102, 3));
      rawChapterData.add(new Tuple2<>(103, 3));
      rawChapterData.add(new Tuple2<>(104, 3));
      rawChapterData.add(new Tuple2<>(105, 3));
      rawChapterData.add(new Tuple2<>(106, 3));
      rawChapterData.add(new Tuple2<>(107, 3));
      rawChapterData.add(new Tuple2<>(108, 3));
      rawChapterData.add(new Tuple2<>(109, 3));
      return sc.parallelizePairs(rawChapterData);
    }

    return sc.textFile("src/main/resources/viewing figures/chapters.csv")
        .mapToPair(commaSeparatedLine -> {
          String[] cols = commaSeparatedLine.split(",");
          return new Tuple2<Integer, Integer>(new Integer(cols[0]), new Integer(cols[1]));
        });
  }

  private static JavaPairRDD<Integer, Integer> setUpViewDataRdd(JavaSparkContext sc,
      boolean testMode) {

    if (testMode) {
      // Chapter views - (userId, chapterId)
      List<Tuple2<Integer, Integer>> rawViewData = new ArrayList<>();
      rawViewData.add(new Tuple2<>(14, 96));
      rawViewData.add(new Tuple2<>(14, 97));
      rawViewData.add(new Tuple2<>(13, 96));
      rawViewData.add(new Tuple2<>(13, 96));
      rawViewData.add(new Tuple2<>(13, 96));
      rawViewData.add(new Tuple2<>(14, 99));
      rawViewData.add(new Tuple2<>(13, 100));
      return sc.parallelizePairs(rawViewData);
    }

    return sc.textFile("src/main/resources/viewing figures/views-*.csv")
        .mapToPair(commaSeparatedLine -> {
          String[] columns = commaSeparatedLine.split(",");
          return new Tuple2<Integer, Integer>(new Integer(columns[0]), new Integer(columns[1]));
        });
  }
}
