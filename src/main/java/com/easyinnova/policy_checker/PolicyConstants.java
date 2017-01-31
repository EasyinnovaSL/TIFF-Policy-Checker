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
