1. A crowdsourced queue monitor for restaurants in PC (Nothing more). 
	( Much like the queue monitors for Disneyland )
  1a. People in line report the wait times
  1b. People outside of the line view the wait times :)
  1c. Possible GPS "Check-in" where count autoincrements based on location.
  Ionic Framework lets you webdev for android.

Basic first iteration: 
  Note that all reporting and viewing needs to be anonymous.
  1. Report wait times
    2a. Report how many people are in front of you. Estimation. Ranges should
        be an option when the number of people in front of you are >10. Wait times
	should be in absolute numbers rather than relative terms such as "busy"
  2. View wait times


What is restaurants in price center change?
- Some sort of ability to add restaurants. Fixed amount of restaurants.
    - Possibly can be scraped from PC website

The logos of the restaurants to select the restaurants?
Some sort of error correcting for people who conflict wait times?

When will the queue reports change?
Every 1 minute, the queue counter decrements unless otherwise updated by the user.

Design questions?

When a restaurant is closed, the restaurant item becomes disabled/nonclickable. 
Closing hours can be retrieved from PC 


As far as the restaurant listings go, alphabetical order. 

1-5
5-10
10-20
20+

-> Wait times based on time it takes to get through the queue

After voting for how long the wait is, the button 


The latest report is taken as the final report.
However, there should be an undo button when the user submits their time.

Show the restaurants logo rather than the text name of the restaurant.
