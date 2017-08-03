/**
 * <h1>Main.java</h1> <p> This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version; or,
 * at your choice, under the terms of the Mozilla Public License, v. 2.0. SPDX GPL-3.0+ or MPL-2.0+.
 * </p> <p> This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License and the Mozilla Public License for more details. </p>
 * <p> You should have received a copy of the GNU General Public License and the Mozilla Public
 * License along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>
 * and at <a href="http://mozilla.org/MPL/2.0">http://mozilla.org/MPL/2.0</a> . </p> <p> NB: for the
 *  statement, include Easy Innova SL or other company/Person contributing the code. </p> <p>
 * 2015 Easy Innova, SL </p>
 */

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
