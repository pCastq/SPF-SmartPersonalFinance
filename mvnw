#!/bin/sh
# Maven Wrapper script for Linux/macOS
set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  echo "Downloading Maven Wrapper..."
  if command -v wget > /dev/null; then
    wget -q -O "$MAVEN_WRAPPER_JAR" "$WRAPPER_URL"
  elif command -v curl > /dev/null; then
    curl -sL -o "$MAVEN_WRAPPER_JAR" "$WRAPPER_URL"
  else
    echo "Error: wget or curl is required to download Maven wrapper" >&2
    exit 1
  fi
fi

exec java \
  $MAVEN_OPTS \
  -classpath "$MAVEN_WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
