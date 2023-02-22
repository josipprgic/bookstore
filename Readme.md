# Simple bookstore Application

Application saves some books and authors in a DB. There is a way to retrieve and buy some books. A simple loyalty system is introduced; after 10 purchased books you can get one for free (however it ca't be a newly released book).

## Authors

* Josip Prgic

### Prerequisites:

    * Local postgres server on port 5432 with user postgres (password postgres)


### Run application:

    * mvn clean install -PcreateDbProfile
    * run main method in Application.java file

### Focused on
    * allow user to insert books
    * style code in a simple way
    * writing tests

### Not focused on
    * writing better error messages/status codes for the user querying the endpoints. I didn't consider it a necessary addition to the
      application since there is no "real" client using the application


### Instructions
    * after application start open postman and start querying endpoints :)
    * first thing you want to do is send a POST request to localhost:8080/user/insert endpoint with parameter username
       example: localhost:8080/user/insert?username=jprgic , method is POST
    * You can check stats for any user by sending GET requests to endpoints localhost:8080/user/loyaltyPoints?username=jprgic 
          and localhost:8080/user/purchases?username=jprgic

    * You can insert a book into the dabase by sending a POST request to localhost:8080/book/insert endpoint. The body of the request
       should look like something like this: 

        {
            "name": "Designing Data-Intensive Applications",
            "publishedDate": "2017-03-10T00:00:00",
            "price": 200.0,
            "bookType": "REGULAR",
            "authors": [
                {
                    "firstName": "Martin",
                    "lastName": "Kleppmann",
                    "dateOfBirth": "1963-05-01T00:00:00"
                }
            ]
        }
    
    * You can get the list of all the books in the database by sending a GET request to localhost:8080/book/all
    * Book purchase can be done by sending a PUT request to localhost:8080/book/buy endpoint
        That endpoint requires two URL parameters: username and useLoyaltyPoints e.g. localhost:8080/book/buy?username=jprgic&useLoyaltyPoints=true
        And the body of the request can be something like this 
            {
                "name": "Designing Data-Intensive Applications",
                "publishedDate": "2017-03-10T00:00:00"
            }
    * Books can be also purchased in bulks by sending a PUT request to this endpoint localhost:8080/book/buyBulk
        similar rules apply as to buying a single book on the previous endpoint 
        you have to set username and useLoyaltyPoints parameters localhost:8080/book/buyBulk?username=jprgic&useLoyaltyPoints=false
        And the body of the request consists of a list of books to buy:
        [
            {
                "name": "Designing Data-Intensive Applications",
                "publishedDate": "2017-03-10T00:00:00"
            },
            {
                "name": "Catch 22",
                "publishedDate": "1961-11-10T00:00:00"
            },
            {
                "name": "Three Musketeers",
                "publishedDate": "1844-04-01T00:00:00"
            }
        ]
    * The database initially starts with books Catch 22 and Three Musketeers
