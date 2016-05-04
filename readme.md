# Clamber Server

The Clamber Server is implemented in Java using the Dropwizard framework to manage and supply data needed by the Clamber Android App.

### Endpoints
- Comments Resource: endpoint for adding and removing comments for climbs in the database. 
- Completed Resource: endpoint for adding, removing and deleting completed climbs from the database.
- Project Resource: endpoint to add a project to the database based on climb and user, get back projects based on user and delete projects for a user. It also handles providing recommendations for a user based on their skill level.
- Ratings Resource: endpoint to add a rating to the database on a climb and to retrieve ratings based on the climb id. It also has a method to return the average of all of the ratings on a climb.
- User Resource: endpoint to add a new user to the database and to get the user information based on username. 
- Walls Resource: endpoint to retrieve data for the four main walls, wall sections and to retrieve the three wall sections updated by the gym that week.