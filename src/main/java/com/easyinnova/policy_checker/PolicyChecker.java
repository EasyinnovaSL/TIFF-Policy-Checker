/**
 * <h1>Validator.java</h1> <p> This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version; or, at your
 * choice, under the terms of the Mozilla Public License, v. 2.0. SPDX GPL-3.0+ or MPL-2.0+. </p>
 * <p> This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the Mozilla Public License for more details. </p> <p> You should
 * have received a copy of the GNU General Public License and the Mozilla Public License along with
 * this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>
 * and at <a href="http://mozilla.org/MPL/2.0">http://mozilla.org/MPL/2.0</a> . </p> <p> NB: for the
 * © statement, include Easy Innova SL or other company/Person contributing the code. </p> <p> ©
 * 2015 Easy Innova, SL </p>
 *
 * @author Víctor Muñoz Solà
 * @version 1.0
 * @since 23/7/2015
 */

package com.easyinnova.policy_checker;

import com.easyinnova.implementation_checker.ValidationResult;
import com.easyinnova.implementation_checker.Validator;
import com.easyinnova.implementation_checker.model.TiffValidationObject;
import com.easyinnova.implementation_checker.rules.RuleResult;
import com.easyinnova.implementation_checker.rules.model.AssertType;
import com.easyinnova.implementation_checker.rules.model.ImplementationCheckerObjectType;
import com.easyinnova.implementation_checker.rules.model.RuleType;
import com.easyinnova.implementation_checker.rules.model.RulesType;
import com.easyinnova.policy_checker.model.Field;
import com.easyinnova.policy_checker.model.Rule;
import com.easyinnova.policy_checker.model.Rules;
import com.easyinnova.tiff.model.types.IccProfile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by easy on 11/03/2016.
 */
public class PolicyChecker {

  public PolicyChecker() {
  }

  public ValidationResult filterISOs(ValidationResult result, List<String> removedRulesId) {
    for (RuleResult ruleResult : result.getResult()) {
      ruleResult.setRelaxed(removedRulesId.contains(ruleResult.getRule().getId()));
    }
    return result;
  }

  public ValidationResult validateRules(String xmlTiffModel, Rules rules) {
    try {
      ImplementationCheckerObjectType rulesObj = new ImplementationCheckerObjectType();
      RulesType rulesType = new RulesType();
      int index = 1;
      for (Rule rule : rules.getRules()) {
        RuleType ruleObj = new RuleType();
        if (rule.getWarning()) ruleObj.setLevel("warning");
        else ruleObj.setLevel("error");
        ruleObj.setId("pol-" + index++);
        ruleObj.setContext("ifd[class=image]");
        String tag = rule.getTag();
        boolean stringValue = false;
        if (tag.equals("ByteOrder")) {
          tag = "byteOrder";
          ruleObj.setContext("tiffValidationObject");
          stringValue = true;
        }
        if (tag.equals("DPI")) {
          tag = "dpi";
          stringValue = true;
        }
        if (tag.equals("EqualXYResolution")) {
          tag = "equalXYResolution";
          stringValue = true;
        }
        if (tag.equals("NumberImages")) {
          ruleObj.setContext("tiffValidationObject");
          tag = "numberImages";
        }
        AssertType assertObj = new AssertType();
        String operator = rule.getOperator();
        if (operator.equals("=")) operator = "==";
        if (rule.getValue().contains(",")) stringValue = true;
        String sTest = "";
        ArrayList<String> values = new ArrayList<>();
        for (String value : rule.getValue().split(";")) {
          String value2 = value;
          if (tag.equals("Compression"))
            value2 = PolicyConstants.compressionCode(value) + "";
          if (tag.equals("Photometric")) {
            value2 = PolicyConstants.photometricCode(value) + "";
            if (value2.equals("1")) values.add("0");
          }
          if (tag.equals("Planar")) {
            value2 = PolicyConstants.planarCode(value) + "";
          }
          values.add(value2);
        }
        for (String value : values) {
          if (tag.equals("Photometric")) tag = "PhotometricInterpretation";
          if (tag.equals("Planar")) tag = "PlanarConfiguration";
          if (sTest.length() > 0) sTest += " || ";
          if (tag.equals("byteOrder")) sTest += "{" + tag + " " + operator + " ";
          else if (tag.equals("numberImages")) sTest += "{" + tag + " " + operator + " ";
          else if (tag.equals("dpi")) sTest += "{" + tag + " " + operator + " ";
          else if (tag.equals("equalXYResolution")) sTest += "{" + tag + " " + operator + " ";
          else sTest += "{tags.tag[name=" + tag + "] " + operator + " ";
          if (stringValue) sTest += "'";
          sTest += value;
          if (stringValue) sTest += "'";
          sTest += "}";
        }
        assertObj.setTest(sTest);
        RuleType.Description desc = new RuleType.Description();
        String op = rule.getOperator();
        desc.setValue(rule.getTag() + " " + op + " " + rule.getValue());
        ruleObj.setDescription(desc);
        if (!rule.getWarning()) assertObj.setValue("Invalid " + rule.getTag());
        else assertObj.setValue("Warning on " + rule.getTag());
        ruleObj.setAssert(assertObj);
        rulesType.getRule().add(ruleObj);
      }
      rulesObj.getRules().add(rulesType);

      TiffValidationObject model = Validator.createValidationObjectFromXml(xmlTiffModel);

      Validator validation = new Validator();
      validation.validate(model, rulesObj, false);

      return validation.getResult();
    } catch (Exception e) {
      return null;
    }
  }

  public static List<Field> getPolicyCheckerFields() {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      Element policyElement = PolicyChecker.getPolicyCheckerOptions(doc);
      ArrayList<Field> fields = new ArrayList<Field>();
      NodeList nodelist = policyElement.getElementsByTagName("field");
      for (int i = 0; i < nodelist.getLength(); i++) {
        Node node = nodelist.item(i);
        NodeList childs = node.getChildNodes();
        Field field = new Field(childs);
        fields.add(field);
      }
      return fields;
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  public static Element getPolicyCheckerOptions(Document doc) {
    // Policy checker
    Element policyChecker = doc.createElement("policyCheckerOptions");
    Element fields = doc.createElement("fields");
    policyChecker.appendChild(fields);
    // Image Width
    Element field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "ImageWidth");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Image Width in pixels");
    addElement(doc, field, "operators", ">,<,=");
    // Image Height
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "ImageLength");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Image Height in pixels");
    addElement(doc, field, "operators", ">,<,=");
    // Pixel Density
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "PixelDensity");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Pixels per centimeter");
    addElement(doc, field, "operators", ">,<,=");
    // Number of images
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "NumberImages");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Number of images in the TIFF");
    addElement(doc, field, "operators", ">,<,=");
    // BitDepth
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "BitDepth");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Number of bits per pixel component");
    addElement(doc, field, "operators", ">,<,=");
    addElement(doc, field, "values", "1,2,4,8,16,32,64");
    // DPI
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "DPI");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Dots per Inch");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", "Even,Uneven");
    // Extra Channels
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "ExtraChannels");
    addElement(doc, field, "type", "integer");
    addElement(doc, field, "description", "Extra pixel components");
    addElement(doc, field, "operators", ">,<,=");
    // XY Resolution
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "EqualXYResolution");
    addElement(doc, field, "type", "boolean");
    addElement(doc, field, "description", "XResolution equal to YResolution");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", "False,True");
    // BlankPage
    //field = doc.createElement("field");
    //fields.appendChild(field);
    //addElement(doc, field, "name", "BlankPage");
    //addElement(doc, field, "type", "boolean");
    //addElement(doc, field, "description", "Page devoid of content (completely white)");
    //addElement(doc, field, "operators", "=");
    //addElement(doc, field, "values", "False,True");
    // NumberBlankPage
    //field = doc.createElement("field");
    //fields.appendChild(field);
    //addElement(doc, field, "name", "NumberBlankImages");
    //addElement(doc, field, "type", "integer");
    //addElement(doc, field, "description", "Number of Blank Pages");
    //addElement(doc, field, "operators", ">,<,=");
    // Compression
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "Compression");
    addElement(doc, field, "type", "string");
    addElement(doc, field, "description", "Compression scheme");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", PolicyConstants.compressionName(1) + "," + PolicyConstants.compressionName(2) + "," + PolicyConstants.compressionName(32773) + "," + PolicyConstants.compressionName(3) + "," + PolicyConstants.compressionName(4) + "," + PolicyConstants.compressionName(5) + "," + PolicyConstants.compressionName(6) + "," + PolicyConstants.compressionName(7) + "," + PolicyConstants.compressionName(8) + "," + PolicyConstants.compressionName(9) + "," + PolicyConstants.compressionName(10) + "");
    // Photometric
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "Photometric");
    addElement(doc, field, "type", "string");
    addElement(doc, field, "description", "Color space of the image data");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", PolicyConstants.photometricName(1) + "," + PolicyConstants.photometricName(2) + "," + PolicyConstants.photometricName(3) + "," + PolicyConstants.photometricName(4) + "," + PolicyConstants.photometricName(5) + "," + PolicyConstants.photometricName(6) + "," + PolicyConstants.photometricName(10) + "");
    // Planar
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "Planar");
    addElement(doc, field, "type", "string");
    addElement(doc, field, "description", "How the pixels components are stored");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", PolicyConstants.planarName(1) + "," + PolicyConstants.planarName(2));
    // Byteorder
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "ByteOrder");
    addElement(doc, field, "type", "string");
    addElement(doc, field, "description", "Byte Order (BigEndian, LittleEndian)");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", ByteOrder.BIG_ENDIAN.toString() + "," + ByteOrder.LITTLE_ENDIAN.toString());
    // FileSize
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "FileSize");
    addElement(doc, field, "type", "string");
    addElement(doc, field, "description", "The file size in bytes");
    addElement(doc, field, "operators", ">,<,=");
    // IccProfileClass
    field = doc.createElement("field");
    fields.appendChild(field);
    addElement(doc, field, "name", "IccProfileClass");
    addElement(doc, field, "type", "string");
    addElement(doc, field, "description", "Class of the device ICC Profile");
    addElement(doc, field, "operators", "=");
    addElement(doc, field, "values", IccProfile.ProfileClass.Abstract + "," + IccProfile.ProfileClass.Input + "," + IccProfile.ProfileClass.Display + "," + IccProfile.ProfileClass.Output + "," + IccProfile.ProfileClass.DeviceLink + "," + IccProfile.ProfileClass.ColorSpace + "," + IccProfile.ProfileClass.NamedColor + "," + IccProfile.ProfileClass.Unknown);

    return policyChecker;
  }

  static void addElement(Document doc, Element conformenceCheckerElement, String name,
                         String content) {
    Element element = doc.createElement(name);
    element.setTextContent(content);
    conformenceCheckerElement.appendChild(element);
  }

}
