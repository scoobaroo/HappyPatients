
console.log("inside updatePatient.js");
$(document).ready(function(){
    $("#update").click(function(){
        console.log("update clicked");
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
            url: 'http://localhost:8080/rest/system/updatePatient/'+id,
            method: 'PUT',
            data: JSON.stringify(jsonObject),
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            success: function(res){ 
                alert("Patient Successfully Updated");
                console.log(res.responseText);
                // alert(JSON.parse(res).responseText);
            },
            error: function(err){
                console.log(err.responseText);
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