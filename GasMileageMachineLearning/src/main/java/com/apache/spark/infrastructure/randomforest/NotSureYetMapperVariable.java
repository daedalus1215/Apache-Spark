package com.apache.spark.infrastructure.randomforest;

import static com.apache.spark.domain.randomforest.Bank.AGE;
import static com.apache.spark.domain.randomforest.Bank.BALANCE;
import static com.apache.spark.domain.randomforest.Bank.DEFAULT;
import static com.apache.spark.domain.randomforest.Bank.EDUCATION;
import static com.apache.spark.domain.randomforest.Bank.LOAN;
import static com.apache.spark.domain.randomforest.Bank.MARITAL;
import static com.apache.spark.domain.randomforest.Bank.OUTCOME;
import static com.apache.spark.domain.randomforest.Bank.PREVIOUS_OUTCOME;

import com.apache.spark.domain.randomforest.BankVariableIndicatorMapper;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class NotSureYetMapperVariable implements BankVariableIndicatorMapper<Row> {

  /**
   * We need to massage the data to be able to process it in Machine Learning. So we are creating
   * 'indicator variables' for the different possible values on columns we want to use as our
   * selected feature variables.
   *
   * @param row the row we want to check the values of to return a new row
   * @return a new row, with the values all converted to values of Double Type.
   */
  @Override
  public Row map(Row row) throws Exception {
    // Convert age to double
    final Double age = Double.valueOf(row.getString(AGE));

    // convert outcome to double
    final double outcome = row.getString(OUTCOME).equals("yes") ? 1.0 : 0.0;

    // create indicator variable for marital status
    final double single = row.getString(MARITAL).equals("single") ? 1.0 : 0.0;
    final double married = row.getString(MARITAL).equals("married") ? 1.0 : 0.0;
    final double divorced = row.getString(MARITAL).equals("divorced") ? 1.0 : 0.0;

    // create indicator variables for education
    final double primary = row.getString(EDUCATION).equals("primary") ? 1.0 : 0.0;
    final double secondary = row.getString(EDUCATION).equals("secondary") ? 1.0 : 0.0;
    final double tertiary = row.getString(EDUCATION).equals("tertiary") ? 1.0 : 0.0;

    // Convert default to double
    final double isDefault = row.getString(DEFAULT).equals("yes") ? 1.0 : 0.0;

    // Create indicator variables for previous outcome column.
    final double isPreviousFailure = row.getString(PREVIOUS_OUTCOME).equals("failure") ? 1.0 : 0.0;
    final double isPreviousOther = row.getString(PREVIOUS_OUTCOME).equals("other") ? 1.0 : 0.0;
    final double isPreviousUnknown = row.getString(PREVIOUS_OUTCOME).equals("unknown") ? 1.0 : 0.0;

    // Convert balance to double
    final Double balance = Double.valueOf(row.getString(BALANCE));

    // Convert loan to double
    final double loan = row.getString(LOAN).equals("yes") ? 1.0 : 0.0;

    return RowFactory
        .create(outcome, age, single, married, divorced, primary, secondary, tertiary, isDefault,
            balance, loan, isPreviousFailure, isPreviousOther, isPreviousUnknown);
  }
}