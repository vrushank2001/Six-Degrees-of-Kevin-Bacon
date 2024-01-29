package ca.yorku.eecs;
import com.sun.net.httpserver.*;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;
import org.json.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;

public class Neo4Jdatabase {
	private Driver driver;
	private String uriDb;
	
	/*
	 *Contructor used to setup the connection between Java application
	 *and Neo4j Database.
	 */
	public Neo4Jdatabase() {
		uriDb = "bolt://localhost:7687"; // may need to change if you used a different port for your DBMS
		Config config = Config.builder().withoutEncryption().build();
		driver = GraphDatabase.driver(uriDb, AuthTokens.basic("neo4j","12345678"), config);
	}
	
	/**
	 * Returns true if the actor with given ID is already present
	 * in the database otherwise false.
	 *
	 * 
	 * @param id The ID of the actor to check for presence in the database.
	 * @return {@code true} if the actor with the given ID is present in the database,
	 *         {@code false} otherwise.
	 */
	public boolean hasActor(String id) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (a: actor) WHERE a.id = '" + id + "' RETURN a;";
            StatementResult result = transaction.run(query);
            
            boolean actorAlreadyPresent = result.hasNext();
            
            transaction.success();
            transaction.close();
            session.close();
            
            return actorAlreadyPresent;
        }
    }
	
	/**
	 * Returns true if the movie with given ID is already present
	 * in the database otherwise false.
	 * 
	 * @param id The ID of the movie to check for presence in the database.
	 * @return {@code true} if the movie with the given ID is present in the database,
	 *	       {@code false} otherwise.
	 */
	public boolean hasMovie(String id) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (m: movie) WHERE m.id = '" + id + "' RETURN m;";
            StatementResult result = transaction.run(query);
            //System.out.println("result  = " + result);
            boolean movieAlreadyPresent = result.hasNext();
            //System.out.println("check has next : "+ movieAlreadyPresent);
            transaction.success();
            transaction.close();
            session.close();
            
            return movieAlreadyPresent;
        }
		
		
    }
	/**
	 * Returns the name of the actor with given ID as a String.
	 * If the actor is not present in the database then it returns
	 * an empty string. If an exception occurs during the process,
	 * it returns "500 INTERNAL SERVER ERROR".
	 *
	 * @param id The ID of the actor.
	 * @return The name of the actor if found, or an empty string if not found,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String getActorName(String id) {
		 if(hasActor(id) == false)
			 return "";
	       
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (a: actor) WHERE a.id = '" + id + "' RETURN a.name AS name;";
			 StatementResult result = transaction.run(query);
			 
			 String name = result.next().get("name").asString();
			 
			 transaction.success();
			 transaction.close();
			 session.close();
			 
			 return name;
		 }
		 
		 catch(Exception e) {
			 e.printStackTrace();
			 return "500 INTERNAL SERVER ERROR";
		 }
	}
	
	/**
	 * Returns the name of the movie with given ID as a String.
	 * If the movie is not present in the database then it returns
	 * an empty string. If an exception occurs during the process,
	 * it returns "500 INTERNAL SERVER ERROR".
	 *
	 * @param id The ID of the movie.
	 * @return The name of the movie if found, or an empty string if not found,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String getMovieName(String id) {
		 if(hasMovie(id) == false) {
		 	//System.out.println("movie not found");
			 return "";
		 }
		 try(Session session = driver.session()){
			 Transaction transaction = session.beginTransaction();
			 String query = "MATCH (m: movie) WHERE m.id = '" + id + "' RETURN m.name AS name;";
			 StatementResult result = transaction.run(query);
			 
			 String name = result.next().get("name").asString();
			 
			 transaction.success();
			 transaction.close();
			 session.close();
			 
			 return name;
		 }
		 
		 catch(Exception e) {
			 e.printStackTrace();
			 return "500 INTERNAL SERVER ERROR";
		 }
	}
	
	/**
	 * Returns the list of movies of an actor with given ID has acted in.
	 * 
	 * @param id The ID of the actor.
	 * @return A list of movie IDs in which the actor with given ID has acted.
	 */
	public List<String> getMoviesOfActor(String id){
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor {id: '" + id + "'})-[:ACTED_IN]->(fof) RETURN DISTINCT fof.id AS id;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfMovies = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfMovies.add(result.next().get("id").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfMovies;
		 }
	}
	
	/**
	 * Returns the list of movies of an actor with given ID has acted in.
	 * 
	 * @param id The ID of the movie.
	 * @return A list of actor IDs who have worked in the movie with given ID.
	 */
	public List<String> getActorsOfMovie(String id){
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor)-[:ACTED_IN]->(m: movie {id : '" + id + "'}) RETURN DISTINCT a.id AS id;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfActors = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfActors.add(result.next().get("id").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfActors;
		 }
	}
	
	/**
	 * Checks whether the relationship between actor with given actorId
	 * and movie with given movieId exists or not.
	 * 
	 * @param actorId The ID of the actor.
	 * @param movieId The ID of the movie.
	 * @return "true" if the relationship exists, "false" if it does not exist,
	 *         "404 NOT FOUND" if either the actor or the movie is not present,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String hasRelationship(String actorId, String movieId) {
		if(!hasActor(actorId) || !hasMovie(movieId))
			return "404 NOT FOUND";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (a: actor {id: '"+ actorId + "'})-[:ACTED_IN]->(m: movie {id: '" + movieId + "'}) RETURN EXISTS((a)-[:ACTED_IN]->(m));";
			StatementResult result = transaction.run(query);
			boolean isRelationshipPresent=false;
			if(result.hasNext()){
			isRelationshipPresent = result.next().get(0).asBoolean();
			}
			transaction.success();
			transaction.close();
			session.close();
			
			if(isRelationshipPresent)
				return "true";
			else
				return "false";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Adds the actor with given name and ID to the database.
	 * 
	 * @param name The name of the actor.
	 * @param id The ID of the actor.
	 * @return "200 OK" if the actor is successfully added,
	 *		   "400 BAD REQUEST" if an actor with the given ID already exists,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String addActor(String name, String id) {
		
		if(hasActor(id))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (a: actor {name: '" + name + "', id: '" + id + "'});";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Adds the movie with given name and ID to the database.
	 * 
	 * @param name The name of the movie.
	 * @param id The ID of the movie.
	 * @return "200 OK" if the movie is successfully added,
	 *		   "400 BAD REQUEST" if an actor with the given ID already exists,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String addMovie(String name, String id) {
		
		if(hasMovie(id))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (m: movie {name: '" + name + "', id: '" + id + "'});";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Adds the directed relationship of
	 * (actor)-[:ACTED_IN]-(movie)
	 * to the database.
	 *
	 * @param actorId The ID of the actor.
	 * @param movieId The ID of the movie.
	 * @return "200 OK" if the relationship is successfully added,
	 * 		   "404 NOT FOUND" if either the actor or the movie is not present,
	 *         "400 BAD REQUEST" if the relationship already exists,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String addRelationship(String actorId, String movieId) {
		
		if(!hasActor(actorId) || !hasMovie(movieId))
			return "404 NOT FOUND";
		
		if(hasRelationship(actorId, movieId).equals("true"))
			return "400 BAD REQUEST";
			
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH (a: actor), (m: movie) WHERE a.id = '" + actorId + "' AND m.id = '" + movieId + "' CREATE (a)-[r:ACTED_IN]->(m);";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Computes the Bacon number of the actor with the given ID
	 * with respect to Kevin Bacon (ID: "nm0000102").
	 *
	 * @param id The ID of the actor.
	 * @return The Bacon number (as @code String) of the actor with respect
	 * 		   to Kevin Bacon, or one of the following:
	 *         "0" if the actor's ID is of Kevin Bacon himself,
	 *         "404 NOT FOUND" if the actor is not present in the database,
	 *         "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String computeBaconNumber(String id) {
		if(!hasActor(id))
			return "404 NOT FOUND";
		
		if(id.equals("nm0000102"))
			return "0";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH path = shortestPath((kb: actor {id: 'nm0000102'})-[*]-(a: actor {id: '" + id +"'})) RETURN length(path)/2 AS baconNumber;";
			StatementResult result = transaction.run(query);
			
			int baconNumber = result.next().get("baconNumber").asInt();
			transaction.success();
			transaction.close();
			session.close();
			
			return baconNumber + "";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Computes the bacon path of the actor with the given ID
	 * with respect to Kevin Bacon (ID: "nm0000102").
	 *
	 * @param id The ID of the actor.
	 * @return A list of actor IDs representing the Bacon path from the given
	 * 		   actor to Kevin Bacon, or one of the following:
	 *         An ArrayList containing a single element "404 NOT FOUND" if the actor is not present in the database,
	 *         An ArrayList containing a single element "nm0000102" if the actor ID is of Kevin Bacon himself,
	 *         An ArrayList containing a single element "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public List<String> computeBaconPath(String id){
		if(!hasActor(id))
			return new ArrayList<>(Arrays.asList(new String[]{"404 NOT FOUND"}));
		
		if(id.equals("nm0000102"))
			return new ArrayList<>(Arrays.asList(new String[] {"nm0000102"}));
		 
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH path = shortestPath((a: actor {id: '" + id + "'})-[*]-(kb: actor {id: 'nm0000102'})) RETURN nodes(path) AS path";
			StatementResult result = transaction.run(query);
			
			//List<Object> pathObject = result.next().get("path").asList();
			List<String> baconPath = new ArrayList<>();
	
			List<Node> pathNode = result.next().get("path").asList(Value::asNode);
			
			for(Node data_entity : pathNode) {
				//Map<String, Object> properties = ((Value) year).asMap();
				
					baconPath.add(data_entity.get("id").asString());
					/*if(data_entity.hasLabel("actor")) {
						baconPath.add(data_entity.get("actorId").asString());
					}
					else if(data_entity.hasLabel("movie")) {
						baconPath.add(data_entity.get("movieId").asString());
					}*/
			}
			
			transaction.success();
			transaction.close();
			session.close();
			
			return baconPath;
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>(Arrays.asList(new String[]{"500 INTERNAL SERVER ERROR"}));
		}
	}
	
	//METHODS FOR NEW FEATURE STARTS FROM HERE.
	
	/**
	 * Returns true if the year node with the given year is already present
	 * in the database; otherwise, returns false.
	 *
	 * @param year The year to check for existence as a node in the database.
	 * @return {@code true} if the year node with the given year exists,
	 *         {@code false} otherwise.
	 */
	public boolean hasYear(String year) {
        try(Session session = driver.session()){
            Transaction transaction = session.beginTransaction();
            String query = "MATCH (y: year) WHERE y.year = '" + year + "' RETURN y;";
            StatementResult result = transaction.run(query);
            
            boolean yearAlreadyPresent = result.hasNext();
            
            transaction.success();
            transaction.close();
            session.close();
            
            return yearAlreadyPresent;
        }
    }
	
	/**
	 * Checks whether the relationship between a movie with the given ID
	 * and a year node with the given year exists or not.
	 *
	 * @param id The ID of the movie.
	 * @param year The year.
	 * @return "true" if the relationship exists, "false" if it does not exist,
	 *         "404 NOT FOUND" if either the movie or the year node is not present,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String hasRelationshipBtwnMovieYear(String id, String year) {
		if(!hasMovie(id) || !hasYear(year))
			return "404 NOT FOUND";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (m: movie {id: '"+ id + "'})-[:RELEASED_IN]->(y: year {year: '" + year + "'}) RETURN EXISTS((m)-[:RELEASED_IN]->(y));";
			StatementResult result = transaction.run(query);
			
			boolean isRelationshipPresent=false;
			if(result.hasNext()){
				isRelationshipPresent = result.next().get(0).asBoolean();
			}
			transaction.success();
			transaction.close();
			session.close();
			
			if(isRelationshipPresent)
				return "true";
			else
				return "false";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Adds a year node with the given year to the database.
	 *
	 * @param year The year to be added as a node.
	 * @return "200 OK" if the year node is successfully added, "400 BAD REQUEST" if the year node already exists,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String addYear(String year) {
		
		if(hasYear(year))
			return "400 BAD REQUEST";
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "CREATE (y: year {year: '" + year + "'});";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Adds the directed relationship of
	 * (movie)-[:RELEASED_IN]->(year)
	 * to the database.
	 *
	 * @param id The ID of the movie.
	 * @param year The year.
	 * @return "200 OK" if the relationship is successfully added,
	 * 		   "404 NOT FOUND" if either the movie or the year node is not present,
	 *         "400 BAD REQUEST" if the relationship already exists,
	 *         or "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public String addRelationshipBtwnMovieYear(String id, String year) {
		
		if(!hasMovie(id) || !hasYear(year))
			return "404 NOT FOUND";
		
		if(hasRelationshipBtwnMovieYear(id, year).equals("true"))
			return "400 BAD REQUEST";
			
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query = "MATCH (m: movie), (y: year) WHERE m.id = '" + id + "' AND y.year = '" + year + "' CREATE (m)-[rmy:RELEASED_IN]->(y);";
			//StatementResult result = transaction.run(query);
			transaction.run(query);
			
			transaction.success();
			transaction.close();
			session.close();
			
			return "200 OK";
		}
		catch(Exception e) {
			e.printStackTrace();
			return "500 INTERNAL SERVER ERROR";
		}
	}
	
	/**
	 * Returns the list of movies released on a specific year.
	 *
	 * @param year The year for which to retrieve the list of movies.
	 * @return A list of movie IDs released in the specified year,
	 *         or an ArrayList containing a single element "404 NOT FOUND" if the year node is not present,
	 *         or an ArrayList containing a single element "500 INTERNAL SERVER ERROR" if an exception occurs.
	 */
	public List<String> getMoviesOfYear(String year){
		if(!hasYear(year))
			return new ArrayList<>(Arrays.asList(new String[] {"404 NOT FOUND"}));
		
		try(Session session = driver.session()){
			Transaction transaction = session.beginTransaction();
			String query =  "MATCH (m: movie)-[:RELEASED_IN]->(y: year {year : '" + year + "'}) RETURN DISTINCT m.id AS id;";
			StatementResult result = transaction.run(query);
			
			List<String> listOfMovies = new ArrayList<>();
			
			while(result.hasNext()) {
				listOfMovies.add(result.next().get("id").asString());
			}
			
			transaction.success();
			transaction.close();
			session.close();
			 
			return listOfMovies;
		 }
		catch(Exception e) {
			e.printStackTrace();
			return new ArrayList<>(Arrays.asList(new String[] {"500 INTERNAL SERVER ERROR"}));
		}
	}
	
}
