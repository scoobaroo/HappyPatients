
console.log("inside js");
$(document).ready(function(){

    $('#viewAll').on('click',function(){
        console.log("View All Button Pressed");
        $.ajax({
            url: 'http://localhost:8080/rest/system/retrieveAll',
            method: 'GET',
            contentType: 'text/plain', //type of data recieved from server.
            success: function(res){ 
                var htmltext ="<h1>All Patients</h1>";
                // htmltext += JSON.stringify(res.body);
                htmltext +=res;
                $('.all').html(htmltext);
            },
            error: function(err){
                console.log(err);
            }
        });
    });

});