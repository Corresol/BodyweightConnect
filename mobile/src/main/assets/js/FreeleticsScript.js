'use strict';

function test() {
}

/**
* Hängt einen CallBack in die Webseite, so das jeder Click eines "Button" in die Applikation
* umgeleitet wird.
**/
function placeOnClickEventHandler(){
    var buttons = document.getElementsByTagName('button');
    var clazz = "androidCallback";
    for (var i = 0; i < buttons.length; ++i) {
          if((typeof($(buttons[i]).attr('class')) === 'undefined' ) || !$(buttons[i]).hasClass(clazz)){
               $(buttons[i]).addClass(clazz);
               $(buttons[i]).on("click",function(){
                    //JSPlugin.showToast(this.getAttribute("data-ng-click"));
                    setTimeout(freeleticsClickHandler,100, this.getAttribute("data-ng-click"));
               });
          }
    }

    buttons = document.getElementsByTagName('a');
    for (var i = 0; i < buttons.length; ++i) {
        if($(buttons[i]).hasClass("overlay-cancel-button") && !$(buttons[i]).hasClass(clazz)){
           $(buttons[i]).addClass(clazz);
           $(buttons[i]).on("click",function(){
                //JSPlugin.showToast(this.getAttribute("data-ng-click"));
                freeleticsClickHandler(this.getAttribute("data-ng-click"));
           });
        }
    }

}


function freeleticsClickHandler(action){
    switch(action){
        case "showTraining()" :
            JSPlugin.Text2Speech('open Training');
            JSPlugin.sendText('open Training');
            setTimeout(function(){
                $(".modal").remove();
                $(".modal-backdrop").remove();
            },500);
            break;
        case "startTraining()" :
            var exerciseTxt = readExercise();
            if(exerciseTxt.indexOf("#")>0){
                var txt = exerciseTxt.split("#");
                JSPlugin.sendText(txt[0]+' '+txt[1]);
                JSPlugin.Text2Speech('First Exercise  '+txt[0]+'  '+txt[1]);
                setTimeout(JSPlugin.Text2Speech,3000,"2");
                setTimeout(JSPlugin.Text2Speech,4000,"1");
                setTimeout(JSPlugin.Text2Speech,5000,"Go");
                setTimeout(JSPlugin.startMetronom,5050,txt[0],"freeletics");
                addClassToExercise("bwc-read");
            }
            break;
        case "nextExercise()" :
            JSPlugin.stopMetronom();
            var exerciseTxt = readExercise();
            if(!isLastExercise()){
                 removeClassFromExercise("bwc-read");
                 var txt = exerciseTxt.split("#");
                 if(txt[0]=='Rest'){
                    JSPlugin.sendText(txt[0]+' '+txt[1]);
                    var restTime = txt[1].replace(" sec","");
                    setTimeout(JSPlugin.Text2Speech,((restTime*1000)-1900),"2");
                    setTimeout(JSPlugin.Text2Speech,((restTime*1000)-900),"1");
                    setTimeout(function(){
                        var txt1 = readExercise();
                        JSPlugin.sendText(txt1.split("#")[0]+" "+txt1.split("#")[1]);
                        JSPlugin.Text2Speech(txt1.split("#")[0]+" "+txt1.split("#")[1]);
                    },(restTime*1000)+100);
                 }
                 JSPlugin.Text2Speech(''+txt[0]+" "+txt[1]);
                 JSPlugin.sendText(txt[0]+' '+txt[1]);
                 //setTimeout(JSPlugin.Text2Speech,1100, txt[1]);
                 setTimeout(JSPlugin.startMetronom,3000,txt[0],"freeletics");
             }else{
                removeClassFromExercise("bwc-read");
                JSPlugin.Text2Speech('finish training');
                JSPlugin.sendText('finish training');
             }
             break;
        case "submit()" :
            JSPlugin.Text2Speech('submit');
            break;
        case "abortTraining()" :
            JSPlugin.stopMetronom();
            JSPlugin.Text2Speech('Abort Training');
            JSPlugin.sendText('');
            break;
        default :
            // do nothing... :-)
    }

}


function clickBWCButton(){
  var buttons = document.getElementsByTagName('button');
  for (var i = 0; i < buttons.length; ++i) {
    if( new RegExp('btn-primary').test(buttons[i].className) ){
        if(new RegExp('showTraining()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
            //buttons[i].style.border='10px solid yellow';
            var a = buttons[i];
            a.click();
        }
        else if(new RegExp('startTraining()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
            //buttons[i].style.border='10px solid red';
            var a = buttons[i];
            a.click();
        }
        else if(new RegExp('nextExercise()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
             //buttons[i].style.border='10px solid green';
             var a = buttons[i];
             a.click();
        }
        else if(new RegExp('submit()').test(buttons[i].getAttribute("data-ng-click")) && visible(buttons[i])){
             //buttons[i].style.border='10px solid blue';
             var a = buttons[i];
             a.click();
        }
    }
  }
}


function visible(elm) {
  if(!elm.offsetHeight && !elm.offsetWidth) { return false; }
  if(getComputedStyle(elm).visibility === 'hidden') { return false; }
  return true;
}


function removeBorder(btn){
    btn.style.border='0px';
}


function readExercise(){
    //console.log("readExercise");
    var txt = "";
    $("li.workout_detail-table-repetitions").each(function(){
        //console.log("Repetitontable");
        if (txt.length>0) return false;
        $( this ).find("li").each(function(){
            if( $(this).children().first().is("h3")===true){
                //console.log("Headline..");
            }else{
                //console.log(typeof($(this).attr('class')) === 'undefined');
                if( (typeof($(this).attr('class')) === 'undefined' || $.trim($(this).attr('class'))=='current' )){
                    //console.log($(this));
                    txt = $(this).find("span.workout_detail-table-exercise").text()+"#"+$(this).find("span.big_and_bold").text();
                    return false;
                }
            }
        });
    });
    return txt;
}


function addClassToExercise(className){
    var found = false;
    $("li.workout_detail-table-repetitions").each(function(){
        if (found===true) return false;
        $( this ).find("li").each(function(){
            if( $(this).children().first().is("h3")===true){
            }else{
                if( (typeof($(this).attr('class')) === 'undefined' )){
                    found=true;
                    $(this).addClass(className);
                    return false;
                }
            }
        });
    });
}

function removeClassFromExercise(className){
    $("li.workout_detail-table-repetitions").each(function(){
        $( this ).find("li").each(function(){
             $(this).removeClass(className);
        });
    });
}


function isLastExercise(){
    var txt = readExercise();
    if(typeof(txt)==='undefined' || txt==='-#' || txt === ''){
        return true
    }
    return false;
}