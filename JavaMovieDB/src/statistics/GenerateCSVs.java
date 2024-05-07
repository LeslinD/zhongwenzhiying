package statistics;

import util.Convert2CSV;

public class GenerateCSVs {
    public static void main(String[] args) {
        Convert2CSV.ConvertToCSV(new GDAPerYear().GenresDistributionByYear(), "csv/1_gdaPerYear.csv");
        Convert2CSV.ConvertToCSV(new ACAPerYear().ActorContributionByYear(), "csv/2_acaPerYear.csv");
        Convert2CSV.ConvertToCSV(new RIAPerYear().RuntimeIntervalsByYear(), "csv/3_riaPerYear.csv");
        Convert2CSV.ConvertToCSV(new OLAPerYear().OriginalLanguagePerYear(), "csv/4_olaPerYear.csv");
        Convert2CSV.ConvertToCSV(new RBRIAPerYear().RevenueBudgetRateIntervalsPerYear(), "csv/5_rbriaPerYear.csv");
    }
}
