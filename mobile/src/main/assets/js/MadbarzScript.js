'use strict';

var arrayOfTimeOuts = [];

function test() {
}

/**
* Hängt einen CallBack in die Webseite, so das jeder Click eines "Button" in die Applikation
* umgeleitet wird.
**/
function placeOnClickEventHandler(){
    //JSPlugin.showToast("place Handler");
    var buttons = document.getElementsByTagName('button');
    var clazz = "androidCallback";
    for (var i = 0; i < buttons.length; ++i) {
          if((typeof($(buttons[i]).attr('class')) === 'undefined' ) || !$(buttons[i]).hasClass(clazz)){
               $(buttons[i]).addClass(clazz);
               $(buttons[i]).on("click",function(){
                    //JSPlugin.showToast(this.getAttribute("data-ng-click"));
                    setTimeout(madbarzClickHandler,100, this.getAttribute("data-ng-click"));
               });
          }
    }
}

function madbarzClickHandler(action){
    //JSPlugin.showToast(action);
    switch(action){
        case "navigateToWorkoutScreen()" :
             clearTimeouts();
             JSPlugin.Text2Speech('open Training');
             JSPlugin.sendText('open Training');
             break;
        case "onStartClick()" :
             clearTimeouts();
             $("i.sound-icon").click();
             var exercise =  $("#CurrentWorkout").text();
             var repetitions =  $("#RepInfo div.rep-number span.rep-value").text();
             JSPlugin.Text2Speech('First Exercise '+exercise+' '+repetitions);
             JSPlugin.sendText(exercise+' '+repetitions);
             setTimeout(JSPlugin.Text2Speech,3000,"2");
             setTimeout(JSPlugin.Text2Speech,4000,"1");
             setTimeout(JSPlugin.Text2Speech,5000,"Go");
             setTimeout(JSPlugin.startMetronom,5050,exercise,"madbarz");
             break;
        case "onNextClick()":
             clearTimeouts();
             JSPlugin.stopMetronom();
             JSPlugin.Text2Speech('Confirm');
             JSPlugin.sendText('Confirm');
             break;
        case "onConfirmClick()":
             clearTimeouts();
             JSPlugin.stopMetronom();
             var txt = readExercise();
             JSPlugin.Text2Speech(''+txt[0]+" "+txt[1]);
             JSPlugin.sendText("REST | Next Exercise"+txt[0]+" "+txt[1]);
             var a =setTimeout(function(){
                var timer = $("#PauseTimer").first("span").text();
                var b = setTimeout(JSPlugin.Text2Speech,2000,(parseInt(timer)-2)+" REST. Click to continue.");
                var c = setTimeout(madbarzClickHandler,parseInt(timer),"skipPause()");
                arrayOfTimeOuts.push(b);
                arrayOfTimeOuts.push(c);
             },3000);
             arrayOfTimeOuts.push(a);

             break;
        case "skipPause()":
             clearTimeouts();
             JSPlugin.stopMetronom();
             var exercise =  $("#CurrentWorkout").text();
             var repetitions =  $("#RepInfo div.rep-number span.rep-value").text();
             JSPlugin.Text2Speech(exercise+' '+repetitions);
             JSPlugin.sendText(exercise+' '+repetitions);
             setTimeout(JSPlugin.Text2Speech,3000,"2");
             setTimeout(JSPlugin.Text2Speech,4000,"1");
             setTimeout(JSPlugin.Text2Speech,5000,"Go");
             setTimeout(JSPlugin.startMetronom,5050,exercise,"madbarz");
             break;
        case "cancelWorkout()" :
            clearTimeouts();
            JSPlugin.stopMetronom();
            JSPlugin.Text2Speech('Cancel Workout');
            JSPlugin.sendText('');
            break;
    }
}

function clickBWCButton(){
    var buttons = document.getElementsByTagName('button');
  for (var i = 0; i < buttons.length; ++i) {
    if( new RegExp('btn-important').test(buttons[i].className) || new RegExp('btn-madbarz-primary').test(buttons[i].className) ){
        if(new RegExp('navigateToWorkoutScreen()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
            var a = buttons[i];
            a.click();
            return;
        }
        else if(new RegExp('onStartClick()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
            var a = buttons[i];
            a.click();
            return;
        }
        else if(new RegExp('onNextClick()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
             var a = buttons[i];
             a.click();
             return;
        }
        else if(new RegExp('onConfirmClick()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
             var a = buttons[i];
             a.click();
             return;
        }
        else if(new RegExp('skipPause()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
            var a = buttons[i];
            a.click();
            return;
        }
        else if(new RegExp('cancelWorkout()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
              var a = buttons[i];
              a.click();
              return;
        }
    }
  }
}

function visible(elm) {
  if(!elm.offsetHeight && !elm.offsetWidth) { return false; }
  if(getComputedStyle(elm).visibility === 'hidden') { return false; }
  return true;
}


function readExercise(){
    var div = $("#workoutPause .next-workout").first().text();
    var div1 = div.split(" - ");
    var div1_1 = div1[0].split(" x ");
    return div1_1;
}


function clearTimeouts(){
    arrayOfTimeOuts.forEach(function(currentValue){
       clearTimeout(currentValue);
    });
    while (arrayOfTimeOuts.length) { arrayOfTimeOuts.pop(); }
}

placeOnClickEventHandler();