# Technical Design of Surf Club App

## Technologies used:

For this project we used android studio to create the app. We coded in Java as it was everyone's most familiar language at the time of production. 

Our back end and database are all run using google firebase. We used Fireauth, Firestore and Firestorage features from this product . We also harness the use of Google maps APIs to provide mapping with our project.

We used a NOSQL database implementation. The database tables can be seen below:




While researching how to implement certain features we often came across old tutorials and deprecated ways of implementing things. We learned it was always essential to check that tutorials and implementations being viewed were using the latest and most up to date api’s and techniques..

It was also learned that before implementing a new feature to check the code previously written. If the new features have similar parts to already implemented features it is important to re use this code instead of implementing it differently. This keeps code familiarity high and easier to read and understand for the whole team. An example of this could be with recyclerViews. There are many ways to implement a recyclerView so it is important to stick to one method of this.

Perhaps next time it would be possible to create an app using koltin to gain experience with another power and upcoming language. As we already know the logic to what we want to do it would make learning the new language fun and enjoyable.
## Features:
### Rental
One of the main features of our app is the ability to rent boards from the surf club. We can view all the boards, select to rent them and then choose the date we would like to rent them for. If the board is already being rented on a particular date we cannot select that option from the calendar view. 
We also added the functionality to view your upcoming rentals and rental history. You can see all the boards you have rented for future dates or can get an overview of all your rentals showing you your top boards and how many times you rented each.
### Notifications 
We added notification functionality to our app so that a user will be reminded if they have an upcoming rental. The user will receive a push notification the day before their rental and when they click on it, they will be brought to their upcoming rentals page in the app. 
### Admin 
we created extra functionality for admins. They are able to manage boards, deleting any if they are no longer being used or they can add boards if the club gets more.
### Users
Users can check their profile and update their profile picture by selecting an existing picture from their camera roll or google drive or by taking one using their camera.
### Messaging 
By adding a message function, we allowed users to send messages to each other directly. You can select a user that you want to start a chat with, bringing you to the chat itself. After this initial chat has been set up, the desired user would pop up in your current chat section.
### Support
In the support section, a user can choose to either find the boathouse or find the pool. If they select ‘find boathouse’, it will bring them to a screen of a map that shows the location of the user and the location of the boathouse using markers. There will then be a line between these markers showing the route that the user can take to get there. If the user selects ‘find pool’ then it will do the same thing as before except the marker will be at the pool instead of the boathouse. 
