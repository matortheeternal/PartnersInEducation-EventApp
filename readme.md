Partners in Education Event Application
===============
For the Google I/O Hackathon at Citrix, Santa Barbara, we created an android application which allows users to connect to a backend server with a MySQL database with volunteer and event information.  This information is distributed to users and users are able to state when they arrive/start volunteering at an event and when they leave/stop volunteering.  The time they volunteered is then recorded and sent to the database.  The app uses GPS to verify if the user is within the vicinity of the event, so they're only able to start a timer tracking their volunteering during the event duration within the vicinity of the event.

Current Status
==================================
We made a backend that hooks up to a test MySQL database using Ruby on Rails.  We also made a frontend android application with a functioning UI.  We're currently upgrading the UI and finishing with populating data from the backend throughout the application.

Required Libraries
===================
You'll need to import appcompat-v7 and the Google APIs libprojects into Eclipse and link them with your build path as libraries correctly in order to compile this project.
