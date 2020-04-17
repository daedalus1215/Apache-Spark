package com.apache.spark.application;

import com.apache.spark.infrastructure.SparkConnection;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Main_KMeansClustering {
    private final static SparkConnection sparkConnection = new SparkConnection.SparkConnectionBuilder().build();
    private final static SparkSession sparkSession = sparkConnection.getSparkSession();

    public static void main(String[] args) {
        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);

        // ******************** Setup ************************************* //
        final Dataset<Row> autoDF = sparkSession
                .read()
                .option("header", "true")
                .csv("src/main/resources/auto-miles-per-gallon.csv");
        autoDF.show(10);
        autoDF.printSchema();

        // ******************** Cleanse Data ************************************* //
        StructType autoSchema = DataTypes
                .createStructType(new StructField[]{
                        DataTypes.createStructField("DOORS", DataTypes.DoubleType, false),
                        DataTypes.createStructField("BODY", DataTypes.DoubleType, false),
                        DataTypes.createStructField("HP", DataTypes.DoubleType, false),
                        DataTypes.createStructField("RPM", DataTypes.DoubleType, false),
                        DataTypes.createStructField("MPG", DataTypes.DoubleType, false)
                });



    }
}
