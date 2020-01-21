package com.smith.scoresApp;

import org.json.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NBAGames extends Object {

	private static HttpURLConnection connection;

	public NBAGames() {
		System.out.println("=============================================================");
		System.out.println("\tNBA games today :  "+getDate());
		System.out.println("=============================================================");

	}
	
	public void getGames() {
		HttpClient client = HttpClient.newHttpClient();
		//HttpRequest request - Builds http request to uri
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://www.balldontlie.io/api/v1/games?dates[]=2020-01-18")).build();
		
		//Uses client to send request asynchronously 
		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())	//(send request, receive response as a String)
			.thenApply(HttpResponse::body) 	//Once response is received apply body method from HttpResponse.
			.thenAccept(NBAGames::parse)
			//.thenAccept(System.out::println)	//prints response body to sysout.
			.join();
		
	}
	
	public static void getGamesAlt() {
		BufferedReader reader;
		String line;	//For reading each line of response...
		StringBuffer responseContent = new StringBuffer();	//Append each line and build response content
		HttpClient h;
		
		try {
			URL url = new URL("https://www.balldontlie.io/api/v1/games?dates[]=" +getDate());
			connection = (HttpURLConnection) url.openConnection();
			
			//Setup for request method
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			
			int status = connection.getResponseCode();
			//System.out.println(status);
			
			if(status > 299) {
				//places input stream in Buffer
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				
				//Reads each line of input until end of file
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			}
			//Reads each line of input until end of file
			else {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while((line = reader.readLine()) != null) {
					responseContent.append(line);
				}
				reader.close();
			}
			
			//System.out.println(responseContent.toString());
			parse(responseContent.toString());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String parse(String responseBody) {
		JSONObject obj = new JSONObject(responseBody);
		JSONArray data = obj.getJSONArray("data");
		
		for(int i=0; i<data.length(); i++) {
			JSONObject games = data.getJSONObject(i);
			String homeTeam = games.getJSONObject("home_team").getString("name");
			String awayTeam = games.getJSONObject("visitor_team").getString("name");
			int awayScore = games.getInt("visitor_team_score");
			int homeScore = games.getInt("home_team_score");
			
			System.out.printf("%20s %10s %10s", awayTeam + " - " + awayScore, "  @  ", homeTeam+ " - " + homeScore +"\n\n");

			//System.out.println(awayTeam +"-  " +awayScore + "  @  " + homeTeam+"-  " +homeScore +"\n\n");
		}
		
		//System.out.println(data);
		
		return null;
	}
	
	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
}
//TODO: Make getDate method, Center align text, clean code, commit, 