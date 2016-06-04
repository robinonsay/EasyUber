# EasyUber

EasyUber is a basic implementation of the Uber API.  

Easy Uber:

  - Authenticates user with Uber acount using OAuth2.0 authentication
  - Displays all available Uber rides available and there respective estimated time of arrival (ETA)
  - Allows users to input their address to get the anticipated price of the Uber, approximatly how far the ride will be, and the duration of the ride.
  - Allows the user to call the uber for that given trip  
 
To make ride requests with EasyUber, you MUST be added as a developer on the API server. EasyUber utilizes Uber's sandbox environment (`https://sandbox-api.uber.com/<version>`). This allows EasyUber to make Uber requests without an actual driver showing up. 
