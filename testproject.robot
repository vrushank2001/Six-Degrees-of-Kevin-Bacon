*** Settings ***
Library           Collections
Library           RequestsLibrary
Test Timeout      30 seconds

Suite Setup     Create Session  localhost   http://localhost:8080

*** Test Cases ***
addActorPass
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       name=Kevin Bacon      actorId=nm0000102
    ${resp}=        PUT On Session      localhost       /api/v1/addActor        json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK
    

addActorFail
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       name=Me      
    ${resp}=        PUT On Session      localhost       /api/v1/addActor        json=${params}      headers=${headers}      expected_status=400
    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

addMoviePass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   name=Parasite  movieId=nm200
    ${resp}=     PUT On Session  localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addMovieFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   name=IronMan  movieId=nm200
    ${resp}=     PUT On Session  localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

addRelationshipPass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm0000102  movieId=nm200
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationshipFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary  actorId=nm0000102  
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

addRelationshipFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary  actorId=15  movieId=nm5000
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND

####### Get Methods ######

getActorPass
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       actorId=nm0000102
    ${resp}=        GET On Session      localhost       /api/v1/getActor        json=${params}      headers=${headers}      expected_status=200

    ${json}=   Set Variable   ${resp.json()}
    # Log to console    ${resp.json()}
    Should Be Equal As Strings  ${json['actorId']}  nm0000102
    should Be Equal as Strings  ${json['name']}   Kevin Bacon
    ${movies}=   Create List   nm200
    Lists Should Be Equal   ${json['movies']}  ${movies}


getActorFail
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       actorId=34
    ${resp}=        GET On Session      localhost       /api/v1/getActor        json=${params}      headers=${headers}      expected_status=404

    #${json}=   Get Text   ${resp.json()}
    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND


getActorFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=u94580   
    ${resp}=     GET On Session  localhost   /api/v1/getActor    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND

getMoviePass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=nm200
    ${resp}=     GET On Session  localhost   /api/v1/getMovie    json=${params}  headers=${headers}  expected_status=200

    ${json}=   Set Variable   ${resp.json()}
    #Log to console    ${resp.json()}
    Should Be Equal As Strings  ${json['movieId']}  nm200
    should Be Equal as Strings  ${json['name']}   Parasite
    ${actor}=   Create List   nm0000102
    Lists Should be Equal   ${json['actors']}  ${actor}


getMovieFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   
    ${resp}=     GET On Session  localhost   /api/v1/getMovie    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST


getMovieFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=u94580   
    ${resp}=     GET On Session  localhost   /api/v1/getMovie   json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND


hasRelationshipPass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=nm200      actorId=nm0000102
    ${resp}=     GET On Session  localhost   /api/v1/hasRelationship    json=${params}  headers=${headers}  expected_status=200

    #{"actorId":"nm0000102","movieId":"nm200","hasRelationship":true}
    ${json}=   Set Variable   ${resp.json()}
   
    Should Be Equal As Strings  ${json['movieId']}  nm200
    Should Be Equal as Strings  ${json['actorId']}  nm0000102 
    Should Be true      ${json['hasRelationship']}    ${true}
   

hasRelationshipFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=nm200    
    ${resp}=     GET On Session  localhost   /api/v1/hasRelationship    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

hasRelationshipFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=nm200   actorId=2
    ${resp}=     GET On Session  localhost   /api/v1/hasRelationship    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND

####### Filling the Database  ########

addActor2
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       name=AK      actorId=nm103
    ${resp}=        PUT On Session      localhost       /api/v1/addActor        json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK
   

addActor3
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       name=PS      actorId=nm104
    ${resp}=        PUT On Session      localhost       /api/v1/addActor        json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK
   

addActor4
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       name=VK      actorId=nm105
    ${resp}=        PUT On Session      localhost       /api/v1/addActor        json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK
  
addMovie2
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   name=IronMan  movieId=nm201
    ${resp}=     PUT On Session  localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addMovie3
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   name=Barbie  movieId=nm202
    ${resp}=     PUT On Session  localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addMovie4
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   name=MI  movieId=nm203
    ${resp}=     PUT On Session  localhost   /api/v1/addMovie    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationship1
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm104  movieId=nm200
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationship2
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm103  movieId=nm201
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationship3
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm104  movieId=nm201
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationship4
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm105  movieId=nm203
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationship5
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm103  movieId=nm203
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

addRelationship6
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm105  movieId=nm202
    ${resp}=     PUT On Session  localhost   /api/v1/addRelationship    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.content}     200 OK

########  Database values added which were required to compute Bacon Number ########

computeBaconNumberPass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm105   
    ${resp}=     GET On Session  localhost   /api/v1/computeBaconNumber    json=${params}  headers=${headers}  expected_status=200

    Should Be Equal As Strings      ${resp.json()['baconNumber']}     3


computeBaconNumberFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   
    ${resp}=     GET On Session  localhost   /api/v1/computeBaconNumber    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

computeBaconNumberFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary      actorId=550
    ${resp}=     GET On Session  localhost   /api/v1/computeBaconNumber    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND


computeBaconPathPass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   actorId=nm105   
    ${resp}=     GET On Session  localhost   /api/v1/computeBaconPath    json=${params}  headers=${headers}  expected_status=200
    #Log to console    ${params}
    ${json}=   Set Variable   ${resp.json()}
    ${baconPaths}=  Create List   nm105   nm203   nm103   nm201   nm104   nm200   nm0000102
    Lists Should be Equal   ${json['baconPath']}  ${baconPaths}


computeBaconPathFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   
    ${resp}=     GET On Session  localhost   /api/v1/computeBaconPath   json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

computeBaconPathFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary      actorId=550 
    ${resp}=     GET On Session  localhost   /api/v1/computeBaconPath    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND


########### New feature Testing    ##########

addYearPass
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       year=2023
    ${resp}=        PUT On Session      localhost       /api/v1/addYear        json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK
    
addYearFail
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       year=2023
    ${resp}=        PUT On Session      localhost       /api/v1/addYear        json=${params}      headers=${headers}      expected_status=400
    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

addYearToMoviePass
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       year=2023    movieId=nm202
    ${resp}=        PUT On Session      localhost       /api/v1/addYearToMovie        json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK

addYearToMoviePass2
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary       year=2023    movieId=nm203
    ${resp}=        PUT On Session      localhost       /api/v1/addYearToMovie       json=${params}      headers=${headers}      expected_status=200
    Should Be Equal As Strings      ${resp.content}     200 OK


addYearToMovieFail
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary        year=2023   
    ${resp}=        PUT On Session      localhost       /api/v1/addYearToMovie       json=${params}      headers=${headers}      expected_status=400
    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST


addYearToMovieFail404
    ${headers}=     Create Dictionary       Content-Type=application/json
    ${params}=      Create Dictionary        year=2020     movieId=nm900   
    ${resp}=        PUT On Session      localhost       /api/v1/addYearToMovie       json=${params}      headers=${headers}      expected_status=404
    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND


hasRelationshipBtwMovieYearPass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   year=2023     movieId=nm202      
    ${resp}=     GET On Session  localhost   /api/v1/hasRelationshipBtwMovieYear    json=${params}  headers=${headers}  expected_status=200

   
    ${json}=   Set Variable   ${resp.json()}
   
    Should Be Equal As Strings  ${json['year']}  2023
    Should Be Equal as Strings  ${json['movieId']}  nm202 
    Should Be true      ${json['hasRelationship']}    ${true}
   


hasRelationshipBtwMovieYearFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=nm200      year=2079   
    ${resp}=     GET On Session  localhost   /api/v1/hasRelationshipBtwMovieYear    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND


hasRelationshipBtwMovieYearFail400
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   movieId=nm200    
    ${resp}=     GET On Session  localhost   /api/v1/hasRelationshipBtwMovieYear    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST



getMoviesOfYearPass
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   year=2023
    ${resp}=     GET On Session  localhost   /api/v1/getMoviesOfYear    json=${params}  headers=${headers}  expected_status=200

    ${json}=   Set Variable   ${resp.json()}
    
    ${movieList}=   Create List   nm202     nm203
    Lists Should be Equal   ${json['MovieList']}  ${movieList}

getMoviesOfYearFail
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   
    ${resp}=     GET On Session  localhost   /api/v1/getMoviesOfYear    json=${params}  headers=${headers}  expected_status=400

    Should Be Equal As Strings      ${resp.content}     400 BAD REQUEST

getMoviesOfYearFail404
    ${headers}=  Create Dictionary   Content-Type=application/json
    ${params}=   Create Dictionary   year=2025
    ${resp}=     GET On Session  localhost   /api/v1/getMoviesOfYear    json=${params}  headers=${headers}  expected_status=404

    Should Be Equal As Strings      ${resp.content}     404 NOT FOUND




