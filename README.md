# Getting Started: Roommates Finder
 # (DEMO: https://cse264-final-project.herokuapp.com/)
## Introduction 
Roommates Finder is an app where we provide a safe and secure environment, in which users can fill out their roommate preferences and look for potential roommates with matching lifestyle. This project basically consist of two parts：
- Backend: the server for Roommates Finder using the Java Spark web framework and deployed on heroku; a command-line administrative app for management of the tables 
- Frontend: a collection of HTML files and a CSS file for distributing and formatting contents; a JavaScript web front-ends using AJAX to communicate backend

## Member Roles: 
Front-end: 
- Yubo Wang, Wenmin Liu, Yuming Tian

Backend: 
- Xingchen Liu, Letong Zhang, Shenyi Yu

## Functionality:
User:
- ~/login                 Login page
- ~/profile/:uid 	        Put user profile 
- ~/profile?searchQuery 	Get all profile matching search query
- ~/profile/:uid 	        Get user profile

Admin:
- all functionalities user have
- ~/profile/:uid 	Delete user account

Web:
- login page: index.html
- search page: search.html
- profile page: profile.html


## User Story/Use Case:
### User
- All users must sign up/login with Google account to access the app.
- In the app, users will be able to view other user profiles by searching on the main page. 
- The aim is to provide users with a private, safe environment to look for roommates.
- Administrators are authorized to delete accounts with the consideration of protecting the application environment as secured, civilized. 
- For example, user Alicia would like to find a roommate for this semester, she signs up on Roommate Finder and searches on it, users who match her search criteria pop up and she can view their user profile. If she finds one of the user profiles disturbed with violent content, she can email the administrator and the admin will delete the account thoughtfully. 
### Admin
Apart from the user flow mention above, the admin has jurisdiction over all the user accounts. When finding content of an account inappropriate, admin will delete the account along with its user profile. 



## Technical Design:
Frontend: 
- We will have a welcome page for users to log in or sign up for an account. 
- After they have set up their profile, the user could use the search function on the main page to seek roommates with matching criteria.

Backend: 
- We will use the Java Spark framework to implement the RestAPI including routes that could sign up or log in with OAuth, 
- update personal profile, retrieve account information based on id, and also accept a search query for seeking roommates. 
- Also, we will set up a database on Heroku to store user information, and we could retrieve information with SQL queries.

## Tools/Libraries/Frameworks:
To run our application, below are the API keys, databases, and deploy method needed to run Roommates Finder:

- Frameworks: Spark
- Tools: Postman, NPM
- Libraries: Google's JSON library, Spark
- Database: Postgres
  - DATABASE_URL = `postgres://gwnqnodzrlxyoi:7e31094af269cc8a9c12fbf4ef752ffc54d58d75646ded60caa7a78fae76fd1c@ec2-52-2-82-109.compute-1.amazonaws.com:5432/d4pu28mdcacupl ` 
- Interactive UI: JS + HTML + CSS
- 3rd party REST API: 
  - Google OAuth
  - Client ID: 939374055996-8c9s33egqvv3lifjc60eh9lf0r3vvdi3.apps.googleusercontent.com
- Deploy method: Heroku

## Basic Requirements
- Git: we use Git to track our works
- Terminal: Mac has good default terminal. For Windows, the default terminal is fine but I recommend Git Bash
- VS code: I recommend the visual studio code for coding
- Github account: you should create an account so that we could share out project with you

## How to Get Started
1. Go to the repo on Github (we will add you to the project)
2. Copy the command starts with `git clone`
3. Create a folder in the local where you want to work on your project
4. Run the `git clone` command in your terminal to get a copy of the project to your folder
5. `git checkout` to the branch you want to work on
6. Start working
7. After finishing your work, use command `git add` to add the files you want to save.
8. Then, `git commit` to save your works
9. Finally, `git push` to save your work on the Github repo

# Developer Instructions

## Backend
### Software Requirements
- Java SDK: code compiling basic
- Maven: for package and deploy, please follow the [https://maven.apache.org/install.html](https://maven.apache.org/install.html) to install the maven

### Java Spark Web Framework
We use Java Spark to map the Routes to the corresponding Spark functions so that when front-ends make a Json call to a specific url, backend can give the response to that request.

### Google API
We use Google API in the backend for authentication purpose. We check auth token sent from the front-ends with Google OAuth. For more details, please check out [https://developers.google.com/identity/sign-in/web/backend-auth](https://developers.google.com/identity/sign-in/web/backend-auth)

### App.java
App.java is the main program of the backend. It contains the set up of the Database, set up of the Routes, and calling the functions in the database.

### Database.java
Database is responsible for the interaction with our heroku database. To do that, we have to write the prepared SQL statements.
   
### Compiling and Running
To compile the code, run `mvn package` in the terminal to compile the program and run the tests. Then, run `mvn exec:java`.
Our database URL is attached below and configured on Heroku, so no need to hard code the URL in the terminal. 
`DATABASE_URL=postgres://gwnqnodzrlxyoi:7e31094af269cc8a9c12fbf4ef752ffc54d58d75646ded60caa7a78fae76fd1c@ec2-52-2-82-109.compute-1.amazonaws.com:5432/d4pu28mdcacupl 
`

## Web
### Software Requirements
- NPM: for package management

### JavaScript
JavaScript is a text-based programming language used both on the client-side and server-side that allows we to make web pages interactive. Incorporating JavaScript improves the user experience of the web page by converting it from a static page into an interactive one. Basically, it is in charge of the functionality of the web page such as setting up the response of button and put the information on the web page.

### HTML
All HTML files are stored in the frontend folder, where JavaScript files are stored at. With HTML we can define headers, paragraphs, links, images, and more, thus the browser knows how to structure the web page we are looking at. It is in charge of the layout of our website, and it sets up the location of the messages, size of button and so on. 

### CSS
All CSS files are stored in the frontend folder, where JavaScript files are stored at. CSS is in charge of the styling of the web page, such as the color of the background, the pop-up form, visualization of tables. 

### Deploy
Since the web has to be deployed with the backend together, to deploy the web on our heroku server, first you have to run deploy.sh with `sh deploy.sh` command in the frontend folder. Then, use command `mvn package; mvn heroku:deploy` in the backend folder to deploy your web on the server.

If you have add new .html, .css or .js files, please add the .html, .css files to the deploy.sh and add the .js files to main.js  reference when you want to deploy.

