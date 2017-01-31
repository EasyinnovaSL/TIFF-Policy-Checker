# TIFF-Policy-Checker
TIFF Policy Checker

## Usage
Usage: policy-checker [--rule <type> <tag> <operator> <value>] <filepath>

Rule specification:

- Type must be 'error' or 'warning'.
- Tag must be an accepted Tag. Use 'policy-checker --list' to see the list of accepted tags.
- Operator must be 'GT' (Grather than), 'LT' (Less than) or 'EQ' (Equals).

Example:

`policy-checker --rule error ImageWidth GT 500`

## Compile

### 1 Check Java version

`java -version`

If not installed, or the version is lower than 1.8 then Java 8 needs to be installed.

### 2 Install with Maven

Type

`mvn install`

Alternatively, for a faster build (without javadoc, testing and gpg singing), you can type

`mvn install -Dmaven.javadoc.skip=true -DskipTests -Dgpg.skip`
