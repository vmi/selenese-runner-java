#!/usr/bin/env groovy

import groovy.transform.SourceURI
import java.nio.file.Paths

// Target files:
// - src/main/java/jp/vmi/selenium/selenese/config/IConfig.java
// - src/main/java/jp/vmi/selenium/selenese/config/DefaultConfig.java

@SourceURI
URI sourceUri

genDir = Paths.get(sourceUri).parent
baseDir = genDir.resolveSibling("main/java/jp/vmi/selenium/selenese")
config = new ConfigSlurper().parse(genDir.resolve('config.groovy').toUri().toURL())

def toUpperCamelCase(s) {
  s.replaceAll(/(^|_)([a-z])/) { m -> m[2].toUpperCase() }
}

def toLowerCamelCase(s) {
  s.replaceAll(/(_)([a-z])/) { m -> m[2].toUpperCase() }
}

def updateOptions(iConfig, dConfig) {
  def oNames = "\n"
  def getters = ""
  def fields = ""
  def accessors = ""
  config.forEach { oName, odef ->
    def cName = oName.toUpperCase()
    def uName = toUpperCamelCase(oName)
    def lName = toLowerCamelCase(oName)
    def option = "name = \"--\" + ${cName}"
    def fType = "String"
    odef.forEach { name, value ->
      if (name == "type") {
        fType = value
      } else {
        option += ","
        if (name == "usage") {
          if (option.length() + value.length() + 10 > 180)
            option += "\n       "
          value = value.replaceAll(/"/, "\\\\\"").replaceAll(/%(\w+)%/) {
            "\" + ${it[1]} + \""
          }
        }
        option += " ${name} = \"${value}\""
      }
    }
    def mType = fType
    def mgPrefix = "get"
    def dValue = "null"
    if (fType == "Boolean") {
      mType = "boolean"
      mgPrefix = "is"
      dValue = "false"
    }
    oNames += """\
    public static final String ${cName} = "${oName.replace('_', '-')}";
"""
    getters += """
    ${mType} ${mgPrefix}${uName}();
"""
    fields += "\n    @Option(${option})\n    private ${fType} ${lName};\n"
    accessors += """
    @Override
    public ${mType} ${mgPrefix}${uName}() {
        return ${lName} != null ? ${lName} : (parentOptions != null ? parentOptions.${mgPrefix}${uName}() : ${dValue});
    }
"""
    if (fType == "String[]") {
      accessors += """
    public void add${uName}(String ${lName}Item) {
        this.${lName} = ArrayUtils.add(this.${lName}, ${lName}Item);
    }
"""
    } else {
      accessors += """
    public void set${uName}(${mType} ${lName}) {
        this.${lName} = ${lName};
    }
"""
    }
  }

  def iBody = iConfig.getText()
  def newIBody = iBody.replaceFirst(/(?s)(?<=\/\/ ### BEGIN OPTION NAMES[^\n]*\n).*(?=\n[^\n]*\/\/ ### END OPTION NAMES)/) { oNames }
  newIBody = newIBody.replaceFirst(/(?s)(?<=\/\/ ### BEGIN GETTERS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END GETTERS)/) { getters }

  iConfig.write(newIBody, "UTF-8")

  def dBody = dConfig.getText()
  def newDBody = dBody.replaceFirst(/(?s)(?<=\/\/ ### BEGIN FIELDS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END FIELDS)/) { fields }
  newDBody = newDBody.replaceFirst(/(?s)(?<=\/\/ ### BEGIN GETTERS & SETTERS[^\n]*\n).*(?=\n[^\n]*\/\/ ### END GETTERS & SETTERS)/) { accessors }

  dConfig.write(newDBody, "UTF-8")
}

updateOptions(
  baseDir.resolve("config/IConfig.java"),
  baseDir.resolve("config/DefaultConfig.java")
)

// Local Variables:
// groovy-indent-offset: 2
// End:
