# Docker file for Ubuntu with OpenJDK 17 and Tomcat 9.
FROM tomcat:9.0.83-jdk17-temurin

LABEL maintainer="Aleksandr Riazantsev <ad.riazantsev@gmail.com>"

# Enviroment variables
ENV CATALINA_HOME /usr/local/tomcat
ENV TOMCAT_WEBAPPS $CATALINA_HOME/webapps

# Set up current directory
WORKDIR /currencyExchanger

# Copy project into Ubuntu
COPY . .

# Update OS
RUN apt -y update && apt-get -y upgrade
# Install Maven.
RUN apt -y install maven
# Create .WAR file
RUN mvn package
# Copy .WAR file into /usr/local/tomcat
RUN cp ./target/CurrencyExchanger.war $TOMCAT_WEBAPPS
# Copy database into /usr/local/tomcat/webapps
RUN cp ./src/main/resources/currencyExchangerDB $CATALINA_HOME

# Set up port
EXPOSE 8080