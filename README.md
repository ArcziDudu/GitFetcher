# GitFetcher

Application issuing rest api. After calling curl e.g. 
curl -X GET "http://localhost:8080/fetcher/api/repos/arcziDudu" -H "Authorization: bearer yourTokenFromGithub" -H "Accept: application/json"
an array of repositories of the specified user is returned with the information: name, url, description, fork(boolean) user(login) branche of the given repository(name, commit(sha,url)) 

"Accept: application/json" header is expected in case of application/xml, http.406 will be returned, in case the user is not found, http.404 will be returned, in case the wrong bearer token is given, http.401 will be returned. 

Technologies used: 
Java 17
Spring boot
Lombok 
Junit5
WebClient
WebTestClient
Wiremock


