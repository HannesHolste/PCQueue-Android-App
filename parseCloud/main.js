/* 
  Background job is run every minute
  It decrements the wait time for each restaurant by one minute 
*/
Parse.Cloud.job("decrementQueueTimes", function(request, status) {
  // Set up to modify user data
  Parse.Cloud.useMasterKey();
  // Create query for all restaurants
  var objectIdList = [];
  var query = new Parse.Query("Restaurant");
  query.each(function(restaurant) {
    objectIdList.push(restaurant.id);
    console.log(restaurant);
    // Decrement the current wait by one minute
    var currentWait = restaurant.get("CurrentWait");
    if (currentWait > 0) {
      restaurant.increment("CurrentWait",-1);
      restaurant.save();
    } else {
      console.log("CurrentWait = " + currentWait);
    }
    return restaurant.save();
  }).then(function() {
    // Make all the clients update with the new times
    console.log("restaurantObjectIds = "+objectIdList.toString());
    Parse.Push.send({
      channels: [ "restaurantUpdates" ],
      data: {
        restaurantObjectIds : objectIdList
      }
    }, {
      success: function() {
        // Push was successful
        console.log("decrementQueueTimes multi-push successfully sent");
      },
      error: function(error) {
        // Handle error
        console.log("decrementQueueTimes had problems sending the multi-push");
      }
    });
    // Set the job's success status
    status.success("Decrement completed successfully.");
  }, function(error) {
    // Set the job's error status
    status.error("Something went wrong while attempting " +
                 "to decrement queue time.");
  });
});

/* 
  Background job is run every minute
  It checks what the current time is and accordingly sets the isClosed field
  Logic handled in-server is OK since the restaurants are all local to La Jolla
  and thus operating times are tied to California time

  We use the operatingHours array from each restaurant which looks like this:
  [{"Sunday":{"close":null,"open":null}},
  {"Monday":{"close":"9:00pm","open":"10:00am"}},
  {"Tuesday":{"close":"9:00pm","open":"10:00am"}},
  {"Wednesday":{"close":"9:00pm","open":"10:00am"}},
  {"Thursday":{"close":"9:00pm","open":"10:00am"}},
  {"Friday":{"close":"9:00pm","open":"10:00am"}},
  {"Saturday":{"close":null,"open":null}}] 
*/
Parse.Cloud.job("updateIsClosed", function(request, status) {
  // Set up to modify user data
  Parse.Cloud.useMasterKey();
  // Only send a push when we need to (which is a lot less than every minute)
  var sendPush = false;
  // Get the current time to make all the close-open assertions
  var currentTime = new Date();
  var UTCday = currentTime.getDay(); // 0-6
  var UTChours = currentTime.getHours();
  var UTCOFFSET = 8; // CA time is UTC-8
  var hours = UTChours-UTCOFFSET;
  var day = UTCday;
  var minutes = currentTime.getMinutes(); // minutes doesn't change via UTC
  /* Fix if we went below 0
     e.g. UTC time is 02:00
          then CA time is not -06:00, but instead
          -06:00 + 24 = 18:00 (6pm) */
  if (UTChours-UTCOFFSET < 0) {
    hours = hours + 24;
    // This also means we're referencing the day before
    day = day - 1;
    // Make sure we roll backwards into Saturday (7) properly from Sunday (0)
    if (day == -1) {
      day = 7;
    }
  }

  console.log("updateIsClosed: Current time is "+currentTime.toString());
  console.log("updateIsClosed: Modified for CA, we have day:"+day+" hour:"+hours+" min:"+minutes);

  // Now iterate through the restaurants and update their isClosed field
  var query = new Parse.Query("Restaurant");
  query.each(function(restaurant) {
    // Parse the restaurant data
    var restaurant_hours = restaurant.get("operatingHours");
    var today_hours = restaurant_hours[day];
    // -> Object {Saturday: Object}
    today_hours = (today_hours)[(Object.keys(today_hours)[0])];
    // -> Object {close: "9:00pm", open: "10:00am"}
    var opening_time = today_hours.open; // "10:00am"
    var closing_time = today_hours.close; // "9:00pm"
    /* If the time field is null for either field, that means
       the restaurant is closed for the entirety of that day
       Note: Assumes either both fields must be null or neither are null */
    var current_status = restaurant.get("isClosed"); // 0 or 1
    if (opening_time == null || closing_time == null) {
      // store should be closed
      if (current_status == 0) {
        restaurant.set("isClosed",1);
        sendPush = true;
      }
      console.log("updateIsClosed: restaurant: " + restaurant.get("Name")
                  + ", open: (null) close: (null) is CLOSED");
    } else {
      // Convert string to military time for simpler comparison
      var m_opening_time = toMilitaryTime(opening_time); // "10:00"
      var m_closing_time = toMilitaryTime(closing_time); // "21:00"
      // Now we see if the current time is within the open-close window
      var anchor_date = "01/01/2013"; // for sake of comparison, no meaning
      var currentHHMM = hours+":"+minutes;
      var debugstr = "ERROR";
      if ((Date.parse(anchor_date+" "+m_opening_time) <= 
           Date.parse(anchor_date+" "+currentHHMM))   &&
          (Date.parse(anchor_date+" "+currentHHMM)    <= 
           Date.parse(anchor_date+" "+m_closing_time))) {
        // store should be open
        if (current_status == 1) {
          restaurant.set("isClosed",0);
          sendPush = true;
        }
        debugstr = "OPEN";
      } else {
        // store should be closed
        if (current_status == 0) {
          restaurant.set("isClosed",1);
          sendPush = true;
        }
        debugstr = "CLOSED";
      }
      console.log("updateIsClosed: restaurant: " + restaurant.get("Name")
        + ", open: (" + opening_time + "->"+m_opening_time 
        + ") close: (" + closing_time+"->"+m_closing_time + ") is " + debugstr);
    }
    // Let the clients know that the restaurant just opened/closed
    // i.e. tell them to force-update said restaurant
    if (sendPush == true) {
      Parse.Push.send({
        channels: [ "restaurantUpdates" ],
        data: {
          restaurantObjectId : restaurant.id
        }
      }, {
        success: function() {
          // Push was successful
          console.log("updateIsClosed push successfully sent");
        },
        error: function(error) {
          // Handle error
          console.log("updateIsClosed push had problems sending!");
        }
      });
    }
    return restaurant.save();
  }).then(function() {
    // Set the job's success status
    status.success("Updated isClosed times successfully.");
  }, function(error) {
    // Set the job's error status
    status.error("Something went wrong while attempting " +
                 "to update isClosed times.");
  });
});

/* If the response is empty i.e.
     "result": []
   that means there was an error (check the Parse logs for deets)
   If the response has an object in it i.e.
     "result": [
        {
            "__type": "Pointer",
            "className": "Restaurant",
            "objectId": "xxxxxxxx"
        }
      ]
   that means that the request was processed correctly by the backend.
 */
Parse.Cloud.define('attemptUpdate', function(request, response) {
  // If the new update is [BLOCK_TIME_DIFF] off of the current
  // time, then we should ignore it because it's probably spam
  var BLOCK_TIME_DIFF = 20;
  // Pull the JSON from the request
  var restaurant_name = request.params.name;
  var new_time = request.params.time;
  console.log("request is this: " + request);
  console.log(restaurant_name + " " + new_time + "!!!");
  // XXX Validate input here before dispatching?
  var query = new Parse.Query("Restaurant");
  query.equalTo("Name", restaurant_name);
  // Some more parse bullshit (tbh wtf):
  query.find({
    success: function(results) {
      // Only one restaurant should match up
      var msg;
      if (results.length != 1) {
        msg = "Warning: " + results.length + " restaurants with same " +
              "name (" + restaurant_name + ") found in database";
        console.log(msg);
      } else {
        // Update the restaurant's wait time
        // XXX Ahem... race conditions anyone?
        var object = results[0];
        object.set("CurrentWait", new_time);
        object.save();
        msg = "Updated wait time for " + restaurant_name + " to " + new_time;
        console.log(msg);
        // Send out a push notification to the clients letting everyone
        // know which restaurant was updated
        // eg. JSON: {restaurantObjectId: "id of changed restaurant"}
        // var json_message = "{\"restaurantObjectId\":\""+object.id+"\"}";
        var json_message = "{\"restaurantObjectId\":\""+object.id+"\"}";
        Parse.Push.send({
          channels: [ "restaurantUpdates" ],
          data: {
            restaurantObjectId : object.id
          }
        }, {
          success: function() {
            // Push was successful
            console.log("attemptUpdate push successfully sent");
          },
          error: function(error) {
            // Handle error
            console.log("attemptUpdate push had problems sending!");
          }
        });
        console.log(msg);
      }
      response.success(msg);
    },
    error: function(error) {
      console.log("Error: " + error.code + " " + error.message);
      response.error(error);
    }
  });
  // done
});

/*
  Converts am-pm time to military time:
  ----------------------------------
  "12:00am" -> "00:00" (subtract 12)
  "12:01am" -> "00:01" (subtract 12)
  ----------------------------------
   "1:00am" -> "1:00" (do nothing)
  "11:00am" -> "11:00" (do nothing)
  "12:00pm" -> "12:00" (do nothing)
  ----------------------------------
   "1:00pm" -> "13:00" (add 12)
   "9:00pm" -> "21:00" (add 12)
  "11:59pm" -> "23:59pm" (add 12)
  ----------------------------------
  Note: Should be refactored
*/
function toMilitaryTime(str) {
    var isPM = /pm/.test(str);
    var is12 = /12:/.test(str);
    if (isPM === true) {
      // Grab number
      var reg = /^[^:].exec(str); // "9:00pm" -> "9"
      if (reg.length > 1) {
        console.log("toMilitaryTime: Wrong format!");
        return str;
      } else {
        if (is12 === true) {
          // 12pm do nothing
          return str.replace(/pm/,"");
        } else {
          // all other pm's, add 12
          var hour = (parseInt(reg[0])+12).toString();
          return str.replace(/^[^:]*/,(hour)).replace(/pm/,"");
        }
      }
    } else {
      if (is12 === true) {
        // subtract 12 (12:00am -> 00:00am -> 00:00)
        return (str.replace(/12:/,"00:")).replace(/am/,"");
      } else {
        // all other am's do nothing
        return str.replace(/am/,"");
      }
    }
}
