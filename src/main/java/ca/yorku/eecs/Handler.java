package ca.yorku.eecs;
import java.io.*;
import java.net.*;
import java.sql.Driver;
import java.util.*;
import com.sun.net.httpserver.*;
import org.json.*;

public class Handler implements HttpHandler {
	private Neo4Jdatabase neo4JObject;
	
	public Handler(){
		this.neo4JObject = new Neo4Jdatabase();
	}

	/**
	 * This method just takes the request which is HttpExchange and checks if its a GET or PUT request
	 */
	@Override
	public void handle(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		try {
            if (request.getRequestMethod().equals("GET")) {
            	handleGet(request);
            }
            else if(request.getRequestMethod().equals("PUT")) {
            	handlePut(request);
            }
            
            //if the request is for neither GET nor PUT features
            else {
            	sendString(request, "501 UNIMPLEMENTED METHOD", 501);
            }
		}
		catch (Exception e) {
        	e.printStackTrace();
        	
        	//if there is server error
        	sendString(request, "500 INTERNAL SERVER ERROR", 500);
        }
	}

	/**
	 * This method handles the PUT request and passes the api request to its particular method which handles it and does the functionality accordingly 
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 */
	private void handlePut(HttpExchange request) throws IOException {
		// TODO Auto-generated method stub
		URI uri = request.getRequestURI();
		String path = uri.getPath();
		try {
		if(path.equals("/api/v1/addActor")) {
			addActorHandler(request);
			//Complete
		}
		else if(path.equals("/api/v1/addMovie")) {
			addMovieHandler(request);
			//complete
		}
		else if(path.equals("/api/v1/addRelationship")) {
			addRelationshipHandler(request);
			//complete
		}
		
		
		//
		//
		//added today
		else if(path.equals("/api/v1/addYear")) {
			addYearHandler(request);
			//complete
		}
		
		else if(path.equals("/api/v1/addYearToMovie")) {
			addRelationOfYear(request);
			//complete
		}
		//
		//
		//
		
		
		else {
			//if the request is for some PUT feature other than specified in the handout
			sendString(request, "501 UNIMPLEMENTED METHOD", 501);
		}
	}
		catch (Exception e) {
        	e.printStackTrace();
        	
        	//if there is server error
        	sendString(request, "500 INTERNAL SERVER ERROR", 500);
        }
	}

	
	/**
	 * This method handles the GET request and passes the api request to its particular method which handles it and does the functionality accordingly
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occured with the json object
	 */
	private void handleGet(HttpExchange request) throws IOException, JSONException {
		// TODO Auto-generated method stub
		URI uri = request.getRequestURI();
		String path = uri.getPath();
		
		if(path.equals("/api/v1/getActor")) {
			//complete
			getActorHandler(request);
		}
		else if(path.equals("/api/v1/getMovie")) {
			//complete
			getMovieHandler(request);
		}
		else if(path.equals("/api/v1/hasRelationship")) {
			//complete
			hasRelationshipHandler(request);
		}
		else if(path.equals("/api/v1/computeBaconNumber")) {
			//complete
			computeBaconNumberHandler(request);
		}
		else if(path.equals("/api/v1/computeBaconPath")) {
			//complete
			computeBaconPathHandler(request);
		}
		else if(path.equals("/api/v1/hasRelationshipBtwMovieYear")) {
			//complete
			hasRelationshipOfYearHandler(request);
		}
		else if(path.equals("/api/v1/getMoviesOfYear")) {
			//complete
			getMovieList(request);
		}
		else {
			//if the request is for some GET feature other than specified in the handout
			sendString(request, "501 UNIMPLEMENTED METHOD", 501);
		}
	}
	
	/**
	 * This methods handles the get request which is about the actor and returns the name, id and list of movies related to the actor along with the code responses
	 * The other code responses are sent according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void getActorHandler(HttpExchange request) throws IOException, JSONException{
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("actorId")) {
			String name, actorId;
			List<String> listOfMovies;
			
			//get the actorId from the request body
			actorId = jsonBody.get("actorId").toString();
			
			//call the method from Neo4Jdatabase class to get actor name
			name = neo4JObject.getActorName(actorId);
			
			//if the actor doesn't exist
			if(name.equals(""))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(name.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				//call the method from Neo4Jdatabase class to get list of movies
				listOfMovies = neo4JObject.getMoviesOfActor(actorId);
				
				
				//add all the data as specified in the handout
				jsonFinalResult.put("actorId", actorId);
				jsonFinalResult.put("name", name);
				jsonFinalResult.put("movies", listOfMovies);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This method handles the get request about the movie which returns the movie id, name and the list of actors who worked in that movie along with the code responses 
	 * The other code responses are sent according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occured with the json object
	 */
	private void getMovieHandler(HttpExchange request) throws IOException, JSONException{
		//Converting request to String
		String stringBody = Utils.getBody(request);
		//System.out.println("stringBody = "+stringBody);

		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		//System.out.println("input: " + jsonBody);
		
		// System.out.println("map object = "+ mapBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("movieId")) {
			String name, movieId;
			List<String> listOfActors;

			//System.out.print("entered");
			
			//get the movieId from the request body
			movieId = jsonBody.get("movieId").toString();
			
			//System.out.println(movieId);
			
			//call the method from Neo4Jdatabase class to get movie name
			name = neo4JObject.getMovieName(movieId);
			//System.out.println("name"+name);

			//if the movie doesn't exist
			if(name.equals(""))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(name.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				//call the method from Neo4Jdatabase class to get list of actors
				listOfActors = neo4JObject.getActorsOfMovie(movieId);
				
				
				//add all the data as specified in the handout
				jsonFinalResult.put("movieId", movieId);
				jsonFinalResult.put("name", name);
				jsonFinalResult.put("actors", listOfActors);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This method handles the get request about the relationship which checks if there is a relation between the given actor and the movie and 
	 * then returns the id of actor and movie along with the code responses with the true or false depending if the relationship exists or not. 
	 * The other code responses are sent according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void hasRelationshipHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("actorId") && jsonBody.has("movieId")) {
			String actorId, movieId;
			Boolean hasRelationship;
			
			//get the actorId from the request body
			actorId = jsonBody.get("actorId").toString();
			
			//get the movieId from the request body
			movieId = jsonBody.get("movieId").toString();
			
			//call the method from Neo4Jdatabase class to get status of relationship
			String resultOfhasRelationship = neo4JObject.hasRelationship(actorId, movieId);
			
			//if the movieId or actorId doesn't exist
			if(resultOfhasRelationship.equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(resultOfhasRelationship.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//if there exists the relationship
				if(resultOfhasRelationship.equals("true"))
					hasRelationship = true;
				
				//if there does not exist the relationship
				else
					hasRelationship = false;
				
				//add all the data as specified in the handout
				jsonFinalResult.put("actorId", actorId);
				jsonFinalResult.put("movieId", movieId);
				jsonFinalResult.put("hasRelationship", hasRelationship);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
				
	}
	
	/**
	 * This method handles the put request about the actor and adds the actor name and id to the database if it does not already exists and provides the code responses according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void addActorHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		if(jsonBody.has("name") && jsonBody.has("actorId")) {
			String name, actorId;
			
			//get the name of actor from the request body
			name = jsonBody.get("name").toString();
			
			//get the actorId from the request body
			actorId = jsonBody.get("actorId").toString();
			
			//call the method from Neo4Jdatabase class to add actor
			String addActorStatus = neo4JObject.addActor(name, actorId);
			
			//if the actorId already exist
			if(addActorStatus.equals("400 BAD REQUEST"))
				sendString(request, "400 BAD REQUEST", 400);
			
			//if there is server error (Java Exception)
			else if(addActorStatus.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//send the response of 200 OK for successful add
				sendString(request, "200 OK", 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This method handles the put request to add the movie name and id to the database if it does not exists already and also provides the code responses according to what happens to the request 
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void addMovieHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		if(jsonBody.has("name") && jsonBody.has("movieId")) {
			String name, movieId;
			
			//get the name of movie from the request body
			name = jsonBody.get("name").toString();
			
			//get the movieId from the request body
			movieId = jsonBody.get("movieId").toString();
			
			//call the method from Neo4Jdatabase class to add movie
			String addMovieStatus = neo4JObject.addMovie(name, movieId);
			
			//if the movieId already exist
			if(addMovieStatus.equals("400 BAD REQUEST"))
				sendString(request, "400 BAD REQUEST", 400);
			
			//if there is server error (Java Exception)
			else if(addMovieStatus.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//send the response of 200 OK for successful add
				sendString(request, "200 OK", 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This mehod handles the put request for adding the relationship between the movie and the actor if it does not already exists and it only adds the relationship if the particular 
	 * actor and the movie is present in the database. This methods then provides the code responses according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void addRelationshipHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		
		if(jsonBody.has("actorId") && jsonBody.has("movieId")) {
			String actorId, movieId;
			
			//get the actorId from the request body
			actorId = jsonBody.get("actorId").toString();
			
			//get the movieId from the request body
			movieId = jsonBody.get("movieId").toString();
			
			//call the method from Neo4Jdatabase class to add relationship
			String addRelationShipStatus = neo4JObject.addRelationship(actorId, movieId);
			
			//if the relationship already exist
			if(addRelationShipStatus.equals("400 BAD REQUEST"))
				sendString(request, "400 BAD REQUEST", 400);
			
			//if there is server error (Java Exception)
			else if(addRelationShipStatus.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			//if either actorId or movieId does not exist in the database
			else if(addRelationShipStatus.equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			else {
				
				//send the response of 200 OK for successful add
				sendString(request, "200 OK", 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This methods handles the get request for getting the Bacon Number of the particular actor. It also checks that the actor for which we want the bacon number that actor exists or not.
	 * If the actor has a bacon number then it provides the number along with the code response otherwise just the code response according  to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void computeBaconNumberHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("actorId")) {
			String actorId, baconNumberString;
			int baconNumber;
			
			//get the actorId from the request body
			actorId = jsonBody.get("actorId").toString();
			
			//call the method from Neo4Jdatabase class to get bacon number
			baconNumberString = neo4JObject.computeBaconNumber(actorId);
			
			//if the actor doesn't exist
			if(baconNumberString.equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(baconNumberString.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//if actorId given is of Kevin Bacon itself
				if(baconNumberString.equals("0"))
					baconNumber = 0;
				
				else
					baconNumber = Integer.parseInt(baconNumberString);
				
				//add all the data as specified in the handout
				jsonFinalResult.put("baconNumber", baconNumber);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This method handles the get request to compute the bacon path of the particular actor. It also checks if the actor exists in the database or not. If the actor exists then 
	 * the bacon path is provided along with the code response otherwise just the code response is provided according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	private void computeBaconPathHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("actorId")) {
			String actorId;
			List<String> baconPath;
			
			//get the actorId from the request body
			actorId = jsonBody.get("actorId").toString();
			
			//call the method from Neo4Jdatabase class to get bacon path
			baconPath = neo4JObject.computeBaconPath(actorId);
			
			//if the actor doesn't exist
			if(baconPath.get(0).equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			//if there is server error (Java Exception)
			else if(baconPath.get(0).equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//add all the data as specified in the handout
				jsonFinalResult.put("baconPath", baconPath);
				
				//send the response of 200 OK along with the result
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the request is not properly formatted or missing some information
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	/**
	 * This method handles the put request for adding the year to the database if it is not already present in the database. It also provides the particular code response according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	//the request of put related to year is handled here
	private void addYearHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		if(jsonBody.has("year")) {
			String year;
			
			//getting the year from the json body 
			year = jsonBody.get("year").toString();
	
			
			//calling the addYear of the neo4Jdatabse to add the year into the system
			String addYearStatus = neo4JObject.addYear(year);
			
			//If the year has already been added to the system then the 400 bad request
			if(addYearStatus.equals("400 BAD REQUEST"))
				sendString(request, "400 BAD REQUEST", 400);
			
			//if the java exception was encountered then the 500 internal server 
			else if(addYearStatus.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//if the year was successfully added to the database then the 200 code
				sendString(request, "200 OK", 200);
			}
		}
		
		//if in the api the information was wrongly inputed or not provided 
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
	
	
	/**
	 * This method handles the PUT request for adding the relationship between the year and the movie if both of them already exists in the database. The code responses are provided according
	 * what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	//the request of put related to the relationship of year with movie is handled here
	private void addRelationOfYear(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		
		if(jsonBody.has("year") && jsonBody.has("movieId")) {
			String year, movieId;
			
			//getting the year from the json body for which the relationship has to be made
			year = jsonBody.get("year").toString();
			
			//getting the id  of the movie from the json body for which the relationship has to be made
			movieId = jsonBody.get("movieId").toString();
			
			//the addRelationshipBtwnMovieYear of the database class is called to add the relationship
			String addRelationShipOfMovieYearStatus = neo4JObject.addRelationshipBtwnMovieYear(movieId,year);
			
			//if the relationship between the movie and the year already exists then the 400 bad request 
			if(addRelationShipOfMovieYearStatus.equals("400 BAD REQUEST"))
				sendString(request, "400 BAD REQUEST", 400);
			
			//if the java exception was encountered then the 500 internal server error
			else if(addRelationShipOfMovieYearStatus.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			//if the year or the movie id was not added to the database before adding the relationship then 404 not found
			else if(addRelationShipOfMovieYearStatus.equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			else {
				
				//if the relationship was made then the 200 code
				sendString(request, "200 OK", 200);
			}
		}
		
		//if there was something missing or wrongly formatted in the request then 400 bad request
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}

	
	/**
	 * This method handles the GET request for checking that is there any relationship between the particular movie and the year. if yes, then the movie id, year and true along 
	 * with the code response is provided otherwise the code response is provided according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	//This method is to handle the request where we want to see if a particular movie is "Released_IN" particular year 
	private void hasRelationshipOfYearHandler(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("year") && jsonBody.has("movieId")) {
			String year, movieId;
			Boolean hasRelationship;
			
			//getting the year for which we want to check the relationship
			year = jsonBody.get("year").toString();
			
			//get the movieId from the request body
			movieId = jsonBody.get("movieId").toString();
			
			//call9ing the hasRelationshipBtwnMovieYear method of the database class to check if the movie was "Released_IN" the particular year
			String resultOfStatus = neo4JObject.hasRelationshipBtwnMovieYear(movieId, year);
			
			//if the year and the movie does not exists in the databse for which we want to check the relationship then 404 not found
			if(resultOfStatus.equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			//if the java exception was encountered then the 500 internal server error
			else if(resultOfStatus.equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//if the movie was released in the particular year then true 
				if(resultOfStatus.equals("true"))
					hasRelationship = true;
				
				//if the movie was not released in the particular year
				else
					hasRelationship = false;
				
				//adding the year, movie and the status of the relationship between them
				jsonFinalResult.put("year", year);
				jsonFinalResult.put("movieId", movieId);
				jsonFinalResult.put("hasRelationship", hasRelationship);
				
				//the final output with the 200 code is sent
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if there was something missing or wrongly formatted in the request then 400 bad request
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
				
	}
	

	/**
	 * This method handles the GET request for getting the list of movies which were released in the particular year if the year is present in the database. if yes, then the 
	 * list of movies is provided along with the code response otherwise code responses are provided according to what happens to the request
	 * @param request the actual request which is coming as the url of the type HttpExchange
	 * @throws IOException exception is thrown which prints the stack trace and gives the 500 internal error if the api were something else
	 * @throws JSONException is thrown if some issues are occurred with the json object
	 */
	//this method is to get the list of movies which were released in the particular year 
	private void getMovieList(HttpExchange request) throws IOException, JSONException {
		//Converting request to String
		String stringBody = Utils.getBody(request);
		
		//Converting String request to query parameters in the form of JSONObject
		JSONObject jsonBody = new JSONObject(stringBody);
		//Map<String, String> mapBody = Utils.splitQuery(stringBody);
		
		JSONObject jsonFinalResult = new JSONObject();
		
		if(jsonBody.has("year")) {
			String year;
			List<String> MovieList;
			
			//getting the year from the json body for which the list of movies has to be given
			year = jsonBody.get("year").toString();
			
			//calling the getMoviesOfYear method of the database class
			MovieList = neo4JObject.getMoviesOfYear(year);
			
			//if the year for which we want the list of movies is not present in the database then the 404 not found
			if(MovieList.get(0).equals("404 NOT FOUND"))
				sendString(request, "404 NOT FOUND", 404);
			
			//if the java exception was encountered then the 500 internal server error
			else if(MovieList.get(0).equals("500 INTERNAL SERVER ERROR"))
				sendString(request, "500 INTERNAL SERVER ERROR", 500);
			
			else {
				
				//adding the list of movies
				jsonFinalResult.put("MovieList", MovieList);
				
				//the result is been sent along with the 200 code
				sendString(request, jsonFinalResult.toString(), 200);
			}
		}
		
		//if the wrongly formatted or the incorrect information was provided then 400 bad request
		else {
			sendString(request, "400 BAD REQUEST", 400);
		}
	}
		
	
	/**
	 * This method sends the body which is obtained from the particular methods
	 * @param request request the actual request which is coming as the url of the type HttpExchange
	 * @param data this is of type string which contains the actual answer to be outputed 
	 * @param restCode this is the code response which was got from the particular methods. and this is of type int
	 * @throws IOException exception is thrown which prints the stack trace of the exception parent class
	 */
	private void sendString(HttpExchange request, String data, int restCode) throws IOException {
		// TODO Auto-generated method stub
		request.sendResponseHeaders(restCode, data.length());
        OutputStream os = request.getResponseBody();
        os.write(data.getBytes());
        os.close();
		
	}

}
