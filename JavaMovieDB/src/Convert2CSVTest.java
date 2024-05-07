import statistics.*;
import util.Convert2CSV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Convert2CSVTest {
    public static void main(String[] args) {
//        CSVConvertorFunctionalityTest();
//        GDAPerYearDataTest();
//        GDAPerYearTest();
//        ACAPerYearDataTest();
//        ACAPerYearTest();
//        RIAPerYearDataTest();
//        RIAPerYearTest()
//        OLAPerYearDataTest();
//        OLAPerYearTest();
//        RBRIAPerYearDataTest();
//        RBRIAPerYearTest();
    }

    public static void CSVConvertorFunctionalityTest() {
        List<List<String>> lines = new ArrayList<>();
        List<String> line = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        List<String> anotherLine = new ArrayList<>(Arrays.asList("E", "F", "G", "H"));

        lines.add(line);
        lines.add(anotherLine);

        Convert2CSV.ConvertToCSV(lines, "test/0_csv_test.csv");
    }

    public static void GDAPerYearDataTest() {
        new GDAPerYear().GenresDistributionByYear().forEach(System.out::println);
    }

    public static void GDAPerYearTest() {
        Convert2CSV.ConvertToCSV(new GDAPerYear().GenresDistributionByYear(), "test/1_gdaPerYear_test.csv");
    }

    public static void ACAPerYearDataTest() {
        new ACAPerYear().ActorContributionByYear().forEach(System.out::println);
    }

    public static void ACAPerYearTest() {
        Convert2CSV.ConvertToCSV(new ACAPerYear().ActorContributionByYear(), "test/2_acaPerYear_test.csv");
    }

    public static void RIAPerYearDataTest() {
        new RIAPerYear().RuntimeIntervalsByYear().forEach(System.out::println);
    }

    public static void RIAPerYearTest() {
        Convert2CSV.ConvertToCSV(new RIAPerYear().RuntimeIntervalsByYear(), "test/3_riaPerYear_test.csv");
    }

    public static void OLAPerYearDataTest() {
        new OLAPerYear().OriginalLanguagePerYear().forEach(System.out::println);
    }

    public static void OLAPerYearTest() {
        Convert2CSV.ConvertToCSV(new OLAPerYear().OriginalLanguagePerYear(), "test/4_olaPerYear_test.csv");
    }

    public static void RBRIAPerYearDataTest() {
        new RBRIAPerYear().RevenueBudgetRateIntervalsPerYear().forEach(System.out::println);
    }

    public static void RBRIAPerYearTest() {
        Convert2CSV.ConvertToCSV(new RBRIAPerYear().RevenueBudgetRateIntervalsPerYear(), "test/5_rbriaPerYear_test.csv");
    }
}
