
console.log("inside makeNewPatient.js");
$(document).ready(function(){
    $("#create").click(function(){
        console.log("Create Clicked");
        var firstName = document.getElementById('firstName').value;
        var lastName = document.getElementById('lastName').value;
        var address = document.getElementById('address').value;
        var birthDate = document.getElementById('birthDate').value;
        var status = document.getElementById('status').value;
        var phoneNumber = document.getElementById('phoneNumber').value;
        jsonObject={
            "firstName":firstName,
            "lastName":lastName,
            "address":address,
            "phoneNumber":phoneNumber,
            "status":status,
            "birthDate":birthDate
        }
        $.ajax({
            url: 'http://localhost:8080/rest/system/addPatient/',
            method: 'POST',
            data: JSON.stringify(jsonObject),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function(res){ 
                console.log(res.responseText);
                alert(res.responseText);
            },
            error: function(err){
                console.log(err);
                alert(err.responseText);
            }
        })
    })
    $("#firstName").attr('required',true);
    $("#lastName").attr('required',true);
    $("#address").attr('required',true);
    $("#status").attr('required',true);
    $("#phoneNumber").attr('required',true);
    $("#birthDate").attr('required',true);

});