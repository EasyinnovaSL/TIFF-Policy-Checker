/**
 * <h1>PolicyConstants.java</h1> <p> This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version; or,
 * at your choice, under the terms of the Mozilla Public License, v. 2.0. SPDX GPL-3.0+ or MPL-2.0+.
 * </p> <p> This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License and the Mozilla Public License for more details. </p>
 * <p> You should have received a copy of the GNU General Public License and the Mozilla Public
 * License along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>
 * and at <a href="http://mozilla.org/MPL/2.0">http://mozilla.org/MPL/2.0</a> . </p> <p> NB: for the
 * © statement, include Easy Innova SL or other company/Person contributing the code. </p> <p> ©
 * 2015 Easy Innova, SL </p>
 */

package com.easyinnova.policy_checker;

/**
 * Created by Adria Llorens on 27/12/2016.
 */
public class PolicyConstants {
  public static int compressionCode(String name) {
    switch (name) {
      case "None":
        return 1;
      case "CCITT":
        return 2;
      case "CCITT GR3":
        return 3;
      case "CCITT GR4":
        return 4;
      case "LZW":
        return 5;
      case "OJPEG":
        return 6;
      case "JPEG":
        return 7;
      case "DEFLATE Adobe":
        return 8;
      case "JBIG BW":
        return 9;
      case "JBIG C":
        return 10;
      case "PackBits":
        return 32773;
    }
    return -1;
  }

  public static String compressionName(int code) {
    switch (code) {
      case 1:
        return "None";
      case 2:
        return "CCITT";
      case 3:
        return "CCITT GR3";
      case 4:
        return "CCITT GR4";
      case 5:
        return "LZW";
      case 6:
        return "OJPEG";
      case 7:
        return "JPEG";
      case 8:
        return "DEFLATE Adobe";
      case 9:
        return "JBIG BW";
      case 10:
        return "JBIG C";
      case 32773:
        return "PackBits";
    }
    return "Unknown";
  }

  public static int photometricCode(String name) {
    switch (name) {
      case "Bilevel":
        return 1;
      case "RGB":
        return 2;
      case "Palette":
        return 3;
      case "Transparency Mask":
        return 4;
      case "CMYK":
        return 5;
      case "YCbCr":
        return 6;
      case "CIELAB":
        return 10;
    }
    return -1;
  }

  public static String photometricName(int code) {
    switch (code) {
      case 0:
      case 1:
        return "Bilevel";
      case 2:
        return "RGB";
      case 3:
        return "Palette";
      case 4:
        return "Transparency Mask";
      case 5:
        return "CMYK";
      case 6:
        return "YCbCr";
      case 8:
      case 9:
      case 10:
        return "CIELAB";
    }
    return "Unknown";
  }

  public static int planarCode(String name) {
    switch (name) {
      case "Chunky":
        return 1;
      case "Planar":
        return 2;
    }
    return -1;
  }

  public static String planarName(int code) {
    switch (code) {
      case 1:
        return "Chunky";
      case 2:
        return "Planar";
    }
    return "Unknown";
  }
}
