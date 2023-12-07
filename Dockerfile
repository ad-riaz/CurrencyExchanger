# Docker file for Ubuntu with OpenJDK 17 and Tomcat 9.
FROM tomcat:9.0.83-jre17-temurin
LABEL maintainer="Aleksandr Riazantsev <ad.riazantsev@gmail.com>"

# Enviroment variables
ENV CATALINA_HOME /usr/local/tomcat
ENV TOMCAT_WEBAPPS $CATALINA_HOME/webapps

WORKDIR /currencyExchanger

COPY . .


# Install Maven.
RUN apt -y update && apt-get -y upgrade
RUN apt -y install maven
RUN cp ./target/CurrencyExchanger.war $TOMCAT_WEBAPPS
RUN cp ./src/main/resources/currencyExchangerDB $CATALINA_HOME

EXPOSE 8080