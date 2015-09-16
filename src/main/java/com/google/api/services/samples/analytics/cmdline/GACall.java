/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.services.samples.analytics.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;


/**
 * This is a basic hello world sample for the Google Analytics API. It is designed to run from the
 * command line and will prompt a user to grant access to their data. Once complete, the sample will
 * traverse the Management API hierarchy by going through the authorized user's first account, first
 * web property, and finally the first profile and retrieve the first profile id. This ID is then
 * used with the Core Reporting API to retrieve the top 25 organic search terms.
 *
 * @author api.nickm@gmail.com
 */
public class GACall {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "Marketing Data App";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/analytics_sample");
  
  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /**
   * Main demo. This first initializes an analytics service object. It then uses the Google
   * Analytics Management API to get the first profile ID for the authorized user. It then uses the
   * Core Reporting API to retrieve the top 25 organic search terms. Finally the results are printed
   * to the screen. If an API error occurs, it is printed here.
   *
   * @param args command line args.
   */
  public static GaData main(String[] args, String[] testDates, int queryType) {
    //queryType tells the GaData method which query to execute
    /*
     * 0:Adwords
     * 1:LinkedIn
     */
    
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      //This is the analytics services object
      Analytics analytics = initializeAnalytics();

      //TODO: This needs to be modified so that it gets the correct analytics ID
      //Deprecated in lieu of hardcode: String profileId = getFirstProfileId(analytics);
      String profileId = "63145830"; 

      //can I execute all queries at once to save time?
      //Determine which query to execute
      GaData gaData = null;

      //eventually this solution should be implemented
      //http://stackoverflow.com/questions/4480334/how-to-call-a-method-stored-in-a-hashmap-java
      if (queryType == 0) {
        gaData = executeAdwordsQuery(analytics, profileId,testDates);
      } else if (queryType == 1) {
        gaData = executeLinkedInBehaviorQuery(analytics, profileId,testDates);
      } else if (queryType == 2) {
        gaData = executeCentroDDBehaviorQuery(analytics, profileId, testDates);
      } else if (queryType == 3) {
        gaData = executeCentroVidBehaviorQuery(analytics, profileId, testDates);
      } else if (queryType == 4) {
        gaData = executeCentroMobBehaviorQuery(analytics, profileId, testDates);
      } else if (queryType == 5) {
        gaData = executeCentroRichBehaviorQuery(analytics, profileId, testDates);
      } else if (queryType == 6) {
        gaData = executeFacebookBehaviorQuery(analytics, profileId, testDates);
      } else if (queryType == 7) {
        gaData = executeTwitterBehaviorQuery(analytics,profileId,testDates);
      } else if (queryType == 8) {
        gaData = executeMasterDigitalBehaviorQuery(analytics,profileId,testDates);
      }else {
        System.out.println("Error: queryType value is not valid");
      }

      return gaData;

    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
      throw new EmptyStackException();
    } catch (Throwable t) { //throwable is the superclass of Exception
      t.printStackTrace();
      throw new EmptyStackException();
    }
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            GACall.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
          + "into analytics-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized Analytics service object.
   *
   * @throws Exception if an issue occurs with OAuth2Native authorize.
   */
  private static Analytics initializeAnalytics() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up and return Google Analytics API client.
    return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
        APPLICATION_NAME).build();
  }



  /**
   * Returns the top 25 organic search keywords and traffic source by visits. The Core Reporting API
   * is used to retrieve this data.
   *
   * @param analytics the analytics service object used to access the API.
   * @param profileId the profile ID from which to retrieve data.
   * @return the response from the API.
   * @throws IOException tf an API error occured.
   */

  
  //Only one query is needed for Adwords since all data come from GA
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeAdwordsQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:adClicks,ga:impressions,ga:CTR,ga:CPC,ga:adCost,ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adGroup")
        .setFilters("ga:source==google;ga:medium==cpc")
        .setMaxResults(200)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeFacebookBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adContent")
        .setFilters("ga:campaign=@FY2015,ga:campaign=@FY2016;ga:source==Facebook;ga:medium==Newsfeed,ga:medium==Right_Rail,ga:medium==Social,ga:medium==Newsfeed_PPE,ga:medium==Newsfeed_Link")
        .setMaxResults(100)
        .execute();
  }
  
  
//This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeMasterDigitalBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adContent")
        .setFilters("ga:source==MobileFuse,ga:source==Sparknotes,ga:source==Pandora,ga:source==Collective,ga:source==YouTube,ga:source==WGME;ga:medium==Mobile,ga:medium==Display,ga:medium==Preroll,ga:medium==CrossPlatform")
        .setMaxResults(25)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeCentroDDBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adContent")
        .setFilters("ga:source==Collective,ga:source==Sparknotes,ga:source==Brand_Exchange,ga:source==Pandora;ga:medium==Display")
        .setMaxResults(100)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeCentroMobBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adContent")
        .setFilters("ga:source==MobileFuse,ga:source==Sparknotes,ga:source==Pandora;ga:medium==Mobile")
        .setMaxResults(25)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeCentroRichBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adContent")
        .setFilters("ga:source==Collective,ga:source==Sparknotes,ga:source==Brand_Exchange,ga:source==Pandora;ga:medium==Rich")
        .setMaxResults(25)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeCentroVidBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign,ga:adContent")
        .setFilters("ga:source==YouTube,ga:source==Collective,ga:source==Brand_Exchange;ga:medium==Preroll")
        .setMaxResults(25)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeLinkedInBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign")
        .setFilters("ga:source==LinkedIn;ga:medium==Social")
        .setMaxResults(25)
        .execute();
  }
  
  //This needs to accept a dateArray which contains two strings the first being a startDate and
  //the second being the endDate
  private static GaData executeTwitterBehaviorQuery(Analytics analytics, String profileId, String[] dateArray) throws IOException {
    return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
        dateArray[0], // Start date.
        dateArray[1], // End date.
        "ga:visits,ga:pageViewsPerVisit,ga:avgTimeOnSite,ga:percentNewVisits,ga:entranceBounceRate") // Metrics.
        .setDimensions("ga:source,ga:medium,ga:campaign")
        .setFilters("ga:source==Twitter;ga:medium==Social")
        .setMaxResults(25)
        .execute();
  }
  
  //Helper method for viewing structor of rows
  
  public static void viewRowsStructure(GaData results) {
    System.out.println("This is the structure of the rows object");
    System.out.println(results.get("rows"));
    System.out.println("Exiting viewRowsStrucure");
  }

  /**
   * Prints the output from the Core Reporting API. The profile name is printed along with each
   * column name and all the data in the rows.
   *
   * @param results data returned from the Core Reporting API.
   */
  public static void printGaData(GaData results) {
    System.out.println(
        "printing results for profile: " + results.getProfileInfo().getProfileName());

    if (results.getRows() == null || results.getRows().isEmpty()) {
      System.out.println("No results Found.");
    } else {

      // Print column headers.
      for (ColumnHeaders header : results.getColumnHeaders()) {
        System.out.printf("%30s", header.getName());
      }
      System.out.println();

      // Print actual data.
      for (List<String> row : results.getRows()) {
        for (String column : row) {
          System.out.printf("%30s", column);
        }
        System.out.println();
      }

      System.out.println();
    }
  }
}
