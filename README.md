# movieTicketBooking
Added Movie ticket booking apis in postman collection.
Added db schema for reference.
if api wants to encrypt change encryptionEnabled=true in config.properties


Deploy the code in tomcat server and use those apis


About apis:

Admin have the rights to add the movie details and ticket rates -- http://localhost:8081/ticketBooking/admin/addHallAndMovieDetails/1.0.0
Admin can see what are all the details already added --http://localhost:8081/ticketBooking/admin/getDetails/1.0.0

User:
User have to SignUp for details regarding movie and theatre -- http://localhost:8081/ticketBooking/authenticate/signUp/1.0.0
After Sign Up login is required to look available movie tickets and all -- http://localhost:8081/ticketBooking/authenticate/login/1.0.0
To see available tickets and movies -- http://localhost:8081/ticketBooking/tickets/getAvailableTickets/1.0.0
To book Ticket and check whether the user is already booked 6 tickets in hall -- http://localhost:8081/ticketBooking/tickets/bookTicket/1.0.0
Payment for movie -- http://localhost:8081/ticketBooking/tickets/ticketPayment/1.0.0







