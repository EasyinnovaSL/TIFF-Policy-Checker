package com.easyinnova.policy_checker;

import com.easyinnova.implementation_checker.ImplementationCheckerValidator;
import com.easyinnova.implementation_checker.ValidationResult;
import com.easyinnova.implementation_checker.rules.RuleResult;
import com.easyinnova.policy_checker.ArgumentParser;
import com.easyinnova.policy_checker.PolicyChecker;
import com.easyinnova.policy_checker.model.Rules;
import com.easyinnova.tiff.reader.TiffReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Adria Llorens on 27/12/2016.
 */
public class Main {

  public static void main(String[] args) {
    // Arguments
    List<String> params = new ArrayList(Arrays.asList(args));

    ArgumentParser parser = new ArgumentParser();
    PolicyChecker policy = new PolicyChecker();

    if (!parser.parse(params)) {
      displayHelp();
    }
    if (parser.isError()){
      return;
    }

    // All OK
    String path = parser.getPath();
    try {
      TiffReader tr = new TiffReader();
      int readerRes = tr.readFile(path, false);
      switch (readerRes) {
        case -1:
          System.out.println("File '" + path + "' does not exist");
          break;
        case -2:
          System.out.println("IO Exception in file '" + path + "'");
          break;
        case 0:
          ImplementationCheckerValidator validator = new ImplementationCheckerValidator();
          Rules rules = parser.getRules();
          ValidationResult result = policy.validateRules(validator.getValidationXmlString(tr), rules);
          printResults("ERROR", result.getErrors());
          printResults("Warning", result.getWarnings(false));
          if (result.getErrors().size() == 0 && result.getWarnings(false).size() == 0){
            System.out.println("All rules OK!");
          }
          break;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void displayHelp() {
    System.out.println("Usage: policy-checker [--rule <type> <tag> <operator> <value>] filepath");
    System.out.println("");
    System.out.println("Rule specification:\n" +
        "\tType must be 'error' or 'warning'.\n" +
        "\tTag must be an accepted Tag. Use 'policy-checker --list' to see the list of accepted tags.\n" +
        "\tOperator must be 'GT' (Grather than), 'LT' (Less than) or 'EQ' (Equals).\n" +
        "\t\tExample: --rule error ImageWidth GT 500\n" +
        "\t\tExample: --rule warning ImageLength EQ 500");
  }

  private static void printResults(String type, List<RuleResult> results) {
    for (RuleResult result : results) {
      System.out.println(type);
      System.out.println("  Rule:   " + result.getRule().getDescription().getValue());
      System.out.println("  Output: " + result.getDescription());
      System.out.println();
    }
  }

}
